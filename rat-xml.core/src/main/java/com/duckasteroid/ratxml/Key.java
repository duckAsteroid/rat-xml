package com.duckasteroid.ratxml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.util.Arrays;
/**
 * Represents a key used in the CDB to store data.
 * 
 * The key is a fixed size binary comprising 9 bytes:
 * 8 bytes the Long identifier of the node
 * 1 byte the node type
 */
public class Key implements Comparable<Key>, Externalizable {
	
	/** type identifier for an XML element */
	public static final byte TYPE_ELEMENT = 0;
	/** type identifier for an XML attribute */
	public static final byte TYPE_ATTRIBUTE = 1;
	/** type identifier for the list of CHILD elements of a node */
	public static final byte TYPE_CHILD_ELEMENTS = 2;
	/** type identifier for the list of CHIL attributes of a node */
	public static final byte TYPE_CHILD_ATTRIBUTES = 3;
		
	/** key data used in CDB */
	private byte[] value;
	
	/** Create a key with the given ID and type */
	private Key(long id, byte type) {
		value = new byte[9];
		ByteBuffer bb = getData();
		bb.putLong(id);
		bb.put(type);
	}
	/**
	 * Create a key object from raw data (presumably from the CDB file)
	 * @param data the data from the file
	 */
	public Key(byte[] data) {
		this.value = data;
	}

	/**
	 * Get the raw 9 byte key value as a ByteBuffer
	 * @return A ByteBuffer that wraps the key value (note this buffer is modifiable)
	 */
	public ByteBuffer getData() {
		return ByteBuffer.wrap(value);
	}
	
	/**
	 * Extract the ID component from this Key
	 * @return the component ID
	 */
	public long getId() {
		return getData().getLong();
	}
	
	/**
	 * Extract the TYPE component from this key
	 * @return the key type identifier
	 */
	public byte getType() {
		return getData().get(8);
	}
	
	/**
	 * Create an Element data key for the given ID
	 * @param id The ID of the element
	 * @return A new key representing an Element key for the given ID 
	 */
	public static Key createElementDataKey(long id) {
		return new Key(id, TYPE_ELEMENT);
	}
	
	/**
	 * Create an Attribute data key for the given ID
	 * @param id The ID of the element
	 * @return A new key representing an Attribute key for the given ID
	 */
	public static Key createAttributeDataKey(long id) {
		return new Key(id, TYPE_ATTRIBUTE);
	}
	
	/**
	 * Create a key for storing metadata about the child elements of
	 * the element represented by this key.
	 * @return A new child element metadata key for this element 
	 */
	public Key getChildMetaDataKey() {
		return new Key(getId(), TYPE_CHILD_ELEMENTS);
	}
	
	/**
	 * Create a key for storing metadata about the child attributes of
	 * the element represented by this key.
	 * @return A new child attribute metadata key for this element
	 */
	public Key getAttributeMetaDataKey() {
		return new Key(getId(), TYPE_CHILD_ATTRIBUTES);
	}
	
	/**
	 * A short type name for human consumption.
	 * <code>A</code> represents attributes, <code>E</code> represents elements.
	 * <code>#</code> represents metadata 
	 * @return A short type name
	 */
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
	
	/**
	 * Returns a string form {@link #getTypeName()}:{@link #getId()}
	 */
	@Override
	public String toString() {
		return getTypeName() + ":" +getId();
	}
	
	/**
	 * A hashcode for the key (based on the 9 byte key value)
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
	
	/**
	 * Compares the key for order based on the {@link #getId()} and then the {@link #getType()}
	 */
	public int compareTo(Key o) {
		// a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
		int comparison = (int)(this.getId() - o.getId());
		if (comparison == 0) {
			comparison = this.getType() - o.getType();
		}
		return comparison;
	}
	
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// make sure value is ready to read into
		value = new byte[9]; 
		in.readFully(value);
	}
	
	public void writeExternal(ObjectOutput out) throws IOException {
		out.write(value);
	}
	
	public byte[] asBytes() {
		return value;
	}	
	
}
