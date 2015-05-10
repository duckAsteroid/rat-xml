package com.duckasteroid.ratxml.converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.duckasteroid.ratxml.Data;
import com.duckasteroid.ratxml.Key;
import com.strangegizmo.cdb.CdbMake;

/**
 * A SAX event handler that will write XML elements & attributes as keys to CDB as they are encountered.
 * This is the principle class responsible for writing the RatXML file format.
 */
class SaxHandler extends DefaultHandler {
	/** Logger for debug */
	private final static Logger LOG = Logger.getLogger(SaxHandler.class.getName()); 
	/** A CDB make instance we will write XML key pairs to */
	private CdbMake cdb;
	
	/**
	 * This map is used to track counts of elements by their ID.
	 * The ID is 
	 */
	private HashMap<String, Integer> elementCounter = new HashMap<String, Integer>();
	/**
	 * This is used to track/build character data in the current element
	 */
	private StringBuilder characters = null;

	/**
	 * Should we trim element content (this also removes whitespace-only nodes from the tree) 
	 */
	private boolean trimWhitespace = false;
	/**
	 * A stack of the most recently created keys
	 */
	private Stack<Key> stack = new Stack<Key>();
	/**
	 * Next available ID for element/attribute
	 */
	private long nextId = 0;
	/**
	 * Create the handler to write to the given CdbMake. This CDB make object
	 * should already be initialised via {@link CdbMake#start(java.io.File)}
	 * 
	 * @param cdb
	 *            The CDB make object we will write to
	 * @param outputMetadata
	 * @param trimWhitespace 
	 */
	public SaxHandler(CdbMake cdb, boolean trimWhitespace) {
		this.cdb = cdb;
		this.trimWhitespace = trimWhitespace;
	}

	/**
	 * Starts by pushing a root "document element" key (id=0) to the stack
	 */
	@Override
	public void startDocument() throws SAXException {
		// push a root (document) key
		stack.push(Key.createElementDataKey(0));
	}

	/**
	 * Starts a new element - flushing tracked
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		Key parent = stack.peek();
		characters = new StringBuilder();
		// a new element gets a new ID
		long elementId = allocateId();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("New element "+qName+"="+elementId);
		}

		// find out if there is an existing count of this child element
		String counterKey = counterKey(parent, qName);
		// how many child elements with this name have there been in 
		// the parent. Kind of like reverse engineered xpath:
		// parent/child[0], parent/child[1] etc.
		// count is the number in square brackets...
		int count = 0;
		if (elementCounter.containsKey(counterKey)) {
			count = elementCounter.get(counterKey);
		}
		
		// an element key for the new ID
		Key elementKey = Key.createElementDataKey(elementId);
		
		// record meta data about this node on the parent
		try {
			// write metadata about this new element onto the parent element
			Key metaKey = parent.getChildMetaDataKey();
			// data describing that the Nth element element with name qName is @ID = id
			cdb.add(metaKey.value, Data.createChildElementData(qName, count, elementId));
		} catch (IOException e) {
			throw new SAXException(e);
		}	
		
		
		// keep track of the number of these elements
		count ++;
		// store it for next time we might need it
		elementCounter.put(counterKey, count);
		
		// now we walk the attribute set and write those to CDB
		try {
			// write all attributes to the cdb
			for (int i = 0; i < attributes.getLength(); i++) {
				// each attribute gets its own CDB ID for the data
				long attrId = allocateId();
				// and a CDB key for that
				Key attr = Key.createAttributeDataKey(attrId);
				// get the value of the attribute
				String value = attributes.getValue(i);
				// write the attribute data to the CDB file
				cdb.add(attr.value, value.getBytes());
				
				// record meta data for attribute names
				Key attrMetaDataKey = elementKey.getAttributeMetaDataKey();
				cdb.add(attrMetaDataKey.value, Data.createChildAttributeData(attributes.getQName(i), attrId));
			}
		} catch (IOException e) {
			throw new SAXException(e);
		}
		// stick the most recent element key on the stack
		stack.push(elementKey);
	}

	/**
	 * Allocates and returns the next element/attribute ID in CDB
	 * @return
	 */
	private long allocateId() {
		return ++nextId;
	}

	/**
	 * A key name for the counter of element X in element Y
	 * @param parent the parent element Y
	 * @param qName the name of element X
	 * @return The key to lookup the number of X's in Y
	 */
	private String counterKey(Key parent, String qName) {
		return Long.toString(parent.getId()) + '/' + qName;
	}
	
	/**
	 * Append character data to the current {@link #characters} buffer
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String data = new String(ch, start, length);
		if(data.length() > 0){
			characters.append(data);
		}
	}

	/**
	 * End of an element - time to write the element data
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// the key of this element
		Key key = stack.pop();
		// character data to write
		String s = characters.toString();
		// trim the whitespace from the data?
		if (trimWhitespace) {
			s = s.trim();
		}
		// write the text data for the element
		if (s.length() > 0) {
			try {
				cdb.add(key.value, s.getBytes());
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}
		// reset character data
		characters = new StringBuilder();
	}

	/**
	 * Finish the CDB file
	 */
	@Override
	public void endDocument() throws SAXException {
		// write the CDB file
		try {
			cdb.finish();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}
}
