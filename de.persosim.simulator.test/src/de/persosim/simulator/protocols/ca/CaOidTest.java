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
		assertNull (CaOid.getStringRepresentation(Pace.id_PACE_DH_GM_3DES_CBC_CBC));
	}

}
