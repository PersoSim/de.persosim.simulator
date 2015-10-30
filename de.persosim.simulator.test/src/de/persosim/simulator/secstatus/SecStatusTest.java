package de.persosim.simulator.secstatus;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.seccondition.SecCondition;
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
	 * Positive test case: check the updateSecStatus method in the SecStatus class.
	 */
	@Test
	public void testUpdateSecStatus_Input_Is_ProcessingData_Object()
	{
		SecStatus test = new SecStatus();
		ProcessingData lol = new ProcessingData();
		test.updateSecStatus(lol);		
	}
	
	/**
	 * Positive test case: check the getCurrentMechanisms method in the Secstatus class.
	 */
	@Test
	public void testGetCurrentMechanisms()
	{
		Collection<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		@SuppressWarnings("unused")
		Collection<SecMechanism> test = securityStatus.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		
	}
	
	/**
	 * Positive test case checking the life cycle allowing access even if the
	 * security conditions do not match.
	 */
	@Test
	public void testCheckAccessConditionsLifecycleAllows() {
		Iso7816LifeCycleState state = Iso7816LifeCycleState.CREATION;
		SecStatus securityStatus = new SecStatus();
		SecCondition secCondition = new SecCondition() {
			
			@Override
			public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
				return Collections.emptySet();
			}
			
			@Override
			public boolean check(Collection<SecMechanism> mechanisms) {
				return false;
			}
		};
		assertTrue(securityStatus.checkAccessConditions(state, secCondition, SecContext.APPLICATION));
	}
	
	/**
	 * Positive test case checking whether the security conditions prohibit access.
	 */
	@Test
	public void testCheckAccessConditionsSecurityConditionsProhibit(){

		Iso7816LifeCycleState state = Iso7816LifeCycleState.OPERATIONAL_ACTIVATED;
		SecStatus securityStatus = new SecStatus();
		SecCondition secCondition = new SecCondition() {
			
			@Override
			public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
				return Collections.emptySet();
			}
			
			@Override
			public boolean check(Collection<SecMechanism> mechanisms) {
				return false;
			}
		};
		assertFalse(securityStatus.checkAccessConditions(state, secCondition, SecContext.APPLICATION));
	}
	
	/**
	 * Positive test case checking whether the security conditions can allow access.
	 */
	@Test
	public void testCheckAccessConditionsSecurityConditionsAllow(){

		Iso7816LifeCycleState state = Iso7816LifeCycleState.OPERATIONAL_ACTIVATED;
		SecStatus securityStatus = new SecStatus();
		SecCondition secCondition = new SecCondition() {
			
			@Override
			public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
				return Collections.emptySet();
			}
			
			@Override
			public boolean check(Collection<SecMechanism> mechanisms) {
				return true;
			}
		};
		assertTrue(securityStatus.checkAccessConditions(state, secCondition, SecContext.APPLICATION));
	}
}
