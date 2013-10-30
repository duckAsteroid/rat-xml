package com.duckasteroid.ratxml;

public class Path {

	public static final Path ROOT = new Path("");
	
	private String path;
	
	public Path(String path) {
		this.path = path;
	}
	
	public Path getChildLiteral(String subPath) {
		return new Path(path +"/" + subPath);
	}

	public Path getChild(String name, int index) {
		if (index < 0) {
			throw new IllegalArgumentException("Index must be >= 0");
		}
		return new Path(path + '/' + name +'[' + index + ']');
	}
	
	public String getName() {
		// find the end of the name
		int index = path.lastIndexOf('#');
		if (index < 0) {
			index  = path.lastIndexOf('/');
		}
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
	
	public Path getAttribute(String name) {
		return new Path(this.toString() + "#"+ name);
	}
	
	public byte[] asKey() {
		return path.getBytes();
	}
	
	public boolean isAttribute() {
		return path.lastIndexOf('#') > 0;
	}
	
	public int getIndex() {
		if (path.endsWith("]")) {
			int pos = path.lastIndexOf('[');
			String s = path.substring(pos + 1, path.length() -1);
			return Integer.parseInt(s);
		}
		return -1;
	}

	public Path getParent() {
		int index = path.lastIndexOf('#');
		if (index < 0) {
			index  = path.lastIndexOf('/');
		}
		if (index <= 0) {
			return null; // Root
		}
		return new Path(path.substring(0, index));
	}
	
	public String toString() {
		return path;
	}
}
