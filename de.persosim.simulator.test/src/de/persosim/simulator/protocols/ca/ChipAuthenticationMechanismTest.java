package de.persosim.simulator.protocols.ca;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.security.KeyPair;
import java.security.PublicKey;

import org.junit.Test;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class ChipAuthenticationMechanismTest extends PersoSimTestCase {
	@Test
	public void testGetUncompressedTerminalEphemeralPublicKeyImmutability() {
		byte[] pubKeyBytes = HexString.toByteArray(
				"047D1EA24146C3ADAC11143E7267B4E3EC572534828DB54904877B8D6EFDC5C13123A9E955890447643735C4F0AB9093FAA0C96DEFA1CE9079DA0B3C43BE6A0255");
		byte[] privKeyBytes = HexString.toByteArray("1183F16814B3947D01DAED7F8D236769F5ABD8020FFF53C5E5FE86A8ABAB02D2");

		KeyPair keyPair = CryptoUtil.reconstructKeyPair(13, pubKeyBytes, privKeyBytes);

		ChipAuthenticationMechanism mechanism = new ChipAuthenticationMechanism(CaOid.OID_id_CA_DH_3DES_CBC_CBC, 1,
				keyPair.getPublic());

		PublicKey result = mechanism.getUncompressedTerminalEphemeralPublicKey();
		assertEquals(keyPair.getPublic(), result);
		assertNotSame(keyPair.getPublic(), result);
	}
}
