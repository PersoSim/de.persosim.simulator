package de.persosim.simulator.secstatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.Authorization;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.HexString;

public class AuthorizationStoreTest extends PersoSimTestCase {
	
	byte [] field0Content, field1Content, field2Content;
	BitField field0, field1, field2;
	Authorization auth0, auth1, auth2;
	Oid oid1, oid2, oid3;
	
	@Before
	public void setUp(){
		field0Content = new byte []{(byte) 0x00, (byte) 0x00};
		field1Content = new byte []{(byte) 0x00, (byte) 0b01000000};
		field2Content = new byte []{(byte) 0b01000000, (byte) 0x00};
		
		field0 = BitField.buildFromBigEndian(16, field0Content);
		field1 = BitField.buildFromBigEndian(16, field1Content);
		field2 = BitField.buildFromBigEndian(16, field2Content);
		
		auth0 = new Authorization(field0);
		auth1 = new Authorization(field1);
		auth2 = new Authorization(field2);
		
		oid1 = new Oid(HexString.toByteArray("0011"));
		oid2 = new Oid(HexString.toByteArray("001122"));
		oid3 = new Oid(HexString.toByteArray("00112233"));
	}
	
	/**
	 * This test checks equals for same objects
	 */
	@Test
	public void testEquals_Same(){
		HashMap<Oid, Authorization> auths1 = new HashMap<>();
		auths1.put(oid1, auth1);
		auths1.put(oid2, auth2);
		AuthorizationStore authStore1 = new AuthorizationStore(auths1);
		
		HashMap<Oid, Authorization> auths2 = new HashMap<>();
		auths2.put(oid1, auth1);
		auths2.put(oid2, auth2);
		AuthorizationStore authStore2 = new AuthorizationStore(auths2);
		
		assertEquals(authStore1, authStore2);
	}
	
	/**
	 * This test checks equals for different number of registered OIDs
	 */
	@Test
	public void testEquals_DifferentOids(){
		HashMap<Oid, Authorization> auths1 = new HashMap<>();
		auths1.put(oid1, auth1);
		auths1.put(oid2, auth2);
		AuthorizationStore authStore1 = new AuthorizationStore(auths1);
		
		HashMap<Oid, Authorization> auths2 = new HashMap<>();
		auths2.put(oid1, auth1);
		auths2.put(oid2, auth2);
		auths2.put(oid3, auth0);
		AuthorizationStore authStore2 = new AuthorizationStore(auths2);
		
		assertNotEquals(authStore1, authStore2);
	}
	
	/**
	 * This test checks equals for different authorizations registered for same OIDs
	 */
	@Test
	public void testEquals_DifferentAuths(){
		HashMap<Oid, Authorization> auths1 = new HashMap<>();
		auths1.put(oid1, auth1);
		auths1.put(oid2, auth2);
		AuthorizationStore authStore1 = new AuthorizationStore(auths1);
		
		HashMap<Oid, Authorization> auths2 = new HashMap<>();
		auths2.put(oid1, auth1);
		auths2.put(oid2, auth0);
		AuthorizationStore authStore2 = new AuthorizationStore(auths2);
		
		assertNotEquals(authStore1, authStore2);
	}
	
}
