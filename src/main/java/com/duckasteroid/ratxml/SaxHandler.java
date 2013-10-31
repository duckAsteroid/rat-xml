package com.duckasteroid.ratxml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
	 * This map is used to track counts of elements by their ID
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

	@Override
	public void startDocument() throws SAXException {
		// push a root (document) key
		stack.push(Key.createElementDataKey(0));
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		Key parent = stack.peek();
		characters = new StringBuilder();
		long elementId = allocateId();

		// find out if there is an existing count of this child element
		String counterKey = Long.toString(parent.getId()) + '/' + qName;
		int count = 0;
		if (elementCounter.containsKey(counterKey)) {
			count = elementCounter.get(counterKey);
		}
		
		Key elementKey = Key.createElementDataKey(elementId);
		
		// record meta data about this node on the parent
		try {
			// write child element name as a metadata
			
			Key metaKey = parent.getChildMetaDataKey();
			String childData = qName + "[" + count+ "]:" + elementId;
			
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(metaKey.toString() + "=" + childData);
			}
			cdb.add(metaKey.value, childData.getBytes());
		} catch (IOException e) {
			throw new SAXException(e);
		}	
		
		
		// keep track of the number of these elements
		count ++;
		elementCounter.put(counterKey, count);
		
		
		try {
			// write all attributes to the cdb
			for (int i = 0; i < attributes.getLength(); i++) {
				long attrId = allocateId();
				Key attr = Key.createAttributeDataKey(attrId);
				String value = attributes.getValue(i);				
				cdb.add(attr.value, value.getBytes());
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(attr.toString() + "=" + value);
				}
				
				// record meta data attribute names
				Key attrMetaDataKey = elementKey.getAttributeMetaDataKey();
				String metaData = attributes.getQName(i) + ":" + attrId;
				cdb.add(attrMetaDataKey.value, metaData.getBytes());
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(attrMetaDataKey.toString() + "=" + attr);
				}
			}
			
		} catch (IOException e) {
			throw new SAXException(e);
		}
		stack.push(elementKey);
	}

	private long allocateId() {
		return ++nextId;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String data = new String(ch, start, length);
		if(data.length() > 0){
			characters.append(data);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		Key key = stack.pop();
		String s = characters.toString();
		if (trimWhitespace) {
			s = s.trim();
		}
		if (s.length() > 0) {
			try {
				cdb.add(key.value, s.getBytes());
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(key.toString()+"=\""+s.toString()+'"');
				}
			} catch (IOException e) {
				
			}
			
		}
		characters = new StringBuilder();
	}


	@Override
	public void endDocument() throws SAXException {
		try {
			cdb.finish();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}
}
