package com.duckasteroid.ratxml.xpath;

import java.util.Iterator;

import org.jaxen.BaseXPath;
import org.jaxen.DefaultNavigator;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;

import com.duckasteroid.ratxml.Node;

/**
 * This class provides a Jaxen navigator over RAT-XML Node instances
 */
public class Navigator extends DefaultNavigator {

	private static final long serialVersionUID = 984597718554121056L;

	
	@Override
	public Iterator<?> getAttributeAxisIterator(Object contextNode)
			throws UnsupportedAxisException {
		Node ctx = (Node)contextNode;
		return ctx.getAttributes();
	}
	
	@Override
	public Iterator<?> getChildAxisIterator(Object contextNode)
			throws UnsupportedAxisException {
		Node ctx = (Node)contextNode;
		return ctx.getChildren();
	}
	
	@Override
	public Iterator<?> getParentAxisIterator(final Object contextNode)
			throws UnsupportedAxisException {
		return new Iterator<Node>() {
			Node ctx = (Node) contextNode;	
			public boolean hasNext() {
				return false; // FIXME *******************************
				//return ctx.getParent();
			}

			public Node next() {
				// TODO Auto-generated method stub
				return null;
			}

			public void remove() {
				throw new UnsupportedOperationException();				
			}			
		};
	}
	
	
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
		return new BaseXPath(o, this);
	}
	
	

}
