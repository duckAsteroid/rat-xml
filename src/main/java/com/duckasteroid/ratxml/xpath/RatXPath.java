package com.duckasteroid.ratxml.xpath;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

/**
 * An subclass of Jaxen's {@link BaseXPath} that knows which {@link org.jaxen.Navigator} to use.
 */
public class RatXPath extends BaseXPath {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -3355826907488846034L;

	/**
	 * Create the XPath given the string form
	 * @param xpathExpr an xpath expression to parse
	 */
	public RatXPath(String xpathExpr) throws JaxenException {
		super(xpathExpr, new Navigator());
	}

}
