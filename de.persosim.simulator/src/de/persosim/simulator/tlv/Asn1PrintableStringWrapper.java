package de.persosim.simulator.tlv;

/**
 * This class provides support for encoding the wrapped ASN.1 data structure "UTF8String"
 */
public class Asn1PrintableStringWrapper extends Asn1ConstructedApplicationWrapper implements Asn1 {
	
	private static Asn1PrintableStringWrapper instance = null;
	
	private Asn1PrintableStringWrapper() {
		super(Asn1PrintableString.getInstance());
	}
	
	public static Asn1PrintableStringWrapper getInstance() {
		if(instance == null) {
			instance = new Asn1PrintableStringWrapper();
		}
		
		return instance;
	}
	
}
