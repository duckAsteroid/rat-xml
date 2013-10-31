package com.duckasteroid.ratxml;

import java.io.File;
import java.io.IOException;

import com.strangegizmo.cdb.Cdb;

/**
 * This class is used to read/parse a rat XML file.
 * <b>WARNING:</b> This class maintains an open connection to the file while in use, 
 * and must be therefore <b>explicitly</b> {@link #close() closed}!
 */
public class Document extends Node {
	/**
	 * The underlying CDB file that we read from.
	 * Note: package access for speed when used in {@link Node}
	 */
	Cdb cdb;
	/**
	 * Create a rat XML document given a file to read data from
	 * @param inputFile The rat XML file to read from
	 * @throws IOException If there is a problem reading the rat XML file
	 */
	public Document(File inputFile) throws IOException {
		super(Path.ROOT);
		this.cdb = new Cdb(inputFile);
		this.document = this;
	}
	
	/**
	 * Get the document element; there should only be one if the source was XML.
	 * @return The root document element
	 */
	public Node getRoot() {
		return new Node(this, path.getChildLiteral(getChildElements().get(0)));
	}
	
	/**
	 * Close this document, and the underlying file resources
	 */
	public void close() {
		cdb.close();
	}
	
	@Override
	public String toString() {
		return "{READER}";
	}
}
