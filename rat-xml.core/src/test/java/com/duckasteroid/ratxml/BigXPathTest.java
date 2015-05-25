package com.duckasteroid.ratxml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;

import com.duckasteroid.ratxml.converter.RatXmlConverter;
import com.duckasteroid.ratxml.io.impl.CdbDataInput;
import com.duckasteroid.ratxml.io.impl.CdbDataFactory;
import com.duckasteroid.ratxml.xpath.RatXPath;
import com.strangegizmo.cdb.Statistics;

public class BigXPathTest extends TestCase {
	
	static {
		final InputStream inputStream = BigXPathTest.class.getResourceAsStream("/debug.logging.properties");
		try
		{
		    LogManager.getLogManager().readConfiguration(inputStream);
		}
		catch (final IOException e)
		{
		    Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
		    Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}
	
	/** Selects all citations created year > 200 */
	public static final String SELECT_CITATIONS = "MedlineCitationSet/MedlineCitation[DateCreated/Year>%s]";
	/** The rat XML document */
	private Document medSamp2014;
	@Override
	protected void setUp() throws Exception {
		Statistics.instance = new StatisticsImpl(BigXPathTest.class.getName()+"."+getName());
		// convert countries XML to RAT-XML
		InputStream stream = getClass().getClassLoader().getResourceAsStream("medsamp2014.xml");
		File cdbFile = new File("medsamp2014.cdb");
		RatXmlConverter.convert(stream, cdbFile, false);
		// read the rat-xml 
		CdbDataFactory factory = new CdbDataFactory();
		CdbDataInput dataInput = (CdbDataInput) factory.createInput(cdbFile);
		dataInput.setCacheMaxSize(20000);
		medSamp2014 = new Document(dataInput);
	}
	
	@Override
	protected void tearDown() throws Exception {
		medSamp2014.close();
		medSamp2014 = null;
	}
	
	public void testAdvancedXPaths() throws SAXPathException {
		int expected = 70;
		for (int i=2000; i < 2014; i++) {
			String xPathString = String.format(SELECT_CITATIONS, i);
			XPath xPath = new RatXPath(xPathString);
			List<?> nodes = xPath.selectNodes(medSamp2014);
			assertNotNull(nodes);
			if (i == 2000) {
				assertEquals(expected, nodes.size());
			} else {
				assertTrue(nodes.size() <= expected);
				expected = nodes.size();
			}
			System.out.println(xPathString+" = "+nodes.size() + "nodes");
			@SuppressWarnings("unchecked")
			Iterator<Node> iter = (Iterator<Node>) nodes.iterator();
			assertNotNull(iter);
			for (int j=0; j < nodes.size(); j++) {
				Node node = iter.next();
				assertNotNull(node);
				assertEquals("MedlineCitation", node.getName());
			}
			
		}	
	}
}
