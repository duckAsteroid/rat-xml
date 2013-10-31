package com.duckasteroid.ratxml;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.xml.sax.InputSource;

import com.duckasteroid.ratxml.xpath.RatXPath;

public class XPathTest extends TestCase {
	public static final String SELECT_MANCHESTER = "//world/continent/country/city[name='Manchester']";
	public static final String SELECT_UK_CITIES = "//world/continent/country[@id='3.1']/city";
	
	private Document ratXml;
	@Override
	protected void setUp() throws Exception {
		// convert countries XML to RAT-XML
		InputStream stream = getClass().getClassLoader().getResourceAsStream("countries.xml");
		File cdbFile = new File("countries.cdb");
		Writer writer = new Writer(cdbFile, true, true);
		writer.write(new InputSource(stream));
		
		// read the rat-xml 
		ratXml = new Document(cdbFile);
	}
	
	@Override
	protected void tearDown() throws Exception {
		ratXml.close();
		ratXml = null;
	}
	
	public void testXPath() throws SAXPathException {
		XPath xPath = new RatXPath(SELECT_MANCHESTER);
		List<?> nodes = xPath.selectNodes(ratXml);
		assertNotNull(nodes);
		assertEquals(1, nodes.size());
		Node manchester = (Node) nodes.get(0);
		assertEquals("3.1.2", manchester.getAttributeValue("id"));
	}
	
}
