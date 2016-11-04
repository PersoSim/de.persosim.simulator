package de.persosim.simulator.tlv;

import java.nio.charset.Charset;

/**
 * 
 */
public class Asn1IcaoCountry extends Asn1Primitive implements Asn1 {
	
	private static Asn1IcaoCountry instance = null;
	
	private Asn1IcaoCountry() {
		super(new TlvTag(UNIVERSAL_PRINTABLE_STRING), REGEX_PATTERN_ICAOCOUNTRY, Charset.forName("US-ASCII"));
	}
	
	public static Asn1IcaoCountry getInstance() {
		if(instance == null) {
			instance = new Asn1IcaoCountry();
		}
		
		return instance;
	}
	
}
