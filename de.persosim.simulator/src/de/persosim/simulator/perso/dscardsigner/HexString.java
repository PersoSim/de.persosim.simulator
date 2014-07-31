package de.persosim.simulator.perso.dscardsigner;

/**
 * Helfer-Klasse zur Konvertierung von Byte-Arrays und Strings in hexadezimaler Form.
 *
 * @author tsenger
 */

public class HexString {

    public static String stringToHex(String s) {
        byte[] stringBytes = s.getBytes();
        return bufferToHex(stringBytes);
    }

    public static String bufferToHex(byte buffer[]) {
        return bufferToHex(buffer, 0, buffer.length);
    }

    public static String bufferToHex(byte buffer[], int startOffset, int length) {
        StringBuilder hexString = new StringBuilder(2 * length);
        int endOffset = startOffset + length;
        for (int i = startOffset; i < endOffset; i++) {
            appendHexPair(buffer[i], hexString);
            hexString.append(" ");
            if ((i + 1) % 16 == 0)
                hexString.append("\n");
        }
        return hexString.toString();
    }

    public static String hexToString(String hexString)
            throws NumberFormatException {
        byte[] bytes = hexToBuffer(hexString);
        return new String(bytes);
    }

    public static byte[] hexToBuffer(String hexString)
            throws NumberFormatException {
        int length = hexString.length();
        byte[] buffer = new byte[(length + 1) / 2];
        boolean evenByte = (length % 2) == 0;
        byte nextByte = 0;
        int bufferOffset = 0;

        for (int i = 0; i < length; i++) {
            char c = hexString.charAt(i);
            int nibble;
            if ((c >= '0') && (c <= '9'))
                nibble = c - '0';
            else if ((c >= 'A') && (c <= 'F'))
                nibble = c - 'A' + 0x0A;
            else if ((c >= 'a') && (c <= 'f'))
                nibble = c - 'a' + 0x0A;
            else
                throw new NumberFormatException("Invalid hex digit '" + c + "'.");
            if (evenByte) {
                nextByte = (byte) (nibble << 4);
            } else {
                nextByte += (byte) nibble;
                buffer[bufferOffset++] = nextByte;
            }
            evenByte = !evenByte;
        }
        return buffer;
    }

    private static void appendHexPair(byte b, StringBuilder hexString) {
        char highNibble = kHexChars[(b & 0xF0) >> 4];
        char lowNibble = kHexChars[b & 0x0F];
        hexString.append(highNibble);
        hexString.append(lowNibble);
    }

    private static final char[] kHexChars = "0123456789abcdef".toCharArray();

}