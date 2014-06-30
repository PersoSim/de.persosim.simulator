package de.persosim.simulator.crypto;

/**
 * This class is intended to be used as source for the CryptoProvider in order
 * to allow using the code base as single source solution when porting to
 * Androir. 
 * 
 * TODO verify how this can work as single source solution on Android
 * 
 * @author slutters
 * 
 */
public class Crypto {
	protected static String provider = "BC";

	/*--------------------------------------------------------------------------------*/

	public static String getCryptoProvider() {
		return provider;
	}

	public static void setCryptoProvider(String newProvider) {
		provider = newProvider;
	}
}
