package com.duckasteroid.ratxml;

import java.io.IOException;

import com.duckasteroid.ratxml.io.DataInput;

/**
 * This class is used to read/parse a rat XML file.
 * <b>WARNING:</b> This class maintains an open connection to the file while in use, 
 * and must be therefore <b>explicitly</b> {@link #close() closed}!
 */
public class Document extends Node {

	/**
	 * Create a rat XML document given a file to read data from
	 * @param input The rat XML DB to read from
	 * @throws IOException If there is a problem reading the rat XML file
	 */
	public Document(DataInput input) throws IOException {
		super(input, Key.createElementDataKey(0), null, null);
	}
	
	/**
	 * Get the document element; there should only be one if the source was XML.
	 * @return The root document element
	 */
	public Node getRoot() {
		return getOrderedChildElements().get(0);
	}
	
	/**
	 * Close this document, and the underlying file resources
	 */
	public void close() {
		input.close();
	}
	
	@Override
	public String toString() {
		return "{READER}";
	}
}
