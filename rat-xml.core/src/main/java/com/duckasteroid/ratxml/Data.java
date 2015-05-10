package com.duckasteroid.ratxml;

public class Data {

	public static byte[] createChildElementData(String childElementName, int index, long elementId) {
		StringBuilder sb = new StringBuilder(childElementName);
		sb.append('[').append(index).append(']');
		sb.append(':').append(elementId);
		return sb.toString().getBytes();
	}

	public static byte[] createChildAttributeData(String qName, long attrId) {
		StringBuilder sb = new StringBuilder(qName);
		sb.append(':').append(attrId);
		return sb.toString().getBytes();
	}
	
	
}
