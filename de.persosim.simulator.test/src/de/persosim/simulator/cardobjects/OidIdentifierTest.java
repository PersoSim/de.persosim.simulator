package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.test.PersoSimTestCase;

public class OidIdentifierTest extends PersoSimTestCase {
	
	private static final byte[] OID_BYTES_0_TO_2 = new byte[] { 0, 1, 2 };
	private static final byte[] OID_BYTES_0_TO_4 = new byte[] { 0, 1, 2, 3, 4 };
	private static final byte[] OID_BYTES_0_TO_6 = new byte[] { 0, 1, 2, 3, 4, 5, 6};

	private static final byte[] OID_BYTES_6_TO_0 = new byte[] { 6, 5, 4, 3, 2, 1, 0};

	CardObject testObject;
	Collection<CardObjectIdentifier> testObjectIdentifiers;
	private OidIdentifier testIdentifier;

	@Before
	public void setUp() throws ReflectiveOperationException, AccessDeniedException {
		testObjectIdentifiers = new LinkedList<>();
		testObject = new AbstractCardObject() {

			@Override
			public Collection<CardObjectIdentifier> getAllIdentifiers() {
				return testObjectIdentifiers;
			}

		};
		
		testIdentifier = new OidIdentifier(new Oid(OID_BYTES_0_TO_4));
	}

	@Test
	public void testMatches_sameOidIdentifier() throws Exception {
		testObjectIdentifiers.add(testIdentifier);
		
		assertTrue(testIdentifier.matches(testObject));
	}

	@Test
	public void testMatches_noOidIdentifier() throws Exception {
		assertFalse(testIdentifier.matches(testObject));
	}

	@Test
	public void testMatches_similarOidIdentifier() throws Exception {
		testObjectIdentifiers.add(new OidIdentifier(new Oid(OID_BYTES_0_TO_4)));
		
		assertTrue(testIdentifier.matches(testObject));
	}

	@Test
	public void testMatches_longerOidIdentifier() throws Exception {
		testObjectIdentifiers.add(new OidIdentifier(new Oid(OID_BYTES_0_TO_6)));
		
		assertTrue(testIdentifier.matches(testObject));
	}

	@Test
	public void testMatches_shorterOidIdentifier() throws Exception {
		testObjectIdentifiers.add(new OidIdentifier(new Oid(OID_BYTES_0_TO_2)));
		
		assertFalse(testIdentifier.matches(testObject));
	}

	@Test
	public void testMatches_matchMultipleOidIdentifier() throws Exception {
		testObjectIdentifiers.add(new OidIdentifier(new Oid(OID_BYTES_6_TO_0)));
		testObjectIdentifiers.add(new OidIdentifier(new Oid(OID_BYTES_0_TO_6)));
		
		assertTrue(testIdentifier.matches(testObject));
	}

}
