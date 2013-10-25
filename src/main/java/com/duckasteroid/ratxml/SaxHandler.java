package com.duckasteroid.ratxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.strangegizmo.cdb.CdbMake;

/**
 * A SAX event handler that will write XML elements & attributes as keys to CDB as they are encountered
 */
class SaxHandler extends DefaultHandler {
	/** A CDB make we will write XML key pairs to */
	private CdbMake cdb;

	private Path currentPath = Path.ROOT;

	/**
	 * This map is used to track counts of elements 
	 */
	private HashMap<String, Integer> elementCounter = new HashMap<String, Integer>();
	/**
	 * This is used to track character data in the current element
	 */
	private StringBuilder characters = null;

	/**
	 * Should we record meta data in CDB (names of child elements/attributes)
	 */
	private boolean metadata;

	/**
	 * Create the handler to write to the given CdbMake. This CDB make object
	 * should already be initialised via {@link CdbMake#start(java.io.File)}
	 * 
	 * @param cdb
	 *            The CDB make object we will write to
	 * @param outputMetadata
	 */
	public SaxHandler(CdbMake cdb, boolean outputMetadata) {
		this.cdb = cdb;
		this.metadata = outputMetadata;
	}

	@Override
	public void startDocument() throws SAXException {
		currentPath = Path.ROOT;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		characters = new StringBuilder();
		// if recording meta data
		if (metadata) {
			try {
				// write child element name as a metadata
				Path metaKey = currentPath.getAttribute(Constants.CHILDREN);
				System.out.println(metaKey +"="+qName);
				cdb.add(metaKey.asKey(), qName.getBytes());
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}		
		
		// find out if there is an existing count of this child element
		String key = currentPath.toString() + "/" + qName;
		int count = 0;
		if (elementCounter.containsKey(key)) {
			count = elementCounter.get(key);
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
				String name = attributes.getLocalName(i);
				attrNames.add(name);
				String value = attributes.getValue(i);
				Path attr = currentPath.getAttribute(name);
				cdb.add(attr.asKey(),
						value.getBytes());

			}
			// if recording meta data - write out attribute names
			if (metadata) {
				Path attrKey = currentPath.getAttribute(Constants.ATTRIBUTES);
				for (String attr : attrNames) {
					cdb.add(attrKey.asKey(), attr.getBytes());
					System.out.println(attrKey.toString() + "=" + attr);
				}
			}
		} catch (IOException e) {
			throw new SAXException(e);
		}
		System.out.println(currentPath.toString());
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
		
		if (characters.length() > 0) {
			try {
				cdb.add(currentPath.asKey(), characters.toString()
						.getBytes());
				System.out.println(currentPath.toString()+"=\""+characters.toString()+'"');
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
