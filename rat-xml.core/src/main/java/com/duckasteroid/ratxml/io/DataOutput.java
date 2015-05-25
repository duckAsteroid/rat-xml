package com.duckasteroid.ratxml.io;

import java.io.IOException;

import com.duckasteroid.ratxml.Key;

public interface DataOutput {
	public void put(Key key, byte[] data) throws IOException;
	public void close() throws IOException;
}
