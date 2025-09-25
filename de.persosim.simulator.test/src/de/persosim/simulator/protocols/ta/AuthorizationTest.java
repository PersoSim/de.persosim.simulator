package de.persosim.simulator.protocols.ta;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.BitField;

public class AuthorizationTest extends PersoSimTestCase {

	byte [] field0Content, field1Content, field2Content;
	BitField field0, field1, field2;
	Authorization auth0;

	@Before
	public void setUp(){
		field0Content = new byte []{(byte) 0x00, (byte) 0x00};
		field1Content = new byte []{(byte) 0x00, (byte) 0b01000000};
		field2Content = new byte []{(byte) 0b01000000, (byte) 0x00};

		field0 = BitField.buildFromBigEndian(16, field0Content);
		field1 = BitField.buildFromBigEndian(16, field1Content);
		field2 = BitField.buildFromBigEndian(16, field2Content);

		auth0 = new Authorization(field0);
	}

	/**
	 * This test checks the effective authorization computed from the same object(s)
	 */
	@Test
	public void testBuildEffectiveAuthorization_Same(){
		Authorization auth1 = new Authorization(field1);
		Authorization auth2 = new Authorization(field1);

		Authorization effectiveAuth = auth1.buildEffectiveAuthorization(auth2);

		assertEquals(auth1, effectiveAuth);
	}

	/**
	 * This test checks the effective authorization computed from the same object(s)
	 */
	@Test
	public void testBuildEffectiveAuthorization_Disjunct(){
		Authorization auth1 = new Authorization(field1);
		Authorization auth2 = new Authorization(field2);

		Authorization effectiveAuth = auth1.buildEffectiveAuthorization(auth2);

		assertEquals(auth0, effectiveAuth);
	}

}
