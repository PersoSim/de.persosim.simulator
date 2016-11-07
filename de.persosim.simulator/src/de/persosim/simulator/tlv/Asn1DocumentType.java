package de.persosim.simulator.tlv;

import java.nio.charset.Charset;

/**
 * This class provides support for encoding the ASN.1 data structure "DocumentType"
 */
public class Asn1DocumentType extends Asn1ConstructedApplicationWrapper implements Asn1 {
	
	private static Asn1DocumentType instance = null;
	
	private Asn1DocumentType() {
		super(new Asn1Primitive(new TlvTag(UNIVERSAL_PRINTABLE_STRING), REGEX_PATTERN_DOCUMENTTYPE, Charset.forName("US-ASCII")){});
	}
	
	public static Asn1DocumentType getInstance() {
		if(instance == null) {
			instance = new Asn1DocumentType();
		}
		
		return instance;
	}
	
}
