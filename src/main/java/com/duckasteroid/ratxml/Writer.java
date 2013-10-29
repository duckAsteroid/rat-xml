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

public class Writer {
	private XMLReader xmlReader;

	public Writer(File outputFile, boolean outputMetadata, boolean trimWhitespace) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    //spf.setNamespaceAware(true);
		SAXParser saxParser = spf.newSAXParser();
		xmlReader = saxParser.getXMLReader();
		CdbMake cdb = new CdbMake();
		cdb.start(outputFile);
		xmlReader.setContentHandler(new SaxHandler(cdb, outputMetadata, trimWhitespace));
	}
	
	public void write(InputSource xml) throws IOException, SAXException {
		xmlReader.parse(xml);
	}
}
