package de.persosim.simulator.tlv;

/**
 * This class provides support for encoding the wrapped ASN.1 data structure "Date"
 */
public class Asn1DateWrapper extends Asn1ConstructedApplicationWrapper implements Asn1 {
	
	private static Asn1DateWrapper instance = null;
	
	private Asn1DateWrapper() {
		super(Asn1Date.getInstance());
	}
	
	public static Asn1DateWrapper getInstance() {
		if(instance == null) {
			instance = new Asn1DateWrapper();
		}
		
		return instance;
	}
	
}
