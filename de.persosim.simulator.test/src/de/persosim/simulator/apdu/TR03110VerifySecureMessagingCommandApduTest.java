package de.persosim.simulator.apdu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;

public class TR03110VerifySecureMessagingCommandApduTest extends PersoSimTestCase {
	byte [] apduData;
	
	@Before
	public void setUp(){
		apduData = new byte [] { (byte) 0x8C, 0x20, (byte) 0x80, 0x00, 0x01, (byte) 0xFF};
	}
	
	@Test
	public void testGetSecureMessaging(){
		IsoSecureMessagingCommandApdu apdu = new TR03110VerifySecureMessagingCommandApdu(apduData);
		assertEquals(0b00000011, apdu.getSecureMessaging());
	}

	@Test
	public void testSetSecureMessaging(){
		IsoSecureMessagingCommandApdu apdu = new TR03110VerifySecureMessagingCommandApdu(apduData);
		apdu.setSecureMessaging((byte) 0);
		assertEquals(0, apdu.getSecureMessaging());
	}
	
}
