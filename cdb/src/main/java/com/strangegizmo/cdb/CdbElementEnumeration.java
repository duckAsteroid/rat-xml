package com.strangegizmo.cdb;

import java.io.IOException;
import java.util.Enumeration;
/**
 * An enumeration over elements in a CDB file. The file must be explicitly closed by
 * closing this enumeration.
 *   
 * @author Chris
 */
public interface CdbElementEnumeration extends Enumeration<CdbElement> {
	/**
	 * Close the enumeration and the file
	 * @throws IOException If an exception occurs while closing the CDB file
	 */
	public void close() throws IOException;
}
