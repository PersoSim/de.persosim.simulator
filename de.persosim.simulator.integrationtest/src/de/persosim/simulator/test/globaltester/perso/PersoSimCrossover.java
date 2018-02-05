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

import de.persosim.simulator.perso.DefaultPersoGt;
import de.persosim.simulator.perso.Personalization;

public class PersoSimCrossover extends DefaultScriptIntegrationTest{

	@Override
	public List<String> getBundleSymbolicNamesForTestSpecificationsChip() {
		List<String> testSpecificationsChip = super.getBundleSymbolicNamesForTestSpecificationsChip();
		testSpecificationsChip.add("com.secunet.globaltester.testscripts.basics");
		testSpecificationsChip.add("com.secunet.globaltester.testscripts.bsi.tr03105_part3_3");
		testSpecificationsChip.add("com.secunet.globaltester.testscripts.integration");
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
		return Arrays.asList("GT Scripts Integrationtests/TestSuites/Layer 6/PACE/PACE_multipleSetAt.gt");
	}

	@Override
	protected int getExpectedNumberOfChipTestCases() {
		return 1;
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
		Personalization perso = new DefaultPersoGt();		
		
		enableSimulator(perso);
		
		performChipTest(getSampleConfigChip(), getTestCasesChip());
		
		TestResult chipResults = getChipTestResult();
		
		disableSimulator();
		
		assertEquals("Number of chip tests", getExpectedNumberOfChipTestCases(), chipResults.testCases);
		assertEquals("Chip test result", 0, chipResults.overallResult);
		
		cleanupChipTest();
	}

}
