package com.duckasteroid.ratxml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.strangegizmo.cdb.Cdb;


/**
 * A revised rat-xml specific dump utility
 */
public class Dump {
	private Cdb cdb;
	
	public Dump(Cdb cdb) {
		this.cdb = cdb;
	}
	
	public static void main(String[] args) throws IOException {
		/* Display a usage message if we didn't get the correct number
		 * of arguments. */
		if (args.length != 1) {
			System.out.println("cdb.dump: usage: cdb.dump file");
			return;
		}

		/* Decode our arguments. */
		String cdbFile = args[0];
		File file = new File(cdbFile);
		
		Dump dump = new Dump(new Cdb(file));
		
		Key key = Key.createElementDataKey(0);
		
		String out = dump.dumpKey(key);
		System.out.println(out);
	}

	public String dumpKey(Key key) {
		StringBuilder s = new StringBuilder();
		appendKey(s, key);
		return s.toString();
	}
	
	public void appendKey(StringBuilder s, Key key) {
		List<String> allData = data(key);
		if (allData.isEmpty()) {
			s.append(key.toString()).append(':').append("[EMPTY]").append('\n');
		}
		for(String entry : allData) {
			s.append(key.toString()).append(':').append(new String(entry)).append('\n');
		}
		switch (key.getType()) {
			case Key.TYPE_ELEMENT :
				Key attr = key.getAttributeMetaDataKey();
				appendKey(s, attr);
				Key children = key.getChildMetaDataKey();
				appendKey(s, children);
				break;
			default:
			case Key.TYPE_ATTRIBUTE :
				break;
			case Key.TYPE_CHILD_ATTRIBUTES :
				List<MetaData> attrMetaData = MetaData.parse(allData);
				for(MetaData m : attrMetaData) {
					appendKey(s, Key.createAttributeDataKey(m.getId()));
				}
				break;
			case Key.TYPE_CHILD_ELEMENTS :
				List<MetaData> childMetaData = MetaData.parse(allData);
				for(MetaData m : childMetaData) {
					appendKey(s, Key.createElementDataKey(m.getId()));
				}
				break;
		}
	}
	
	public List<String> data(Key key) {
		ArrayList<String> result = new ArrayList<String>(2);
		byte[] data = cdb.find(key.asBytes());
		while(data != null) {
			result.add(new String(data));
			data = cdb.findnext(key.asBytes());
		}
		return result;
	}
}
