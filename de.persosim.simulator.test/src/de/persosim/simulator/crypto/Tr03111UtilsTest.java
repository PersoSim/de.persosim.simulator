package de.persosim.simulator.crypto;

import static org.junit.Assert.assertArrayEquals;

import java.math.BigInteger;

import org.junit.Test;

import de.persosim.simulator.crypto.Tr03111Utils;

import de.persosim.simulator.protocols.Tr03110Utils;
import de.persosim.simulator.test.PersoSimTestCase;

public class Tr03111UtilsTest extends PersoSimTestCase {
	
	/**
	 * Negative test: test {@link Tr03110Utils#i2os(BigInteger, int)} for a negative BigInteger.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testI2os_BigInt_NegativeBigInt() {
		BigInteger bigInt = new BigInteger("-42");
		Tr03111Utils.i2os(bigInt, 42);
	}
	
	/**
	 * Negative test: test {@link Tr03110Utils#i2os(BigInteger, int)} for a provided length smaller than the length of the actual octet String.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testI2os_BigInt_lengthSmallerThanOctetString() {
		BigInteger bigInt = new BigInteger("42");
		Tr03111Utils.i2os(bigInt, 0);
	}
	
	/**
	 * Positive test: test {@link Tr03110Utils#i2os(BigInteger, int)} for a provided length matching the length of the actual octet String.
	 */
	@Test
	public void testI2os_BigInt_lengthMatchingOctetString() {
		BigInteger bigInt = new BigInteger("42");
		byte[] expectedResult = new byte[] {(byte) 42};
		byte[] result = Tr03111Utils.i2os(bigInt, expectedResult.length);
		assertArrayEquals(expectedResult, result);
	}
	
	/**
	 * Positive test: test {@link Tr03110Utils#i2os(BigInteger, int)} for a provided length longer than the length of the actual octet String.
	 */
	@Test
	public void testI2os_BigInt_lengthLongerThanOctetString() {
		BigInteger bigInt = new BigInteger("42");
		byte[] expectedResult = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 42};
		byte[] result = Tr03111Utils.i2os(bigInt, expectedResult.length);
		assertArrayEquals(expectedResult, result);
	}
	
	/**
	 * Positive test: test {@link Tr03110Utils#i2os(byte[], int)} for a provided length shorter than the length of the actual octet String.
	 */
	@Test
	public void testI2os_ByteArray_lengthShorterThanOctetString() {
		byte[] bigInt = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 42};
		byte[] expectedResult = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 42};
		byte[] result = Tr03111Utils.i2os(bigInt, expectedResult.length);
		assertArrayEquals(expectedResult, result);
	}
	
	/**
	 * Positive test: test {@link Tr03110Utils#i2os(byte[], int)} for a provided length longer than the length of the actual octet String.
	 */
	@Test
	public void testI2os_ByteArray_lengthLongerThanOctetString() {
		byte[] bigInt = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 42};
		byte[] expectedResult = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 42};
		byte[] result = Tr03111Utils.i2os(bigInt, expectedResult.length);
		assertArrayEquals(expectedResult, result);
	}
	
	/**
	 * Positive test: test {@link Tr03110Utils#i2os(byte[], int)} for a provided lengthmatching the length of the actual octet String.
	 */
	@Test
	public void testI2os_ByteArray_lengthMatchingOctetString() {
		byte[] bigInt = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 42};
		byte[] expectedResult = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 42};
		byte[] result = Tr03111Utils.i2os(bigInt, expectedResult.length);
		assertArrayEquals(expectedResult, result);
	}
	
}
