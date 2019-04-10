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
	public static final TlvTag TAG_6E = new TlvTag((byte) 0x6E);
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
	public static final TlvTag TAG_8A = new TlvTag((byte) 0x8A);
	public static final TlvTag TAG_8E = new TlvTag((byte) 0x8E);
	public static final TlvTag TAG_91 = new TlvTag((byte) 0x91);
	public static final TlvTag TAG_92 = new TlvTag((byte) 0x92);
	public static final TlvTag TAG_97 = new TlvTag((byte) 0x97);
	public static final TlvTag TAG_99 = new TlvTag((byte) 0x99);
	public static final TlvTag TAG_A0 = new TlvTag((byte) 0xA0);
	public static final TlvTag TAG_A1 = new TlvTag((byte) 0xA1);
	public static final TlvTag TAG_A2 = new TlvTag((byte) 0xA2);
	public static final TlvTag TAG_A3 = new TlvTag((byte) 0xA3);
	public static final TlvTag TAG_A4 = new TlvTag((byte) 0xA4);
	public static final TlvTag TAG_A5 = new TlvTag((byte) 0xA5);
	public static final TlvTag TAG_A6 = new TlvTag((byte) 0xA6);
	public static final TlvTag TAG_AA = new TlvTag((byte) 0xAA);
	public static final TlvTag TAG_AB = new TlvTag((byte) 0xAB);
	public static final TlvTag TAG_AC = new TlvTag((byte) 0xAC);
	public static final TlvTag TAG_AD = new TlvTag((byte) 0xAD);
	public static final TlvTag TAG_AE = new TlvTag((byte) 0xAE);
	public static final TlvTag TAG_E1 = new TlvTag((byte) 0xE1);
	public static final TlvTag TAG_E0 = new TlvTag((byte) 0xE0);
	public static final TlvTag TAG_AltSet = new TlvTag((byte) 0x31);
	public static final TlvTag TAG_5F20 = new TlvTag(new byte []{0x5F, 0x20});
	public static final TlvTag TAG_5F24 = new TlvTag(new byte []{0x5F, 0x24});
	public static final TlvTag TAG_5F25 = new TlvTag(new byte []{0x5F, 0x25});
	public static final TlvTag TAG_5F29 = new TlvTag(new byte []{0x5F, 0x29});
	public static final TlvTag TAG_5F37 = new TlvTag(new byte []{0x5F, 0x37});
	public static final TlvTag TAG_7F21 = new TlvTag(new byte []{0x7F, 0x21});
	public static final TlvTag TAG_7F49 = new TlvTag(new byte []{0x7F, 0x49});
	public static final TlvTag TAG_7F4C = new TlvTag(new byte []{0x7F, 0x4C});
	public static final TlvTag TAG_7F4E = new TlvTag(new byte []{0x7F, 0x4E});

	public static final TlvTag TAG_NULL = new TlvTag(Asn1.UNIVERSAL_NULL);
	public static final TlvTag TAG_BOOLEAN = new TlvTag(Asn1.BOOLEAN);
	public static final TlvTag TAG_INTEGER = new TlvTag(Asn1.INTEGER);
	public static final TlvTag TAG_NUMERIC_STRING = new TlvTag(Asn1.UNIVERSAL_NUMERIC_STRING);
	public static final TlvTag TAG_BIT_STRING = new TlvTag(Asn1.BIT_STRING);
	public static final TlvTag TAG_OCTET_STRING = new TlvTag(Asn1.OCTET_STRING);
	public static final TlvTag TAG_OID = new TlvTag(Asn1.OBJECT_IDENTIFIER);
	public static final TlvTag TAG_SEQUENCE = new TlvTag(Asn1.SEQUENCE);
	public static final TlvTag TAG_SET = new TlvTag(Asn1.SET);
	public static final TlvTag TAG_IA5_STRING = new TlvTag(Asn1.IA5_STRING);
	public static final TlvTag TAG_PRINTABLE_STRING = new TlvTag(Asn1.UNIVERSAL_PRINTABLE_STRING);
	public static final TlvTag TAG_UTF8_STRING = new TlvTag(Asn1.UNIVERSAL_UTF8String);
	
	
	public static final TlvValuePlain DER_BOOLEAN_TRUE = new TlvValuePlain(new byte [] {(byte) 0xFF});
	public static final TlvValuePlain DER_BOOLEAN_FALSE = new TlvValuePlain(new byte [] {0});
	
	
}
