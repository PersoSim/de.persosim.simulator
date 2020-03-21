package de.persosim.simulator.test.globaltester.perso;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.junit.Test;

import com.secunet.globaltester.testcontrol.callback.soap.TestControlCallback.TestResult;

import de.persosim.simulator.perso.DefaultPersoGt;
import de.persosim.simulator.perso.Personalization;

public class ExtAuthCrossover extends DefaultPersoGtCrossover {
	
	@Override
	protected void modifySampleConfigForPerso(SampleConfig sampleConfig)
			throws IOException {
		super.modifySampleConfigForPerso(sampleConfig);

	    sampleConfig.put("TAv2", "AUTH_EXT", "true");
	    
	}

	@Override
	public IProject getSampleConfigTerminal() throws IOException {
		return null;
	}

	@Override
	public List<String> getTestCasesTerminal() {
		return Collections.emptyList();
	}
	
	@Override
	public List<String> getTestCasesChip() {
		ArrayList<String> testcasesChip = new ArrayList<>();
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/generate_data/testsuite_Gen_ALL_Certificate_Sets.gtsuite");

		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_L/EAC2_ISO7816_L_38.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_L/EAC2_ISO7816_L_39.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_L/EAC2_ISO7816_L_40.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_L/EAC2_ISO7816_L_41.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_L/EAC2_ISO7816_L_42.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_L/EAC2_ISO7816_L_43.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_L/EAC2_ISO7816_L_44.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_L/EAC2_ISO7816_L_45.gt");

//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_09.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_10.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_11.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_12.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_13.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_14.gt");

		
		return testcasesChip;
	}

	@Test
	public void defaultTest() throws Exception {
		Personalization perso = new DefaultPersoGt();		
		
		enableSimulator(perso);

		performChipTest(getSampleConfigChip(), getTestCasesChip());
		
		TestResult chipResults = getChipTestResult();
		
		disableSimulator();
		
		assertEquals("Chip test result", 0, chipResults.overallResult);
		
		cleanupChipTest();
	}

	@Test
	public void isoMTest() throws Exception {
		Personalization perso = new DefaultPersoGt();		
		
		enableSimulator(perso);
		
		ArrayList<String> testcasesChip = new ArrayList<>();
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/generate_data/testsuite_Gen_ALL_Certificate_Sets.gtsuite");

		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_01.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_02.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_03.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_04.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_05.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_06.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_07.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_08.gt");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_09.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_10.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_11.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_12.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_13.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_M/EAC2_ISO7816_M_14.gt");
		
		performChipTest(getSampleConfigChip(), testcasesChip);
		
		TestResult chipResults = getChipTestResult();
		
		disableSimulator();
		
		assertEquals("Chip test result", 2, chipResults.overallResult);
		
		cleanupChipTest();
	}

}
