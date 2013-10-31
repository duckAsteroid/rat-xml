package com.duckasteroid.ratxml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class InputOutputTest extends TestCase {

	public void testBooks() throws ParserConfigurationException, SAXException, IOException {
		// ok first up - we write books XML to books.cdb
		InputStream stream = getClass().getClassLoader().getResourceAsStream("books.xml");
		File cdbFile = new File("books.cdb");
		Writer writer = new Writer(cdbFile, true, false);
		writer.write(new InputSource(stream));
		
		// now we open books.xml again for DOM
		stream = getClass().getClassLoader().getResourceAsStream("books.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(stream);
		Element domRoot = doc.getDocumentElement();
		domRoot.normalize();
		
		// now we open books.cdb
		Document reader = new Document(cdbFile);
		
		// test the metadata
		List<String> childElements = reader.getChildElements();
		assertNotNull(childElements);
		assertEquals(1, childElements.size());
		String firstChild = childElements.get(0);
		assertEquals(domRoot.getNodeName() + "[0]", firstChild);
		
		Node cdbRoot = reader.getChildElement(domRoot.getNodeName(), 0);
		
		// compare trees
		compareNode(domRoot, cdbRoot);
	}
	
	private static void compareNode(Element dom, Node cdb)  {
		// where we concat text nodes
		StringBuilder text = new StringBuilder();
		// keeping count of children
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		// children
		NodeList childNodes = dom.getChildNodes();
		for(int i = 0; i < childNodes.getLength(); i++) {
			org.w3c.dom.Node child = childNodes.item(i);
			switch(child.getNodeType()) {
			case org.w3c.dom.Node.ATTRIBUTE_NODE :
				assertEquals("Attribute '"+child.getNodeName()+"'", child.getNodeValue(), cdb.getAttributeValue(child.getNodeName()));
				break;
			case org.w3c.dom.Node.ELEMENT_NODE :
				String childName = child.getNodeName();
				int count = 0;
				if (counter.containsKey(childName)) {
					count = counter.get(childName);
				}
				compareNode((Element)child, cdb.getChildElement(childName, count));
				counter.put(childName, ++count);
				break;
			case org.w3c.dom.Node.TEXT_NODE :
				String childText = child.getNodeValue();
				if (!childText.isEmpty()) {
					text.append(childText);
				}
				break;
			}
		}
		
		// text content
		assertEquals(xmlNormalise(text.toString()), xmlNormalise(cdb.getText()));	
	}
	
	private static String xmlNormalise(String src) {
		src = src.replaceAll("\\n", " ");
		src = src.replaceAll(" +", " ");
		return src;
	}
}
