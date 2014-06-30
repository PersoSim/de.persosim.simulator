package de.persosim.simulator.test;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.utils.InfoSource;
import de.persosim.simulator.utils.PersoSimLogger;

/**
 * Superclass for all test cases PersoSim.
 * 
 * This class provides a generic implementation of InfoSource suitable during test execution.
 * 
 * The {@link #setUpClass()} method handles correct setup of PersoSimLogger for the test execution.
 * 
 * @author amay
 * 
 */
public class PersoSimTestCase implements InfoSource, Iso7816 {

	@Override
	public String getIDString() {
		return getClass().getCanonicalName();
	}
	
	@BeforeClass
	public static void setUpClass() {
		//setup logging
		PersoSimLogger.init();
		
		//register BouncyCastle provider
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

}
