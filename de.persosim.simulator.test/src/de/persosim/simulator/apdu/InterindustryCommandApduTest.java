package de.persosim.simulator.apdu;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.Utils;

public class InterindustryCommandApduTest extends PersoSimTestCase {
	byte [] apduHeader;
	byte [] lc;
	byte [] commandData;
	
	@Before
	public void setUp(){
		apduHeader = new byte [] { (byte) 0x0C, 0x20, (byte) 0x80, 0x00};
		lc = new byte [] {0x01};
		commandData = new byte [] {(byte) 0xFF};
	}
	
	@Test
	public void testGetSecureMessaging(){
		IsoSecureMessagingCommandApdu apdu = new InterindustryCommandApdu(Utils.concatByteArrays(apduHeader, lc, commandData), null);
		assertEquals(0b00000011, apdu.getSecureMessaging());
	}

	@Test
	public void testRewrapApdu(){
		IsoSecureMessagingCommandApdu apdu = new InterindustryCommandApdu(Utils.concatByteArrays(apduHeader, lc, commandData), null);
		IsoSecureMessagingCommandApdu result = (IsoSecureMessagingCommandApdu) apdu.rewrapApdu((byte) 0, Utils.concatByteArrays(lc, commandData));
		
		byte [] expectedHeader = Arrays.copyOf(apduHeader, apduHeader.length);
		expectedHeader[0] = (byte) 0x00;
		assertEquals(apdu.getCommandData(), result.getCommandData());
		assertArrayEquals(expectedHeader, result.getHeader());
		assertArrayEquals(Utils.concatByteArrays(apduHeader, lc, commandData), result.getPredecessor().toByteArray());
	}
	
}
