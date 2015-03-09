package de.persosim.simulator.utils;

import static org.junit.Assert.*;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testToUnsignedByteArrayBigIntegerZero() {
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
	 * Positive Test case: method getDate gets a dateString as input.
	 * The result should be the 1.08.1999 with the time 00:00:00:00
	 */
	@Test
	public void testGetDateStringdate() 
	{
		String s = "19990801";
		Date actual = Utils.getDate(s);
	 	Calendar calendar = Calendar.getInstance();
	 	calendar.setTime(actual);
	 	
	 	assertEquals("year", 1999, calendar.get(Calendar.YEAR));
	 	assertEquals("month", Calendar.AUGUST, calendar.get(Calendar.MONTH));
	 	assertEquals("day", 1, calendar.get(Calendar.DAY_OF_MONTH));
	 	assertEquals("hour", 00, calendar.get(Calendar.HOUR));
	 	assertEquals("minute", 00, calendar.get(Calendar.MINUTE));
	 	assertEquals("second", 00, calendar.get(Calendar.SECOND));
	 	assertEquals("millisecond", 00, calendar.get(Calendar.MILLISECOND));
	 	
	}
	
	/**
	 * Negative Test case: method getDate gets a null string.
	 * The result should be a NPE cause of the string which is null.
	 */
	@Test(expected=NullPointerException.class)
	public void testGetDateString()
	{
		byte bytevalue = 1;
		String nullstring = null;
		Utils.getDate(nullstring, bytevalue);	
		
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
	 * Positive Test case: method appendBytes gets an empty value and a byte array as input.
	 * The result should be a new object which has the same content as trailingByteArray.
	 */
	@Test
	public void testAppendBytes_LeadingByteEmpty()
	{
		byte[] leadingByteArray = new byte[]{};
		byte[] trailingByteArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		byte[] expectedArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame("method returns the same object", trailingByteArray, result);
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive Test case: method appendBytes gets an empty array and a byte array as input.
	 * The result should be a new object which has the same content as leadingByteArray.
	 */
	@Test
	public void testAppendBytes_TrailingByteEmpty()
	{
		byte[] leadingByteArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		byte[] trailingByteArray = new byte[]{};
		byte[] expectedArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame("method returns the same object", leadingByteArray, result);
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive case: method appendBytes gets an byte array and null as input.
	 * The result should be a new object which has the same content as leadingByteArray.
	 */
	@Test
	public void testAppendBytes_TrailingByteArrayNull()
	{
		byte[] leadingByteArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		byte[] trailingByteArray = null;
		byte[] expectedArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame("method returns the same object", leadingByteArray, result);
		assertArrayEquals(expectedArray, result);
		
	}
	
	/**
	 * Positive case: method appendBytes gets an byte array and null as input.
	 * The result should be a new object which has the same content as the trailingByteArray.
	 */
	@Test
	public void testAppendBytes_LeadingByteArrayNull()
	{
		byte[] leadingByteArray = null;
		byte[] trailingByteArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		byte[] expectedArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame("method returns the same object", trailingByteArray, result);
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive Test case: method appendBytes gets two empty arrays as input.
	 * The result should be a new object, which content is empty.
	 */
	@Test
	public void testAppendBytes_EmptyArrays()
	{
		byte[] leadingByteArray = new byte[]{};
		byte[] trailingByteArray = new byte[]{};
		byte[] expectedArray = new byte[]{};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame("method returns the same object", leadingByteArray, result);
		assertNotSame("method returns the same object", trailingByteArray, result);
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive Test case: method appendBytes gets two byte arrays as input.
	 * The result should be a new object which doens't skip the zeros and has the content 
	 * of the leadingByteArray and trailingByteArray in the right order.
	 */
	@Test
	public void testAppendBytes_LeadingByteArrayZero()
	{
		byte[] leadingByteArray = new byte[]{0x00,0x00};
		byte[] trailingByteArray = new byte[]{(byte)0xFF,(byte) 0xFF};
		byte[] expectedArray = new byte[]{0x00,0x00,(byte)0xFF,(byte) 0xFF};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive Test case: method appendBytes gets two byte arrays as input.
	 * The result should be a new object which doesn't ignore the zeros on the end 
	 * and has the content of the leadingByteArray and trailingBytArray in the right order.
	 */
	@Test
	public void testAppendBytes_TrailingByteArrayZero()
	{
		byte[] leadingByteArray = new byte[]{(byte)0xFF,(byte) 0xFF};
		byte[] trailingByteArray = new byte[]{0x00,0x00};
		byte[] expectedArray = new byte[]{(byte)0xFF,(byte) 0xFF,0x00,0x00};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertArrayEquals(expectedArray, result);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type long with the lowest value.
	 * The result is a new object of the type byte.
	 */
	@Test
	public void testToUnsignedByteArrayLong_LowestValue()
	{
		long longVar = 0x00;
		byte[] actual = Utils.toUnsignedByteArray(longVar);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of the limit of signed long.
	 * The result is a new object of the type byte.
	 */
	@Test
	public void testToUnsignedByteArrayLong_SignedLongLimit()
	{
		long longVar = 9223372036854775807l;
		byte[] actual = Utils.toUnsignedByteArray(longVar);
		byte[] expected = new byte[]{(byte) 0x7F,(byte) 0xFF,(byte) 0x00,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input, which exceeds the limits of signed long.
	 * The result is a new object of the type byte.
	 */
	@Test
	public void testToUnsignedByteArrayLong_ExceedLimitSignedLong()
	{
		long longVar = 0x7FFFFFFFFFFFFFFFl;
		byte[] actual = Utils.toUnsignedByteArray(longVar);
		byte[] expected = new byte[]{(byte) 0x7F,(byte) 0xFF,(byte) 0x00,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
		
		assertArrayEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method toUnsignedByteArray gets input with the lowest value of type int.
	 * The result should be a new byte array object.
	 */
	@Test
	public void testToUnsignedByteArrayInt_LowestValue()
	{
		int intVar = 0x00;
		byte[] actual = Utils.toUnsignedByteArray(intVar);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input with the highest value of the type int.
	 * The result should be a new object of the type byte.
	 */
	@Test
	public void testToUnsignedByteArrayInt_HighestValue()
	{
		int intVar = 0xFFFFFFFF; 
		byte[] actual = Utils.toUnsignedByteArray(intVar);
		byte[] expected = new byte[]{(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of the limit of signed int.
	 * The result should be a new object of the type byte. 
	 */
	@Test
	public void testToUnsignedByteArrayInt_SignedIntLimit()
	{
		int intVar = 0x7FFFFFFF; 
		byte[] actual = Utils.toUnsignedByteArray(intVar);
		byte[] expected = new byte[]{(byte) 0x7F,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input, which exceeds the limit of signed int.
	 * The result should be a new object of the type byte. 
	 */
	@Test
	public void testToUnsignedByteArrayInt_ExceedLimitSignedInt()
	{
		int intVar = 0x80000000; 
		byte[] actual = Utils.toUnsignedByteArray(intVar);
		byte[] expected = new byte[]{(byte) 0x80,(byte) 0x00,(byte) 0x00,(byte) 0x00};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input with the highest value of type short.
	 * The result should be a new object of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayShort_HighestValue()
	{
		short shortVar = (byte)0xFFFF;
		byte[] actual = Utils.toUnsignedByteArray(shortVar);
		byte[] expected = new byte[]{(byte) 0xFF,(byte) 0xFF};
	
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of the limit of signed short.
	 * The result should be a new object of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayShort_SignedShortLimit()
	{
		short shortVar = 0x7FFF;
		byte[] actual = Utils.toUnsignedByteArray(shortVar);
		byte[] expected = new byte[]{(byte) 0x7F,(byte) 0xFF};
	
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input, which exceeds the limit of signed short.
	 * The result should be a new object of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayShort_ExceedLimitSignedShort()
	{
		short shortVar = (short) 0x8000;
		byte[] actual = Utils.toUnsignedByteArray(shortVar);
		byte[] expected = new byte[]{(byte) 0x80,(byte) 0x00};
	
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type short with the lowest value.
	 * The result should be a new byte array object.
	 */
	@Test
	public void testToUnsignedByteArrayShort_LowestValue()
	{
		short shortVar = 0x00;
		byte[] actual = Utils.toUnsignedByteArray(shortVar);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x00};
	
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input with highest value of type byte.
	 * The result should be a new object of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayByte_HighestValue()
	{
		byte byteVar = (byte) 0xFF;
		byte[] actual = Utils.toUnsignedByteArray(byteVar);
		byte[] expected = new byte[]{(byte) 0xFF};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type byte.
	 * The result should be a new object of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayByte_LowestValue()
	{
		byte byteVar = (byte) 0x00;
		byte[] actual = Utils.toUnsignedByteArray(byteVar);
		byte[] expected = new byte[]{(byte) 0x00};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of the limit of signed byte.
	 * The result should be a new object of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayByte_SignedByteLimit()
	{
		byte byteVar = (byte) 0x7F;
		byte[] actual = Utils.toUnsignedByteArray(byteVar);
		byte[] expected = new byte[]{(byte) 0x7F};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type byte, which exceed the limit of signed byte.
	 * The result should be a new object of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayByte_ExceedLimitSignedByte()
	{
		byte byteVar = (byte) 0x80;
		byte[] actual = Utils.toUnsignedByteArray(byteVar);
		byte[] expected = new byte[]{(byte) 0x80};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method removeLeadingZeroBytes gets an byte array as input.
	 */
	@Test
	public void testRemoveLeadingZeroBytesByteArray_LowestValue()
	{
		byte[] bytearray = new byte[]{(byte) 0x00, (byte) 0x00};
		byte[] expected = new byte[]{(byte) 0x00};
		
		byte[] actual = Utils.removeLeadingZeroBytes(bytearray);	 	
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method removeLeadingZeroBytes gets an byte array as input.
	 * The result should be the array without the leading zero bytes.
	 */
	@Test
	public void testRemoveLeadingZeroBytesByteArray()
	{
		byte[] bytearray = new byte[]{(byte) 0x00, (byte) 0xFF};
		byte[] actual = Utils.removeLeadingZeroBytes(bytearray);
		 
		byte[] expected = new byte[]{(byte) 0xFF};	
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToShort gets byte with the highest value as input.
	 * The result should be a new object of type short.
	 */
	@Test
	public void testMaskUnsignedByteToShortByte_HighestByte() 
	{
		byte byteVar = (byte) 0xFF;
		short expected = 0xFF;
		
		short actual = Utils.maskUnsignedByteToShort(byteVar);
		
		assertEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method maskUnsignedByteToShort gets a byte with the lowest value as input.
	 * The result should be a new object of type short.
	 */
	@Test
	public void testMaskUnsignedByteToShortByte_LowestValue() 
	{
		
		byte byteVar = (byte) 0x00;
		short expected = 0x00;

		short actual = Utils.maskUnsignedByteToShort(byteVar);
		
		assertEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method maskUnsignedByteToShort gets input of the limit of signed byte.
	 * The result should be a new object of type short.
	 */
	@Test
	public void testMaskUnsignedByteToShortByte_SignedByteLimit() 
	{
		byte byteVar = (byte) 0x7F;

		short actual = Utils.maskUnsignedByteToShort(byteVar);;
		short expected = 0x7F;

		assertEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method maskUnsignedByteToShort gets input, which exceeds the limit of signed byte.
	 * The result should be a new object of type short.
	 */
	@Test
	public void testMaskUnsignedByteToShortByte_ExceedLimitSignedByte() 
	{
		byte byteVar = (byte) 0x80;
		short expected = 0x80;
		
		short actual = Utils.maskUnsignedByteToShort(byteVar);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets the highest byte as input.
	 * The result should be a new object of type int.
	 */
	@Test
	public void testMaskUnsignedByteToIntByte_HighestValue()
	{
		byte byteVar = (byte)0xFF;
		int expected = 0xFF;
		
		int actual = Utils.maskUnsignedByteToInt(byteVar);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets the lowest byte as input.
	 * The result should be a new object of type int.
	 */
	@Test
	public void testMaskUnsignedByteToIntByte_LowestValue()
	{
		byte byteVar = 0x00;
		int expected = 0x00;
		
		int actual = Utils.maskUnsignedByteToInt(byteVar);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets input of the limit of signed byte.
	 * The result should be a new object of type int.
	 */
	@Test
	public void testMaskUnsignedByteToIntByte_SignedByteLimit()
	{
		byte byteVar = 0x7F;
		int expected = 0x7F;
		
		int actual = Utils.maskUnsignedByteToInt(byteVar);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets input, which exceeds the limit of signed byte.
	 * The result should be a new object of type int.
	 */
	@Test
	public void testMaskUnsignedByteToIntByte_ExceedLimitSignedByte()
	{
		byte byteVar = (byte)0x80;
		int expected = 0x80;
		
		int actual = Utils.maskUnsignedByteToInt(byteVar);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedShortToInt gets a short with the highest value of type short.
	 * The result should be a new object of type int.
	 */
	@Test
	public void testMaskUnsignedShortToIntShort_HighestValue()
	{
		short shortVar = (byte)0xFFFF; 
		int expected = 0xFFFF;
		
		int actual  = Utils.maskUnsignedShortToInt(shortVar);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedShortToInt gets a short with the lowest value of type short.
	 * The result should be a new object of type int.
	 */
	@Test
	public void testMaskUnsignedShortToIntShort_LowestValue()
	{
		short shortVar = 0x00;
		int expected = 0x00;
		
		int actual  = Utils.maskUnsignedShortToInt(shortVar);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatenate gets the highest and lowest byte as input.
	 * The result should be the concatenation of these 2 bytes.
	 */
	@Test
	public void testConcatenateByte_HighestAndLowestByte()
	{
		byte byteVar1 = (byte) 0xFF;
		byte byteVar2 = (byte) 0x00;
		short expected = (short) 0xFF00;
		
		short actual = Utils.concatenate(byteVar1, byteVar2);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatenate gets the lowest and highest byte as input.
	 * The result should be the concatenation of these 2 bytes.
	 */
	@Test
	public void testConcatenateByte_LowestAndHighestByte()
	{
		byte byteVar1 = (byte) 0x00;
		byte byteVar2 = (byte) 0xFF;
		short expected = (short) 0xFF;
		
		short actual = Utils.concatenate(byteVar1, byteVar2);
		
		assertEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatenate gets two lowest bytes as input.
	 * The result should be the concatenation of these two bytes.
	 */
	@Test
	public void testConcatenateByte_TwoLowestBytes()
	{
		byte byteVar1 = (byte) 0x00;
		byte byteVar2 = (byte) 0x00;
		short expected = (short) 0x00;
		
		short actual = Utils.concatenate(byteVar1, byteVar2);
		
		assertEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatenate gets two highest bytes as input.
	 * The result should be the concatenation of these two bytes.
	 */
	@Test
	public void testConcatenateByte_TwoHighestBytes()
	{
		byte byteVar1  = (byte) 0xFF; 
		byte byteVar2 = (byte) 0xFF;
		short expected = (short) 0xFFFF;
		
		short actual = Utils.concatenate(byteVar1, byteVar2);
		
		assertEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatenate gets the two bytes as input.
	 * The result should is the concatenation of the two bytes in the right order.
	 */
	@Test
	public void testConcatenateByte_TwoBytes()
	{
		byte byteVar1 = (byte) 0x01;
		byte byteVar2 = (byte) 0x01;
		short expected = (short) 0x101;
		
		short result = Utils.concatenate(byteVar1, byteVar2);
		
		assertEquals(expected, result);	
		
	}

	/**
	 * Positive Test case: method arrayContainsEqual gets an Object array and an string value as input.
	 * The result is the boolean value, which depends on if the entries of the array are equal to the object.
	 */
	@Test
	public void testArrayContainsEqualString_StringArrayAndString()
	{
		String[] stringarray =  new String[2];
		stringarray[1] = "1";
		stringarray[0] = "1";
		String string = "1";
		
		boolean actual  = Utils.arrayContainsEqual(stringarray,string);
		
		assertTrue(actual);
		
	}
	
	/**
	 * Positive Test case: method logarithm gets a double as input and int for the base input.
	 * The result is the the logarithm of the double value with the base of type int.
	 */
	@Test
	public void testLogarithm_DoubleAndBase()
	{
		int base = 100;
		double doubleVar = 10;
		double DELTA = 1e-15;
		double actual = Utils.logarithm(doubleVar, base);
		double expected = 0.5;
		
		assertEquals(expected, actual,DELTA);
		
	}
	
	/**
	 * Positive Test case: method binaryEncode gets a byte array as input.
	 * The result should be value in binary.
	 */
	@Test
	public void testBinaryEncodeByteArray_LowestValue()
	{
		byte[] bytearray = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00};
		String actual  = Utils.binaryEncode(bytearray);
		String expected = "00000000 00000000 00000000";
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method binaryEncode gets a byte array as input.
	 * The result should be value in binary.
	 */
	@Test
	public void testBinaryEncodeByteArray_HighestValue()
	{
		byte[] bytearray = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
		String actual  = Utils.binaryEncode(bytearray);
		String expected = "11111111 11111111 11111111";
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method isAnyNull gets a null Object as input.
	 * The result should be the true.
	 */
	@Test
	public void testIsAnyNull_True() 
	{
		Object nullobject = null;
		boolean actual  = Utils.isAnyNull(nullobject);
		
		assertTrue(actual);
		
	}
	
	/**
	 * Positive Test case: method isAnyNull gets a Object as input.
	 * The result should be false, because the Object isn't null.
	 */
	@Test
	public void testIsAnyNull_False()
	{
		Object test = new Object();
		boolean actual = Utils.isAnyNull(test);
		
		assertFalse(actual);
		
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets byte array with the lowest byte value as input.
	 * The result should be a new object with the same value of the type short.
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArray_LowestByte()
	{
		byte[] bytearray = new byte[]{(byte) 0x00};
		short actual = Utils.getShortFromUnsignedByteArray(bytearray);
		short expected = 0x00;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets byte array with the highest byte value as input.
	 * The result should be a new object with same value of the type short. 
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArray_HighestByte()
	{
		byte[] bytearray = new byte[]{(byte) 0xFF};
		short actual = Utils.getShortFromUnsignedByteArray(bytearray);
		short expected = 0xFF;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets input of the limit of signed byte.
	 * The result should be a new object of type short.
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArray_SignedByteLimit()
	{
		byte[] bytearray = new byte[]{(byte) 0x7F};
		short actual = Utils.getShortFromUnsignedByteArray(bytearray);
		short expected = 0x7F;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets two byte arrays with the highest value.
	 * The result should be a new object of type short.
	 * 
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArray_TwoBytes()
	{
		byte[] bytearray = new byte[]{(byte) 0xFF,(byte)0xFF};
		short actual = Utils.getShortFromUnsignedByteArray(bytearray);
		short expected = (byte)0xFFFF;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets input, which exceeds the limit of signed byte.
	 * The result should be a new object of type short.
	 * 
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArray_ExceedLimitSignedByte()
	{
		byte[] bytearray = new byte[]{(byte) 0x80};
		short actual = Utils.getShortFromUnsignedByteArray(bytearray);
		short expected = 0x80;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets byte array with the lowest value as input.
	 * The result should be a new object with the same value of type int.
	 */
	@Test
	public void testGetIntFromUnsignedByteArrayByteArray_LowestByte()
	{
		byte[] bytearray = new byte[]{(byte) 0x00};
		int actual = Utils.getIntFromUnsignedByteArray(bytearray);
		int expected = 0x00;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets byte array with the lowest value as input.
	 * The result should be a new object with the value of type int.
	 */ 
	@Test
	public void testGetIntFromUnsignedByteArrayByteArray_TwoBytes()
	{
		byte[] bytearray = new byte[]{(byte) 0xFF,(byte)0xFF};
		int actual = Utils.getIntFromUnsignedByteArray(bytearray);
		int expected = 0xFFFF;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets byte array with the highest value as input.
	 * The result should be a new object of the type int with the same value.
	 */
	@Test
	public void testGetIntFromUnsignedByteArrayByteArray_HighestByte()
	{
		byte[] bytearray = new byte[]{(byte) 0xFF};
		int actual = Utils.getIntFromUnsignedByteArray(bytearray);
		int expected = 0xFF;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets input of limit of signed byte.
	 * The result should be a new object with the same value of type int.
	 */
	@Test
	public void testGetIntFromUnsignedByteArrayByteArray_SingedByteLimit()
	{
		byte[] bytearray = new byte[]{(byte) 0x7F};
		int actual = Utils.getIntFromUnsignedByteArray(bytearray);
		int expected = 0x7F;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets input of the limit of signed byte.
	 * The result should be a new object of type int.
	 */
	@Test
	public void testGetIntFromUnsignedByteArrayByteArray_ExceedLimitSignedByte()
	{
		byte[] bytearray = new byte[]{(byte) 0x80};
		int actual = Utils.getIntFromUnsignedByteArray(bytearray);
		int expected = 0x80;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getBigIntegerFromUnsignedByteArray gets byte array with the highest byte value as input.
	 * The result should be a new object of the type BigInteger.
	 */
	@Test
	public void testGetBigIntegerFromUnsignedByteArrayByteArray_HighestValue()
	{
		byte[] bytearray = new byte[]{(byte) 0xFF};
		long exp = 0xFF;
		BigInteger expected = BigInteger.valueOf(exp);
		
		BigInteger actual = Utils.getBigIntegerFromUnsignedByteArray(bytearray);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getBigIntegerFromUnsignedByteArray gets byte array with the highest byte value as input.
	 * The result should be a new object of the type BigInteger.
	 */
	@Test
	public void testGetBigIntegerFromUnsignedByteArrayByteArray_LowestValue()
	{
		byte[] bytearray = new byte[]{(byte) 0x00};
		long exp = 0x00;
		BigInteger expected = BigInteger.valueOf(exp);
		
		BigInteger actual = Utils.getBigIntegerFromUnsignedByteArray(bytearray);
				
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getBigIntegerFromUnsignedByteArray gets byte array with 2 bytes as input.
	 * The result should be a new object of the type BigInteger.
	 */
	@Test
	public void testGetBigIntegerFromUnsignedByteArrayByteArray_2Bytes() 
	{
		byte[] bytearray = new byte[]{(byte) 0xFF,(byte) 0x7F};
		long exp = 0xFF7F;
		BigInteger expected = BigInteger.valueOf(exp);
		
		BigInteger actual = Utils.getBigIntegerFromUnsignedByteArray(bytearray);
				
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays, 
	 * which are empty and returns a concatenation of them.
	 */
	@Test
	public void testConcatByteArraysByteArrays_EmptyByteArrays()
	{
		byte[] array1 = new byte[]{};
		byte[] array2 = new byte[]{};
		byte[] array3 = new byte[]{};	
		byte[] expected = new byte[]{};

		byte[] actual = Utils.concatByteArrays(array1,array2,array3);
		
		assertNotSame("method returns the same object", array1, actual);
		assertNotSame("method returns the same object",	array2, actual);
		assertNotSame("method returns the same object", array3, actual);
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays with the lowest byte value as input.
	 * The result is the concatenation of these three arrays.
	 */
	@Test
	public void testConcatByteArraysByteArrays_LowestByteArrays()
	{
		byte[] array1 = new byte[]{0x00};
		byte[] array2 = new byte[]{0x00};
		byte[] array3 = new byte[]{0x00};
		byte[] expected = new byte[]{0x00,0x00,0x00};
		
		byte[] actual = Utils.concatByteArrays(array1,array2,array3);
		
		assertArrayEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays with the highest byte value as input.
	 * The result should be the concatenation of them.
	 */
	@Test
	public void testConcatByteArraysByteArrays_HighestByteArrays() 
	{
		byte[] array1 = new byte[]{(byte) 0xFF};
		byte[] array2 = new byte[]{(byte) 0xFF};
		byte[] array3 = new byte[]{(byte) 0xFF};
		byte[] expected = new byte[]{(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};

		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		assertNotSame("method returns the same object", array1, result);
		assertArrayEquals(expected, result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays, the first element has the value null.
	 * The result should be a new object with the 2 byte arrays in the right order without the first element, which is null.
	 */
	@Test
	public void testConcatByteArraysByteArrays_FirstElementIsNull()
	{
		byte[] array1 = null;
		byte[] array2 = new byte[]{(byte) 0x7F};
		byte[] array3 = new byte[]{(byte) 0xFF};
		byte[] expected =  new byte[]{(byte)0x7F,(byte)0xFF};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		assertNotSame("method returns the same object", array2, result);
		assertArrayEquals(expected, result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays, the last element has the value null.
	 * The result should be a new object with the 2 byte arrays in the right order without the second element, which is null.
	 */
	@Test
	public void testConcatByteArraysByteArrays_SecondElementIsNull()
	{
		byte[] array1 =  new byte[]{(byte) 0x7F};
		byte[] array2 = null;
		byte[] array3 = new byte[]{(byte) 0xFF};
		
		byte[] expected =  new byte[]{(byte)0x7F,(byte)0xFF};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		assertNotSame("method returns the same object", array1, result);
		assertArrayEquals(expected, result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays, the last element has the value null.
	 * The result should be a new object with the 2 byte arrays in the right order without the third element, which is null.
	 */
	@Test
	public void testConcatByteArraysByteArrays_ThirdElementIsNull()
	{
		byte[] array1 =  new byte[]{(byte) 0x7F};
		byte[] array2 = new byte[]{(byte) 0xFF};
		byte[] array3 = null;
		
		byte[] expected =  new byte[]{(byte)0x7F,(byte)0xFF};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		assertNotSame("method returns the same object", array1, result);
		assertArrayEquals(expected, result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays, the last element has the value null.
	 * The result should be a new object without the null elements from array1-3.
	 */
	@Test
	public void testConcatByteArraysByteArrays_AllElemtentsAreNull()
	{
		byte[] array1 = null;
		byte[] array2 = null;
		byte[] array3 = null;
		
		byte[] expected =  new byte[]{};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		assertArrayEquals(expected, result);
		assertNotNull("The return object is null",result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays.
	 * The result should be a new object with the three byte arrays in the right order.
	 */
	@Test
	public void testConcatByteArraysByteArrays_EnsureArrayConcatination()
	{
		byte[] array1 = new byte[]{(byte) 0xFF};
		byte[] array2 = new byte[]{(byte) 0x7F};
		byte[] array3 = new byte[]{(byte) 0x00};
		
		byte[] expected = new byte[]{(byte)0xFF,(byte)0x7F, (byte)0x00};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);

		assertArrayEquals("The elements are not in the right order",expected, result);
		assertNotSame("method returns the same object", array1, result);
		
	}
}
