package de.persosim.simulator.protocols.ca;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.persosim.simulator.protocols.pace.Pace;

public class CaOidTest {

	/**
	 * Negative test: That null is returned for an Oid that is not an CaOid
	 */
	@Test
	public void testGetStringRepresentation_nonCaOid() {
		assertNull (CaOid.id_CA_ECDH_AES_CBC_CMAC_128.getStringRepresentation(Pace.id_PACE_ECDH_GM_AES_CBC_CMAC_128.toByteArray()));
	}

}
