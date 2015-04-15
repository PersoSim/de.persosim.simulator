package de.persosim.simulator.test.globaltester.perso;

import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.util.Collection;

import org.junit.Test;

import de.persosim.simulator.test.globaltester.GtSuiteDescriptor;
import de.persosim.simulator.test.globaltester.JobDescriptor;
import de.persosim.simulator.test.globaltester.SimulatorReset;

public class GtDefaultPersoTestRi extends GtDefaultPersoTest {
	
	@Test
	public void testAllApplicableTests() throws Exception {
		String suiteName;
		
		gtServer.clearCollectedResults();
		
		Collection<JobDescriptor> suites = getAllApplicableGtTests();
		for (JobDescriptor curSuite : suites) {
			if (curSuite instanceof SimulatorReset){
				resetSimulator();
			} else if (curSuite instanceof GtSuiteDescriptor){
				suiteName = ((GtSuiteDescriptor)curSuite).getTestSuiteName();
				log(this, "RINOTE: test suite name: " + suiteName);
				
				if((suiteName.equals("testsuite_ISO7816_R")) || (suiteName.startsWith("EAC2_ISO7816_R"))) {
					this.setRiKey((byte) 0x01, false);
					gtServer.runSuiteAndSaveResults((GtSuiteDescriptor)curSuite);
					this.setRiKey((byte) 0x02, true);
					gtServer.runSuiteAndSaveResults((GtSuiteDescriptor)curSuite);
				} else {
					gtServer.runSuiteAndSaveResults((GtSuiteDescriptor)curSuite);
				}
			}
		}
		
		gtServer.checkAndClearResults(0, 0);
	}
	
}
