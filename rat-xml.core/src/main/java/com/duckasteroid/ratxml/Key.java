package com.duckasteroid.ratxml;

import java.nio.ByteBuffer;
import java.util.Arrays;
/**
 * Represents a key value used in the CDB
 */
public class Key implements Comparable<Key> {
	public static final byte TYPE_ELEMENT = 0;
	public static final byte TYPE_ATTRIBUTE = 1;
	public static final byte TYPE_CHILD_ELEMENTS = 2;
	public static final byte TYPE_CHILD_ATTRIBUTES = 3;
		
	/** key data used in CDB */
	public byte[] value;
	
	private Key(long id, byte type) {
		value = new byte[9];
		ByteBuffer bb = getData();
		bb.putLong(id);
		bb.put(type);
	}
	
	public Key(byte[] data) {
		this.value = data;
	}
	
	public ByteBuffer getData() {
		return ByteBuffer.wrap(value);
	}
	
	public long getId() {
		return getData().getLong();
	}
	
	public byte getType() {
		return getData().get(8);
	}
	
	public static Key createElementDataKey(long id) {
		return new Key(id, TYPE_ELEMENT);
	}
	
	public Key getChildMetaDataKey() {
		return new Key(getId(), TYPE_CHILD_ELEMENTS);
	}
	
	public Key getAttributeMetaDataKey() {
		return new Key(getId(), TYPE_CHILD_ATTRIBUTES);
	}
	
	public static Key createAttributeDataKey(long id) {
		return new Key(id, TYPE_ATTRIBUTE);
	}
	
	public String getTypeName() {
		switch(getType()) {
		case TYPE_ELEMENT:
			return "E";
		case TYPE_ATTRIBUTE:
			return "A";
		case TYPE_CHILD_ATTRIBUTES:
			return "#A";
		case TYPE_CHILD_ELEMENTS:
			return "#E";
		default:
			return "?";
		}
	}
	
	@Override
	public String toString() {
		return getTypeName() + ":" +getId();
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
	
	public int compareTo(Key o) {
		// a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
		int comparison = (int)(this.getId() - o.getId());
		if (comparison == 0) {
			comparison = this.getType() - o.getType();
		}
		return comparison;
	}
	
}
