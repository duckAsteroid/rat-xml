package com.duckasteroid.ratxml;

import java.util.ArrayList;
import java.util.List;

import com.strangegizmo.cdb.Cdb;

/**
 * This represents the read interface to a Node in the rat-xml
 */
public class Node {
	protected Cdb cdb;
	private Path path;

	public Node(Cdb cdb, Path path) {
		this.cdb = cdb;
		this.path = path;
	}

	public String getName() {
		return path.getName();
	}

	public List<String> getChildElements() {
		return getMetaData(path.getAttribute(Constants.CHILDREN));
	}
	
	public List<String> getAttributeNames() {
		return getMetaData(path.getAttribute(Constants.ATTRIBUTES));
	}
	
	private List<String> getMetaData(Path metaDataKey) {
		ArrayList<String> children = new ArrayList<String>();
		String childName = null;
		do {
			byte[] b = cdb.findnext(metaDataKey.asKey());
			if (b == null) {
				childName = null;
			} else {
				childName = new String(b);
				children.add(childName);
			}
		} while (childName != null);

		return children;
	}

	public String getAttribute(String attr) {
		Path key = path.getAttribute(attr);
		byte[] data = cdb.find(key.asKey());
		if (data == null) {
			return null;
		}
		return new String(data);
	}

	public String getText() {
		byte[] data = cdb.find(path.asKey());
		if (data == null) {
			return "";
		}
		return new String(data);
	}

	public Node getChildElement(String name, Integer index) {
		return new Node(cdb, path.getChild(name, index));
	}
	
	public Node getChildAttribute(String name) {
		return new Node(cdb, path.getAttribute(name));
	}
	
	public boolean isAttribute() {
		return path.isAttribute();
	}

	public String toString() {
		return path.toString();
	}

}
