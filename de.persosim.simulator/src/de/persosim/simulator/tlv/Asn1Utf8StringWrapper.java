package de.persosim.simulator.tlv;

/**
 * 
 */
public class Asn1Utf8StringWrapper extends Asn1ConstructedApplicationWrapper implements Asn1 {
	
	private static Asn1Utf8StringWrapper instance = null;
	
	private Asn1Utf8StringWrapper() {
		super(Asn1Utf8String.getInstance());
	}
	
	public static Asn1Utf8StringWrapper getInstance() {
		if(instance == null) {
			instance = new Asn1Utf8StringWrapper();
		}
		
		return instance;
	}
	
}
