package de.persosim.simulator.tlv;

import java.nio.charset.Charset;

/**
 * This class provides support for encoding the ASN.1 data structure "PrintableString"
 */
public class Asn1PrintableString extends Asn1Primitive implements Asn1 {
	
	private static Asn1PrintableString instance = null;
	
	private Asn1PrintableString() {
		super(new TlvTag(UNIVERSAL_PRINTABLE_STRING), REGEX_PATTERN_PRINTABLESTRING, Charset.forName("US-ASCII"));
	}
	
	public static Asn1PrintableString getInstance() {
		if(instance == null) {
			instance = new Asn1PrintableString();
		}
		
		return instance;
	}
	
}
