package com.duckasteroid.ratxml;

import java.util.Arrays;

import junit.framework.TestCase;

public class PathTest extends TestCase {

	static final String PATH = "/A[0]/B[1]/C[2]";
	Path abc = new Path(PATH);
	Path subject;
	
	public void testGetChild() {
		try {
			subject = abc.getChild("TEST", -13);
			fail("Illegal index");
		}
		catch(IllegalArgumentException e){
			// expected
		}
		
		subject = abc.getChild("TEST", 12);
		assertEquals(PATH + "/TEST[12]", subject.toString());
	}

	public void testGetAttribute() {
		subject = abc.getAttribute("wibble");
		assertEquals(PATH + "#wibble", subject.toString());
	}

	public void testAsKey() {
		byte[] expected = new byte[]{47, 65, 91, 48, 93, 47, 66, 91, 49, 93, 47, 67, 91, 50, 93};
		byte[] key = abc.asKey();
		assertTrue("Key bytes equal", Arrays.equals(expected, key));
		
	}

	public void testIsAttribute() {
		subject = abc.getAttribute("id");
		assertTrue(subject.isAttribute());
		assertFalse(abc.isAttribute());
	}

	public void testGetIndex() {
		assertEquals(2, abc.getIndex());
	}

	public void testGetParent() {
		subject = abc.getParent();
		assertEquals("/A[0]/B[1]", subject.toString());
		assertNull(Path.ROOT.getParent());
	}
	
	public void testGetName() {
		subject = abc;
		assertEquals("C", subject.getName());
		subject = subject.getParent();
		assertEquals("B", subject.getName());
		subject = subject.getParent();
		assertEquals("A", subject.getName());
		subject = subject.getParent();
		assertEquals(null, Path.ROOT.getName());
	}

}
