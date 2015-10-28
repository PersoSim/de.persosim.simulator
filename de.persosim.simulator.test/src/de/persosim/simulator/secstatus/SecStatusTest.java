package de.persosim.simulator.secstatus;

import java.util.Collection;
import java.util.HashSet;

import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;

public class SecStatusTest extends PersoSimTestCase{
	
	SecStatus securityStatus;
	@Mocked SecMechanism mechanism;
	
	@Before
	public void setUp(){
		securityStatus = new SecStatus();
	}
	
	//TODO define tests for SecStatus
	
	/**
	 * Positive test case: check that the getCurrentMechanisms method correctly finds mechanisms
	 */
	@Test
	public void testGetCurrentMechanisms()
	{
		SecMechanism mechanismToFind = new SecMechanism() {
			@Override
			public boolean needsDeletionInCaseOf(SecurityEvent event) {
				return false;
			}
		};
		
		populateSecStatus(SecContext.APPLICATION, mechanismToFind);
		
		Collection<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(mechanismToFind.getClass());
		
		Collection<SecMechanism> foundMechanisms = securityStatus.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		assertEquals(1, foundMechanisms.size());
		assertSame(mechanismToFind, foundMechanisms.iterator().next());
		
	}
	
}
