package com.duckasteroid.ratxml;

import com.strangegizmo.cdb.Statistics;

public class StatisticsImpl extends Statistics {

	private String name;
	private int count = 0;
	
	public StatisticsImpl(String name) {
		this.name = name;
	}
	
	@Override
	public void recordIO() {
		count++;
	}

	@Override
	public void close() {
		System.out.println(name + " IO Count="+count);
	}

}
