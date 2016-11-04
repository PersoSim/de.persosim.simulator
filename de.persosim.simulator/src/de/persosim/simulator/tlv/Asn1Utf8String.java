package de.persosim.simulator.tlv;

import java.nio.charset.Charset;

/**
 * This class provides support for encoding the ASN.1 data structure "UTF8String"
 */
public class Asn1Utf8String extends Asn1Primitive implements Asn1 {
	
	private static Asn1Utf8String instance = null;
	
	private Asn1Utf8String() {
		super(new TlvTag(UNIVERSAL_UTF8String), null, Charset.forName("UTF-8"));
	}
	
	public static Asn1Utf8String getInstance() {
		if(instance == null) {
			instance = new Asn1Utf8String();
		}
		
		return instance;
	}
	
}
