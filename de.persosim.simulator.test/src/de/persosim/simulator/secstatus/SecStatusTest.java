package de.persosim.simulator.secstatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;
import mockit.Mocked;

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
	
	/**
	 * Adds a mechanism to the {@link SecStatus} by wrapping it in a {@link SecStatusMechanismUpdatePropagation}
	 * @param context
	 * @param mechanisms
	 */
	private void populateSecStatus(SecContext context, SecMechanism ...mechanisms){
		for (SecMechanism mechanism : mechanisms){
			SecStatusMechanismUpdatePropagation updatePropagation = new SecStatusMechanismUpdatePropagation(context, mechanism);
			securityStatus.updateMechanisms(updatePropagation);	
		}
	}
	
	/**
	 * Check that restoring using an incorrect ID causes the correct exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRestoreNotExisting(){
		securityStatus.restoreSecStatus(10);
	}
	
	@Test
	public void testStoreAndRestoreStatus(){
		SecMechanism beforeStoring = new SecMechanism() {
			@Override
			public boolean needsDeletionInCaseOf(SecurityEvent event) {
				return false;
			}
		};
		SecMechanism afterStoring = new SecMechanism() {
			
			@Override
			public boolean needsDeletionInCaseOf(SecurityEvent event) {
				return true;
			}
		};

		securityStatus.storeSecStatus(0);
		
		populateSecStatus(SecStatus.SecContext.GLOBAL, beforeStoring);
		
		Collection<Class<? extends SecMechanism>> previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(beforeStoring.getClass());
		
		Collection<SecMechanism> mechanismsBeforeStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(1, mechanismsBeforeStore.size());
		assertSame(beforeStoring, mechanismsBeforeStore.iterator().next());
		
		securityStatus.storeSecStatus(1);
		
		populateSecStatus(SecStatus.SecContext.GLOBAL, afterStoring);
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(beforeStoring.getClass());
		Collection<SecMechanism> mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(1, mechanismsAfterStore.size());
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(afterStoring.getClass());
		mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(1, mechanismsAfterStore.size());
		
		securityStatus.restoreSecStatus(1);
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(beforeStoring.getClass());
		mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(1, mechanismsAfterStore.size());
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(afterStoring.getClass());
		mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(0, mechanismsAfterStore.size());
		
		securityStatus.restoreSecStatus(0);
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(beforeStoring.getClass());
		mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(0, mechanismsAfterStore.size());
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(afterStoring.getClass());
		mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(0, mechanismsAfterStore.size());
	}
}
