package com.duckasteroid.ratxml.io;

import java.io.IOException;
import java.util.List;

import com.duckasteroid.ratxml.Key;
import com.duckasteroid.ratxml.Node;
/**
 * A RAT-XML data storage
 */
public interface DataInput {
	
	
	public Node getNode(Key childKey, Node parent, String name);	
	public String getText(Key key);
	public List<String> getMetaData(Key key);
	
	public void close() throws IOException;
}
