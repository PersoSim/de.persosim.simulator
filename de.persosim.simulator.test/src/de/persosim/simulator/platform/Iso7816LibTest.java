package de.persosim.simulator.platform;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;

public class Iso7816LibTest extends PersoSimTestCase implements Iso7816 {

	byte[] iso1;
	byte[] iso2;
	byte[] iso3;
	byte[] iso4;
	byte[] iso2MsbSet;
	byte[] iso4MsbSet;
	byte[] iso2MsbSetMax;
	byte[] iso4MsbSetMax;
	byte[] iso2ZeroEnc;
	byte[] iso4ZeroEnc;
	byte[] iso2Extended;
	byte[] iso3Extended;
	byte[] iso4Extended;
	byte[] iso2ExtendedMsbSet;
	byte[] iso4ExtendedMsbSet;
	byte[] iso2ExtendedMsbSetMax;
	byte[] iso4ExtendedMsbSetMax;
	byte[] iso2ExtendedZeroEnc;
	byte[] iso4ExtendedZeroEnc;

	/**
	 * Create APDUs for ISO cases 1 - 4 for testing.
	 */
	@Before
	public void setUp() {
		iso1 = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00 };
		iso2 = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x01 };
		iso3 = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x01, (byte) 0xFF };
		iso4 = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x01, (byte) 0xFF,
				0x01 };

		iso2MsbSet = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0x80 };
		iso4MsbSet = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x01,
				(byte) 0xFF, (byte) 0x80 };

		iso2MsbSetMax = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00,
				(byte) 0xFF };
		iso4MsbSetMax = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x01,
				(byte) 0xFF, (byte) 0xFF };

		iso2ZeroEnc = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x00 };
		iso4ZeroEnc = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x01,
				(byte) 0xFF, 0x00 };

		iso2Extended = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x00, 0x00,
				0x01 };
		iso3Extended = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x00, 0x00,
				0x01, (byte) 0xFF };
		iso4Extended = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x00, 0x00,
				0x01, (byte) 0xFF, 0x00, 0x01 };

		iso2ExtendedMsbSet = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x00,
				(byte) 0x80, 0x00 };
		iso4ExtendedMsbSet = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x00,
				0x00, 0x01, (byte) 0xFF, (byte) 0x80, 0x00 };

		iso2ExtendedMsbSetMax = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00,
				0x00, (byte) 0xFF, (byte) 0xFF };
		iso4ExtendedMsbSetMax = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00,
				0x00, 0x00, 0x01, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

		iso2ExtendedZeroEnc = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x00,
				0x00, 0x00 };
		iso4ExtendedZeroEnc = new byte[] { 0x00, (byte) 0x80, 0x00, 0x00, 0x00,
				0x00, 0x01, (byte) 0xFF, 0x00, 0x00 };
	}

	@Test
	public void testGetNeIsoCase1() {
		assertEquals(0, Iso7816Lib.getNe(iso1));
	}

	@Test
	public void testGetNeIsoCase2() {
		assertEquals(1, Iso7816Lib.getNe(iso2));
	}

	@Test
	public void testGetNeIsoCase3() {
		assertEquals(0, Iso7816Lib.getNe(iso3));
	}

	@Test
	public void testGetNeIsoCase4() {
		assertEquals(1, Iso7816Lib.getNe(iso4));
	}

	@Test
	public void testGetNeIsoCase2MsbSet() {
		assertEquals(128, Iso7816Lib.getNe(iso2MsbSet));
	}

	@Test
	public void testGetNeIsoCase4MsbSet() {
		assertEquals(128, Iso7816Lib.getNe(iso4MsbSet));
	}

	@Test
	public void testGetNeIsoCase2MsbSetMax() {
		assertEquals(255, Iso7816Lib.getNe(iso2MsbSetMax));
	}

	@Test
	public void testGetNeIsoCase4MsbSetMax() {
		assertEquals(255, Iso7816Lib.getNe(iso4MsbSetMax));
	}

	@Test
	public void testGetNeIsoCase2ZeroEncoded() {
		assertEquals(256, Iso7816Lib.getNe(iso2ZeroEnc));
	}

	@Test
	public void testGetNeIsoCase4ZeroEncoded() {
		assertEquals(256, Iso7816Lib.getNe(iso4ZeroEnc));
	}

	@Test
	public void testGetNeIsoCase2Extended() {
		assertEquals(1, Iso7816Lib.getNe(iso2Extended));
	}

	@Test
	public void testGetNeIsoCase3Extended() {
		assertEquals(0, Iso7816Lib.getNe(iso3Extended));
	}

	@Test
	public void testGetNeIsoCase4Extended() {
		assertEquals(1, Iso7816Lib.getNe(iso4Extended));
	}

	@Test
	public void testGetNeIsoCase2ExtendedMsbSet() {
		assertEquals(32768, Iso7816Lib.getNe(iso2ExtendedMsbSet));
	}

	@Test
	public void testGetNeIsoCase4ExtendedMsbSet() {
		assertEquals(32768, Iso7816Lib.getNe(iso4ExtendedMsbSet));
	}

	@Test
	public void testGetNeIsoCase2ExtendedMsbSetMax() {
		assertEquals(65535, Iso7816Lib.getNe(iso2ExtendedMsbSetMax));
	}

	@Test
	public void testGetNeIsoCase4ExtendedMsbSetMax() {
		assertEquals(65535, Iso7816Lib.getNe(iso4ExtendedMsbSetMax));
	}

	@Test
	public void testGetNeIsoCase2ExtendedZeroEncoded() {
		assertEquals(65536, Iso7816Lib.getNe(iso2ExtendedZeroEnc));
	}

	@Test
	public void testGetNeIsoCase4ExtendedZeroEncoded() {
		assertEquals(65536, Iso7816Lib.getNe(iso4ExtendedZeroEnc));
	}
	
	@Test
	public void testSetSecureMessagingStatus_FirstInterindustry() {
		assertEquals("no change", (byte) 0x00, Iso7816Lib.setSecureMessagingStatus((byte) 0x00, SM_OFF_OR_NO_INDICATION));
		assertEquals("set proprieteary", (byte) 0x04, Iso7816Lib.setSecureMessagingStatus((byte) 0x00, SM_PROPRIETARY));
		assertEquals("set command header not processed", (byte) 0x08, Iso7816Lib.setSecureMessagingStatus((byte) 0x00, SM_COMMAND_HEADER_NOT_PROCESSED));
		assertEquals("set command header authenticated", (byte) 0x0C, Iso7816Lib.setSecureMessagingStatus((byte) 0x00, SM_COMMAND_HEADER_AUTHENTICATED));
		
		assertEquals("unset", (byte) 0x00, Iso7816Lib.setSecureMessagingStatus((byte) 0x0C, SM_OFF_OR_NO_INDICATION));
	}

	@Test(expected = IllegalArgumentException.class) 
	public void testSetSecureMessagingStatus_FirstInterindustry_InvalidSmStatus() {
		Iso7816Lib.setSecureMessagingStatus((byte) 0x00, (byte) 0x08);
	}

	@Test 
	public void testSetSecureMessagingStatus_FurtherInterindustry() {
		assertEquals("no change", (byte) 0x40, Iso7816Lib.setSecureMessagingStatus((byte) 0x40, SM_OFF_OR_NO_INDICATION));
		assertEquals("set command header not processed", (byte) 0x60, Iso7816Lib.setSecureMessagingStatus((byte) 0x40, SM_COMMAND_HEADER_NOT_PROCESSED));
		
		assertEquals("unset", (byte) 0x40, Iso7816Lib.setSecureMessagingStatus((byte) 0x60, SM_OFF_OR_NO_INDICATION));
	}

	@Test(expected = IllegalArgumentException.class) 
	public void testSetSecureMessagingStatus_FurtherInterindustry_InvalidSmStatus() {
		Iso7816Lib.setSecureMessagingStatus((byte) 0x40, SM_PROPRIETARY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetSecureMessagingStatus_InterindustryReserverd() {
		Iso7816Lib.setSecureMessagingStatus((byte) 0x20, SM_OFF_OR_NO_INDICATION);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetSecureMessagingStatus_ProprietaryFormat() {
		Iso7816Lib.setSecureMessagingStatus((byte)0x80, SM_OFF_OR_NO_INDICATION);
	}
}
