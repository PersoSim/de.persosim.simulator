package de.persosim.simulator.test.globaltester.perso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.perso.DefaultPersoGt;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.test.globaltester.GlobalTesterTest;
import de.persosim.simulator.test.globaltester.GtConstants;
import de.persosim.simulator.test.globaltester.JobDescriptor;
import de.persosim.simulator.test.globaltester.SimulatorReset;

/**
 * Main testcase that checks the configuration of the DefaultPerso against all
 * GlobalTester tests known to be good.
 * <p/>
 * This TestCase shall be extended for each new working functionality and
 * provide a means to quickly check the overall status.
 * <p/>
 * For debugging purposes the content of this Suite can be quickly adapted to
 * contain only specific configurations. Therefore code is contained in line
 * comments that can be quickly adapted to specific test means. When using this
 * option keep in mind that all results of such debugging should be covered by a
 * specific unit test.
 * 
 * @author amay
 * 
 */
public class GtDefaultPersoTest extends GlobalTesterTest {

	@Override
	public Personalization getPersonalization() {
		return new DefaultPersoGt();
	}
	
	@Override
	public Collection<String> getSupportedProfiles() {
		HashSet<String> retVal = new HashSet<String>();
		retVal.add(GtConstants.PROFILE_CA2);
		retVal.add(GtConstants.PROFILE_ECDH);
		retVal.add(GtConstants.PROFILE_PACE);
		retVal.add(GtConstants.PROFILE_PACE_CAN);
		retVal.add(GtConstants.PROFILE_PACE_ECDH);
		retVal.add(GtConstants.PROFILE_PACE_GM);
		retVal.add(GtConstants.PROFILE_TA2);
		retVal.add(GtConstants.PROFILE_EPASSPORT);
		retVal.add(GtConstants.PROFILE_eID);
		retVal.add(GtConstants.PROFILE_MIG);
		retVal.add(GtConstants.PROFILE_ECDSA);
		retVal.add(GtConstants.PROFILE_EPASSPORT_DG3);
		retVal.add(GtConstants.PROFILE_EPASSPORT_DG4);
		retVal.add(GtConstants.PROFILE_RI);
		retVal.add(GtConstants.PROFILE_AUX);
		retVal.add(GtConstants.PROFILE_CNG_PIN_PUK);
		retVal.add(GtConstants.PROFILE_CNG_PIN_AR);
		retVal.add(GtConstants.PROFILE_CNG_CAN_AR);
		retVal.add(GtConstants.PROFILE_eID_DG1);
		retVal.add(GtConstants.PROFILE_eID_DG2);
		retVal.add(GtConstants.PROFILE_eID_DG3);
		retVal.add(GtConstants.PROFILE_eID_DG4);
		retVal.add(GtConstants.PROFILE_eID_DG5);
		retVal.add(GtConstants.PROFILE_eID_DG6);
		retVal.add(GtConstants.PROFILE_eID_DG7);
		retVal.add(GtConstants.PROFILE_eID_DG8);
		retVal.add(GtConstants.PROFILE_eID_DG9);
		retVal.add(GtConstants.PROFILE_eID_DG10);
		retVal.add(GtConstants.PROFILE_eID_DG11);
		retVal.add(GtConstants.PROFILE_eID_DG12);
		retVal.add(GtConstants.PROFILE_eID_DG13);
		retVal.add(GtConstants.PROFILE_eID_DG17);
		retVal.add(GtConstants.PROFILE_eID_DG18);
		retVal.add(GtConstants.PROFILE_eID_DG19);
		retVal.add(GtConstants.PROFILE_eID_DG20);
		retVal.add(GtConstants.PROFILE_eID_DG21);
		return retVal;
	}
	
	@Override
	public Collection<JobDescriptor> getAllApplicableGtTests() {
		Collection<JobDescriptor> retVal = 
		new ArrayList<JobDescriptor>();
		
		retVal.add(GtConstants.SUITE_EAC2_ISO7816_H);
		retVal.add(GtConstants.SUITE_EAC2_ISO7816_I);
		retVal.add(GtConstants.SUITE_EAC2_ISO7816_J);
		retVal.add(GtConstants.SUITE_EAC2_ISO7816_K);
		retVal.add(GtConstants.SUITE_EAC2_ISO7816_L);
		retVal.add(GtConstants.SUITE_EAC2_ISO7816_M);
		retVal.add(GtConstants.SUITE_EAC2_ISO7816_N);

		retVal.add(new SimulatorReset());

		retVal.add(GtConstants.SUITE_EAC2_ISO7816_O);
		retVal.add(GtConstants.SUITE_EAC2_ISO7816_P);

		retVal.add(new SimulatorReset());
	    
		retVal.add(GtConstants.SUITE_EAC2_ISO7816_Q);
		retVal.add(GtConstants.SUITE_EAC2_ISO7816_R);
		
//		retVal.add(GtConstants.SUITE_EAC2_DATA_A);
//		retVal.add(GtConstants.SUITE_EAC2_DATA_B);
//		retVal.add(GtConstants.SUITE_EAC2_DATA_C);
//		retVal.add(GtConstants.SUITE_EAC2_EIDDATA_B);
		
		retVal.add(GtConstants.SUITE_SAC_ISO7816_P);
		
		return retVal;
	}

}
