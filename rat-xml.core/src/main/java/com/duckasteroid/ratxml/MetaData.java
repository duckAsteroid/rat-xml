package com.duckasteroid.ratxml;

import java.util.ArrayList;
import java.util.List;

public class MetaData {
	private String name;
	private long id;
	
	protected MetaData(String name, long id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}

	public static List<MetaData> parse(List<String> raw) {
		ArrayList<MetaData> result = new ArrayList<MetaData>(raw.size());
		for(String rawEntry : raw) {
			String[] meta =rawEntry.split(":");
			result.add(new MetaData(meta[0], Long.parseLong(meta[1])));
		}
		return result;
	}
}
