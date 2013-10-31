package com.duckasteroid.ratxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.strangegizmo.cdb.CdbMake;

/**
 * A SAX event handler that will write XML elements & attributes as keys to CDB as they are encountered.
 * This is the principle class responsible for writing the RatXML file format.
 */
class SaxHandler extends DefaultHandler {
	/** A CDB make instance we will write XML key pairs to */
	private CdbMake cdb;
	/** Track the current path (e.g. for text nodes) */
	private Path currentPath = Path.ROOT;

	/**
	 * This map is used to track counts of elements 
	 */
	private HashMap<String, Integer> elementCounter = new HashMap<String, Integer>();
	/**
	 * This is used to track/build character data in the current element
	 */
	private StringBuilder characters = null;

	/**
	 * Should we record meta data in CDB (names of child elements/attributes)
	 * Doing any kind of XPath later requires it
	 */
	private boolean metadata;
	/**
	 * Should we trim element content (this also removes whitespace-only nodes from the tree) 
	 */
	private boolean trimWhitespace = false;

	/**
	 * Create the handler to write to the given CdbMake. This CDB make object
	 * should already be initialised via {@link CdbMake#start(java.io.File)}
	 * 
	 * @param cdb
	 *            The CDB make object we will write to
	 * @param outputMetadata
	 * @param trimWhitespace 
	 */
	public SaxHandler(CdbMake cdb, boolean outputMetadata, boolean trimWhitespace) {
		this.cdb = cdb;
		this.metadata = outputMetadata;
		this.trimWhitespace = trimWhitespace;
	}

	@Override
	public void startDocument() throws SAXException {
		currentPath = Path.ROOT;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		characters = new StringBuilder();
		
		// find out if there is an existing count of this child element
		String key = currentPath.toString() + "/" + qName;
		int count = 0;
		if (elementCounter.containsKey(key)) {
			count = elementCounter.get(key);
		}
		
		// if recording meta data
		if (metadata) {
			try {
				// write child element name as a metadata
				Path metaKey = currentPath.getMetaData(Constants.CHILDREN);
				String childName = qName + "[" + count+ "]";
				System.out.println(metaKey.toString() + "=" + childName);
				cdb.add(metaKey.asKey(), childName.getBytes());
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}		
		
		
		// calculate the new child element name
		currentPath = currentPath.getChild(qName, count);
		
		// keep track of the number of these elements
		count ++;
		elementCounter.put(key, count);
		
		
		try {
			// record all attribute names for meta data
			ArrayList<String> attrNames = new ArrayList<String>();
			// write all attributes to the cdb
			for (int i = 0; i < attributes.getLength(); i++) {
				String name = attributes.getQName(i);
				attrNames.add(name);
				String value = attributes.getValue(i);
				Path attr = currentPath.getAttribute(name);
				cdb.add(attr.asKey(),
						value.getBytes());
				System.out.println(attr.toString() + "=" + value);

			}
			// if recording meta data - write out attribute names
			if (metadata) {
				Path attrKey = currentPath.getMetaData(Constants.ATTRIBUTES);
				for (String attr : attrNames) {
					cdb.add(attrKey.asKey(), attr.getBytes());
					System.out.println(attrKey.toString() + "=" + attr);
				}
			}
		} catch (IOException e) {
			throw new SAXException(e);
		}
		//System.out.println(currentPath.toString());
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
		String s = characters.toString();
		if (trimWhitespace) {
			s = s.trim();
		}
		if (s.length() > 0) {
			try {
				cdb.add(currentPath.asKey(), s.getBytes());
				System.out.println(currentPath.toString()+"=\""+s.toString()+'"');
			} catch (IOException e) {
				
			}
			
		}
		characters = new StringBuilder();
		currentPath = currentPath.getParent();
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
