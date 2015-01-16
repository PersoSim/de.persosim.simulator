package de.persosim.simulator.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class HexStringTest {

	@Test
	public void testDump() {
		byte[] buffer = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
				(byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F, (byte) 0x10};
		String dump = HexString.dump(buffer, 0, (short) 16);
		System.out.println("Hex Dump:\n"+dump);
		assertEquals(" 0000  01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F 10  ................\n", dump);
	}
	
	/**
	 * Positive test case: simple byte 00
	 */
	@Test
	public void testToByteArray_Value_00() {
		String s = "00";
		byte[] exp = new byte[]{(byte) 0x00};
		
		byte[] recv = HexString.toByteArray(s);
		
		assertArrayEquals(exp, recv);
		
		
	}
	
	/**
	 * Positive test case: convert "42" to byte array
	 */
	@Test
	public void testToByteArray_Value_42() {
		String s = "42";
		byte[] exp = new byte[]{(byte) 0x42};
		
		byte[] recv = HexString.toByteArray(s);
		
		assertArrayEquals(exp, recv);
		
		
	}
	
	/**
	 * Negative test case: get IllegalArgumentException because of odd value FIXME LSG tis is not an odd value, but a string with odd length
	 * entered
	 */
	@Test
	public void testToByteArray_ODD_Value() {
		String s = "ABC";

		boolean thrown = false;

		 //FIXME LSG we won't merge warnings on master ;-)
		byte[] exp = new byte[] { (byte) 0x0A, (byte) 0xBC };
		byte[] recv = null;
		try {
			//FIXME LSG while this implementation, (catching and expecting an exception) is technically correct JUnit proivdes better means, search for "JUnit expected exception"

			recv = HexString.toByteArray(s);
		} catch (IllegalArgumentException e) {

			thrown = true;

		}
		assertTrue(thrown);

	}
	
	/**
	 * Positive test case: convert empty string to byte array
	 * 
	 */
	@Test
	public void testToByteArray_Empty_Value() {
		String s = " "; //FIXME LSG this is not an empty String!!
		byte[] exp = new byte[]{};
		byte[] recv = HexString.toByteArray(s);
		
		assertArrayEquals(exp, recv);
	}
	
	/**
	 * Positive test case: convert empty string to byte array //FIXME LSG this description does not match the method content
	 * 
	 */
	@Test
	public void testToByteArray_SingleByte_HighestBitSet() {
		String s = "F5";
		byte[] exp = new byte[]{(byte) 0xF5};
		byte[] recv = HexString.toByteArray(s);
		
		assertArrayEquals(exp, recv);
	}
	
	/**
	 * Positive test case: convert a very long "F" string to byte array
	 */
	@Test
	public void testToByteArray_Value_FF() {
		//FIXME LSG this description of the input and expected result is ugly. Try to describe them in one simple loop or similar.
		String s = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"
				+ "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"
				+ "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
		byte[] exp = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

		byte[] recv = HexString.toByteArray(s);

		assertArrayEquals(exp, recv);
	}
	
	/**
	 * Positive test case: convert String to byte array for which the most
	 * significant bit is expected to be set.
	 */
	@Test
	public void testToByteArray_HighestBitSet() {
		String s = "80FF00";
		byte[] exp = new byte[]{(byte) 0x80, (byte) 0xFF, (byte) 0x00};
		
		byte[] recv = HexString.toByteArray(s);
		
		assertArrayEquals(exp, recv);
	}
	
	/**
	 * Positive test case: convert String to byte array for which the most
	 * significant bit is expected not to be set.
	 */
	@Test
	public void testToByteArray_HighestBitUnset() {
		String s = "7FFF00";
		byte[] exp = new byte[] { (byte) 0x7F, (byte) 0xFF, (byte) 0x00 };

		byte[] recv = HexString.toByteArray(s);

		assertArrayEquals(exp, recv);
	}
	
	/**
	 * Positive test case: convert String with leading "00" to byte array.
	 */
	@Test
	public void testToByteArray_LeadingZeroByte() {
		String s = "00FF00";
		byte[] exp = new byte[]{(byte) 0x00, (byte) 0xFF, (byte) 0x00};
		
		byte[] recv = HexString.toByteArray(s);
		
		assertArrayEquals(exp, recv);
	}
	

}
