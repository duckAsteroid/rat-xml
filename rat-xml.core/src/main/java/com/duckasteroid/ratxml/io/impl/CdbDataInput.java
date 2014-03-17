package com.duckasteroid.ratxml.io.impl;

import java.util.ArrayList;
import java.util.List;

import com.duckasteroid.ratxml.Key;
import com.duckasteroid.ratxml.Node;
import com.duckasteroid.ratxml.io.DataInput;
import com.duckasteroid.ratxml.util.LruCache;
import com.strangegizmo.cdb.Cdb;

public class CdbDataInput implements DataInput {

	public static final Integer DEFAULT_CACHE_SIZE = 500;
	public static final String CACHE_SIZE_PROPERTY_NAME = "rat-xml.cdb.cache.size";
	
	private Cdb cdb;
	private LruCache<Key, List<String>> metaDataCache = null;
	private LruCache<Key, Node> nodeCache = null;
	
	public CdbDataInput(Cdb cdb, boolean useCache) {
		this.cdb = cdb;
		if (useCache) {
			metaDataCache = new LruCache<Key, List<String>>(Integer.getInteger(CACHE_SIZE_PROPERTY_NAME, DEFAULT_CACHE_SIZE));
			nodeCache = new LruCache<Key, Node>(Integer.getInteger(CACHE_SIZE_PROPERTY_NAME, DEFAULT_CACHE_SIZE));
		}
	}
	
	public String getText(Key key) {
		byte[] data = cdb.find(key.value);
		if (data == null)
		{
			return "";
		}
		return new String(data);
	}

	public synchronized List<String> getMetaData(Key key) {
		if (metaDataCache == null) {
			return loadMetaData(key);
		}
		else if (!metaDataCache.containsKey(key)) {
			List<String> metaData = loadMetaData(key);
			metaDataCache.put(key, metaData);
		}
		return metaDataCache.get(key);
	}

	private List<String> loadMetaData(Key key) {
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

	public synchronized Node getNode(Key childKey, Node parent, String name) {
		if (nodeCache == null) {
			return new Node(this, childKey, parent, name);
		}
		if(!nodeCache.containsKey(childKey)) {
			nodeCache.put(childKey, new Node(this, childKey, parent, name));
		}
		return nodeCache.get(childKey);
	}

}
