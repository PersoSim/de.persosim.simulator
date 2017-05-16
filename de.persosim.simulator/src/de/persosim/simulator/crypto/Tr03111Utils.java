package de.persosim.simulator.crypto;

import java.math.BigInteger;
import java.util.Arrays;

import de.persosim.simulator.utils.Utils;

/**
 * This class provides implementations for utility functions described in TR-03110 tech guidelines.
 *
 */
public class Tr03111Utils {
	
	/**
	 * This method converts field elements to octet strings.
	 * Implementation complies with Field Element to Octet String Conversion Primitive (FE2OS)
	 * as defined by TR-03111 v2.0 3.1.3 Conversion between Field Elements and Octet Strings
	 * 
	 * @param x a field element
	 * @param p the prime of the field
	 * @return the converted octet string
	 */
	public static byte[] fe2os(BigInteger x, BigInteger p) {
		byte[] pArray = Utils.toUnsignedByteArray(p);
		int l = pArray.length;
		return i2os(x, l);
	}
	
	/**
	 * This method converts integers to octet strings. Implementation complies
	 * with Integer to Octet String Conversion Primitive (I2OS) as defined by
	 * TR-03111 v2.0 3.1.2 Conversion between Integers and Octet Strings.
	 * 
	 * @param x
	 *            a non-negative integer
	 * @param l
	 *            desired length of the octet string
	 * @return the converted octet string
	 */
	public static byte[] i2os(BigInteger x, int l) {
		if(x.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("x must be non-negative");
		}
		
		byte[] result = Utils.toUnsignedByteArray(x);
		
		return i2os(result, l);
	}
	
	/**
	 * This method converts byte array representations of non-negative integers
	 * to octet strings. The integer representation will be interpreted as
	 * non-negative, i.e. unsigned. Implementation complies with Integer to
	 * Octet String Conversion Primitive (I2OS) as defined by TR-03111 v2.0
	 * 3.1.2 Conversion between Integers and Octet Strings.
	 * 
	 * @param x
	 *            the integer byte array representation
	 * @param l
	 *            desired length of the octet string
	 * @return the converted octet string
	 */
	public static byte[] i2os(byte[] x, int l) {
		byte[] input;
		
		if(x.length > l) {
			input = Utils.removeLeadingZeroBytes(x);
		} else{
			input = x;
		}
		
		if(l < input.length) {
			throw new IllegalArgumentException("l too small for provided x");
		}
		
		if(l > input.length) {
			byte[] padding = new byte[l-input.length];
			Arrays.fill(padding, (byte) 0x00);
			input = Utils.concatByteArrays(padding, input);
		}
		
		return input;
	}
	
}
