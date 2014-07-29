package de.persosim.simulator.tlv;

/**
 * This interface holds ASN1 conform constants in order to address them by name
 * instead of magic numbers. These values are defined in the ASN.1 standard.
 * 
 * @author amay
 * 
 */
public interface Asn1 {

	// tag classes
	public static final byte CLASS                  = (byte) 0xC0;
	public static final byte CLASS_UNIVERSAL        = (byte) 0x00;
	public static final byte CLASS_APPLICATION      = (byte) 0x40;
	public static final byte CLASS_CONTEXT_SPECIFIC = (byte) 0x80;
	public static final byte CLASS_PRIVATE          = (byte) 0xC0;
	
	// tag encoding
	public static final byte ENCODING               = (byte) 0x20;
	public static final byte ENCODING_PRIMITIVE     = (byte) 0x00;
	public static final byte ENCODING_CONSTRUCTED   = (byte) 0x20;

	// Universal class tags
	public static final byte UNIVERSAL_BOOLEAN = 0x01;
	public static final byte UNIVERSAL_INTEGER = 0x02;
	public static final byte UNIVERSAL_BIT_STRING = 0x03;
	public static final byte UNIVERSAL_OCTET_STRING = 0x04;
	public static final byte UNIVERSAL_NULL = 0x05;
	public static final byte UNIVERSAL_OBJECT_IDENTIFIER = 0x06;
	public static final byte UNIVERSAL_OBJECT_DESCRIPTOR = 0x07;
	public static final byte UNIVERSAL_INSTANCE_OF = 0x08;
	public static final byte UNIVERSAL_EXTERNAL = 0x08;
	public static final byte UNIVERSAL_REAL = 0x09;
	public static final byte UNIVERSAL_ENUMERATED = 0x0A;
	public static final byte UNIVERSAL_EMBEDDED_PDV = 0x0B;
	public static final byte UNIVERSAL_UTF8String = 0x0c;
	public static final byte UNIVERSAL_RELATIVE_OID = 0x0D;
	public static final byte UNIVERSAL_SEQUENCE = 0x10;
	public static final byte UNIVERSAL_SEQUENCE_OF = 0x10;
	public static final byte UNIVERSAL_SET = 0x11;
	public static final byte UNIVERSAL_SET_OF = 0x11;
	public static final byte UNIVERSAL_NUMERIC_STRING = 0x12;
	public static final byte UNIVERSAL_PRINTABLE_STRING = 0x13;
	public static final byte UNIVERSAL_TELETEX_STRING = 0x14;
	public static final byte UNIVERSAL_T61_STRING = 0x14;
	public static final byte UNIVERSAL_VIDEOTEX_STRING = 0x15;
	public static final byte UNIVERSAL_IA5_STRING = 0x16;
	public static final byte UNIVERSAL_UTC_TIME = 0x17;
	public static final byte UNIVERSAL_GENERALIZED_TIME = 0x18;
	public static final byte UNIVERSAL_GRAPHIC_STRING = 0x19;
	public static final byte UNIVERSAL_VISIBLE_STRING = 0x1A;
	public static final byte UNIVERSAL_ISO646_STRING = 0x1A;
	public static final byte UNIVERSAL_GENERAL_STRING = 0x1B;
	public static final byte UNIVERSAL_UNIVERSAL_STRING = 0x1C;
	public static final byte UNIVERSAL_CHARACTER_STRING = 0x1D;
	public static final byte UNIVERSAL_BMP_STRING = 0x1E;

	// Tags frequently used within PersoSim (this list should be extended when needed)
	public static final byte INTEGER = UNIVERSAL_INTEGER;
	public static final byte BIT_STRING = UNIVERSAL_BIT_STRING;
	public static final byte OCTET_STRING = UNIVERSAL_OCTET_STRING;
	public static final byte OBJECT_IDENTIFIER = UNIVERSAL_OBJECT_IDENTIFIER;
	public static final byte SEQUENCE = ENCODING_CONSTRUCTED | UNIVERSAL_SEQUENCE;
	public static final byte SET = ENCODING_CONSTRUCTED | UNIVERSAL_SET;

}
