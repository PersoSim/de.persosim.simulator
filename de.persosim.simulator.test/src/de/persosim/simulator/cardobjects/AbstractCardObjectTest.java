package de.persosim.simulator.cardobjects;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class AbstractCardObjectTest extends PersoSimTestCase {
	
	AbstractFile masterFile;
	
	SecStatus securityStatus;
	
	class IdentifiableObjectImpl extends AbstractCardObject {
		protected int id;
		protected Collection<CardObjectIdentifier> identifiers;
		
		public IdentifiableObjectImpl(SecStatus securityStatus, int id) {
			this.id = id;
			this.identifiers = new ArrayList<CardObjectIdentifier>();
		}
		
		@Override
		public Collection<CardObjectIdentifier> getAllIdentifiers() {
			Collection<CardObjectIdentifier> result = super.getAllIdentifiers();
			result.addAll(identifiers);
			return result;
		}

		public void addOidIdentifier(CardObjectIdentifier identifier) {
			identifiers.add(identifier);
		}
		
		public int getId() {
			return id;
		}
		
		@Override
		public String toString() {
			return "" + id;
		}
	}
	
	@Before
	public void setUp() throws ReflectiveOperationException, AccessDeniedException{
		// set up OIDs
		byte[] oidByteArray1 = HexString.toByteArray("00112233445566778899");
		byte[] oidByteArray2 = HexString.toByteArray("00112233AABBCCDDEEFF");
		byte[] oidByteArray3 = HexString.toByteArray("55667788990011223344");
		
		Oid anonymousTypeOid1 = new Oid(oidByteArray1);
		OidIdentifier oidIdentifier1 = new OidIdentifier(anonymousTypeOid1);
		
		Oid anonymousTypeOid2 = new Oid(oidByteArray2);
		OidIdentifier oidIdentifier2 = new OidIdentifier(anonymousTypeOid2);
		
		Oid anonymousTypeOid3 = new Oid(oidByteArray3);
		OidIdentifier oidIdentifier3 = new OidIdentifier(anonymousTypeOid3);
		
		IdentifiableObjectImpl identifiableObjectImpl1 = new IdentifiableObjectImpl(securityStatus, 1);
		identifiableObjectImpl1.addOidIdentifier(oidIdentifier1);
		
		IdentifiableObjectImpl identifiableObjectImpl2 = new IdentifiableObjectImpl(securityStatus, 2);
		identifiableObjectImpl2.addOidIdentifier(oidIdentifier2);
		
		IdentifiableObjectImpl identifiableObjectImpl3 = new IdentifiableObjectImpl(securityStatus, 3);
		identifiableObjectImpl3.addOidIdentifier(oidIdentifier3);
		
		IdentifiableObjectImpl identifiableObjectImpl123 = new IdentifiableObjectImpl(securityStatus, 123);
		identifiableObjectImpl123.addOidIdentifier(oidIdentifier1);
		identifiableObjectImpl123.addOidIdentifier(oidIdentifier2);
		identifiableObjectImpl123.addOidIdentifier(oidIdentifier3);

		securityStatus = new SecStatus();
		
		// setup fresh object tree
		masterFile = new MasterFile();
		masterFile.setSecStatus(securityStatus);
		
		masterFile.addChild(identifiableObjectImpl1);
		masterFile.addChild(identifiableObjectImpl2);
		masterFile.addChild(identifiableObjectImpl3);
		masterFile.addChild(identifiableObjectImpl123);
	}
	
	/**
	 * Positive test: check whether a full match is returned correctly.
	 */
	@Test
	public void testFindChildren_FullMatch() {
		byte[] oidByteArray = HexString.toByteArray("55");
		Oid anonymousTypeOid = new Oid(oidByteArray) {
			@Override
			public String getIdString() {
				return "anonymous type OID";
			}
		};
		OidIdentifier oidIdentifier = new OidIdentifier(anonymousTypeOid);
		
		Collection<CardObject> cardObjects = masterFile.findChildren(oidIdentifier);
		
		ArrayList<Integer> expectedCardObjectIds = new ArrayList<Integer>();
		expectedCardObjectIds.add(3);
		expectedCardObjectIds.add(123);
		
		assertEquals(expectedCardObjectIds.size(), cardObjects.size());
		
		IdentifiableObjectImpl idObjImpl;
		int idObjImplId;
		for(CardObject cardObject : cardObjects) {
			assertTrue(cardObject instanceof IdentifiableObjectImpl);
			idObjImpl = (IdentifiableObjectImpl) cardObject;
			idObjImplId = idObjImpl.getId();
			
			assertTrue(expectedCardObjectIds.contains(idObjImplId));
			expectedCardObjectIds.remove(new Integer(idObjImplId));
		}
		
		assertEquals(0, expectedCardObjectIds.size());
	}
	
	/**
	 * Positive test: check whether several partial matches are returned correctly.
	 */
	@Test
	public void testFindChildren_MultiMatch() {
		byte[] oidByteArray = HexString.toByteArray("00");
		Oid anonymousTypeOid = new Oid(oidByteArray);
		OidIdentifier oidIdentifier = new OidIdentifier(anonymousTypeOid);
		
		Collection<CardObject> cardObjects = masterFile.findChildren(oidIdentifier);
		
		ArrayList<Integer> expectedCardObjectIds = new ArrayList<Integer>();
		expectedCardObjectIds.add(1);
		expectedCardObjectIds.add(2);
		expectedCardObjectIds.add(123);
		
		assertEquals(expectedCardObjectIds.size(), cardObjects.size());
		
		IdentifiableObjectImpl idObjImpl;
		int idObjImplId;
		for(CardObject cardObject : cardObjects) {
			assertTrue(cardObject instanceof IdentifiableObjectImpl);
			idObjImpl = (IdentifiableObjectImpl) cardObject;
			idObjImplId = idObjImpl.getId();
			
			assertTrue(expectedCardObjectIds.contains(idObjImplId));
			expectedCardObjectIds.remove(new Integer(idObjImplId));
		}
		
		assertEquals(0, expectedCardObjectIds.size());
	}
	
	/**
	 * Positive test: check for no match result.
	 */
	@Test
	public void testFindChildren_noMatch() {
		byte[] oidByteArray = HexString.toByteArray("FF");
		Oid anonymousTypeOid = new Oid(oidByteArray);
		OidIdentifier oidIdentifier = new OidIdentifier(anonymousTypeOid);
		
		Collection<CardObject> cardObjects = masterFile.findChildren(oidIdentifier);
		
		assertEquals(0, cardObjects.size());
	}
	
	/**
	 * Positive test: check whether several partial matches of multiple identifiers are returned correctly.
	 */
	@Test
	public void testFindChildren_MultipleIdentifierMatch() {
		byte[] oidByteArray1 = HexString.toByteArray("0011223344");
		Oid anonymousTypeOid1 = new Oid(oidByteArray1);
		
		byte[] oidByteArray2 = HexString.toByteArray("00112233AA");
		Oid anonymousTypeOid2 = new Oid(oidByteArray2);
		
		OidIdentifier oidIdentifier1 = new OidIdentifier(anonymousTypeOid1);
		OidIdentifier oidIdentifier2 = new OidIdentifier(anonymousTypeOid2);
		
		Collection<CardObject> cardObjects = masterFile.findChildren(oidIdentifier1, oidIdentifier2);
		
		assertEquals(1, cardObjects.size());
		
		assertEquals(123, ((IdentifiableObjectImpl) cardObjects.iterator().next()).getId());
	}

}
