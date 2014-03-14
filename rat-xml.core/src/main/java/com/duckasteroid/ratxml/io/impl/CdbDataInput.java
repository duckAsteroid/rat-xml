package com.duckasteroid.ratxml.io.impl;

import java.util.ArrayList;
import java.util.List;

import com.duckasteroid.ratxml.Key;
import com.duckasteroid.ratxml.io.DataInput;
import com.strangegizmo.cdb.Cdb;

public class CdbDataInput implements DataInput {

	private Cdb cdb;
	
	public CdbDataInput(Cdb cdb) {
		this.cdb = cdb;
	}
	
	public String getText(Key key) {
		byte[] data = cdb.find(key.value);
		if (data == null)
		{
			return "";
		}
		return new String(data);
	}

	public List<String> getMetaData(Key key) {
		ArrayList<String> result = new ArrayList<String>();
		byte[] b = null;
		String metaData = null;
		do
		{
			if (b == null)
			{
				b = cdb.find(key.value);
			}
			else
			{
				b = cdb.findnext(key.value);
			}
			if (b == null)
			{
				metaData = null;
				break;
			}
			else
			{
				metaData = new String(b);
			}
			result.add(metaData);
		}
		while (metaData != null);
		return result;
	}

	public void close() {
		cdb.close();
	}

}
