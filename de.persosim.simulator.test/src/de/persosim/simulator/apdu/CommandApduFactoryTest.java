package de.persosim.simulator.apdu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class CommandApduFactoryTest extends PersoSimTestCase {
	
	/**
	 * This test checks for special treatment of generic proprietary plain command APDUs
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_GenericProprietaryPlain() {
		byte[] apdu = HexString.toByteArray("80F1F2F3");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertFalse(result);
	}
	
	/**
	 * This test checks for special treatment of generic proprietary command APDUs with SM
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_GenericProprietarySm() {
		byte[] apdu = HexString.toByteArray("8CF1F2F3");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertFalse(result);
	}
	
	/**
	 * This test checks for special treatment of generic plain inter-industry command APDUs
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_GenericInterIndustryPlain() {
		byte[] apdu = HexString.toByteArray("00F1F2F3");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertFalse(result);
	}
	
	/**
	 * This test checks for special treatment of generic inter-industry command APDUs with SM
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_GenericInterIndustrySm() {
		byte[] apdu = HexString.toByteArray("0CF1F2F3");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertFalse(result);
	}
	
	/**
	 * This test checks for special treatment of INS 0x20 P1P2 0x8000 plain proprietary command APDUs
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_20800ProprietaryPlain() {
		byte[] apdu = HexString.toByteArray("80208000");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertTrue(result);
	}
	
	/**
	 * This test checks for special treatment of INS 0x20 P1P2 0x8000 proprietary command APDUs with SM
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_20800ProprietarySm() {
		byte[] apdu = HexString.toByteArray("8C208000");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertTrue(result);
	}
	
	/**
	 * This test checks for special treatment of INS 0x2A P1P2 0xAEAC plain proprietary command APDUs
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_2AAEACProprietaryPlain() {
		byte[] apdu = HexString.toByteArray("802AAEAC");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertTrue(result);
	}
	
	/**
	 * This test checks for special treatment of INS 0x2A P1P2 0xAEAC proprietary command APDUs with SM
	 */
	@Test
	public void testMatchesIsoCompatibleProprietaryCommandApdu_2AAEACProprietarySm() {
		byte[] apdu = HexString.toByteArray("8C2AAEAC");
		boolean result = CommandApduFactory.matchesIsoCompatibleProprietaryCommandApdu(apdu);
		assertTrue(result);
	}
	
}
