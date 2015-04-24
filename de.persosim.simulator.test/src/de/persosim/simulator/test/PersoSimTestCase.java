package de.persosim.simulator.test;

import java.security.Provider;
import java.security.Security;

import mockit.Mock;
import mockit.MockUp;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;

import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.utils.InfoSource;

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

	protected static Provider bcProvider;

	@Override
	public String getIDString() {
		return getClass().getCanonicalName();
	}
	
	@BeforeClass
	public static void setupClass(){
		bcProvider = new BouncyCastleProvider();
		Security.addProvider(bcProvider);
		new MockUp<Crypto>(){

			@Mock
			public Provider getCryptoProvider() {
				return bcProvider;
			}
		};
	}

}
