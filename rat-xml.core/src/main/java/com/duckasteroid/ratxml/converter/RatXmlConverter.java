package com.duckasteroid.ratxml.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.duckasteroid.ratxml.io.impl.CdbDataOutput;
import com.strangegizmo.cdb.CdbMake;

/**
 * A main class for taking an XML file and converting it to Rat XML
 */
public class RatXmlConverter {

	/**
	 * Convert the input XML file into the output rat XML file
	 * 
	 * @param input
	 *            An XML file to convert
	 * @param output
	 *            A file to write the rat XML to
	 * @throws ParserConfigurationException
	 *             If there is a problem creating the SAX parser
	 * @throws SAXException
	 *             If there is a problem processing the input file
	 * @throws IOException
	 *             If there is a problem reading the XML or writing the rat XML
	 */
	public static final void convert(File input, File output,
			boolean trimWhitespace) throws ParserConfigurationException,
			SAXException, IOException {
		InputSource xml = new InputSource(new FileInputStream(input));
		convert(xml, output, trimWhitespace);
	}
	
	/**
	 * Convert the input XML stream into the output rat XML file
	 * 
	 * @param stream
	 *            A stream of XML to convert
	 * @param output
	 *            A file to write the rat XML to
	 * @throws ParserConfigurationException
	 *             If there is a problem creating the SAX parser
	 * @throws SAXException
	 *             If there is a problem processing the input file
	 * @throws IOException
	 *             If there is a problem reading the XML or writing the rat XML
	 */
	public static final void convert(InputStream stream, File output,
			boolean trimWhitespace) throws ParserConfigurationException,
			SAXException, IOException {
		InputSource xml = new InputSource(stream);
		convert(xml, output, trimWhitespace);
	}

	/**
	 * Convert the input XML into the output rat XML file
	 * 
	 * @param xml
	 *            An XML input source to convert
	 * @param output
	 *            A file to write the rat XML to
	 * @throws ParserConfigurationException
	 *             If there is a problem creating the SAX parser
	 * @throws SAXException
	 *             If there is a problem processing the input file
	 * @throws IOException
	 *             If there is a problem reading the XML or writing the rat XML
	 */
	public static final void convert(InputSource xml, File output,
			boolean trimWhitespace) throws ParserConfigurationException,
			SAXException, IOException {
		// create the CDB file
		CdbMake cdb = new CdbMake();
		cdb.start(output);
		CdbDataOutput dataOutput = new CdbDataOutput(cdb);
		// open the XML file
		SAXParserFactory spf = SAXParserFactory.newInstance();
		// spf.setNamespaceAware(true);
		SAXParser saxParser = spf.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
		xmlReader.setContentHandler(new SaxHandler(dataOutput, trimWhitespace));
		xmlReader.parse(xml);
	}

	/**
	 * A main method to invoke from the command line
	 * 
	 * @param args
	 *            expects 2 arguments input and output filenames
	 */
	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException {
		if (args.length < 2)
			throw new IllegalArgumentException(
					"Required 2 args: XML input file, rat-xml output file");
		File input = new File(args[0]);
		File output = new File(args[1]);
		convert(input, output, false);
	}

}
