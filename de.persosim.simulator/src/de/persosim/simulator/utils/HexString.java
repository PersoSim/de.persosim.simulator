package de.persosim.simulator.utils;

import java.math.BigInteger;
import java.util.Arrays;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * Encapsulate methods handling String representations of byte Arrays.
 * 
 * @author amay
 *
 */
public class HexString {
	public static final String HEXCHARACTERS = "0123456789ABCDEF";

	/**
	 * @see #dump(byte[], int, int)
	 */
	public static String dump(byte[] bytes) {
		return dump(bytes, 0, bytes.length);
	}

	/**
	 * Dump buffer in hexadecimal format with offset and character codes. Output
	 * 16 bytes per line
	 * 
	 * @param rpByte
	 *            Byte array to be dumped
	 * @param offset
	 *            Offset into byte buffer
	 * @param length
	 *            Length of data to be dumped
	 * @return human readable representation of rpByte
	 */
	public static String dump(byte[] rpByte, int offset, int length) {

		// define some default parameter
		int widths = 16; // number of bytes per line
		int indent = 1; // number of blanks to indent each line

		StringBuffer buffer = new StringBuffer(80);
		int i, tmpOffset, len;
		char ch;

		if ((rpByte == null) || (length < 0))
			throw new IllegalArgumentException();

		while (length > 0) {
			for (i = 0; i < indent; i++)
				buffer.append(' ');

			buffer.append(hexifyShort(offset));
			buffer.append("  ");

			tmpOffset = offset;
			len = widths < length ? widths : length;

			for (i = 0; i < len; i++, tmpOffset++) {
				buffer.append(HEXCHARACTERS.charAt((rpByte[tmpOffset] & 0xF0) >> 4));
				buffer.append(HEXCHARACTERS.charAt((rpByte[tmpOffset] & 0x0F)));
				buffer.append(' ');
			}

			for (; i < widths; i++) {
				buffer.append("   ");
			}

			buffer.append(' ');
			tmpOffset = offset;

			for (i = 0; i < len; i++, tmpOffset++) {
				ch = (char) (rpByte[tmpOffset] & 0xFF);
				if ((ch < 32) || ((ch >= 127)))
					ch = '.';
				buffer.append(ch);
			}

			buffer.append('\n');

			offset += len;
			length -= len;
		}
		return buffer.toString();

	}

	/**
	 * Encode the BigInteger in a HexString.
	 * 
	 * @see #encode(byte[])
	 * @see Utils#toUnsignedByteArray(BigInteger)
	 * @param input
	 * @return
	 */
	public static String encode(BigInteger input) {
		return encode(Utils.toUnsignedByteArray(input));
	}

	/**
	 * @see #encode(byte[])
	 */
	public static String encode(byte input) {
		return encode(new byte[] { input });
	}

	/**
	 * This method returns a String representation of a byte array
	 * 
	 * @param input
	 *            the byte array to be represented as String
	 * @return a String representation of the provided byte array
	 */
	public static String encode(byte[] input) {
		if (input == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder(2 * input.length);

		for (byte b : input) {
			builder.append(HEXCHARACTERS.charAt((b & 0xF0) >> 4));
			builder.append(HEXCHARACTERS.charAt((b & 0x0F)));
		}

		return builder.toString();
	}

	/**
	 * Convert integer value to hexadecimal byte representation.
	 * 
	 * @param val
	 *            Value to be converted
	 * @return two digit hexadecimal string
	 */
	public static String hexifyByte(int val) {
		return "" + HEXCHARACTERS.charAt((val & 0xF0) >> 4)
				+ HEXCHARACTERS.charAt((val & 0x0F));
	}

	/**
	 * Convert integer value to hexadecimal short representation.
	 * 
	 * @param val
	 *            Value to be converted
	 * @return four digit hexadecimal string
	 */
	public static String hexifyShort(int val) {
		return hexifyByte((val >>> 8) & 0xFF) + hexifyByte(val & 0xFF);
	}

	/**
	 * Converts a hexadecimal String into a byte array.
	 * 
	 * @param inputString
	 *            the hexadecimal String to be converted
	 * @return a byte array representation of the hexadecimal String. The new
	 *         modified method uses the HexBinaryAdapter. Method converts a 
	 *         value type to a bound type.
	 *         The HexBinaryAdapter provides the ability to marshal and 
	 *         unmarshal between a String  and a byte array.
	 */
	public static byte[] toByteArray(String inputString) {

		inputString = inputString.replaceAll("\\s", "");
		if (inputString.length() % 2 != 0) {
			throw new IllegalArgumentException(
					"hexadecimal string must be of even length");
			};

		if (inputString.length() == 0) {
			return new byte[0];
			};

		HexBinaryAdapter adapter = new HexBinaryAdapter();
		byte[] result = adapter.unmarshal(inputString);
		return result;
	}
}
