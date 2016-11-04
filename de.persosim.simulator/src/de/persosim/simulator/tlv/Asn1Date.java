package de.persosim.simulator.tlv;

import java.nio.charset.Charset;

/**
 * This class provides support for encoding the ASN.1 data structure "Date"
 */
public class Asn1Date extends Asn1Primitive implements Asn1 {
	
	private static Asn1Date instance = null;
	
	private Asn1Date() {
		super(new TlvTag(UNIVERSAL_NUMERIC_STRING), REGEX_PATTERN_DATE, Charset.forName("US-ASCII"));
	}
	
	public static Asn1Date getInstance() {
		if(instance == null) {
			instance = new Asn1Date();
		}
		
		return instance;
	}
	
}
