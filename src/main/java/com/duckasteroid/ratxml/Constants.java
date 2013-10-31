package com.duckasteroid.ratxml;

/**
 * Constants used in {@link Path} expressions in the rat XML file
 */
interface Constants {
	/** Meta data name for child element names */
	public static final String CHILDREN = "?children";
	/** Meta data name for attribute names */
	public static final String ATTRIBUTES = "?attrs";
	/** path separator character */
	public static final char PATH_SEPARATOR = '/';
	/** Attribute separator character */
	public static final char ATTRIBUTE_SEPARATOR = '@';
	/** Meta data separator character */
	public static final char METADATA_SEPARATOR = '#';
	
}
