package com.strangegizmo.cdb;

public abstract class Statistics {

	public abstract void recordIO();
	
	public static Statistics instance = new VoidStatistics();
	
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

	public abstract void close();
}
