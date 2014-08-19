package de.persosim.simulator.test.globaltester.perso;

import java.util.ArrayList;
import java.util.Collection;

import de.persosim.simulator.perso.DefaultPersoTestPkiTemplate01;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.test.globaltester.GtConstants;
import de.persosim.simulator.test.globaltester.GtSuiteDescriptor;
import de.persosim.simulator.test.globaltester.JobDescriptor;

//FIXME SLS following comments apply to all new Persos (without having looked at them in detail)
//FIXME SLS name of test class should be similar to that of the class under test
public class TestPerso001Test extends GtDefaultPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new DefaultPersoTestPkiTemplate01();
		}
		
		return persoCache;
	}

	//FIXME SLS This Method is overidden in all ne testcases, why not introduce a common ancestor?
	@Override
	public Collection<JobDescriptor> getAllApplicableGtTests() {
		Collection<JobDescriptor> retVal = 
		new ArrayList<JobDescriptor>();
		
		retVal.add(new GtSuiteDescriptor(GtConstants.PROJECT_EPA_EAC2_BSI, "EAC2_ISO7816_H_04a"));

		return retVal;
	}
	
}
