package com.duckasteroid.ratxml;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import com.duckasteroid.ratxml.converter.RatXmlConverter;
import com.duckasteroid.ratxml.io.impl.CdbDataInputFactory;
import com.strangegizmo.cdb.Statistics;
/**
 * This class is a simple test of the Node API - using a simple XML file as source.
 * RAT-XML file is generated from the XML during setup.
 * 
 * @author Chris
 */
public class NodeTest extends TestCase {
	private Document ratXml;
	Node subject;
	
	@Override
	protected void setUp() throws Exception {
		Statistics.instance = new StatisticsImpl(NodeTest.class.getName()+"."+getName());
		// convert countries XML to RAT-XML
		InputStream stream = getClass().getClassLoader().getResourceAsStream("countries.xml");
		File cdbFile = new File("countries.cdb");
		RatXmlConverter.convert(stream, cdbFile, false);
		
		// read the rat-xml
		CdbDataInputFactory factory = new CdbDataInputFactory();
		ratXml = new Document(factory.create(cdbFile));
	}
	
	@Override
	protected void tearDown() throws Exception {
		ratXml.close();
		ratXml = null;
	}
	
	public void testGetRoot() {
		subject = ratXml.getRoot();
		assertNotNull(subject);
		assertEquals("world", subject.getName());
	}
	
	public void testGetChildren() {
		subject = ratXml.getRoot();
		Iterator<Node> children = subject.getOrderedChildElements().iterator();
		assertNotNull(children);
		assertTrue(children.hasNext());
		subject = children.next();
		assertNotNull(subject);
		assertEquals("continent", subject.getName());
		assertEquals("1", subject.getAttributeValue("id"));
		assertTrue(children.hasNext());
		subject = children.next();
		assertNotNull(subject);
		assertEquals("continent", subject.getName());
		assertEquals("3", subject.getAttributeValue("id"));
		assertTrue(children.hasNext());
		subject = children.next();
		assertNotNull(subject);
		assertEquals("continent", subject.getName());
		assertEquals("5", subject.getAttributeValue("id"));
		assertFalse(children.hasNext());
	}
	
	public void testNodeList() {
		subject = ratXml.getRoot();
		Iterator<Node> children = subject.getChildElements("continent").iterator();
		assertNotNull(children);
		assertTrue(children.hasNext());
		subject = children.next();
		assertNotNull(subject);
		assertEquals("continent", subject.getName());
		assertEquals("1", subject.getAttributeValue("id"));
		assertTrue(children.hasNext());
		subject = children.next();
		assertNotNull(subject);
		assertEquals("continent", subject.getName());
		assertEquals("3", subject.getAttributeValue("id"));
		assertTrue(children.hasNext());
		subject = children.next();
		assertNotNull(subject);
		assertEquals("continent", subject.getName());
		assertEquals("5", subject.getAttributeValue("id"));
		assertFalse(children.hasNext());
	}
}
