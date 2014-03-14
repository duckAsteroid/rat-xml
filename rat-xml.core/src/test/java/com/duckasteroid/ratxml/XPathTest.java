package com.duckasteroid.ratxml;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.xml.sax.InputSource;

import com.duckasteroid.ratxml.converter.Writer;
import com.duckasteroid.ratxml.xpath.RatXPath;

public class XPathTest extends TestCase {
	/** Selects just the single city element representing Manchester */
	public static final String SELECT_MANCHESTER = "//world/continent/country/city[name='Manchester']";
	/** Selects UK cities (London, Manchester, Edinburgh) */
	public static final String SELECT_UK_CITIES = "//world/continent/country[@id='3.1']/city";
	
	private Document ratXml;
	@Override
	protected void setUp() throws Exception {
		// convert countries XML to RAT-XML
		InputStream stream = getClass().getClassLoader().getResourceAsStream("countries.xml");
		File cdbFile = new File("countries.cdb");
		Writer writer = new Writer(cdbFile, true);
		writer.write(new InputSource(stream));
		
		// read the rat-xml 
		ratXml = new Document(cdbFile);
	}
	
	@Override
	protected void tearDown() throws Exception {
		ratXml.close();
		ratXml = null;
	}
	
	public void testSimpleXPath() throws SAXPathException {
		XPath xPath = new RatXPath(SELECT_MANCHESTER);
		List<?> nodes = xPath.selectNodes(ratXml);
		assertNotNull(nodes);
		assertEquals(1, nodes.size());
		Node manchester = (Node) nodes.get(0);
		assertEquals("3.1.2", manchester.getAttributeValue("id"));
	}
	
	public void testMultiXPath() throws SAXPathException {
		XPath xPath = new RatXPath(SELECT_UK_CITIES);
		List<?> nodes = xPath.selectNodes(ratXml);
		assertNotNull(nodes);
		assertEquals(3, nodes.size());
		Iterator<?> iter = nodes.iterator();
		assertNotNull(iter);
		
		assertTrue(iter.hasNext());
		Node city = (Node) iter.next();
		assertNotNull(city);
		assertEquals("city", city.getName());
		assertEquals("3.1.1", city.getAttributeValue("id"));
		
		assertTrue(iter.hasNext());
		city = (Node) iter.next();
		assertNotNull(city);
		assertEquals("city", city.getName());
		assertEquals("3.1.2", city.getAttributeValue("id"));
		
		assertTrue(iter.hasNext());
		city = (Node) iter.next();
		assertNotNull(city);
		assertEquals("city", city.getName());
		assertEquals("3.1.3", city.getAttributeValue("id"));
	}
}