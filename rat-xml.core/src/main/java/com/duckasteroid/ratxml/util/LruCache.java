package com.duckasteroid.ratxml.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Code shamelessly taken from StackOverflow...
 * http://stackoverflow.com/questions
 * /221525/how-would-you-implement-an-lru-cache-in-java-6
 * 
 * @param <A>
 *            Key class
 * @param <B>
 *            Value class
 */
public class LruCache<A, B> extends LinkedHashMap<A, B> {

    private static final long serialVersionUID = 4230177758216129994L;

    private int maxEntries;

    /**
     * Create an LRU cache that will store at most {@link #maxEntries} entries
     * 
     * @param maxEntries
     *            The maximum number of entries in the cache
     */
    public LruCache(final int maxEntries) {
	super(maxEntries + 1, 1.0f, true);
	setMaxEntries(maxEntries);
    }

    public int getMaxEntries() {
	return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
	if (maxEntries <= 0) {
	    throw new IllegalArgumentException(
		    "Cache max must be a positive integer (i.e. > 0)");
	}
	this.maxEntries = maxEntries;
    }

    /**
     * Returns <tt>true</tt> if this <code>LruCache</code> has more entries than
     * the maximum specified when it was created.
     * 
     * <p>
     * This method <em>does not</em> modify the underlying <code>Map</code>; it
     * relies on the implementation of <code>LinkedHashMap</code> to do that,
     * but that behavior is documented in the JavaDoc for
     * <code>LinkedHashMap</code>.
     * </p>
     * 
     * @param eldest
     *            the <code>Entry</code> in question; this implementation
     *            doesn't care what it is, since the implementation is only
     *            dependent on the size of the cache
     * @return <tt>true</tt> if the oldest
     * @see java.util.LinkedHashMap#removeEldestEntry(Map.Entry)
     */
    @Override
    protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
	return super.size() > maxEntries;
    }
}
