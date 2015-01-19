package de.persosim.simulator.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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
	 * Positive test case: convert a simple byte with the value 00
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
	 * Negative test case: get IllegalArgumentException because 
	 * of string with odd length entered
	 */
	@Test(expected=IllegalArgumentException.class)
	public void IllegalArgumentException() {
//		String s ="ABC";
//		byte[] c = HexString.toByteArray(s);
		HexString.toByteArray("ABC");
		
	}
		
	/**
	 * Positive test case: convert empty string to byte array
	 * 
	 */
	@Test
	public void testToByteArray_Empty_Value() {
		String s = ""; 
		byte[] exp = new byte[]{};
		byte[] recv = HexString.toByteArray(s);
		
		assertArrayEquals(exp, recv);
	}
	
	/**
	 * Positive test case: convert space to byte array
	 * 
	 */
	@Test
	public void testToByteArray_SPACE_Value() {
		String s = " "; 
		byte[] exp = new byte[]{};
		byte[] recv = HexString.toByteArray(s);
		
		assertArrayEquals(exp, recv);
	}
	
	/**
	 * Positive test case: convert the singlebyte F5
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
