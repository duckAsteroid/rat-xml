package com.duckasteroid.ratxml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.strangegizmo.cdb.CdbMake;

/**
 * Used to write XML data to a Rat XML file.
 */
public class Writer {
	
	private XMLReader xmlReader;
	private CdbMake cdb;
	private boolean trimWhitespace;

	/**
	 * Create a writer to output Rat XML data 
	 * @param outputFile A file where the Rat XML will be written. If this file exists it will be overwritten.
	 * @param trimWhitespace A flag to indicate if whitespace should be removed from the output. This also removes "empty" (whitespace only) elements from the output.
	 * @throws IOException If there is a problem writing to the 
	 */
	public Writer(File outputFile, boolean trimWhitespace) throws IOException {
		this.cdb = new CdbMake();
		this.cdb.start(outputFile);
		this.trimWhitespace = trimWhitespace;		
	}
	/**
	 * Process a given XML document and write it to the rat XML file. This method can only be called once.
	 * @param xml An input source containing the XML data to write to the rat xml file
	 * @throws IOException If there is a problem reading XML or writing rat XML
	 * @throws SAXException If there is a problem reading the XML
	 * @throws ParserConfigurationException If there is a problem creating a SAX parser for the XML
	 */
	public void write(InputSource xml) throws IOException, SAXException, ParserConfigurationException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    //spf.setNamespaceAware(true);
		SAXParser saxParser = spf.newSAXParser();
		xmlReader = saxParser.getXMLReader();
		xmlReader.setContentHandler(new SaxHandler(cdb, trimWhitespace));
		xmlReader.parse(xml);
		
	}
}
