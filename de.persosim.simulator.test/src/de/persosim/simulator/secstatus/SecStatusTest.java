package de.persosim.simulator.secstatus;

import mockit.Mocked;

import org.junit.Before;

import de.persosim.simulator.test.PersoSimTestCase;

public class SecStatusTest extends PersoSimTestCase{
	
	SecStatus securityStatus;
	@Mocked SecMechanism mechanism;
	
	@Before
	public void setUp(){
		securityStatus = new SecStatus();
	}
	
	//TODO define tests for SecStatus
}
