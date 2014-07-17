package de.persosim.simulator.tlv;

/**
 * This interface provides easy access to frequently used {@link TlvTag} objects.
 * 
 * @author slutters
 *
 */
public interface TlvConstants {
	public static final TlvTag TAG_06 = new TlvTag((byte) 0x06);
	public static final TlvTag TAG_42 = new TlvTag((byte) 0x42);
	public static final TlvTag TAG_53 = new TlvTag((byte) 0x53);
	public static final TlvTag TAG_65 = new TlvTag((byte) 0x65);
	public static final TlvTag TAG_67 = new TlvTag((byte) 0x67);
	public static final TlvTag TAG_73 = new TlvTag((byte) 0x73);
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
	public static final TlvTag TAG_91 = new TlvTag((byte) 0x91);
	public static final TlvTag TAG_A0 = new TlvTag((byte) 0xA0);
	public static final TlvTag TAG_5F20 = new TlvTag(new byte []{0x5F, 0x20});
	public static final TlvTag TAG_5F24 = new TlvTag(new byte []{0x5F, 0x24});
	public static final TlvTag TAG_5F25 = new TlvTag(new byte []{0x5F, 0x25});
	public static final TlvTag TAG_5F29 = new TlvTag(new byte []{0x5F, 0x29});
	public static final TlvTag TAG_5F37 = new TlvTag(new byte []{0x5F, 0x37});
	public static final TlvTag TAG_7F21 = new TlvTag(new byte []{0x7F, 0x21});
	public static final TlvTag TAG_7F49 = new TlvTag(new byte []{0x7F, 0x49});
	public static final TlvTag TAG_7F4C = new TlvTag(new byte []{0x7F, 0x4C});
	public static final TlvTag TAG_7F4E = new TlvTag(new byte []{0x7F, 0x4E});

	public static final TlvTag TAG_OID = new TlvTag(Asn1.OBJECT_IDENTIFIER);
	public static final TlvTag TAG_SEQUENCE = new TlvTag(Asn1.SEQUENCE);
	
	public static final TlvValuePlain DER_BOOLEAN_TRUE = new TlvValuePlain(new byte [] {(byte) 0xFF});
	public static final TlvValuePlain DER_BOOLEAN_FALSE = new TlvValuePlain(new byte [] {0});
	
	
}
