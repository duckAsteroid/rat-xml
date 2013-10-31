package com.duckasteroid.ratxml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.duckasteroid.ratxml.Writer;

/**
 * A main class for taking an XML file and converting it to Rat XML
 */
public class RatXmlConverter {

	/**
	 * Convert the input XML file into the output rat XML file
	 * @param input An XML file to convert
	 * @param output A file to write the rat XML to
	 * @throws ParserConfigurationException If there is a problem creating the SAX parser
	 * @throws SAXException If there is a problem processing the input file
	 * @throws IOException If there is a problem reading the XML or writing the rat XML
	 */
	public static final void convert(File input, File output)  throws ParserConfigurationException, SAXException, IOException {
		Writer w = new Writer(output, true, true);
		InputSource xml = new InputSource(new FileInputStream(input));
		w.write(xml);
	}
	
	/**
	 * A main method to invoke from the command line
	 * @param args expects 2 arguments input and output filenames
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		if (args.length < 2)
			throw new IllegalArgumentException("Required 2 args: XML input file, rat-xml output file");
		File input = new File(args[0]);
		File output = new File(args[1]);
		convert(input, output);
	}

}
