package com.duckasteroid.ratxml.io;

import java.io.File;
import java.io.IOException;

public interface DataInputFactory {
	public DataInput create(File f) throws IOException;
}
