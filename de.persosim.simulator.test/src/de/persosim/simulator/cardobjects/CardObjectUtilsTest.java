package de.persosim.simulator.cardobjects;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;

import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;

public class CardObjectUtilsTest extends PersoSimTestCase {
	/**
	 * Positive test case checking the life cycle allowing access even if the
	 * security conditions do not match.
	 */
	@Test
	public void testCheckAccessConditionsLifecycleAllows() {
		Iso7816LifeCycleState state = Iso7816LifeCycleState.CREATION;
		SecStatus securityStatus = new SecStatus();
		Collection<SecCondition> secConditions = new HashSet<>();
		secConditions.add(new SecCondition() {
			
			@Override
			public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
				return Collections.emptySet();
			}
			
			@Override
			public boolean check(Collection<SecMechanism> mechanisms) {
				return false;
			}
		});
		assertTrue(CardObjectUtils.checkAccessConditions(state, securityStatus, secConditions, SecContext.APPLICATION));
	}
	
	/**
	 * Positive test case checking whether the security conditions prohibit access.
	 */
	@Test
	public void testCheckAccessConditionsSecurityConditionsProhibit(){

		Iso7816LifeCycleState state = Iso7816LifeCycleState.OPERATIONAL_ACTIVATED;
		SecStatus securityStatus = new SecStatus();
		Collection<SecCondition> secConditions = new HashSet<>();
		secConditions.add(new SecCondition() {
			
			@Override
			public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
				return Collections.emptySet();
			}
			
			@Override
			public boolean check(Collection<SecMechanism> mechanisms) {
				return false;
			}
		});
		assertFalse(CardObjectUtils.checkAccessConditions(state, securityStatus, secConditions, SecContext.APPLICATION));
	}
	
	/**
	 * Positive test case checking whether the security conditions can allow access.
	 */
	@Test
	public void testCheckAccessConditionsSecurityConditionsAllow(){

		Iso7816LifeCycleState state = Iso7816LifeCycleState.OPERATIONAL_ACTIVATED;
		SecStatus securityStatus = new SecStatus();
		Collection<SecCondition> secConditions = new HashSet<>();
		secConditions.add(new SecCondition() {
			
			@Override
			public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
				return Collections.emptySet();
			}
			
			@Override
			public boolean check(Collection<SecMechanism> mechanisms) {
				return true;
			}
		});
		assertTrue(CardObjectUtils.checkAccessConditions(state, securityStatus, secConditions, SecContext.APPLICATION));
	}
}
