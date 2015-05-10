package com.strangegizmo.cdb;

/**
 * This class is used to record statistics about CDB IO operations.
 * 
 * A VOID implementation is provided that does nothing in normal runtime
 * 
 * @author Chris
 */
public abstract class Statistics {

	/**
	 * Record an IO event
	 */
	public abstract void recordIO();
	
	/**
	 * Called when all IO operations are complete 
	 */
	public abstract void close();
	
	/**
	 * The instance used internally by CDB to record IO operations.
	 * Defaults to the VOID instance.
	 */
	public static Statistics instance = new VoidStatistics();
	
	/**
	 * A do nothing implementation of the API
	 * 
	 * @author Chris
	 */
	private static final class VoidStatistics extends Statistics {
		@Override
		public void recordIO() {
			// do nothing
		}
		
		@Override
		public void close() {
			// do nothing	
		}
		
	}
	
}
