package com.duckasteroid.ratxml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This represents the read interface to data in the rat-xml. It is something equivalent
 * to the Node in DOM. It represents data in elements and attributes.
 */
public class Node {
	protected Document document;
	protected Path path;

	protected Node(Path path) {
		this.path = path;
	}
	
	/**
	 * Create the node in the given document and at the given path
	 * @param document The owner Rat XML document 
	 * @param path The path into the document
	 */
	public Node(Document document, Path path) {
		this.document = document;
		this.path = path;
	}

	/**
	 * The name of the node (e.g. element name or attribute name)
	 * @return The node name
	 */
	public String getName() {
		return path.getName();
	}
	
	/**
	 * The document of this node
	 * @return The document of this node
	 */
	public Document getOwnerDocument() {
		return document;
	}
	
	/**
	 * Get the parent node of this node
	 * @return The parent or <code>null</code> if this is the root/document
	 */
	public Node getParent() {
		Path parentPath =  path.getParent();
		if (parentPath == null) {
			return null;
		}
		return new Node(document, parentPath);
	}

	/**
	 * Get the names of the child elements of this node in metadata form.
	 * These names include an "index" (e.g. <code>foo[0]</code> or <code>foo[12]</code>) 
	 * @return The names of the child elements, if any. The list may be empty but never <code>null</code>.
	 */
	public List<String> getChildElements() {
		return getMetaData(path.getMetaData(Constants.CHILDREN));
	}
	
	/**
	 * Get the names of the attributes on this node. 
	 * If this node is an attribute or a document, the list will be empty. 
	 * @return A list of attribute names (if any). List may be empty but never <code>null</code>.
	 */
	public List<String> getAttributeNames() {
		return getMetaData(path.getMetaData(Constants.ATTRIBUTES));
	}
	
	/**
	 * Get a node iterator over the attributes (if any)
	 * @return An iterator over the attributes
	 */
	public Iterator<Node> getAttributes() {
		final Iterator<String> attrNameIter = getAttributeNames().iterator();
		return new Iterator<Node>() {
			public boolean hasNext() {
				return attrNameIter.hasNext();
			}
			public Node next() {
				return getAttributeNode(attrNameIter.next());
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	/**
	 * Get a node iterator over the children (if any)
	 * @return An iterator over the child elements
	 */
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
	
	/**
	 * A utility method to gather together all values for a given meta data key
	 * @param metaDataKey The key to retrieve
	 * @return A list of all values in that meta data key.
	 */
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

	/**
	 * Get the value of the given attribute.
	 * @param attr The name of the attribute on this node
	 * @return The value of the attribute if it exists or <code>null</code> if there is no such attribute.
	 */
	public String getAttributeValue(String attr) {
		Path key = path.getAttribute(attr);
		byte[] data = document.cdb.find(key.asKey());
		if (data == null) {
			return null;
		}
		return new String(data);
	}

	/**
	 * Get the text content of this element
	 * @return The text in this element
	 */
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
	
	public Node getAttributeNode(String name) {
		return new Node(document, path.getAttribute(name));
	}
	
	public boolean isAttribute() {
		return path.isAttribute();
	}

	public String toString() {
		return Node.class.getName()+"{"+path.toString()+"}";
	}
}
