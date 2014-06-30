package de.persosim.simulator.crypto;

public class CryptoSupportSelector {
	private CryptoSupportSelector() {
		//should not be instantiated
	}
	
	public static CryptoSupport getCryptoSupport(String cipherName, String macName) {
		switch (CryptoUtil.getCipherNameAsString(cipherName)) {
		case "AES":
			return new CryptoSupportAes(cipherName, macName);
		default:
			throw new IllegalArgumentException("algorithm " + cipherName + " is unknown or not supported");
		}
	}
}
