package com.duckasteroid.ratxml;
/**
 * This class handles the reading and writing of the data/content for any node in the tree
 * @author Chris
 */
public class Data {

	/**
	 * Create element reference data 
	 * @param childElementName The name of the child element
	 * @param index The index of the element
	 * @param elementId The ID of node that corresponds
	 * @return The binary data to write to this node
	 */
	public static byte[] createChildElementData(String childElementName, int index, long elementId) {
		StringBuilder sb = new StringBuilder(childElementName);
		sb.append('[').append(index).append(']');
		sb.append(':').append(elementId);
		return sb.toString().getBytes();
	}

	/**
	 * Create attribute reference data
	 * @param qName The name of the attribute
	 * @param attrId The ID of the node that corresponds to the attribute
	 * @return The binary data to write to this node
	 */
	public static byte[] createChildAttributeData(String qName, long attrId) {
		StringBuilder sb = new StringBuilder(qName);
		sb.append(':').append(attrId);
		return sb.toString().getBytes();
	}
	
	
}
