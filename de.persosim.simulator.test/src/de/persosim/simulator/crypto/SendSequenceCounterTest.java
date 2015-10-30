package de.persosim.simulator.crypto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class SendSequenceCounterTest extends PersoSimTestCase {
	
	/**
	 * Positive test case: construct send sequence counter providing minimum value argument for maximum value of 1 byte length,
	 * check default value, max value and max byte length
	 */
	@Test
	public void testSendSequenceCounterBigIntegerBigInteger_minValue1ByteLength() {
		SendSequenceCounter ssc = new SendSequenceCounter(BigInteger.ONE, BigInteger.ONE);
		assertEquals(BigInteger.ONE, ssc.getValue());
		assertEquals(BigInteger.ONE, ssc.getMaxValue());
	}
	
	/**
	 * Positive test case: construct send sequence counter providing maximum value argument for maximum value of 1 byte length,
	 * check default value, max value and max byte length
	 */
	@Test
	public void testSendSequenceCounterBigIntegerBigInteger_maxValue1ByteLength() {
		SendSequenceCounter ssc = new SendSequenceCounter(BigInteger.ONE, new BigInteger("255"));
		assertEquals(BigInteger.ONE, ssc.getValue());
		assertEquals(new BigInteger("255"), ssc.getMaxValue());
	}
	
	/**
	 * Positive test case: construct send sequence counter providing minimum value argument for maximum value of 2 byte length,
	 * check default value, max value and max byte length
	 */
	@Test
	public void testSendSequenceCounterBigIntegerBigInteger_minValue2ByteLength() {
		SendSequenceCounter ssc = new SendSequenceCounter(BigInteger.ONE, new BigInteger("511"));
		assertEquals(BigInteger.ONE, ssc.getValue());
		assertEquals(new BigInteger("511"), ssc.getMaxValue());
	}
	
	/**
	 * Positive test case: construct send sequence counter providing maximum value argument for maximum value of 2 byte length,
	 * check default value, max value and max byte length
	 */
	@Test
	public void testSendSequenceCounterBigIntegerBigInteger_maxValue2ByteLength() {
		SendSequenceCounter ssc = new SendSequenceCounter(BigInteger.ONE, new BigInteger("65535"));
		assertEquals(BigInteger.ONE, ssc.getValue());
		assertEquals(new BigInteger("65535"), ssc.getMaxValue());
	}
	
	/**
	 * Positive test case: construct send sequence counter with default initial value providing only maximum value,
	 * check default value, max value and max byte length
	 */
	@Test
	public void testSendSequenceCounterBigInteger() {
		SendSequenceCounter ssc = new SendSequenceCounter(BigInteger.ONE);
		assertEquals(BigInteger.ZERO, ssc.getValue());
		assertEquals(BigInteger.ONE, ssc.getMaxValue());
	}
	
	/**
	 * Positive test case: construct send sequence counter providing maximum byte length argument for maximum value,
	 * check default value, max value and max byte length
	 */
	@Test
	public void testSendSequenceCounterBigIntegerInt() {
		SendSequenceCounter ssc = new SendSequenceCounter(BigInteger.ONE, 2);
		assertEquals(BigInteger.ONE, ssc.getValue());
		assertEquals(new BigInteger("65535"), ssc.getMaxValue());
	}
	
	/**
	 * Positive test case: increment ssc without expected overflow
	 */
	@Test
	public void testIncrement_noOverflow() {
		SendSequenceCounter ssc = new SendSequenceCounter(new BigInteger("42"));
		ssc.increment();
		assertEquals(new BigInteger("42"), ssc.getMaxValue());
		assertEquals(BigInteger.ONE, ssc.getValue());
	}
	
	/**
	 * Positive test case: increment ssc with expected overflow
	 */
	@Test
	public void testIncrement_overflow() {
		SendSequenceCounter ssc = new SendSequenceCounter(new BigInteger("42"), new BigInteger("42"));
		ssc.increment();
		assertEquals(new BigInteger("42"), ssc.getMaxValue());
		assertEquals(BigInteger.ZERO, ssc.getValue());
	}
	
	/**
	 * Positive test case: test byte array encoding for minimum value
	 */
	@Test
	public void testToByteArray_MinValue() {
		SendSequenceCounter ssc = new SendSequenceCounter(BigInteger.ZERO, 2);
		byte[] expected = HexString.toByteArray("0000");
		assertArrayEquals(expected, ssc.toByteArray());
	}
	
	/**
	 * Positive test case: test byte array encoding for maximum value
	 */
	@Test
	public void testToByteArray_MaxValue() {
		SendSequenceCounter ssc = new SendSequenceCounter(new BigInteger("65535"), 2);
		byte[] expected = HexString.toByteArray("FFFF");
		assertArrayEquals(expected, ssc.toByteArray());
	}
	
}
