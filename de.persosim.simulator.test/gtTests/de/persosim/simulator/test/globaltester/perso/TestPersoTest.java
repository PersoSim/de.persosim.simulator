package de.persosim.simulator.test.globaltester.perso;

import java.util.ArrayList;
import java.util.Collection;

import de.persosim.simulator.test.globaltester.GtConstants;
import de.persosim.simulator.test.globaltester.GtSuiteDescriptor;
import de.persosim.simulator.test.globaltester.JobDescriptor;

public class TestPersoTest extends GtDefaultPersoTest {
	
	@Override
	public Collection<JobDescriptor> getAllApplicableGtTests() {
		Collection<JobDescriptor> retVal = 
		new ArrayList<JobDescriptor>();
		
		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_P_01"));
		//TODO implement more testcases here

		return retVal;
	}
	
}
