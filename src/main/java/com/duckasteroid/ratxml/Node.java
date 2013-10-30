package com.duckasteroid.ratxml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.strangegizmo.cdb.Cdb;

/**
 * This represents the read interface to a Node in the rat-xml
 */
public class Node {
	protected Reader document;
	protected Path path;

	protected Node(Path path) {
		this.path = path;
	}
	
	public Node(Reader document, Path path) {
		this.document = document;
		this.path = path;
	}

	public String getName() {
		return path.getName();
	}
	
	public Reader getOwnerDocument() {
		return document;
	}
	
	public Node getParent() {
		Path parentPath =  path.getParent();
		if (parentPath == null) {
			return null;
		}
		return new Node(document, parentPath);
	}

	public List<String> getChildElements() {
		return getMetaData(path.getAttribute(Constants.CHILDREN));
	}
	
	public List<String> getAttributeNames() {
		return getMetaData(path.getAttribute(Constants.ATTRIBUTES));
	}
	
	public Iterator<Node> getAttributes() {
		final Iterator<String> attrNameIter = getAttributeNames().iterator();
		return new Iterator<Node>() {
			public boolean hasNext() {
				return attrNameIter.hasNext();
			}
			public Node next() {
				return getChildAttribute(attrNameIter.next());
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public Iterator<Node> getChildren() {
		final Iterator<String> childNameIter = getChildElements().iterator();
		return new Iterator<Node>() {
			public boolean hasNext() {
				return childNameIter.hasNext();
			}
			public Node next() {
				return new Node(document, path.getChildLiteral(childNameIter.next()));
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	protected boolean exists() {
		return document.cdb.find(path.asKey()) != null;
	}

	private List<String> getMetaData(Path metaDataKey) {
		ArrayList<String> children = new ArrayList<String>();
		String childName = null;
		byte[] b = null;
		do {
			if (b == null) {
				b = document.cdb.find(metaDataKey.asKey());
			}
			else {
				b = document.cdb.findnext(metaDataKey.asKey());
			}
			if (b == null) {
				childName = null;
			} else {
				childName = new String(b);
				children.add(childName);
			}
		} while (childName != null);

		// order from CDB is reverse of order added
		Collections.reverse(children);
		return children;
	}

	public String getAttribute(String attr) {
		Path key = path.getAttribute(attr);
		byte[] data = document.cdb.find(key.asKey());
		if (data == null) {
			return null;
		}
		return new String(data);
	}

	public String getText() {
		byte[] data = document.cdb.find(path.asKey());
		if (data == null) {
			return "";
		}
		return new String(data);
	}

	public Node getChildElement(String name, Integer index) {
		return new Node(document, path.getChild(name, index));
	}
	
	public Node getChildAttribute(String name) {
		return new Node(document, path.getAttribute(name));
	}
	
	public boolean isAttribute() {
		return path.isAttribute();
	}

	public String toString() {
		return path.toString();
	}
}
