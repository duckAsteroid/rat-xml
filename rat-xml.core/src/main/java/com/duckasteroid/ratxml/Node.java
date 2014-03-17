/*
 * Copyright 2013 Chris Senior
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.duckasteroid.ratxml;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.duckasteroid.ratxml.io.DataInput;

/**
 * This represents the read interface to data in the rat-xml. It is something
 * equivalent to the Node in DOM. It represents data in elements and attributes.
 */
public class Node {
	/** A reference to this nodes parent */
	protected Node parent;

	/** The data input that this node was loaded from */
	protected DataInput input;

	/** The key for this node in the CDB */
	protected Key key;

	/** The name of this node */
	protected String name;

	/** A cache of child elements */
	private ArrayList<Node> childElements = null;

	/** A cache of child attributes */
	private HashMap<String, Node> childAttributes = null;

	/** A cache of the text */
	private String text = null;

	/**
	 * Create the node in the given document and at the given path
	 * 
	 * @param input
	 *            The data input to load from
	 * @param document
	 *            The owner Rat XML document
	 * @param path
	 *            The path into the document
	 */
	public Node(DataInput input, Key key, Node parent, String name) {
		this.input = input;
		this.parent = parent;
		this.key = key;
		this.name = name;
	}

	/**
	 * The name of the node (e.g. element name or attribute name)
	 * 
	 * @return The node name
	 */
	public String getName() {
		int endIndex = name.lastIndexOf('[');
		if (endIndex < 0) {
			endIndex = name.length();
		}
		return name.substring(0, endIndex);
	}

	/**
	 * The document of this node
	 * 
	 * @return The document of this node
	 */
	public Document getOwnerDocument() {
		if (parent instanceof Document) {
			return (Document) parent;
		}
		return parent.getOwnerDocument();
	}

	/**
	 * Get the parent node of this node
	 * 
	 * @return The parent or <code>null</code> if this is the root/document
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Get the child elements of this node.
	 * 
	 * @return The names of the child elements, if any. The list may be empty
	 *         but never <code>null</code>.
	 */
	public Map<String, Node> getChildElements() {
		final HashMap<String, Node> children = new HashMap<String, Node>();
		processMetaData(key.getChildMetaDataKey(), new MetaDataHandler() {
			public void handle(String name, long id) {
				children.put(name, input.getNode(Key.createElementDataKey(id),
						Node.this, name));
			}
		});
		return children;
	}

	/**
	 * A list of the elements with a given name (in the order they are declared)
	 * 
	 * @param elementName
	 *            the name of the elements to get
	 * @return A collection with the nodes in it (if any)
	 */
	public List<Node> getChildElements(String elementName) {
		return new NodeList(elementName);
	}

	/**
	 * Returns a collection of all children - attributes and elements
	 * 
	 * @return A list of all children (attributes and elements)
	 */
	public List<Node> getAllChildren() {
		final ArrayList<Node> children = new ArrayList<Node>();
		children.addAll(getOrderedChildElements());
		children.addAll(getAttributes().values());
		return children;
	}

	public synchronized List<Node> getOrderedChildElements() {
		if (childElements == null) {
			childElements = new ArrayList<Node>();
			processMetaData(key.getChildMetaDataKey(), new MetaDataHandler() {
				public void handle(String name, long id) {
					childElements.add(input.getNode(
							Key.createElementDataKey(id), Node.this, name));
				}
			});
			if (childElements.size() > 1) {
				Collections.reverse(childElements);
			}
		}
		return childElements;
	}

	/**
	 * Get the attributes on this node. If this node is an attribute or a
	 * document, the list will be empty.
	 * 
	 * @return A list of attribute names (if any). List may be empty but never
	 *         <code>null</code>.
	 */
	public synchronized Map<String, Node> getAttributes() {
		if (childAttributes == null) {
			childAttributes = new HashMap<String, Node>();
			processMetaData(key.getAttributeMetaDataKey(),
					new MetaDataHandler() {
						public void handle(String name, long id) {
							Node n = input.getNode(
									Key.createAttributeDataKey(id), Node.this,
									name);
							childAttributes.put(name, n);
						}
					});
		}
		return childAttributes;
	}

	private interface MetaDataHandler {

		public void handle(String name, long id);
	}

	/**
	 * A utility method to gather together all values for a given meta data key
	 * 
	 * @param metaDataKey
	 *            The key to retrieve
	 * @return A list of all values in that meta data key.
	 */
	private void processMetaData(Key metaDataKey, MetaDataHandler handler) {
		List<String> metaData = input.getMetaData(metaDataKey);
		for (String metaDataEntry : metaData) {
			String[] meta = metaDataEntry.split(":");
			handler.handle(meta[0], Long.parseLong(meta[1]));
		}
	}

	/**
	 * Get the text content of this element
	 * 
	 * @return The text in this element
	 */
	public synchronized String getText() {
		if (text == null) {
			text = input.getText(key);
		}
		return text;
	}

	public boolean isAttribute() {
		return key.getType() == Key.TYPE_ATTRIBUTE;
	}

	public String toString() {
		return Node.class.getName() + "{" + key.toString() + "}";
	}

	public String getAttributeValue(String key) {
		Node attr = getAttributes().get(key);
		if (attr == null) {
			return null;
		}
		return attr.getText();
	}

	private class NodeList extends AbstractList<Node> {

		private String elementName;

		private Map<String, Node> elements;

		private Integer size = null;

		public NodeList(String elementName) {
			this.elementName = elementName;
			this.elements = getChildElements();
		}

		@Override
		public Node get(int index) {
			return elements.get(elementName + "[" + index + "]");
		}

		@Override
		public synchronized int size() {
			if (size == null) {
				size = 0;
				for (Map.Entry<String, Node> entry : elements.entrySet()) {
					String keyName = entry.getKey();
					keyName = keyName.substring(0, keyName.indexOf('['));
					if (keyName.equals(elementName)) {
						size++;
					}
				}
			}
			return size;
		}

	}
}
