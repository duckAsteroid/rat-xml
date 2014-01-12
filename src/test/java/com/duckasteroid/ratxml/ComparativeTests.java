package com.duckasteroid.ratxml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * This class performs comparative tests on object model traversal between normal Java XML technology (and an XML file); and RAT-XML with a CDB file.
 */
public class ComparativeTests extends TestCase
{

	@Override
	protected void setUp() throws Exception	{
		System.gc();
	}
	
	private InputStream getResource(String name) {
		return ComparativeTests.class.getClassLoader().getResourceAsStream(name);
	}


	public void testXMLXPath() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException	{
		long timestamp = System.currentTimeMillis();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document doc = documentBuilder.parse(getResource("standard.xml"));
		long duration = System.currentTimeMillis() - timestamp;
		System.out.println("XML> Took "+duration+" ms to load DOM");
		
		
		timestamp = System.currentTimeMillis();
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		XPathExpression expression = xPath.compile("/auction/site/people/person[@id = \"person0\"]");
		NodeList nodes = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
		duration = System.currentTimeMillis() - timestamp;
		System.out.println("XML> Took "+duration+" ms to resolve DOM XPath to "+nodes.getLength()+ " nodes");
	}


	@Override
	protected void tearDown() throws Exception	{
		System.gc();
	}
}
