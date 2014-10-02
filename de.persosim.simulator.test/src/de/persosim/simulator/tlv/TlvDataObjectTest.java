package de.persosim.simulator.tlv;

import static org.junit.Assert.*;

import org.junit.Test;

public class TlvDataObjectTest implements TlvConstants {

	private static final String INDENT1 = "\n    ";
	private static final String INDENT2 = "\n        ";

	/**
	 * Positive test: PrimitiveTlvDataObject should be represented identical to it's toString()
	 */
	@Test
	public void testGetTlvDataObject_singlePrimitiveObject() {
		TlvDataObject obj = TlvDataObjectFactory.createTLVDataObject("020101");
		String expected = obj.toString();
		assertEquals(expected, TlvDataObject.dumpTlvObject(obj));
	}
	
	/**
	 * Negative test: return an empty String on null parameter
	 */
	@Test
	public void testGetTlvDataObject_nullArgument() {
		TlvDataObject obj = null;
		String expected = "";
		assertEquals(expected, TlvDataObject.dumpTlvObject(obj));
	}
	
	/**
	 * Positive test: ConstructedTlvDataObject with no child at all
	 */
	@Test
	public void testGetTlvDataObject_constructedObjectNoChild() {
		TlvDataObject obj = TlvDataObjectFactory.createTLVDataObject("3000");
		String expected = obj.toString();
		assertEquals(expected, TlvDataObject.dumpTlvObject(obj));
	}
	
	/**
	 * Positive test: Simple ConstructedTlvDataObject with one primitive child
	 */
	@Test
	public void testGetTlvDataObject_constructedObjectOneChild() {
		TlvDataObject obj = TlvDataObjectFactory.createTLVDataObject("3003020101");
		String expected = "30|03|" +
		INDENT1+"02|01|01";
		assertEquals(expected, TlvDataObject.dumpTlvObject(obj));
	}
	
	/**
	 * Positive test: Simple ConstructedTlvDataObject with two primitive children
	 */
	@Test
	public void testGetTlvDataObject_constructedObjectTwoChildren() {
		TlvDataObject obj = TlvDataObjectFactory.createTLVDataObject("3006020101020102");
		String expected = "30|06|" +
			INDENT1 + "02|01|01" + 
			INDENT1 + "02|01|02";
		assertEquals(expected, TlvDataObject.dumpTlvObject(obj));
	}
	
	/**
	 * Positive test: ConstructedTlvDataObject with one constructed child, which has two primitive children
	 */
	@Test
	public void testGetTlvDataObject_constructedObjectConstructedChildTwoChildren() {
		TlvDataObject obj = TlvDataObjectFactory.createTLVDataObject("30083006020101020102");
		String expected = "30|08|" +
		    INDENT1 + "30|06|" +
			INDENT2 + "02|01|01" + 
			INDENT2 + "02|01|02";
		assertEquals(expected, TlvDataObject.dumpTlvObject(obj));
	}
	
	/**
	 * Positive test: ConstructedTlvDataObject with one constructed child and primitive children afterwards
	 */
	@Test
	public void testGetTlvDataObject_complexConstructedObject() {
		TlvDataObject obj = TlvDataObjectFactory.createTLVDataObject("300B3006020101020102020101");
		String expected = "30|0B|" +
			    INDENT1 + "30|06|" +
				INDENT2 + "02|01|01" + 
				INDENT2 + "02|01|02" + 
				INDENT1 + "02|01|01";
		assertEquals(expected, TlvDataObject.dumpTlvObject(obj));
	}
	
}
