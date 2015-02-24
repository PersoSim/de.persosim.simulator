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
	 * Negative test case: method appendBytes gets trailinbytes as input, which is null;
	 */
	@Test(expected=NullPointerException.class)
	public void testAppendBytes_Trailinbytes_Is_Null()
	{
		byte[] leadingbyte = new byte[]{(byte) 0x65};
		Utils.appendBytes(leadingbyte, null);
	}
	
	//FIXME LSG test method appendBytes completely!
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type long.
	 */
	@Test
	public void testToUnsignedByteArray_Input_Long()
	{
		long test = 1;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x01};
		
		assertArrayEquals(expected, actual);
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type int.
	 */
	@Test
	public void testToUnsignedByteArray_Input_Int()
	{
		int test = 1;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x01};
		
		assertArrayEquals(expected, actual);
		
		
	}
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type short.
	 */
	@Test
	public void testToUnsignedByteArray_Input_Short()
	{
		short test = 1;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x00,(byte) 0x01};
	
		assertArrayEquals(expected, actual);
	}
	
	
	/**
	 * Positive Test case: method toUnsignedByteArray gets input of type byte.
	 */
	@Test
	public void testToUnsignedByteArray_Input_Byte()
	{
		byte test = (byte) 0x01;
		byte[] actual = Utils.toUnsignedByteArray(test);
		byte[] expected = new byte[]{(byte) 0x01};
		
		assertArrayEquals(expected, actual);
	}
	
	/**
	 * Positive Test case: method removeLeadingZeroBytes gets byte array as input.
	 */
	@Test
	public void testRemoveLeadingZeroBytes_Input_Byte_Array()
	{
		byte[] test = new byte[]{(byte) 0x00, (byte) 0x09};
		byte[] actual = Utils.removeLeadingZeroBytes(test);
		 
		byte[] expected = new byte[]{(byte) 0x09};	
		
		assertArrayEquals(expected, actual);
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToShort gets byte value as input.
	 */
	@Test
	public void testMaskUnsignedByteToShort_Input_Byte()
	{
		byte b = (byte) 0x09; //FIXME LSG why change previous value 0xAA to this one? 0xAA is better 
		
		short actual = Utils.maskUnsignedByteToShort(b);
		short expected = 9; 
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method maskUnsignedByteToInt gets byte value as input.
	 */
	@Test
	public void testMaskUnsignedByteToInt_Input_Byte()
	{
		byte b = 0x01;
		int actual = Utils.maskUnsignedByteToInt(b);
		int expected = 1;
		
		assertEquals(expected, actual);
	}
	
	/**
	 * Positive Test case: method maskUnsignedShortToInt gets short value as input.
	 */
	@Test
	public void testMaskUnsignedShortToInt_Input_Short()
	{
		short b = 1;
		int actual  = Utils.maskUnsignedShortToInt(b);
		int expected = 1;
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method concatenate gets 2 different bytes as input.
	 */
	@Test
	public void testConcatenate_Input_Two_Bytes()
	{
		byte a = 0x01,b = 0x02;
		short actual = Utils.concatenate(a, b);
		short expected = 258; //FIXME LSG this is essentially not different to the previous state! I cant immediately see that this is correct
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method getDate gets a dateString as input.
	 */
	@Test
	public void testGetDate_Input_Stringdate() 
	{
		String s = "19990801";
		Date actual = Utils.getDate(s);

		//FIXME LSG why did you remove the comment below without any change to the code?
		//FIXME LSG this looks weird, try asserting the parts instead of building a second object
	 	Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1999);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.DATE, 01);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date date = calendar.getTime();

		assertEquals(date, actual);

	}
	
	/**
	 * Positive Test case: method getDate gets a null string.
	 */
	@Test(expected=NullPointerException.class)
	public void testGetDatedate_InputString_Is_Null()
	{
		byte a = 1;
		String s = null;
		Utils.getDate(s, a);
		
		
	}
	
	/**
	 * Positive Test case: method arrayContainsEqual gets an Object array and an object as input.
	 * FIXME LSG use JavaDoc links where possible in order to not break readability when refactoring
	 * FIXME LSG params and return below don't match actual method
	 * @param String array
	 * @param string value
	 * @return true, if one of the arrays entries equals the given object.
	 */
	@Test
	public void testArrayContainsEqual_Input_Stringarray_And_string()
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
	 * @param value the value
	 * @param base the base
	 * @return the logarithm of the given value to the given base
	 */
	@Test
	public void testLogarithm_Input_Double_and_Base()
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
	 */
	@Test
	public void testBinaryEncode_Input_Byte_Array()
	{
		byte[] test = new byte[]{(byte) 0x01, (byte) 0x01, (byte) 0x01};
		String actual  = Utils.binaryEncode(test);
		String expected = "00000001 00000001 00000001";
		
		assertEquals(expected, actual);
		
	}
	
	/**
	 * Positive Test case: method isAnyNull becomes a null Object as input.
	 */
	@Test
	public void testIsAnyNulltrue_Input_Object()
	{
		Object a = null;
		boolean actual  = Utils.isAnyNull(a);
		
		assertTrue(actual);
	}
	
	/**
	 * Positive Test case: method isAnyNull gets a Object as input.
	 */
	@Test
	public void testIsAnyNullfalse_Input_Object()
	{
		Object a = new Object();
		boolean actual = Utils.isAnyNull(a);
		
		assertFalse(actual);
	}
	
	/**
	 * Positive Test case: method getShortFromUnsignedByteArray gets byte array as input.
	 */
	@Test
	public void testGetShortFromUnsignedByteArray_Input_Byte_Array()
	{
		byte[] test = new byte[]{(byte) 0x01};
		short actual = Utils.getShortFromUnsignedByteArray(test);
		short expected = 1;
		assertEquals(expected, actual);
	}
	
	/**
	 * Positive Test case: method getIntFromUnsignedByteArray gets byte array as input.
	 */
	@Test
	public void testGetIntFromUnsignedByteArray_Input_Byte_Array()
	{
		byte[] test = new byte[]{(byte) 0x01};
		int actual = Utils.getIntFromUnsignedByteArray(test);
		int expected = 1;
		assertEquals(expected, actual);
	}
	
	/**
	 * Positive Test case: method getBigIntegerFromUnsignedByteArray gets byte array as input.
	 */
	@Test
	public void testGetBigIntegerFromUnsignedByteArray_Input_Byte_Array()
	{
		byte[] test = new byte[]{(byte) 0x01};
		BigInteger actual = Utils.getBigIntegerFromUnsignedByteArray(test);
		BigInteger expected = BigInteger.ONE;
		
		assertEquals(expected, actual);
	}
	
}
