package com.duckasteroid.ratxml.io.impl;

import java.io.File;
import java.io.IOException;

import com.duckasteroid.ratxml.io.DataInput;
import com.duckasteroid.ratxml.io.DataFactory;
import com.duckasteroid.ratxml.io.DataOutput;
import com.strangegizmo.cdb.Cdb;
import com.strangegizmo.cdb.CdbMake;

/**
 * A factory for CDB file data input
 * @author Chris
 */
public class CdbDataFactory implements DataFactory {
	
	/**
	 * {@inheritDoc}
	 */
	public DataInput createInput(File f) throws IOException {
		return new CdbDataInput(new Cdb(f), true);
	}

	public DataOutput createOutput(File f) throws IOException {
		CdbMake make = new CdbMake();
		make.start(f);
		return new CdbDataOutput(make);
	}

}
