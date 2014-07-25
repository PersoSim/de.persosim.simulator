package de.persosim.simulator.test.globaltester.perso;

import java.util.ArrayList;
import java.util.Collection;

import de.persosim.simulator.test.globaltester.GtConstants;
import de.persosim.simulator.test.globaltester.GtSuiteDescriptor;
import de.persosim.simulator.test.globaltester.JobDescriptor;

/**
 * Debug Testcase
 * <p/>
 * This testcase is intended to be modified during development on every branch
 * in order to speed up testing against GlobalTester.
 * <p/>
 * The contents of this testcase are a subset of the contents checked within
 * {@link GtDefaultPersoTest} and can be altered as needed. In order to keep the
 * CI runs clean when a branch is finished this suite should not produce any
 * failures.
 * 
 * @author amay
 * 
 */
public class GtDebugPersoTest extends GtDefaultPersoTest {

	
	@Override
	public Collection<JobDescriptor> getAllApplicableGtTests() {
		Collection<JobDescriptor> retVal = 
		new ArrayList<JobDescriptor>();
		
//		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_H_01"));
//		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_K_01"));
		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_I_01"));
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_P);

		return retVal;
	}
}
