package com.duckasteroid.ratxml;

import com.strangegizmo.cdb.Cdb;

public class Reader extends Node {
	
	Cdb cdb;
	
	public Reader(Cdb cdb) {
		super(Path.ROOT);
		this.cdb = cdb;
		this.document = this;
	}
	
	public Node getRoot() {
		return new Node(this, path.getChildLiteral(getChildElements().get(0)));
	}
	
	public void close() {
		cdb.close();
	}
	
	@Override
	public String toString() {
		return "{READER}";
	}
}
