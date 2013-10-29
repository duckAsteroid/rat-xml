package com.duckasteroid.ratxml;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.xml.sax.InputSource;

import com.duckasteroid.ratxml.xpath.Navigator;
import com.strangegizmo.cdb.Cdb;

import junit.framework.TestCase;

public class XPathTest extends TestCase {
	public static final String SELECT_MANCHESTER = "//world/continent/country/city[name='Manchester']";
	public static final String SELECT_UK_CITIES = "//world/continent/country[@id='3.1']/city";
	
	private Reader ratXml;
	@Override
	protected void setUp() throws Exception {
		// convert countries XML to RAT-XML
		InputStream stream = getClass().getClassLoader().getResourceAsStream("countries.xml");
		File cdbFile = new File("countries.cdb");
		Writer writer = new Writer(cdbFile, true, true);
		writer.write(new InputSource(stream));
		
		// read the rat-xml 
		Cdb cdb = new Cdb(cdbFile); 
		ratXml = new Reader(cdb);
	}
	
	@Override
	protected void tearDown() throws Exception {
		ratXml.close();
		ratXml = null;
	}
	
	public void test() throws SAXPathException {
		Navigator n = new Navigator();
		XPath xPath = n.parseXPath(SELECT_MANCHESTER);
		List<?> nodes = xPath.selectNodes(ratXml);
		assertNotNull(nodes);
		assertEquals(1, nodes.size());
		Node manchester = (Node) nodes.get(0);
		assertEquals("3.1.2", manchester.getAttribute("id"));
	}
	
}
