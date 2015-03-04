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
	public void testAppendBytes_leadingByteEmpty()
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
	 * The result should be a new object which has the same content as trailingByteArray.
	 */
	@Test
	public void testAppendBytes_TrailingByteEmpty()
	{
		byte[] leadingByteArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		byte[] trailingByteArray = new byte[]{};
		byte[] expectedArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame("method returns the same object", trailingByteArray, result);
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive case: method appendBytes gets an byte array and null as input.
	 * The result should be a new object which has the same content as leadingByteArray.
	 */
	@Test
	public void testAppendBytes_trailingByteArrayNull()
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
	public void testAppendBytes_leadingByteArrayNull()
	{
		byte[] leadingByteArray = null;
		byte[] trailingByteArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		byte[] expectedArray = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame("method returns the same object", leadingByteArray, result);
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive Test case: method appendBytes gets two empty arrays as input.
	 * The result should be a new object, which content is empty.
	 */
	@Test
	public void testAppendBytesleadingByteArrayEmptyAndtrailingByteArrayEmpty()
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
	public void testAppendBytes_leadingByteArrayZero()
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
	public void testAppendBytes_trailingByteArrayZero()
	{
		byte[] leadingByteArray = new byte[]{(byte)0xFF,(byte) 0xFF};
		byte[] trailingByteArray = new byte[]{0x00,0x00};
		byte[] expectedArray = new byte[]{(byte)0xFF,(byte) 0xFF,0x00,0x00};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertArrayEquals(expectedArray, result);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type long.
	 */
	@Test
	public void testToUnsignedByteArrayLongLowestValue()
	{
		long test = 0x00;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type long.
	 */
	@Test
	public void testToUnsignedByteArrayLongHigestValue()
	{
		long test = 0xFF;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0xFF};
		
		assertArrayEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type int.
	 */
	@Test
	public void testToUnsignedByteArrayIntLowestValue()
	{
		int test = 0x00;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type int.
	 */
	@Test
	public void testToUnsignedByteArrayIntHighestValue()
	{
		int test = 0xFF;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0xFF};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type short.
	 */
	@Test
	public void testToUnsignedByteArrayShortHighestValue()
	{
		short test = 0xFF;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0xFF};
	
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type short.
	 */
	@Test
	public void testToUnsignedByteArrayShortLowestValue()
	{
		short test = 0x00;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x00};
	
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayByteHighestValue()
	{
		byte test = (byte) 0xFF;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0xFF};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayByteLowestValue()
	{
		byte test = (byte) 0x00;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x00};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayByteRangeLimit()
	{
		byte test = (byte) 0x7F;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x7F};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type byte.
	 */
	@Test
	public void testToUnsignedByteArrayPassByteRange()
	{
		byte test = (byte) 0x80;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x80};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method removeLeadingZeroBytes gets an byte array as input.
	 */
	@Test
	public void testRemoveLeadingZeroBytesByteArrayZero()
	{
		byte[] test = new byte[]{(byte) 0x00, (byte) 0x00};
		byte[] expected = new byte[]{(byte) 0x00};
		
		byte[] actual = Utils.removeLeadingZeroBytes(test);	 	
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method removeLeadingZeroBytes gets an byte array as input.
	 * The result should be the array without the leading zero bytes.
	 */
	@Test
	public void testRemoveLeadingZeroBytesByteArray()
	{
		byte[] test = new byte[]{(byte) 0x00, (byte) 0xFF};
		byte[] actual = Utils.removeLeadingZeroBytes(test);
		 
		byte[] expected = new byte[]{(byte) 0xFF};	
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToShort gets a byte as input.
	 */
	@Test
	public void testMaskUnsignedByteToShortHighestByte() 
	{
		byte b = (byte) 0xFF;
		short expected = 0xFF;
		
		short actual = Utils.maskUnsignedByteToShort(b);
		
		assertEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method maskUnsignedByteToShort gets a byte as input.
	 */
	@Test
	public void testMaskUnsignedByteToShortByteZero() 
	{
		
		byte b = (byte) 0x00;
		short expected = 0x00;

		short actual = Utils.maskUnsignedByteToShort(b);
		
		assertEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method maskUnsignedByteToShort gets a byte as input.
	 */
	@Test
	public void testMaskUnsignedByteToShortByteByteRangeLimit() 
	{
		byte b = (byte) 0x7F;

		short actual = Utils.maskUnsignedByteToShort(b);;
		short expected = 0x7F;

		assertEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method maskUnsignedByteToShort gets a byte as input.
	 */
	@Test
	public void testMaskUnsignedByteToShortBytePassRange() 
	{
		byte b = (byte) 0x80;
		short expected = 0x80;
		
		short actual = Utils.maskUnsignedByteToShort(b);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets byte value as input.
	 */
	@Test
	public void testMaskUnsignedByteToIntByteHighestValue()
	{
		byte b = (byte)0xFF;
		int expected = 0xFF;
		
		int actual = Utils.maskUnsignedByteToInt(b);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets byte value as input.
	 */
	@Test
	public void testMaskUnsignedByteToIntByteLowestValue()
	{
		byte b = 0x00;
		int expected = 0x00;
		
		int actual = Utils.maskUnsignedByteToInt(b);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets byte value as input.
	 */
	@Test
	public void testMaskUnsignedByteToIntByteRangeLimit()
	{
		byte b = 0x7F;
		int expected = 0x7F;
		
		int actual = Utils.maskUnsignedByteToInt(b);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets byte value as input.
	 */
	@Test
	public void testMaskUnsignedByteToIntBytePassRange()
	{
		byte b = (byte)0x80;
		int expected = 0x80;
		
		int actual = Utils.maskUnsignedByteToInt(b);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedShortToInt gets a short as input.
	 */
	@Test
	public void testMaskUnsignedShortToIntShortHighestValue()
	{
		short b = 0xFF;
		int expected = 0xFF;
		
		int actual  = Utils.maskUnsignedShortToInt(b);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedShortToInt gets a short as input.
	 */
	@Test
	public void testMaskUnsignedShortToIntShortLowestValue()
	{
		short b = 0x00;
		int expected = 0x00;
		
		int actual  = Utils.maskUnsignedShortToInt(b);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatenate gets the highest and lowest byte as input.
	 */
	@Test
	public void testConcatenateHighestAndLowestByte()
	{
		byte a = (byte) 0xFF;
		byte b = (byte) 0x00;
		short expected = (short) 0xFF00;
		
		short actual = Utils.concatenate(a, b);
		
		assertEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatenate gets the lowest and highest byte as input.
	 */
	@Test
	public void testConcatenateLowestAndHighestByte()
	{
		byte a = (byte) 0x00;
		byte b = (byte) 0xFF;
		short expected = (short) 0xFF;
		
		short actual = Utils.concatenate(a, b);
		
		assertEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatenate gets two lowest bytes as input.
	 */
	@Test
	public void testConcatenateTwoLowestBytes()
	{
		byte a = (byte) 0x00;
		byte b = (byte) 0x00;
		short expected = (short) 0x00;
		
		short actual = Utils.concatenate(a, b);
		
		assertEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatenate gets two highest bytes as input.
	 */
	@Test
	public void testConcatenateTwoHighestBytes()
	{
		byte a = (byte) 0xFF;
		byte b = (byte) 0xFF;
		short expected = (short) 0xFFFF;
		
		short actual = Utils.concatenate(a, b);
		
		assertEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatenate gets the two bytes as input.
	 * the result should be a new object which content is the concatenation of the two bytes in the right order.
	 */
	@Test
	public void testConcatenateTwoBytes()
	{
		byte a = (byte) 0x01;
		byte b = (byte) 0x01;
		short expected = (short) 0x101;
		
		short result = Utils.concatenate(a, b);
		
		assertEquals(expected, result);	
		
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
	 	assertEquals("month", 7, calendar.get(Calendar.MONTH));
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
		byte a = 1;
		String s = null;
		Utils.getDate(s, a);	
		
	}
	
	/**
	 * Positive Test case: method arrayContainsEqual gets an Object array and an string value as input.
	 * The result is the boolean value, which depends on if the entries of the array are equal to the object.
	 */
	@Test
	public void testArrayContainsEqualStringArrayAndString()
	{
		String[] a =  new String[2];
		a[1] = "1";
		a[0] = "1";
		String b = "1";
		
		boolean actual  = Utils.arrayContainsEqual(a, b);
		
		assertTrue(actual);
		
	}
	
	/**
	 * Positive Test case: method logarithm gets a double as input and int for the base input.
	 * The result is the the logarithm of the double value with the base of type int.
	 */
	@Test
	public void testLogarithmDoubleAndBase()
	{
		int base = 100;
		double doublevalue = 10;
		double DELTA = 1e-15;
		double actual = Utils.logarithm(doublevalue, base);
		double expected = 0.5;
		
		assertEquals(expected, actual,DELTA);
		
	}
	
	/**
	 * Positive Test case: method binaryEncode gets a byte array as input.
	 * The result should be value in binary.
	 */
	@Test
	public void testBinaryEncodeByteArrayLowestValue()
	{
		byte[] test = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00};
		String actual  = Utils.binaryEncode(test);
		String expected = "00000000 00000000 00000000";
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method binaryEncode gets a byte array as input.
	 * The result should be value in binary.
	 */
	@Test
	public void testBinaryEncodeByteArrayHighestValue()
	{
		byte[] test = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
		String actual  = Utils.binaryEncode(test);
		String expected = "11111111 11111111 11111111";
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method isAnyNull gets a null Object as input.
	 * The result should be the true.
	 */
	@Test
	public void testIsAnyNulltrueObject()
	{
		Object a = null;
		boolean actual  = Utils.isAnyNull(a);
		
		assertTrue(actual);
		
	}
	
	/**
	 * Positive Test case: method isAnyNull gets a Object as input.
	 * The result should be false, because the Object isn't null.
	 */
	@Test
	public void testIsAnyNullfalseObject()
	{
		Object a = new Object();
		boolean actual = Utils.isAnyNull(a);
		
		assertFalse(actual);
		
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets byte array as input.
	 * 
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArrayLowestByte()
	{
		byte[] test = new byte[]{(byte) 0x00};
		short actual = Utils.getShortFromUnsignedByteArray(test);
		short expected = 0x00;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets byte array as input.
	 * 
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArrayHighestByte()
	{
		byte[] test = new byte[]{(byte) 0xFF};
		short actual = Utils.getShortFromUnsignedByteArray(test);
		short expected = 0xFF;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets byte array as input.
	 * 
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArrayByteRangeLimit()
	{
		byte[] test = new byte[]{(byte) 0x7F};
		short actual = Utils.getShortFromUnsignedByteArray(test);
		short expected = 0x7F;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets byte array as input.
	 * 
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArrayPassByteRange()
	{
		byte[] test = new byte[]{(byte) 0x80};
		short actual = Utils.getShortFromUnsignedByteArray(test);
		short expected = 0x80;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets byte array with the lowest value as input.
	 */
	@Test
	public void testGetIntFromUnsignedByteArrayByteArrayLowestByte()
	{
		byte[] test = new byte[]{(byte) 0x00};
		int actual = Utils.getIntFromUnsignedByteArray(test);
		int expected = 0x00;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets byte array with the highest value as input.
	 */
	@Test
	public void testGetIntFromUnsignedByteArrayByteArrayHighestByte()
	{
		byte[] test = new byte[]{(byte) 0xFF};
		int actual = Utils.getIntFromUnsignedByteArray(test);
		int expected = 0xFF;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets byte array with the  as input.
	 */
	@Test
	public void testGetIntFromUnsignedByteArrayByteArrayByteRangeLimit()
	{
		byte[] test = new byte[]{(byte) 0x7F};
		int actual = Utils.getIntFromUnsignedByteArray(test);
		int expected = 0x7F;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets byte array as input.
	 */
	@Test
	public void testGetIntFromUnsignedByteArrayByteArrayPassByteRange()
	{
		byte[] test = new byte[]{(byte) 0x80};
		int actual = Utils.getIntFromUnsignedByteArray(test);
		int expected = 0x80;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getBigIntegerFromUnsignedByteArray gets byte array as input.
	 */
	@Test
	public void testGetBigIntegerFromUnsignedByteArrayByteArrayHighestValue()
	{
		byte[] test = new byte[]{(byte) 0xFF};
		long exp = 0xFF;
		BigInteger expected = BigInteger.valueOf(exp);
		
		BigInteger actual = Utils.getBigIntegerFromUnsignedByteArray(test);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getBigIntegerFromUnsignedByteArray gets byte array as input.
	 */
	@Test
	public void testGetBigIntegerFromUnsignedByteArrayByteArrayLowestValue()
	{
		byte[] test = new byte[]{(byte) 0x00};
		long exp = 0x00;
		BigInteger expected = BigInteger.valueOf(exp);
		
		BigInteger actual = Utils.getBigIntegerFromUnsignedByteArray(test);
				
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays, 
	 * which are empty and returns a concatenation of them.
	 */
	@Test
	public void testConcatByteArraysEmptyByteArrays()
	{
		byte[] array1 = new byte[]{};
		byte[] array2 = new byte[]{};
		byte[] array3 = new byte[]{};	
		byte[] expected = new byte[]{};

		byte[] actual = Utils.concatByteArrays(array1,array2,array3);
	
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays as input.
	 * The result is the concatenation of these three arrays.
	 */
	@Test
	public void testConcatByteArraysLowestByteArrays()
	{
		byte[] array1 = new byte[]{0x00};
		byte[] array2 = new byte[]{0x00};
		byte[] array3 = new byte[]{0x00};
		byte[] expected = new byte[]{0x00,0x00,0x00};
		
		byte[] actual = Utils.concatByteArrays(array1,array2,array3);
		
		assertArrayEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays as input.
	 * The result should be the concatenation of them.
	 */
	@Test
	public void testConcatByteArraysHighestByteArrays() 
	{
		byte[] array1 = new byte[]{(byte) 0xFF};
		byte[] array2 = new byte[]{(byte) 0xFF};
		byte[] array3 = new byte[]{(byte) 0xFF};
		byte[] expected = new byte[]{(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};

		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		assertArrayEquals(expected, result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays, the first element has the value null.
	 * The result should be a new object with the 2 byte arrays in the right order without the null element.
	 */
	@Test
	public void testConcatByteArraysNull()
	{
		byte[] array1 = null;
		byte[] array2 = new byte[]{(byte) 0xFF};
		byte[] array3 = new byte[]{(byte) 0xFF};
		byte[] expected =  new byte[]{(byte)0xFF,(byte)0xFF};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		assertArrayEquals(expected, result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three byte arrays, the last element has the value null.
	 * The result should be a new object with the 2 byte arrays in the right order without the null element.
	 */
	@Test
	public void testConcatByteArrays_2ndNull()
	{
		byte[] array1 =  new byte[]{(byte) 0xFF};
		byte[] array3 = new byte[]{(byte) 0xFF};
		byte[] array2 = null;
		byte[] expected =  new byte[]{(byte)0xFF,(byte)0xFF};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		assertArrayEquals(expected, result);
		
	}
}
