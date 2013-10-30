package com.duckasteroid.ratxml.xpath;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

public class RatXPath extends BaseXPath {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -3355826907488846034L;

	public RatXPath(String xpathExpr) throws JaxenException {
		super(xpathExpr, new Navigator());
	}

}
