package de.persosim.simulator.protocols;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.protocols.ca.Ca;
import de.persosim.simulator.protocols.ri.Ri;
import de.persosim.simulator.protocols.ri.RiOid;
import de.persosim.simulator.utils.HexString;


public class GenericOidTest
{
	@Test
	public void testDotStringConversion()
	{
		String riOidAsDotString = new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)).getOid().toDotString();
		// System.out.println("RiOid: " + riOidAsDotString);
		GenericOid genericOID = new GenericOid(riOidAsDotString);
		// System.out.println("GenericOid: " + genericOID.toDotString());
		assertEquals(riOidAsDotString, genericOID.toDotString());

		byte[] rawRiOid = new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)).getOid().toByteArray();
		String rawRiOidAsHexString = HexString.encode(rawRiOid);
		// System.out.println("RiOid raw: " + rawRiOidAsHexString);
		byte[] rawGenericOid = genericOID.toByteArray();
		String rawGenericOidAsHexString = HexString.encode(rawGenericOid);
		// System.out.println("GenericOid raw: " + rawGenericOidAsHexString);
		assertEquals(rawRiOidAsHexString, rawGenericOidAsHexString);

		GenericOid genericOIDFromRawRiOid = new GenericOid(rawRiOid);
		assertEquals(riOidAsDotString, genericOIDFromRawRiOid.toDotString());
		assertEquals(rawRiOidAsHexString, HexString.encode(genericOIDFromRawRiOid.toByteArray()));

		String caOidAsDotString = Ca.OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128.getOid().toDotString();
		// System.out.println("CaOid: " + caOidAsDotString);
		genericOID = new GenericOid(caOidAsDotString);
		// System.out.println("GenericOid: " + genericOID.toDotString());
		assertEquals(caOidAsDotString, genericOID.toDotString());
	}

}
