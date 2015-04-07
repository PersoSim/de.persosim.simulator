package de.persosim.simulator.crypto;

import java.security.Provider;

import de.persosim.simulator.Activator;

/**
 * This class is intended to be used as source for the CryptoProvider in order
 * to allow using the code base as single source solution when porting to
 * Android. 
 * 
 * TODO verify how this can work as single source solution on Android
 * 
 * @author slutters
 * 
 */
public class Crypto {
	protected static String provider = "BC";

	/*--------------------------------------------------------------------------------*/

//	public static String getCryptoProvider() {
//		return Activator.objectImplementingInterface.getCryptoProviderString();
//	}
	
	public static Provider getCryptoProvider() {
		return Activator.objectImplementingInterface.getCryptoProviderObject();
	}
	
	public static Provider getCryptoProviderObject() {
		return Activator.objectImplementingInterface.getCryptoProviderObject();
	}
	
}
