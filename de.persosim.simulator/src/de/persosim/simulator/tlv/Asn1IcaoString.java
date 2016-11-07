package de.persosim.simulator.tlv;

import java.nio.charset.Charset;

/**
 * This class provides support for encoding the ASN.1 data structure "ICAOString"
 */
public class Asn1IcaoString extends Asn1Primitive implements Asn1 {
	
	private static Asn1IcaoString instance = null;
	
	private Asn1IcaoString() {
		super(new TlvTag(UNIVERSAL_PRINTABLE_STRING), REGEX_PATTERN_ICAOSTRING, Charset.forName("US-ASCII"));
	}
	
	public static Asn1IcaoString getInstance() {
		if(instance == null) {
			instance = new Asn1IcaoString();
		}
		
		return instance;
	}
	
}
