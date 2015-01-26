package de.persosim.simulator.utils;

import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * 
 * @author slutters
 *
 */
public abstract class Utils {
		
	public static final byte[] BITMASK            = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80};
	public static final byte[] BITMASK_COMPLEMENT = new byte[]{(byte) 0xFE, (byte) 0xFD, (byte) 0xFB, (byte) 0xF7, (byte) 0xEF, (byte) 0xDF, (byte) 0xBF, (byte) 0x7F};
	
	public static final short MASK_BYTE_TO_SHORT = (short) 0x00FF;
	public static final int MASK_BYTE_TO_INT = (short) 0x000000FF;
	public static final int MASK_SHORT_TO_INT = 0x0000FFFF;
	
	public static final byte DATE_SET_MIN_VALUE = (byte) -1;
	public static final byte DATE_NO_CHECKS = (byte) 0;
	public static final byte DATE_SET_MAX_VALUE = (byte) 1;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns an unsigned byte masked to short
	 * @param byteValue the byte to be masked to short
	 * @return the unsigned byte masked to short
	 */
	public static short maskUnsignedByteToShort(byte byteValue) {
		return (short) (byteValue & MASK_BYTE_TO_SHORT);
	}
	
	/**
	 * Returns an unsigned byte masked to int
	 * @param byteValue the byte to be masked to int
	 * @return the unsigned byte masked to int
	 */
	public static int maskUnsignedByteToInt(byte byteValue) {
		return (int) (byteValue & MASK_BYTE_TO_INT);
	}
	
	/**
	 * Returns an unsigned short masked to int
	 * @param shortValue the short to be masked to int
	 * @return the unsigned short masked to int
	 */
	public static int maskUnsignedShortToInt(short shortValue) {
		return (int) (shortValue & MASK_SHORT_TO_INT);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns a short resulting from concatenating two bytes
	 * @param byte1 the leading byte
	 * @param byte2 the trailing byte
	 * @return a short resulting from concatenating two bytes
	 */
	public static short concatenate(byte byte1, byte byte2) {
		short concatenation;
		
		concatenation = maskUnsignedByteToShort(byte1);
		concatenation <<= 8;
		concatenation |= maskUnsignedByteToShort(byte2);
		
		return concatenation;
	}
	
	/**
	 * Returns whether an array contains duplicate elements.
	 * Array objects must implement Comparable interface.
	 * @param c the array to be checked
	 * @return true if at least one occurrence of duplicate elements has been found, false otherwise
	 */
	public static <T extends Comparable<T>>boolean containsDuplicateElements(T[] c) {
		if(c == null) {
			throw new ClassCastException();
		}
		
		if(c.length < 2) {
			return false;
		}
		
		for(int i = 0; i < (c.length - 1); i++) {
			for(int j = i + 1; j < c.length; j++) {
				if(c[i].compareTo(c[j]) == 0) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*
	 * c1 master
	 * c2 compared to master
	 * 
	 * -1 - c2 contains less than all of c1's elements
	 *  0 - c2 contains all of c1's elements
	 * +1 - c2 contains more than all of c1's elements
	 * 
	 */
	public static <T extends Comparable<T>>byte compareSets(T[] c1, T[] c2) {
		boolean match;
		
		if((c1 == null) || (c2 == null)) {
			throw new ClassCastException();
		}
		
		if(c1.length == 0) {
			if(c2.length == 0) {
				return 0;
			} else{
				return 1;
			}
		} else{
			if(c2.length == 0) {
				return -1;
			}
		}
		
		if(c1.length > c2.length) {
			return -1;
		}
		
		for(byte i = 0; i < c1.length; i++) {
			match = false;
			
			for(byte j = 0; j < c2.length; j++) {
				if(c1[i].compareTo(c2[j]) == 0) {
					match = true;
					break;
				}
			}
			
			if(!match) {
				return -1;
			}
		}
		
		if(c1.length < c2.length) {
			return 1;
		} else{
			return 0;
		}
	}
	
	/**
	 * Returns a concatenation of one or more byte arrays
	 * @param byteArrays one or more byte arrays
	 * @return a concatenation of one or more byte arrays
	 */
	public static byte[] concatByteArrays(byte[]... byteArrays) {
		if ( byteArrays == null || byteArrays.length == 0 ) {throw new IllegalArgumentException( "parameters must not be null or empty" );}
		
		ByteArrayOutputStream outputStream;
		
		outputStream = new ByteArrayOutputStream();
		
		for(byte[] currentByteArray : byteArrays) {
			try {
				outputStream.write(currentByteArray);
			} catch (IOException e) {
				logException(Utils.class, e);
			}
		}
		
		return outputStream.toByteArray();
	}
	
	/**
	 * Returns a byte array that has been appended by the provided bytes
	 * @param leadingByteArray leading byte array
	 * @param trailingBytes one or more trailing bytes
	 * @return a byte array that has been appended by the provided bytes
	 */
	public static byte[] appendBytes(byte[] leadingByteArray, byte... trailingBytes) {
		if ( trailingBytes == null || trailingBytes.length == 0 ) {throw new IllegalArgumentException( "parameters must not be null or empty" );}
		
		ByteArrayOutputStream outputStream;
		
		outputStream = new ByteArrayOutputStream();
		
		try {
			outputStream.write(leadingByteArray);
		} catch (IOException e) {
			logException(Utils.class, e);
		}
		
		for(byte currentByte : trailingBytes) {
			outputStream.write(currentByte);
		}
		
		return outputStream.toByteArray();
	}
	
	/**
	 * Returns a byte array converted to a boolean array in binary representation
	 * @param in the byte array to be converted
	 * @return a byte array converted to a boolean array in binary representation
	 */
	public static boolean[] binaryEncodeByteArray(byte[] in) {
		boolean[] out;
		byte bitMask;
		
		out = new boolean[8 * in.length];

	    /* process every bit */
	    for (int i = 0; i < (8 * in.length); i++) {
	    	bitMask = BITMASK[7 - (i % 8)];
	    	out[i] = (in[i / 8] & bitMask) == bitMask;
	    }
	    
	    return out;
	}
	
	public static String binaryEncode(byte[] in) {
		boolean[] out;
		StringBuilder sb;
		
		out = binaryEncodeByteArray(in);
		
		sb = new StringBuilder(out.length + (in.length - 1));
		for(int i = 0; i < out.length; i++) {
			/* separate bytes by white space */
			if((i > 0) && (i % 8 == 0)) {
				sb.append(" ");
			}
			
			if(out[i]) {
				sb.append("1");
			} else{
				sb.append("0");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns the logarithm of the given value to the given base
	 * @param value the value
	 * @param base the base
	 * @return the logarithm of the given value to the given base
	 */
	public static double logarithm(double value, int base) {
		return Math.log10(value)/Math.log10(base);
	}
	
	/**
	 * Returns the unsigned absolute value of minimum 1 byte length.
	 * 
	 * In general the same as BigInteger's toByteArary():byte[]
	 * with the following differences:
	 * - result is unsigned
	 * - BigInteger.ZERO --> new byte[]{(byte) 0x00}
	 * 
	 * @param bigInt signed value
	 * @return unsigned absolute value of minimum 1 byte length
	 */
	public static byte[] toUnsignedByteArray(BigInteger bigInt) {
		if(bigInt == null) {throw new NullPointerException();}
		
		byte[] resultTMP, resultFINAL;
		BigInteger bigIntTmp;
		
		/* reset leading sign bit (if present), i.e. compute absolute value */
		bigIntTmp = bigInt.abs();
		
		if(bigIntTmp.compareTo(BigInteger.ZERO) == 0) {
			resultTMP = new byte[]{(byte) 0x00};
		} else{
			resultTMP = bigIntTmp.toByteArray();
		}
		
		/*
		 * Crop leading empty byte previously used for sign (if present).
		 * Leading sign byte is 0x00 due to taking absolute value previously.
		 */
		if((resultTMP[0] == (byte) 0x00) && (resultTMP.length > 1)) {
			resultFINAL = new byte[resultTMP.length - 1];
			System.arraycopy(resultTMP, 1, resultFINAL, 0, resultFINAL.length);
		} else{
			resultFINAL = resultTMP;
		}
		
		return resultFINAL;
	}
	
	/**
	 * Returns an unsigned byte array representation of an unsigned long.
	 * Returned Array has length 8; unused bytes are padded to 0x00.
	 * @param input the long
	 * @return the unsigned byte array representation of the unsigned long
	 */
	public static byte[] toUnsignedByteArray(long input) {
		return new byte[]{
				(byte) ((input & 0xFF00000000000000L) >> 56),
				(byte) ((input & 0x00FF000000000000L) >> 48),
				(byte) ((input & 0x0000FF0000000000L) >> 50),
				(byte) ((input & 0x000000FF00000000L) >> 32),
				(byte) ((input & 0x00000000FF000000L) >> 24),
				(byte) ((input & 0x0000000000FF0000L) >> 16),
				(byte) ((input & 0x000000000000FF00L) >> 8),
				(byte) (input & 0x00000000000000FFL)};
	}
	
	/**
	 * Returns an unsigned byte array representation of an unsigned int.
	 * Returned Array has length 4; unused bytes are padded to 0x00.
	 * @param input the int
	 * @return the unsigned byte array representation of the unsigned int
	 */
	public static byte[] toUnsignedByteArray(int input) {
		return new byte[]{(byte) ((input & 0xFF000000) >> 24), (byte) ((input & 0x00FF0000) >> 16), (byte) ((input & 0x0000FF00) >> 8), (byte) (input & 0x000000FF)};
	}
	
	/**
	 * Returns an unsigned byte array representation of an unsigned short
	 * Returned Array has length 2; unused bytes are padded to 0x00.
	 * @param input the short
	 * @return the unsigned byte array representation of the unsigned short
	 */
	public static byte[] toUnsignedByteArray(short input) {
		return new byte[]{(byte) ((input & (short) 0xFF00) >> 8), (byte) (input & (short) 0x00FF)};
	}
	
	/**
	 * Returns an unsigned byte array representation of an unsigned byte
	 * Returned Array has length 1; unused bytes are padded to 0x00.
	 * @param number the byte
	 * @return the unsigned byte array representation of the unsigned byte
	 */
	public static byte[] toUnsignedByteArray(byte number) {
		return new byte[]{number};
	}
	
	/**
	 * Returns a byte array with leading 0x00 bytes removed.
	 * If the input byte array is all 0x00, all will be removed except for one.
	 * @param input the byte array to be cropped
	 * @return the byte array with leading 0x00 bytes removed.
	 */
	public static byte[] removeLeadingZeroBytes(byte[] input) {
		int index;
		
		for(index = 0; index < input.length; index++) {
			if(input[index] != (byte) 0x00) {
				break;
			}
		}
		
		if(index == input.length) {
			/* if input is all 0x00 */
			index--;
		}
		
		return Arrays.copyOfRange(input, index, input.length);
	}
	
	/**
	 * Returns an unsigned int representation of the provided byte array. The
	 * byte value is interpreted as being unsigned. This method works for
	 * integers up to 0xFFFFFFFF.
	 * 
	 * @param value
	 * @param maxValue
	 * @return
	 */
	private static int getDataTypeFromUnsignedByteArray(byte[] value, long maxValue) {
		if(value == null) {throw new IllegalArgumentException("value must not be null");}
		if(value.length < 1) {throw new IllegalArgumentException("value must have byte length > 0");}
		
		BigInteger bigInt;
		
		bigInt = new BigInteger(1, value);
		
		if (bigInt.compareTo(new BigInteger("" +maxValue)) > 0){
			throw new IllegalArgumentException("value too big for signed data type");
		}
		
		return bigInt.intValue();
	}
	
	/**
	 * Constructs a {@link BigInteger} from an unsigned byte array.
	 * @param unsigned
	 *            the byte array to be treated as unsigned
	 * @return the {@link BigInteger} created from a concatenation of a 0x00
	 *         byte and the input
	 */
	public static BigInteger getBigIntegerFromUnsignedByteArray(byte[] unsigned) {
		return new BigInteger(concatByteArrays(new byte[] { 0 }, unsigned));
	}
	
	public static int getIntFromUnsignedByteArray(byte[] value) {
		return (int) getDataTypeFromUnsignedByteArray(value, 0x0FFFFFFFFl);
	}
	
	public static short getShortFromUnsignedByteArray(byte[] value) {
		return (short) getDataTypeFromUnsignedByteArray(value, 0x0FFFFl);
	}

	/**
	 * Check if any of the given objects is null.
	 * 
	 * @param toTest
	 * @return true, iff one of the given objects is null
	 */
	public static boolean isAnyNull(Object ...toTest) {
		for(Object o : toTest){
			if (o == null){
				return true;
			}
		}
		return false;
	}

	/**
	 * This method compares all entries of the given array with the given
	 * object.
	 * 
	 * @param array
	 * @param object
	 * @return true, iff one of the arrays entries {@link #equals(Object)} the
	 *         given object.
	 */
	public static boolean arrayContainsEqual(Object[] array, Object object) {
		for (Object current : array) {
			if (current.equals(object)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean arrayHasPrefix(byte [] data, byte [] prefix){
		if (data.length < prefix.length){
			return false;
		}
		for (int i = 0; i < prefix.length; i++){
			if (data [i] != prefix[i]){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method creates a {@link Date} object from a {@link String} representation with respect to year, month and day.
	 * The provided String is expected to be exactly 8 characters long and encoded as follows: YYYYMMDD.
	 * If the String parts for month or day contain non-numeric characters a NumberFormatException will be thrown
	 * Well formatted date strings will not be checked for validity, e.g. december 34th would not be discarded.
	 * @param dateString the date encoded as follows: YYYYMMDD
	 * @return a {@link Date} object
	 */
	public static Date getDate(String dateString) {
		return getDate(dateString, (byte) 0);
	}
	
	/**
	 * This method creates a {@link Date} object from a {@link String} representation with respect to year, month and day.
	 * The provided String is expected to be exactly 8 characters long and encoded as follows: YYYYMMDD.
	 * If the String parts for month or day contain non-numeric characters, they will be handled according to the second provided parameter:
	 * -1: the minimum possible value will be chosen
	 *  0: a NumberFormatException will be thrown
	 *  1: the maximum possible value will be chosen
	 * Well formatted date strings will not be checked for validity, e.g. 20140199 would not be discarded.
	 * @param dateString the date encoded as follows: YYYYMMDD
	 * @param handleNonNumericCharacters determine how non-numeric characters will be handled
	 * @return a {@link Date} object
	 */
	public static Date getDate(String dateString, byte handleNonNumericCharacters) {
		if (dateString == null) {throw new NullPointerException("date must not be null");}
		if (dateString.length() != 8) {throw new IllegalArgumentException("date must be exactly 8 characters long");}

		Calendar calendar = Calendar.getInstance();

		int year = Integer.parseInt(dateString.substring(0, 4));

		calendar.set(Calendar.YEAR, year);

		int month, day;

		try {
			month = Integer.parseInt(dateString.substring(4, 6));
			month--; // set month is zero based
		} catch (NumberFormatException e) {
			switch (handleNonNumericCharacters) {
			case -1:
				month = Calendar.JANUARY;
				break;
			case 0:
				throw e;
			case 1:
				month = Calendar.DECEMBER;
				break;
			default:
				throw new IllegalArgumentException("invalid value for handling illegal month");
			}
		}

		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, 1);

		try {
			day = Integer.parseInt(dateString.substring(6, 8));
		} catch (NumberFormatException e) {
			switch (handleNonNumericCharacters) {
			case -1:
				day = 1;
				break;
			case 0:
				throw e;
			case 1:
				day = calendar.getActualMaximum(Calendar.DATE);
				break;
			default:
				throw new IllegalArgumentException("invalid value for handling illegal day");
			}
		}

		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}
	
}
