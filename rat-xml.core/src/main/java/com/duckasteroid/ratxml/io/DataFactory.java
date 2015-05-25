package com.duckasteroid.ratxml.io;

import java.io.File;
import java.io.IOException;
/**
 * Factory API for creating DataInput from a file
 * @author Chris
 */
public interface DataFactory {
	public DataInput createInput(File f) throws IOException;
	public DataOutput createOutput(File f) throws IOException;
}
