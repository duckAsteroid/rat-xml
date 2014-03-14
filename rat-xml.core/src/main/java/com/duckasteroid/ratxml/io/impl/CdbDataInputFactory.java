package com.duckasteroid.ratxml.io.impl;

import java.io.File;
import java.io.IOException;

import com.duckasteroid.ratxml.io.DataInput;
import com.duckasteroid.ratxml.io.DataInputFactory;
import com.strangegizmo.cdb.Cdb;

public class CdbDataInputFactory implements DataInputFactory {

	public DataInput create(File f) throws IOException {
		return new CdbDataInput(new Cdb(f));
	}

}
