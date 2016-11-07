package de.persosim.simulator.tlv;

/**
 * This class provides support for encoding the wrapped ASN.1 data structure "ICAOCountry"
 */
public class Asn1IcaoCountryWrapper extends Asn1ConstructedApplicationWrapper implements Asn1 {
	
	private static Asn1IcaoCountryWrapper instance = null;
	
	private Asn1IcaoCountryWrapper() {
		super(Asn1IcaoCountry.getInstance());
	}
	
	public static Asn1IcaoCountryWrapper getInstance() {
		if(instance == null) {
			instance = new Asn1IcaoCountryWrapper();
		}
		
		return instance;
	}
	
}
