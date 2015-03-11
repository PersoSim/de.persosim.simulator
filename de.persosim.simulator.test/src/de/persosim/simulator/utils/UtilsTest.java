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
	 * Positive Test case: method appendBytes gets two arrays. The first one is empty.
	 * The result should be a new object which has the same content as trailingByteArray.
	 */
	@Test
	public void testAppendBytes_LeadingByteEmpty()
	{
		byte[] leadingByteArray = new byte[]{};
		byte[] trailingByteArray = new byte[]{(byte) 0x01,(byte) 0x02,(byte) 0x03};
		byte[] expectedArray = new byte[]{(byte) 0x01,(byte) 0x02,(byte) 0x03};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame(trailingByteArray, result);
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive Test case: method appendBytes gets two arrays. The second one is empty.
	 * The result should be a new object which has the same content as leadingByteArray.
	 */
	@Test
	public void testAppendBytes_TrailingByteEmpty()
	{
		byte[] leadingByteArray = new byte[]{(byte) 0x01,(byte) 0x02,(byte) 0x03};
		byte[] trailingByteArray = new byte[]{};
		byte[] expectedArray = new byte[]{(byte) 0x01,(byte) 0x02,(byte) 0x03};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame(leadingByteArray, result);
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive case: method appendBytes gets two arrays. The second one is null.
	 * The result should be a new object which has the same content as leadingByteArray.
	 */
	@Test
	public void testAppendBytes_TrailingByteArrayNull()
	{
		byte[] leadingByteArray = new byte[]{(byte) 0x01,(byte) 0x02,(byte) 0x03};
		byte[] trailingByteArray = null;
		byte[] expectedArray = new byte[]{(byte) 0x01,(byte) 0x02,(byte) 0x03};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame(leadingByteArray, result);
		assertArrayEquals(expectedArray, result);
		
	}
	
	/**
	 * Positive case: method appendBytes gets two arrays. The first one null.
	 * The result should be a new object which has the same content as the trailingByteArray.
	 */
	@Test
	public void testAppendBytes_LeadingByteArrayNull()
	{
		byte[] leadingByteArray = null;
		byte[] trailingByteArray = new byte[]{(byte) 0x01,(byte) 0x02,(byte) 0x03};
		byte[] expectedArray = new byte[]{(byte) 0x01,(byte) 0x02,(byte) 0x03};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame(trailingByteArray, result);
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive Test case: method appendBytes gets two empty arrays.
	 * The result should be a new object which content is empty.
	 */
	@Test
	public void testAppendBytes_EmptyArrays()
	{
		byte[] leadingByteArray = new byte[]{};
		byte[] trailingByteArray = new byte[]{};
		byte[] expectedArray = new byte[]{};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertNotSame(leadingByteArray, result);
		assertNotSame(trailingByteArray, result);
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive Test case: method appendBytes gets two arrays.
	 * The result should be a new object which doens't skip the zeros and has the content 
	 * of the leadingByteArray and trailingByteArray in the right order.
	 */
	@Test
	public void testAppendBytes_LeadingByteArrayZero()
	{
		byte[] leadingByteArray = new byte[]{0x00,0x00};
		byte[] trailingByteArray = new byte[]{(byte)0x01,(byte) 0x02};
		byte[] expectedArray = new byte[]{0x00,0x00,(byte)0x01,(byte) 0x02};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertArrayEquals(expectedArray, result);	
		
	}
	
	/**
	 * Positive Test case: method appendBytes gets two arrays.
	 * The result should be a new object which doesn't ignore the zeros on the end 
	 * and has the content of the leadingByteArray and trailingBytArray in the right order.
	 */
	@Test
	public void testAppendBytes_TrailingByteArrayZero()
	{
		byte[] leadingByteArray = new byte[]{(byte) 0x01,(byte) 0x02};
		byte[] trailingByteArray = new byte[]{0x00,0x00};
		byte[] expectedArray = new byte[]{(byte)0x01,(byte) 0x02,0x00,0x00};
		
		byte[] result = Utils.appendBytes(leadingByteArray, trailingByteArray);
		
		assertArrayEquals(expectedArray, result);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets long value with leading zero bytes.
	 * The result should be a new array object with the same content in the right order.
	 */
	@Test
	public void testToUnsignedByteArrayLong_LeadingBytesZero()
	{
		byte[] actual = Utils.toUnsignedByteArray((long) 0x0011223344556677L);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x11,(byte) 0x22,(byte) 0x33,(byte) 0x44,(byte) 0x55,(byte) 0x66,(byte) 0x77};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets long value with the most significant bit.
	 * The result should be a new array object with the same content in the right order.
	 */
	@Test
	public void testToUnsignedByteArrayLong_HighestBit()
	{
		byte[] actual = Utils.toUnsignedByteArray((long) 0x1011223344556617L);
		byte[] expected = new byte[]{(byte) 0x10,(byte) 0x11,(byte) 0x22,(byte) 0x33,(byte) 0x44,(byte) 0x55,(byte) 0x66,(byte) 0x17};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets integer value with leading zero bytes.
	 * The result should be a new array object with the same content in the right order.
	 */
	@Test
	public void testToUnsignedByteArrayInt_LeadingBytesZero()
	{
		byte[] actual = Utils.toUnsignedByteArray((int) 0x00112233);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x11,(byte) 0x22,(byte) 0x33};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets integer value with the most significant bit.
	 * The result should be a new array object with the same content in the right order.
	 */
	@Test
	public void testToUnsignedByteArrayInt_HighestBit()
	{
		byte[] actual = Utils.toUnsignedByteArray((int) 0x10112213);
		byte[] expected = new byte[]{(byte) 0x10,(byte) 0x11,(byte) 0x22,(byte) 0x13};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets short value with leading zero bytes.
	 * The result should be a new array object with the same content in the right order.
	 */
	@Test
	public void testToUnsignedByteArrayShort_LeadingBytesZero()
	{
		byte[] actual = Utils.toUnsignedByteArray((short) 0x0022);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x22};
	
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets short value with the most significant bit.
	 * The result should be a new array object with the same content in the right order.
	 */
	@Test
	public void testToUnsignedByteArrayShort_HighestBit()
	{
		byte[] actual = Utils.toUnsignedByteArray((short) 0x1012);
		byte[] expected = new byte[]{(byte) 0x10,(byte) 0x12};
	
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets highest byte value.
	 * The result should be a new array object.
	 */
	@Test
	public void testToUnsignedByteArrayByte_HighestValue()
	{
		byte[] actual = Utils.toUnsignedByteArray((byte) 0xFF);
		byte[] expected = new byte[]{(byte) 0xFF};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets the lowest byte value.
	 * The result should be a new array object.
	 */
	@Test
	public void testToUnsignedByteArrayByte_LowestValue()
	{
		byte[] actual = Utils.toUnsignedByteArray((byte) 0x00);
		byte[] expected = new byte[]{(byte)0x00};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets byte value.
	 * The result should be a new array object.
	 */
	@Test
	public void testToUnsignedByteArrayByte_SignedByteLimit()
	{
		byte[] actual = Utils.toUnsignedByteArray((byte) 0x7F);
		byte[] expected = new byte[]{(byte) 0x7F};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input which exceed the limit of signed byte.
	 * The result should be a byte value.
	 */
	@Test
	public void testToUnsignedByteArrayByte_ExceedLimitSignedByte()
	{
		byte[] actual = Utils.toUnsignedByteArray((byte) 0x80);
		byte[] expected = new byte[]{(byte) 0x80};
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method removeLeadingZeroBytes gets array.
	 * The result should be the array without the leading zero bytes.
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
	 * Positive Test case: method removeLeadingZeroBytes gets array.
	 * The result should be the array without the leading zero bytes.
	 */
	@Test
	public void testRemoveLeadingZeroBytesByteArray_EnsureOrder()
	{
		byte[] bytearray = new byte[]{(byte) 0x00, (byte) 0x01, (byte) 0x10,(byte) 0x02};
		byte[] actual = Utils.removeLeadingZeroBytes(bytearray);
		 
		byte[] expected = new byte[]{(byte) 0x01, (byte) 0x10,(byte) 0x02};	
		
		assertArrayEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToShort gets byte with the highest value as input.
	 * The result should be a short value.
	 */
	@Test
	public void testMaskUnsignedByteToShortByte_HighestByte() 
	{
		short actual = Utils.maskUnsignedByteToShort((byte) 0xFF);
		short expected = 0xFF;
		
		assertEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method maskUnsignedByteToShort gets a byte with the lowest value as input.
	 * The result should be a short value.
	 */
	@Test
	public void testMaskUnsignedByteToShortByte_LowestValue() 
	{
		short expected = 0x00;
		short actual = Utils.maskUnsignedByteToShort((byte) 0x00);
		
		assertEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method maskUnsignedByteToShort gets input of the limit of signed byte.
	 * The result should be a short value.
	 */
	@Test
	public void testMaskUnsignedByteToShortByte_SignedByteLimit() 
	{
		short actual = Utils.maskUnsignedByteToShort((byte) 0x7F);
		short expected = 0x7F;

		assertEquals(expected, actual);
		
	}

	/**
	 * Positive Test case: method maskUnsignedByteToShort gets input, which exceeds the limit of signed byte.
	 * The result should be a short value.
	 */
	@Test
	public void testMaskUnsignedByteToShortByte_ExceedLimitSignedByte() 
	{
		short actual = Utils.maskUnsignedByteToShort((byte) 0x80);
		short expected = 0x80;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets the highest byte as input.
	 * The result should be a integer value.
	 */
	@Test
	public void testMaskUnsignedByteToIntByte_HighestValue()
	{
		int actual = Utils.maskUnsignedByteToInt((byte) 0xFF);
		int expected = 0xFF;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets the lowest byte as input.
	 * The result should be a integer value.
	 */
	@Test
	public void testMaskUnsignedByteToIntByte_LowestValue()
	{
		int actual = Utils.maskUnsignedByteToInt((byte) 0x00);
		int expected = 0x00;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets input of the limit of signed byte.
	 * The result should be a integer value.
	 */
	@Test
	public void testMaskUnsignedByteToIntByte_SignedByteLimit()
	{
		int actual = Utils.maskUnsignedByteToInt((byte) 0x7F);
		int expected = 0x7F;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets input, which exceeds the limit of signed byte.
	 * The result should be a integer value.
	 */
	@Test
	public void testMaskUnsignedByteToIntByte_ExceedLimitSignedByte()
	{
		int actual = Utils.maskUnsignedByteToInt((byte) 0x80);
		int expected = 0x80;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedShortToInt gets input with leading zero bytes.
	 * The result should be a integer value.
	 */
	@Test
	public void testMaskUnsignedShortToIntShort_LeadingZeroBytes()
	{
		int actual  = Utils.maskUnsignedShortToInt((short) 0x0002);
		int expected = 0x0002;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedShortToInt gets input with the with the most significant bit.
	 * The result should be a integer value.
	 */
	@Test
	public void testMaskUnsignedShortToIntShort_HighestBit()
	{
		int actual  = Utils.maskUnsignedShortToInt((short) 0x1001);
		int expected = 0x1001;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatenate gets the highest and lowest byte as input.
	 * The result should be the concatenation of these two bytes.
	 */
	@Test
	public void testConcatenateByte_HighestAndLowestByte()
	{
		short actual = Utils.concatenate((byte) 0xFF, (byte) 0x00);
		short expected = (short) 0xFF00;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatenate gets the lowest and highest byte as input.
	 * The result should be the concatenation of these two bytes.
	 */
	@Test
	public void testConcatenateByte_LowestAndHighestByte()
	{ 
		short actual = Utils.concatenate((byte) 0x00, (byte) 0xFF);
		short expected = (short) 0xFF;
		
		assertEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatenate gets two lowest bytes as input.
	 * The result should be the concatenation of these two bytes.
	 */
	@Test
	public void testConcatenateByte_TwoLowestBytes()
	{
		short actual = Utils.concatenate((byte) 0x00, (byte) 0x00);
		short expected = (short) 0x00;
		
		assertEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatenate gets two highest bytes as input.
	 * The result should be the concatenation of these two bytes.
	 */
	@Test
	public void testConcatenateByte_TwoHighestBytes()
	{
		short actual = Utils.concatenate((byte) 0xFF, (byte) 0xFF);
		short expected = (short) 0xFFFF;
		
		assertEquals(expected, actual);	
		
	}
	
	/**
	 * Positive Test case: method concatenate gets the two bytes as input.
	 * The result should is the concatenation of the two bytes in the right order.
	 */
	@Test
	public void testConcatenateByte_TwoBytes()
	{
		short result = Utils.concatenate((byte) 0x01, (byte) 0x01);
		short expected = (short) 0x101;
		
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
	 * Positive Test case: method logarithm gets two values.
	 * The result is the logarithm of these values.
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
	 * Positive Test case: method getShortFromUnsignedByteArray gets byte array with the highest byte value as input.
	 * The result should be a new object with same value of the type short. 
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArray_LeadingByteZero()
	{
		byte[] bytearray = new byte[]{(byte) 0x00,(byte) 0x01, (byte) 0x02};
		short actual = Utils.getShortFromUnsignedByteArray(bytearray);
		short expected = 0x0102;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets byte array with the highest byte value as input.
	 * The result should be a new object with same value of the type short. 
	 */
	@Test
	public void testGetShortFromUnsignedByteArrayByteArray_HighestBit()
	{
		byte[] bytearray = new byte[]{(byte) 0xFF};
		short actual = Utils.getShortFromUnsignedByteArray(bytearray);
		short expected = 0xFF;

		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets array.
	 * The result should be a integer value.
	 */
	@Test
	public void testGetIntFromUnsignedByteArrayByteArray_LeadingByteZero()
	{
		byte[] bytearray = new byte[]{(byte) 0x00, (byte) 0x02, (byte) 0x03};
		int actual = Utils.getIntFromUnsignedByteArray(bytearray);
		int expected = 0x000203;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets array.
	 * The result should be a integer value.
	 */
	@Test
	public void testGetIntFromUnsignedByteArrayByteArray_HighestBit()
	{
		byte[] bytearray = new byte[]{(byte) 0x10, (byte) 0x02, (byte) 0x03};
		int actual = Utils.getIntFromUnsignedByteArray(bytearray);
		int expected = 0x100203;
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getBigIntegerFromUnsignedByteArray gets array with the highest byte value as input.
	 * The result should be a new object of the type BigInteger.
	 */
	@Test
	public void testGetBigIntegerFromUnsignedByteArrayByteArray_LeadingBytesZero()
	{
		byte[] bytearray = new byte[]{0x00, 0x01, 0x02};
		long exp = 0x000102;
		BigInteger expected = BigInteger.valueOf(exp);
		
		BigInteger actual = Utils.getBigIntegerFromUnsignedByteArray(bytearray);
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getBigIntegerFromUnsignedByteArray gets array.
	 * The result is a BigInteger value.
	 */
	@Test
	public void testGetBigIntegerFromUnsignedByteArrayByteArray_2Bytes() 
	{
		byte[] bytearray = new byte[]{(byte) 0x01,(byte) 0x02};
		long exp = 0x0102;
		BigInteger expected = BigInteger.valueOf(exp);
		
		BigInteger actual = Utils.getBigIntegerFromUnsignedByteArray(bytearray);
				
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three arrays, 
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
		
		assertNotSame(array1, actual);
		assertNotSame(array2, actual);
		assertNotSame(array3, actual);
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
	 * Positive Test case: method concatByteArrays gets three arrays.
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
		
		assertArrayEquals(expected, result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three arrays, the first element has the value null.
	 * The result should be a new object with the 2 arrays in the right order without the first element, which is null.
	 */
	@Test
	public void testConcatByteArraysByteArrays_FirstElementIsNull()
	{
		byte[] array1 = null;
		byte[] array2 = new byte[]{(byte) 0x01};
		byte[] array3 = new byte[]{(byte) 0x02};
		byte[] expected =  new byte[]{(byte)0x01,(byte)0x02};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		assertArrayEquals(expected, result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three arrays, the second element has the value null.
	 * The result should be a new object with the two arrays in the right order without the second element, which is null.
	 */
	@Test
	public void testConcatByteArraysByteArrays_SecondElementIsNull()
	{
		byte[] array1 =  new byte[]{(byte) 0x01};
		byte[] array2 = null;
		byte[] array3 = new byte[]{(byte) 0x02};
		
		byte[] expected =  new byte[]{(byte)0x01,(byte)0x02};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		
		assertArrayEquals(expected, result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three arrays, the last element has the value null.
	 * The result should be a new object with two arrays in the right order without the third element, which is null.
	 */
	@Test
	public void testConcatByteArraysByteArrays_ThirdElementIsNull()
	{
		byte[] array1 =  new byte[]{(byte) 0x01};
		byte[] array2 = new byte[]{(byte) 0x02};
		byte[] array3 = null;
		
		byte[] expected =  new byte[]{(byte)0x01,(byte)0x02};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);
		
		assertArrayEquals(expected, result);
		
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three arrays, all elements are null.
	 * The result should be a new object without the null elements of all three arrays.
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
	}
	
	/**
	 * Positive Test case: method concatByteArrays gets three arrays.
	 * The result should be a new object with the three byte arrays in the right order.
	 */
	@Test
	public void testConcatByteArraysByteArrays_EnsureArrayConcatination()
	{
		byte[] array1 = new byte[]{(byte) 0x01};
		byte[] array2 = new byte[]{(byte) 0x02};
		byte[] array3 = new byte[]{(byte) 0x10};
		
		byte[] expected = new byte[]{(byte)0x01,(byte)0x02, (byte)0x10};
		
		byte[] result = Utils.concatByteArrays(array1,array2,array3);

		assertArrayEquals(expected, result);
	}
}
