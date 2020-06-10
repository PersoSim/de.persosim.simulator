package de.persosim.simulator.tlv;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.persosim.simulator.utils.HexString;

public class Asn1BitStringTest {
	
	/**
	 * positive test: simple one byte 
	 */
	@Test
	public void testAsn1BitString_0005() {
		Asn1BitString testObject = new Asn1BitString(HexString.toByteArray("03020005"));

		assertEquals(8 , testObject.getNumberOfBits());
		assertEquals(true , testObject.getBit(0));
		assertEquals(false, testObject.getBit(1));
		assertEquals(true , testObject.getBit(2));
		assertEquals(false, testObject.getBit(3));
	}
	
	/**
	 * positive test: one byte w/ unused bits
	 */
	@Test
	public void testAsn1BitString_0450() {
		Asn1BitString testObject = new Asn1BitString(HexString.toByteArray("03020450"));

		assertEquals(4 , testObject.getNumberOfBits());
		assertEquals(true , testObject.getBit(0));
		assertEquals(false, testObject.getBit(1));
		assertEquals(true , testObject.getBit(2));
		assertEquals(false, testObject.getBit(3));
	}
	
	/**
	 * positive test: one byte w/ odd number of unused bits
	 */
	@Test
	public void testAsn1BitString_03A8() {
		Asn1BitString testObject = new Asn1BitString(HexString.toByteArray("030203A8"));

		assertEquals(5 , testObject.getNumberOfBits());
		assertEquals(true , testObject.getBit(0));
		assertEquals(false, testObject.getBit(1));
		assertEquals(true , testObject.getBit(2));
		assertEquals(false, testObject.getBit(3));
		assertEquals(true , testObject.getBit(4));
	}
	
	/**
	 * positive test: two bytes 
	 */
	@Test
	public void testAsn1BitString_00C465() {
		Asn1BitString testObject = new Asn1BitString(HexString.toByteArray("030300C465"));

		assertEquals(16 , testObject.getNumberOfBits());
		assertEquals(true , testObject.getBit(0));
		assertEquals(false, testObject.getBit(1));
		assertEquals(true , testObject.getBit(2));
		assertEquals(false, testObject.getBit(3));
		assertEquals(false, testObject.getBit(4));
		assertEquals(true , testObject.getBit(5));
		assertEquals(true , testObject.getBit(6));
		assertEquals(false, testObject.getBit(7));
		assertEquals(false, testObject.getBit(8));
		assertEquals(false, testObject.getBit(9));
		assertEquals(true , testObject.getBit(10));
		assertEquals(false, testObject.getBit(11));
		assertEquals(false, testObject.getBit(12));
		assertEquals(false, testObject.getBit(13));
		assertEquals(true , testObject.getBit(14));
		assertEquals(true , testObject.getBit(15));
	}
	
	/**
	 * positive test: 3 bytes w/ unused bits
	 */
	@Test
	public void testAsn1BitString_066e5dc0() {
		Asn1BitString testObject = new Asn1BitString(HexString.toByteArray("0304066e5dc0"));

		assertEquals(18 , testObject.getNumberOfBits());
		
		assertEquals(true , testObject.getBit(0));
		assertEquals(true, testObject.getBit(1));
		assertEquals(true , testObject.getBit(2));
		assertEquals(false, testObject.getBit(3));
		assertEquals(true , testObject.getBit(4));
		assertEquals(true , testObject.getBit(5));
		assertEquals(true , testObject.getBit(6));
		assertEquals(false, testObject.getBit(7));
		assertEquals(true , testObject.getBit(8));
		assertEquals(false, testObject.getBit(9));
		assertEquals(false, testObject.getBit(10));
		assertEquals(true , testObject.getBit(11));
		assertEquals(true , testObject.getBit(12));
		assertEquals(true , testObject.getBit(13));
		assertEquals(false, testObject.getBit(14));
		assertEquals(true , testObject.getBit(15));
		assertEquals(true , testObject.getBit(16));
		assertEquals(false, testObject.getBit(17));
	}
	// more than one byte
	// odd/even unused bits
	// more than one byte w/ unused bits
	
	/**
	 * Negative test: Invalid tag
	 */
	@Test (expected=IllegalArgumentException.class)
	public void testAsn1BitString_wrongTag() {
		new Asn1BitString(HexString.toByteArray("0402000C"));
	}
	
	//below are test values taken from actual real world issues
	
	/**
	 * positive test: only one bit (used in GT default DS certificates)
	 */
	@Test
	public void testAsn1BitString_0780() {
		Asn1BitString testObject = new Asn1BitString(HexString.toByteArray("03020780"));

		assertEquals(1 , testObject.getNumberOfBits());
		assertEquals(true , testObject.getBit(0));
	}
	
	/**
	 * positive test: example that uncovered flaw in the past (see GTHELP-256)
	 */
	@Test
	public void testAsn1BitString_02C4() {
		Asn1BitString testObject = new Asn1BitString(HexString.toByteArray("030202C4"));

		assertEquals(6 , testObject.getNumberOfBits());
		assertEquals(true , testObject.getBit(0));
		assertEquals(false, testObject.getBit(1));
		assertEquals(false, testObject.getBit(2));
		assertEquals(false, testObject.getBit(3));
		assertEquals(true , testObject.getBit(4));
		assertEquals(true , testObject.getBit(5));
	}
	
	
}
