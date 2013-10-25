package com.duckasteroid.ratxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class RatXmlConverter {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		if (args.length < 2)
			throw new IllegalArgumentException("Required 2 args: XML input file, rat-xml output file");
		File input = new File(args[0]);
		File output = new File(args[1]);
		Writer w = new Writer(output, true);
		InputSource xml = new InputSource(new FileInputStream(input));
		w.write(xml);
	}

}
