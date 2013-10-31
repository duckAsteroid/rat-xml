package com.duckasteroid.ratxml;

import com.strangegizmo.cdb.Cdb;

/**
 * Represents a path (key) used in the RatXML file to store data from the original XML.
 * For example an element path would be <code>/world[0]/continent[1]/name[0]</code>. This can be interpreted as:
 * The first element named "name", in the second element named "continent", in the first element named "world" in the document.
 * A path for the "id" attribute on the previous "continent" element would be <code>/world[0]/continent[1]@id</code>
 */
public class Path {
	/** singleton representing the root */
	public static final Path ROOT = new Path("");
	/** the string form of the path */
	private String path;
	/**
	 * Create a path from a string. 
	 * Note: for performance no checks are performed on the path syntax. We fail later if it's bad...
	 * @param path The path in string form
	 */
	public Path(String path) {
		this.path = path;
	}
	
	/**
	 * Given a literal partial child path such as <code>continent[1]</code>, create a new complete path instance   
	 * @param subPath The literal partial path of a child element
	 * @return A new path to the child
	 */
	public Path getChildLiteral(String subPath) {
		return new Path(path + Constants.PATH_SEPARATOR + subPath);
	}

	/**
	 * Given the name and the index of a child element, create a path to it 
	 * @param name The name of a child element
	 * @param index The index of the child
	 * @return A new path to the child element
	 */
	public Path getChild(String name, int index) {
		if (index < 0) {
			throw new IllegalArgumentException("Index must be >= 0");
		}
		return new Path(path + Constants.PATH_SEPARATOR + name +'[' + index + ']');
	}
	
	/**
	 * Get the name of the element/attribute at this path - that is the last segment of the path.
	 * This will be the element or attribute name with no index etc.
	 * @return The name of the element/attribute
	 */
	public String getName() {
		// find the end of the name
		int index = getEndOfNameIndex();
		if (index < 0) {
			return null; // Root has no name
		}
		// look for [index] syntax
		int end = path.indexOf('[', index);
		if (end < 0) {
			end = path.length();
		}
		return path.substring(index + 1, end);
	}
	
	/**
	 * Get the path to a child attribute 
	 * @param name The name of the child attribute
	 * @return A new path to the child attribute
	 */
	public Path getAttribute(String name) {
		return new Path(this.toString() + Constants.ATTRIBUTE_SEPARATOR + name);
	}
	
	/**
	 * Get the path to a piece of metadata on this path
	 * @param name The name of the metadata 
	 * @return A path to store the metadata
	 */
	public Path getMetaData(String name) {
		return new Path(this.toString() + Constants.METADATA_SEPARATOR + name);
	}
	
	/**
	 * Get the full path name as a key to use with {@link Cdb} 
	 * @return
	 */
	public byte[] asKey() {
		return path.getBytes();
	}
	
	/**
	 * Is this a path to an attribute
	 * @return <code>true</code> if this is an attribute path
	 */
	public boolean isAttribute() {
		return path.lastIndexOf(Constants.ATTRIBUTE_SEPARATOR) > 0;
	}
	
	/**
	 * Get the index of this path (if it is an element path)
	 * @return The index of this path (if it has one), or <code>-1</code>.
	 */
	public int getIndex() {
		if (path.endsWith("]")) {
			int pos = path.lastIndexOf('[');
			String s = path.substring(pos + 1, path.length() -1);
			return Integer.parseInt(s);
		}
		return -1;
	}

	/**
	 * Get the parent path of this path
	 * @return The parent path
	 */
	public Path getParent() {
		int index = getEndOfNameIndex();
		if (index <= 0) {
			return null; // Root
		}
		return new Path(path.substring(0, index));
	}
	
	/**
	 * Utility method to work out where the name of this path ends
	 * @return The index of the end of the name, or <code>-1</code> if no end available (e.g. root)
	 */
	private int getEndOfNameIndex() {
		int index = path.lastIndexOf(Constants.ATTRIBUTE_SEPARATOR);
		if (index < 0) {
			index  = path.lastIndexOf(Constants.METADATA_SEPARATOR);
		}
		if (index < 0) {
			index  = path.lastIndexOf(Constants.PATH_SEPARATOR);
		}
		return index;
	}
	
	/**
	 * The full path as a string
	 */
	public String toString() {
		return path;
	}
}
