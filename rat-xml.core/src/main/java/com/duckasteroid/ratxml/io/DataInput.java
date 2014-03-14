package com.duckasteroid.ratxml.io;

import java.util.List;

import com.duckasteroid.ratxml.Key;

public interface DataInput {
	public String getText(Key key);
	public List<String> getMetaData(Key key);
	public void close();
}
