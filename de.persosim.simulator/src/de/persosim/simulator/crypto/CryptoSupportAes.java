package de.persosim.simulator.crypto;

public class CryptoSupportAes extends CryptoSupport {
	public static final String ALGORITHM = "AES";
	
	public CryptoSupportAes(String algorithmNameModePadding, String macName) {
		super(algorithmNameModePadding, macName);
		
		if(!this.getCipherName().equals(ALGORITHM)) {
			throw new IllegalArgumentException("algorithm must be " + ALGORITHM);
		}
	}
	
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public String getCipherAlgorithm() {
		return ALGORITHM;
	}
	
}
