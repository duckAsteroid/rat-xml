package com.duckasteroid.ratxml;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import org.xml.sax.InputSource;

import com.strangegizmo.cdb.Cdb;

import junit.framework.TestCase;

public class NodeTest extends TestCase {
	private Reader ratXml;
	Node subject;
	
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
	
	public void testGetRoot() {
		subject = ratXml.getRoot();
		assertNotNull(subject);
		assertEquals("world", subject.getName());
	}
	
	public void testGetChildren() {
		subject = ratXml.getRoot();
		Iterator<Node> children = subject.getChildren();
		assertNotNull(children);
		assertTrue(children.hasNext());
		subject = children.next();
		assertNotNull(subject);
		assertEquals("continent", subject.getName());
		assertEquals("1", subject.getAttribute("id"));
		assertTrue(children.hasNext());
		subject = children.next();
		assertNotNull(subject);
		assertEquals("continent", subject.getName());
		assertEquals("3", subject.getAttribute("id"));
		assertTrue(children.hasNext());
		subject = children.next();
		assertNotNull(subject);
		assertEquals("continent", subject.getName());
		assertEquals("5", subject.getAttribute("id"));
		assertFalse(children.hasNext());
	}
}
