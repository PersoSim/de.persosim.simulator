package de.persosim.simulator.apdu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class CommandApduFactoryTest extends PersoSimTestCase {
	
	/**
	 * This test checks for special treatment of proprietary plain command APDUs
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_Proprietary() {
		byte[] apdu = HexString.toByteArray("80F1F2F3");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertTrue(result);
	}
	
	/**
	 * This test checks for special treatment of proprietary command APDUs with proprietary SM
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_ProprietaryProprietarySm() {
		byte[] apdu = HexString.toByteArray("84F1F2F3");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertFalse(result);
	}
	
	/**
	 * This test checks for special treatment of plain command APDUs
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_InterIndustry() {
		byte[] apdu = HexString.toByteArray("00F1F2F3");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertFalse(result);
	}
	
	/**
	 * This test checks for special treatment of proprietary SM command APDUs
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_ProprietarySm() {
		byte[] apdu = HexString.toByteArray("8CF1F2F3");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertTrue(result);
	}
	
}
