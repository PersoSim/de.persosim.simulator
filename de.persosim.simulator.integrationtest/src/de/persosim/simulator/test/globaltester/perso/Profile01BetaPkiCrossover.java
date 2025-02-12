package de.persosim.simulator.test.globaltester.perso;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.junit.Ignore;
import org.junit.Test;

import com.secunet.globaltester.crossover.DefaultScriptIntegrationTest;
import com.secunet.globaltester.testcontrol.callback.soap.TestControlCallback.TestResult;
import com.secunet.globaltester.testscripts.bsi.tr03105_part3_3.ics.CertGenerator;

import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.Profile01BetaPki;

public class Profile01BetaPkiCrossover extends DefaultScriptIntegrationTest{

	@Override
	public IProject getSampleConfigChip() throws IOException {
		IProject sampleConfigProject = importSampleConfig("de.persosim.simulator.integrationtest", "Configs/CFG.DFLT.BETAPKI");
		SampleConfig sampleConfig = SampleConfig.getSampleConfigForProject(sampleConfigProject);

		sampleConfig.setHaveToSaveToProjectAfterPut(false);
		// Certificate generator needs some file to copy as a root certificate, it is never used in this case
		sampleConfig.put("TR-03105-3.3", "CVCA_ROOT_AT", CertGenerator.getCertGenDirTR03105_3_3() + "/DV_CERT_17.cvcert");
		// These are pregenerated certificates and the generator should use these instead of generating anything
		sampleConfig.put("TR-03105-3.3", "DV_CERT_17", CertGenerator.getCertGenDirTR03105_3_3() + "/DV_CERT_17.cvcert");
		sampleConfig.put("TR-03105-3.3", "AT_CERT_17f", CertGenerator.getCertGenDirTR03105_3_3() + "/AT_CERT_17f.cvcert");
		sampleConfig.put("TR-03105-3.3", "AT_KEY_17", CertGenerator.getCertGenDirTR03105_3_3() + "/AT_KEY_17");
		sampleConfig.put("TR-03105-3.3", "AT_KEY_17_PRIV", CertGenerator.getCertGenDirTR03105_3_3() + "/AT_KEY_17.pkcs8");

		sampleConfig.saveToProject();
		sampleConfig.setHaveToSaveToProjectAfterPut(true);

		return sampleConfigProject;
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

		assertEquals("Chip test result", 0, chipResults.overallResult);

		cleanupChipTest();
	}

}
