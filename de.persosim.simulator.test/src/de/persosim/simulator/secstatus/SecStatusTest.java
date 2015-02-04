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
	
}
