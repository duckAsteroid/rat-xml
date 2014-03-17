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
import com.duckasteroid.ratxml.io.impl.CdbDataInputFactory;
import com.duckasteroid.ratxml.xpath.RatXPath;
import com.strangegizmo.cdb.Statistics;

public class BigXPathTest extends TestCase {
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
		Writer writer = new Writer(cdbFile, true);
		writer.write(new InputSource(stream));
		// read the rat-xml 
		CdbDataInputFactory factory = new CdbDataInputFactory();
		medSamp2014 = new Document(factory.create(cdbFile));
	}
	
	@Override
	protected void tearDown() throws Exception {
		medSamp2014.close();
		medSamp2014 = null;
	}
	
	public void testAdvancedXPaths() throws SAXPathException {
		int expected = 103;
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
