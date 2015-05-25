package com.duckasteroid.ratxml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.duckasteroid.ratxml.converter.RatXmlConverter;
import com.duckasteroid.ratxml.io.impl.CdbDataFactory;
import com.strangegizmo.cdb.Statistics;

/**
 * This class tests the creation of a RAT-XML file from an XML sample.
 * The resulting RAT-XML is then loaded and compared with the DOM of the original XML.
 * 
 * @author Chris
 */
public class InputOutputTest extends TestCase {
	private Document reader;

	@Override
	protected void setUp() throws Exception {
		Statistics.instance = new StatisticsImpl(InputOutputTest.class.getName()+"."+getName());
	}
	
	public void testBooks() throws ParserConfigurationException, SAXException, IOException {
		// ok first up - we write books XML to books.cdb
		InputStream stream = getClass().getClassLoader().getResourceAsStream("books.xml");
		File cdbFile = new File("books.cdb");
		RatXmlConverter.convert(stream, cdbFile, false);
		
		// now we open books.xml again for DOM
		stream = getClass().getClassLoader().getResourceAsStream("books.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(stream);
		Element domRoot = doc.getDocumentElement();
		domRoot.normalize();
		
		// now we open books.cdb
		CdbDataFactory factory = new CdbDataFactory();
		reader = new Document(factory.createInput(cdbFile));
		
		// test the metadata
		Collection<Node> childElements = reader.getChildElements().values();
		assertNotNull(childElements);
		assertEquals(1, childElements.size());
		Node firstChild = childElements.iterator().next();
		assertEquals(domRoot.getNodeName() + "[0]", firstChild.name);
		
		// compare trees
		compareNode(domRoot, firstChild);
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
				Node attr = cdb.getAttributes().get(child.getNodeName());
				assertEquals("Attribute '"+child.getNodeName()+"'", child.getNodeValue(), attr.getText());
				break;
			case org.w3c.dom.Node.ELEMENT_NODE :
				String childName = child.getNodeName();
				int count = 0;
				if (counter.containsKey(childName)) {
					count = counter.get(childName);
				}
				compareNode((Element)child, cdb.getChildElements().get(childName +"["+count+"]"));
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
	
	@Override
	protected void tearDown() throws Exception {
		reader.close();
	}
}
