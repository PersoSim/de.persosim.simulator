package de.persosim.simulator.test.globaltester.perso;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.junit.Ignore;
import org.junit.Test;

import com.secunet.globaltester.crossover.DefaultScriptIntegrationTest;
import com.secunet.globaltester.testcontrol.callback.soap.TestControlCallback.TestResult;

import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.Profile01BetaPki;

public class Profile01BetaPkiCrossover extends DefaultScriptIntegrationTest{

	@Override
	public List<String> getBundleSymbolicNamesForTestSpecificationsChip() {
		List<String> testSpecificationsChip = super.getBundleSymbolicNamesForTestSpecificationsChip();
		testSpecificationsChip.add("com.secunet.globaltester.testscripts.basics");
		testSpecificationsChip.add("com.secunet.globaltester.testscripts.bsi.tr03105_part3_3");
		return testSpecificationsChip;
	}
	
	@Override
	public IProject getSampleConfigChip() throws IOException {
		return importSampleConfig("de.persosim.simulator.integrationtest", "Configs/CFG.DFLT.BETAPKI");
	}

	@Override
	public IProject getSampleConfigTerminal() throws IOException {
		return null;
	}

	@Override
	public List<String> getTestCasesTerminal() {
		return Arrays.asList("Enter terminal test case path here");
	}
	
	@Override
	public List<String> getTestCasesChip() {
		return Arrays.asList("GT Scripts BSI TR03105 Part 3.3" + "/TestSuites/Layer6/ISO7816_H/EAC2_ISO7816_H_01.gt", //PACE - unauthenticated, CAN
				"GT Scripts BSI TR03105 Part 3.3" + "/TestSuites/Layer6/ISO7816_Q/EAC2_ISO7816_Q_01.gt"); // TA and age verification
	}

	@Override
	protected int getExpectedNumberOfChipTestCases() {
		return 2;
	}
	
	@Ignore
	@Test
	public void defaultTest() throws Exception {
		startTerminalTest();
		waitForTerminalTestReady();
		
		performChipTest();
		
		defaultAsserts();
		
		cleanupTerminalTest();
		cleanupChipTest();
	}
	
	@Test
	public void smokeTest() throws Exception {
		Personalization perso = new Profile01BetaPki();		
		
		enableSimulator(perso);
		
		performChipTest(getSampleConfigChip(), getTestCasesChip());
		
		TestResult chipResults = getChipTestResult();
		
		disableSimulator();
		
		assertEquals("Number of chip tests", getExpectedNumberOfChipTestCases(), chipResults.testCases);
		assertEquals("Chip test result", 0, chipResults.overallResult);
		
		cleanupChipTest();
	}

}
