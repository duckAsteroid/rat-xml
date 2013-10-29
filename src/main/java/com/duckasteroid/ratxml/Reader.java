package com.duckasteroid.ratxml;

import com.strangegizmo.cdb.Cdb;

public class Reader extends Node {
	
	public Reader(Cdb cdb) {
		super(cdb, Path.ROOT);
	}
	
	public Node getRoot() {
		return new Node(cdb, path.getChildLiteral(getChildElements().get(0)));
	}
	
	public void close() {
		cdb.close();
	}
	
}
