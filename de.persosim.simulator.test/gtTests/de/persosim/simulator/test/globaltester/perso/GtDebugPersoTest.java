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
		
//		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_H_01"));  //PACE - unauthenticated, CAN
//		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_K_01"));  // TA
//		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_L_13a")); // AT - read DG
//		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_L_17"));  // Age verification
//		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_L_19"));  // CommunityID verification
//		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_R_01"));  // RI
		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_DATA_B_01"));  // RI


//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_H);
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_I);
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_J);
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_K);
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_L);
		
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_M);
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_N);
//		retVal.add(new SimulatorReset());
//		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_O_01"));
		
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_O);
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_P);
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_Q);
//		retVal.add(GtConstants.SUITE_EAC2_ISO7816_R);

//		retVal.add(GtConstants.SUITE_EAC2_DATA_A);
//		retVal.add(GtConstants.SUITE_EAC2_DATA_B);
//		retVal.add(GtConstants.SUITE_EAC2_DATA_C);
//		retVal.add(GtConstants.SUITE_EAC2_EIDDATA_B);
		return retVal;
	}
}
