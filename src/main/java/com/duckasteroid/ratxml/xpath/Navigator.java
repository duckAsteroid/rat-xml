package com.duckasteroid.ratxml.xpath;

import org.jaxen.DefaultNavigator;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;

import com.duckasteroid.ratxml.Node;

/**
 * This class provides a Jaxen navigator over RAT-XML Node instances
 */
public class Navigator extends DefaultNavigator {

	private static final long serialVersionUID = 984597718554121056L;

	public String getAttributeName(Object o) {
		return ((Node)o).getName();
	}

	public String getAttributeNamespaceUri(Object o) {
		return "";
	}

	public String getAttributeQName(Object o) {
		return ((Node)o).getName();
	}

	public String getAttributeStringValue(Object o) {
		return ((Node)o).getText();
	}

	public String getCommentStringValue(Object o) {
		return null;
	}

	public String getElementName(Object o) {
		return ((Node)o).getName();
	}

	public String getElementNamespaceUri(Object o) {
		return "";
	}

	public String getElementQName(Object o) {
		return ((Node)o).getName();
	}

	public String getElementStringValue(Object o) {
		return ((Node)o).getText();
	}

	public String getNamespacePrefix(Object o) {
		return "";
	}

	public String getNamespaceStringValue(Object o) {
		return "";
	}

	public String getTextStringValue(Object o) {
		return ((Node)o).getText();
	}

	public boolean isAttribute(Object o) {
		return ((Node)o).isAttribute();
	}

	public boolean isComment(Object o) {
		return false;
	}

	public boolean isDocument(Object o) {
		//return ((Node)o).isRoot();
		return false;
	}

	public boolean isElement(Object o) {
		return !((Node)o).isAttribute();
	}

	public boolean isNamespace(Object o) {
		return false;
	}

	public boolean isProcessingInstruction(Object o) {
		return false;
	}

	public boolean isText(Object o) {
		return false;
	}

	public XPath parseXPath(String o) throws SAXPathException {
		// TODO Auto-generated method stub
		return null;
	}

}
