package de.persosim.simulator.test;

import java.io.File;
import java.security.Provider;
import java.security.Security;

import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.cryptoprovider.bc.ProviderBc;
import org.junit.BeforeClass;

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

	private static final String TMP_FOLDER = "tmp";

	@Override
	public String getIDString() {
		return getClass().getCanonicalName();
	}
	
	public static File getTmpFolder() {
		File tmpFolder = new File(TMP_FOLDER);
		if (!tmpFolder.isDirectory())
			tmpFolder.mkdirs();
		return tmpFolder;
	}
	
	@BeforeClass
	public static void setupClass(){
		Crypto.setCryptoProvider(new ProviderBc().getCryptoProviderObject());

	}

}
