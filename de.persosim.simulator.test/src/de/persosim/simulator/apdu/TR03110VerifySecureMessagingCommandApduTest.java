package de.persosim.simulator.apdu;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.bouncycastle.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.Utils;

public class TR03110VerifySecureMessagingCommandApduTest extends PersoSimTestCase {
	byte [] apduHeader;
	byte [] commandData;
	
	@Before
	public void setUp(){
		apduHeader = new byte [] { (byte) 0x8C, 0x20, (byte) 0x80, 0x00};
		commandData = new byte [] { 0x01, (byte) 0xFF };
	}
	
	@Test
	public void testGetSecureMessaging(){
		IsoSecureMessagingCommandApdu apdu = new TR03110VerifySecureMessagingCommandApdu(Utils.concatByteArrays(apduHeader, commandData), null);
		assertEquals(0b00000011, apdu.getSecureMessaging());
	}

	@Test
	public void testRewrapApdu(){
		IsoSecureMessagingCommandApdu apdu = new TR03110VerifySecureMessagingCommandApdu(Utils.concatByteArrays(apduHeader, commandData), null);
		IsoSecureMessagingCommandApdu result = (IsoSecureMessagingCommandApdu) apdu.rewrapApdu((byte) 0, commandData);
		
		byte [] expectedHeader = Arrays.copyOf(apduHeader, apduHeader.length);
		expectedHeader[0] = (byte) 0x80;
		assertEquals(apdu.getCommandData(), result.getCommandData());
		assertArrayEquals(expectedHeader, result.getHeader());
		assertArrayEquals(Utils.concatByteArrays(apduHeader, commandData), result.getPredecessor().toByteArray());
	}
	
}
