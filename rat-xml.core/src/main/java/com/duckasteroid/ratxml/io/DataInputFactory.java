package com.duckasteroid.ratxml.io;

import java.io.File;
import java.io.IOException;
/**
 * Factory API for creating DataInput from a file
 * @author Chris
 */
public interface DataInputFactory {
	public DataInput create(File f) throws IOException;
}
