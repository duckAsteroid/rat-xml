package com.duckasteroid.ratxml.io.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.duckasteroid.ratxml.Key;
import com.duckasteroid.ratxml.Node;
import com.duckasteroid.ratxml.io.DataInput;
import com.duckasteroid.ratxml.util.LruCache;
import com.strangegizmo.cdb.Cdb;

/**
 * A data input that uses the CDB file as a backing store.
 * Optionally uses and LRU memory cache to improve performance.
 */
public class CdbDataInput implements DataInput {
	/** Logging */
	private final static Logger LOGGER = Logger.getLogger(CdbDataInput.class
			.getName());

	public static final Integer DEFAULT_CACHE_SIZE = 5000;
	public static final String CACHE_SIZE_PROPERTY_NAME = "rat-xml.cdb.cache.size";

	private Cdb cdb;
	private LruCache<Key, List<String>> metaDataCache = null;
	private LruCache<Key, Node> nodeCache = null;

	public CdbDataInput(Cdb cdb, boolean useCache) {
		this.cdb = cdb;
		if (useCache) {
			int cacheSize = Integer.getInteger(CACHE_SIZE_PROPERTY_NAME, DEFAULT_CACHE_SIZE);
			metaDataCache = new LruCache<Key, List<String>>(cacheSize);
			nodeCache = new LruCache<Key, Node>(cacheSize);
		} else if (LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.warning("No cache enabled for " + cdb);
		}
	}
	
	public int getCacheMaxSize() {
		return metaDataCache.getMaxEntries();
	}
	
	public void setCacheMaxSize(int maxSize) {
		metaDataCache.setMaxEntries(maxSize);
		nodeCache.setMaxEntries(maxSize);
	}

	public String getText(Key key) {
		byte[] data = cdb.find(key.asBytes());
		String result;
		if (data == null) {
			return result = "";
		}
		else {
			result = new String(data);
		}
		//LOG.finest("getText "+key+"="+result);
		return result;
	}

	public synchronized List<String> getMetaData(Key key) {
		if (metaDataCache == null) {
			return loadMetaData(key);
		} else if (!metaDataCache.containsKey(key)) {
			//LOG.finest("getMetaData "+key + " [--miss]");
			List<String> metaData = loadMetaData(key);
			metaDataCache.put(key, metaData);
		}
		return metaDataCache.get(key);
	}

	private List<String> loadMetaData(Key key) {
		//LOG.finest("Load meta data "+key);
		ArrayList<String> result = new ArrayList<String>();
		byte[] b = null;
		String metaData = null;
		do {
			if (b == null) {
				b = cdb.find(key.asBytes());
			} else {
				b = cdb.findnext(key.asBytes());
			}
			if (b == null) {
				metaData = null;
				break;
			} else {
				metaData = new String(b);
			}
			result.add(metaData);
		} while (metaData != null);
		return result;
	}

	public void close() {
		cdb.close();
	}

	public synchronized Node getNode(Key childKey, Node parent, String name) {
		if (nodeCache == null) {
			//LOG.finest("getNode [NEW] "+childKey + ", "+parent+", "+name);
			return new Node(this, childKey, parent, name);
		}
		if (!nodeCache.containsKey(childKey)) {
			//LOG.finest("getNode "+childKey+ " [--miss]");
			nodeCache.put(childKey, new Node(this, childKey, parent, name));
		}
		// LOG.finest("Cached getNode "+childKey + ", "+parent+", "+name);
		return nodeCache.get(childKey);
	}

}
