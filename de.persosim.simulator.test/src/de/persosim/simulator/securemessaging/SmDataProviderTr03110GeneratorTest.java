package de.persosim.simulator.securemessaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class SmDataProviderTr03110GeneratorTest extends PersoSimTestCase {
	
	// test data from ICAO Doc 9303 Part 3 Vol 2, Worked Example Appendix 6 to section IV
	private static final String ICAO_SK_ENC = "979EC13B1CBFE9DCD01AB0FED307EAE5";
	private static final String ICAO_SK_MAC = "F1CB1F1FB5ADF208806B89DC579DC1F8";
	
	/**
	 * Positive test: check that SmDataProvider is correctly generated and can
	 * be used to reconstruct original DataProvider matching the original object
	 */
	@Test
	public void testGenerateSmDataProvider_reconstructDataProvider() {
		SecretKeySpec cipherKey = new SecretKeySpec(HexString.toByteArray(ICAO_SK_ENC), "DESede");
		SecretKeySpec macKey = new SecretKeySpec(HexString.toByteArray(ICAO_SK_MAC), "DESede");
		SmDataProviderTr03110 smdpOri = new SmDataProviderTr03110(cipherKey, macKey);
		
		SmDataProviderTr03110Generator smdpg = new SmDataProviderTr03110Generator(smdpOri);
		
		SmDataProviderTr03110 smdpNew = smdpg.generateSmDataProvider();
		
		assertEquals(smdpOri, smdpNew);
		assertNotSame(smdpOri, smdpNew);
	}
	
	/**
	 * Positive test: check that SmDataProvider is correctly generated and can
	 * be used to reconstruct original DataProvider mismatching the original
	 * object that has been modified afterwards
	 */
	@Test
	public void testGenerateSmDataProvider_reconstructDataProviderModified() {
		SecretKeySpec cipherKey = new SecretKeySpec(HexString.toByteArray(ICAO_SK_ENC), "DESede");
		SecretKeySpec macKey = new SecretKeySpec(HexString.toByteArray(ICAO_SK_MAC), "DESede");
		SmDataProviderTr03110 smdpOri = new SmDataProviderTr03110(cipherKey, macKey);
		
		SmDataProviderTr03110Generator smdpg = new SmDataProviderTr03110Generator(smdpOri);
		
		smdpOri.nextIncoming();
		
		SmDataProviderTr03110 smdpNew = smdpg.generateSmDataProvider();
		
		assertNotEquals(smdpOri, smdpNew);
		assertNotSame(smdpOri, smdpNew);
	}
	
}
