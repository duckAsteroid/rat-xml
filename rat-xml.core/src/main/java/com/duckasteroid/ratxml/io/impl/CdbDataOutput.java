package com.duckasteroid.ratxml.io.impl;

import java.io.IOException;

import com.duckasteroid.ratxml.Key;
import com.duckasteroid.ratxml.io.DataOutput;
import com.strangegizmo.cdb.CdbMake;

public class CdbDataOutput implements DataOutput {

	private CdbMake make;
	
	public CdbDataOutput(CdbMake make) {
		this.make = make;
	}

	public void put(Key key, byte[] data) throws IOException {
		make.add(key.asBytes(), data);
	}

	public void close() throws IOException {
		make.finish();
	}

}
