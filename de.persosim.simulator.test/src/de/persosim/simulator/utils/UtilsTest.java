package de.persosim.simulator.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testToUnsignedByteArray_BigIntegerZero() {
		byte[] exp = new byte[]{(byte) 0x00};
		byte[] recv = Utils.toUnsignedByteArray(BigInteger.ZERO);
		assertArrayEquals(exp, recv);
	}
	
	/**
	 * Positive test case: parse complete and valid date String for all byte parameters.
	 * For a complete and valid date String the results for all byte parameters are expected to be identical.
	 */
	@Test
	public void testGetDateStringByte_CompleteString() {
		Date dateReceivedMin = Utils.getDate("19640229", (byte) -1);
		Date dateReceivedNorm = Utils.getDate("19640229", (byte) 0);
		Date dateReceivedMax = Utils.getDate("19640229", (byte) 1);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1964);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.DATE, 29);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Date dateExpected = calendar.getTime();
		
		assertEquals(dateExpected, dateReceivedMin);
		assertEquals(dateExpected, dateReceivedNorm);
		assertEquals(dateExpected, dateReceivedMax);
	}
	
	/**
	 * Negative test case: parse date String shorter than expected.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetDateStringByte_StringTooShort() {
		Utils.getDate("1964022", (byte) 0);
	}
	
	/**
	 * Negative test case: parse date String with illegal day part and no rule for compensation.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetDateStringByte_IllegalDay() {
		Utils.getDate("1964022X", (byte) 0);
	}
	
	/**
	 * Negative test case: parse date String with illegal day part and invalid rule for compensation.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetDateStringByte_IllegalDayInvalidCompensationRule() {
		Utils.getDate("1964022X", (byte) 2);
	}
	
	/**
	 * Negative test case: parse date String with illegal month part and no rule for compensation.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetDateStringByte_IllegalMonth() {
		Utils.getDate("19640X29", (byte) 0);
	}
	
	/**
	 * Negative test case: parse date String with illegal month part and invalid rule for compensation.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetDateStringByte_IllegalMonthInvalidCompensationRule() {
		Utils.getDate("19640X29", (byte) 2);
	}
	
	/**
	 * Negative test case: parse date String longer than expected.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetDateStringByte_StringTooLongt() {
		Utils.getDate("196402290", (byte) 0);
	}
	
	/**
	 * Positive test case: parse date String with incomplete month to be rounded down.
	 */
	@Test
	public void testGetDateStringByte_IncompleteMonthRoundDown() {
		Date dateReceived = Utils.getDate("1964XX29", (byte) -1);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1964);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DATE, 29);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Date dateExpected = calendar.getTime();
		
		assertEquals(dateExpected, dateReceived);
	}
	
	/**
	 * Positive test case: parse date String with incomplete month to be rounded up.
	 */
	@Test
	public void testGetDateStringByte_IncompleteMonthRoundUp() {
		Date dateReceived = Utils.getDate("1964XX29", (byte) 1);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1964);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DATE, 29);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Date dateExpected = calendar.getTime();
		
		assertEquals(dateExpected, dateReceived);
	}
	
	/**
	 * Positive test case: parse date String with incomplete month to be rounded down.
	 */
	@Test
	public void testGetDateStringByte_IncompleteDayRoundDown() {
		Date dateReceived = Utils.getDate("196402XX", (byte) -1);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1964);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Date dateExpected = calendar.getTime();
		
		assertEquals(dateExpected, dateReceived);
	}
	
	/**
	 * Positive test case: parse date String with incomplete month to be rounded down.
	 */
	@Test
	public void testGetDateStringByte_IncompleteDayRoundUp() {
		Date dateReceived = Utils.getDate("196402XX", (byte) 1);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1964);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.DATE, 29);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Date dateExpected = calendar.getTime();
		
		assertEquals(dateExpected, dateReceived);
	}
	
	/**
	 * Positive test case: check data array for smaller prefix.
	 */
	@Test
	public void testArrayHasPrefix_PrefixSmallerThanData() {
		byte[] data = HexString.toByteArray("00112233445566778899");
		byte[] prefix = HexString.toByteArray("001122");
		
		assertTrue(Utils.arrayHasPrefix(data, prefix));
	}
	
	/**
	 * Positive test case: check data array for same size prefix.
	 */
	@Test
	public void testArrayHasPrefix_PrefixSameSizeAsData() {
		byte[] data = HexString.toByteArray("00112233445566778899");
		byte[] prefix = data;
		
		assertTrue(Utils.arrayHasPrefix(data, prefix));
	}
	
	/**
	 * Positive test case: check data array for larger prefix.
	 */
	@Test
	public void testArrayHasPrefix_PrefixLargerThanData() {
		byte[] data = HexString.toByteArray("001122");
		byte[] prefix = HexString.toByteArray("00112233445566778899");
		
		assertFalse(Utils.arrayHasPrefix(data, prefix));
	}
	
	/**
	 * Negative test case: check data array for deviating prefix.
	 */
	@Test
	public void testArrayHasPrefix_PrefixDeviating() {
		byte[] data = HexString.toByteArray("00112233445566778899");
		byte[] prefix = HexString.toByteArray("FF");
		
		assertFalse(Utils.arrayHasPrefix(data, prefix));
	}
	
	/**
	 * Negative test case: method appendBytes gets appendBytes as input, which is null;
	 */
	@Test(expected=IllegalArgumentException.class)
	public void appendBytestrailinbytesisnull()
	{
		byte[] leadingbyte = new byte[]{(byte) 0x65};
		Utils.appendBytes(leadingbyte, null);
		
	}
	
	/**
	 * Negative test case: method appendBytes gets trailingbytes as input, which is less then 1.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void appendBytestrailingbytesisless1()
	{
		byte[] test = new byte[]{};
		byte[] leadingbyte = new byte[]{(byte) 0x65};
		
		Utils.appendBytes(leadingbyte, test);
	}
	
	/**
	 * Negative test case: the method concanByteArrays gets input null.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void concatByteArraysinputisnull()
	{
		Utils.concatByteArrays(null);
		
	}
	
	/**
	 * Negative test case: method toUnsignedByteArray gets an input, which is null.
	 */
	@Test(expected=NullPointerException.class)
	public void toUnsignedByteArrayinputisnull()
	{
		BigInteger test = null;
		Utils.toUnsignedByteArray(test);
		
	}
	
	/**
	 * Negative test case: method appendBytes gets trailingbytes as input, which is less then 1.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void toUnsignedByteA()
	{
		byte[] test = new byte[]{};
		Utils.appendBytes(test, null);
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type long.
	 */
	@Test
	public void toUnsignedByteArrayinputlong()
	{
		long test = 34343;
		Utils.toUnsignedByteArray(test);
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type int.
	 */
	@Test
	public void toUnsignedByteArrayinputint()
	{
		int test = 34343;
		Utils.toUnsignedByteArray(test);
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type short.
	 */
	@Test
	public void toUnsignedByteArrayinputshort()
	{
		short test = 343;
		Utils.toUnsignedByteArray(test);
	}
	
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type byte.
	 */
	@Test
	public void toUnsignedByteArrayinputbyte()
	{
		byte test = (byte) 0xFF;
		Utils.toUnsignedByteArray(test);
	}
	
	/**
	 * Positive Test case: method removeLeadingZeroBytes gets byte array as input.
	 */
	@Test
	public void removeLeadingZeroBytes()
	{
		byte[] test = new byte[]{ (byte) 0xFF};
		Utils.removeLeadingZeroBytes(test);
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToShort gets byte value as input.
	 */
	@Test
	public void maskUnsignedByteToShort()
	{
		byte b = 100;
		Utils.maskUnsignedByteToShort(b);
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets byte value as input.
	 */
	@Test
	public void maskUnsignedByteToInt()
	{
		byte b = 100;
		Utils.maskUnsignedByteToInt(b);
	}
	
	/**
	 * Positive Test case: method maskUnsignedShortToInt gets short value as input.
	 */
	@Test
	public void maskUnsignedShortToInt()
	{
		short b = 110;
		Utils.maskUnsignedShortToInt(b);
	}
	
	/**
	 * Positive Test case: method concatenate gets 2 different bytes as input.
	 */
	@Test
	public void concatenate()
	{
		byte a =1,b = 2;
		Utils.concatenate(a, b);
	}
	
	/**
	 * Positive Test case: method getDate gets a dateString as input.
	 */
	@Test
	public void getDate()
	{
		
	 String s = "19990801";
	 Utils.getDate(s);
	}
	
	/**
	 * Positive Test case: method getDate gets a null string.
	 */
	@Test(expected=NullPointerException.class)
	public void getDatedateStringisnull()
	{
		byte a = 0;
		String s = null;
		Utils.getDate(s, a);
		
	}
	
	/**
	 * Positive Test case: method arrayContainsEqual gets an Object array and an object as input.s
	 */
	@Test
	public void arrayContainsEqual()
	{
		String[] a =  new String[2];
		a[1] = "1";
		a[0] = "1";
		String b = "1";
		
		Utils.arrayContainsEqual(a, b);
	}
	
	/**
	 * Positive Test case: method logarithm becomes an double and int input.
	 */
	@Test
	public void logarithm()
	{
		int base = 2;
		Utils.logarithm(0, base);
	}
	
	/**
	 * Positive Test case: method binaryEncode becomes a byte array as input.
	 */
	@Test
	public void binaryEncode()
	{
		byte[] test = new byte[]{(byte) 0x66, (byte) 0x66, (byte) 0x66};
		Utils.binaryEncode(test);
	}
	
	/**
	 * Positive Test case: method isAnyNull becomes a null Object as input.
	 */
	@Test
	public void isAnyNulltrue()
	{
		Object a = null;
		Utils.isAnyNull(a);
	}
	
	/**
	 * Positive Test case: method isAnyNull becomes a Object as input.
	 */
	@Test
	public void isAnyNullfalse()
	{
		Object a = new Object();
		Utils.isAnyNull(a);
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets byte array as input.
	 */
	@Test
	public void getShortFromUnsignedByteArray()
	{
		byte[] test = new byte[]{(byte) 0x66};
		Utils.getShortFromUnsignedByteArray(test);
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets byte array as input.
	 */
	@Test
	public void getIntFromUnsignedByteArray()
	{
		byte[] test = new byte[]{(byte) 0x66};
		Utils.getIntFromUnsignedByteArray(test);
	}
	
	/**
	 * Positive Test case: method getBigIntegerFromUnsignedByteArray gets byte array as input.
	 */
	@Test
	public void getBigIntegerFromUnsignedByteArray()
	{
		byte[] test = new byte[]{(byte) 0x66};
		Utils.getBigIntegerFromUnsignedByteArray(test);
	}
	
	/**
	 * Positive Test case: method getBigIntegerFromUnsignedByteArray gets byte array as input.
	 */
	@Test
	public void toUnsignedByteArra()
	{
		byte[] test = new byte[]{(byte) 0x66};
		Utils.getBigIntegerFromUnsignedByteArray(test);
	}
}
