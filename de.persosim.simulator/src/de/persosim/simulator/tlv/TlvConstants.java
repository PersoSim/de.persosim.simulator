package de.persosim.simulator.tlv;

/**
 * This interface provides easy access to frequently used {@link TlvTag} objects.
 * 
 * @author slutters
 *
 */
public interface TlvConstants {
	
	public static final TlvTag TAG_06 = new TlvTag((byte) 0x06);
	public static final TlvTag TAG_53 = new TlvTag((byte) 0x53);
	public static final TlvTag TAG_7C = new TlvTag((byte) 0x7C);
	public static final TlvTag TAG_80 = new TlvTag((byte) 0x80);
	public static final TlvTag TAG_81 = new TlvTag((byte) 0x81);
	public static final TlvTag TAG_82 = new TlvTag((byte) 0x82);
	public static final TlvTag TAG_83 = new TlvTag((byte) 0x83);
	public static final TlvTag TAG_84 = new TlvTag((byte) 0x84);
	public static final TlvTag TAG_85 = new TlvTag((byte) 0x85);
	public static final TlvTag TAG_86 = new TlvTag((byte) 0x86);
	public static final TlvTag TAG_87 = new TlvTag((byte) 0x87);
	public static final TlvTag TAG_88 = new TlvTag((byte) 0x88);
	public static final TlvTag TAG_7F4C = new TlvTag((short) 0x7F4C);
	public static final TlvTag TAG_7F49 = new TlvTag((short) 0x7F49);

	public static final TlvValuePlain DER_BOOLEAN_TRUE = new TlvValuePlain(new byte [] {(byte) 0xFF});
	public static final TlvValuePlain DER_BOOLEAN_FALSE = new TlvValuePlain(new byte [] {0});
	
}
