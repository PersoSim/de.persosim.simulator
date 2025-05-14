package de.persosim.simulator.test.globaltester.perso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.globaltester.base.resources.GtResourceHelper;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.junit.Test;

import com.secunet.globaltester.crossover.DefaultScriptIntegrationTest;
import com.secunet.globaltester.cvcerts.authority.certgen.CertGeneratorBase;
import com.secunet.globaltester.testcontrol.callback.soap.TestControlCallback.SubTestResult;
import com.secunet.globaltester.testcontrol.callback.soap.TestControlCallback.TestResult;

import de.persosim.simulator.perso.DefaultPersoGt;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoGtCrossover extends DefaultScriptIntegrationTest{

	public static final String isRootFolderName = CertGeneratorBase.CERTIFICATES_DIR + "/cv/is";
	public static final String atRootFolderName = CertGeneratorBase.CERTIFICATES_DIR + "/cv/at";
	public static final String stRootFolderName = CertGeneratorBase.CERTIFICATES_DIR + "/cv/st";

	protected static final String eac2Certs = com.secunet.globaltester.protocols.eac2.certificates.Eac2CertificatesFactory.PROTOCOL_NAME;


	@Override
	public IProject getSampleConfigChip() throws Exception {
		IProject sampleConfigProject = super.getSampleConfigChip();
		SampleConfig sampleConfig = SampleConfig.getSampleConfigForProject(sampleConfigProject);
		//remove unsupported profiles
        modifySampleConfigForPerso(sampleConfig);

		//copy root certificates/keys
		GtResourceHelper.copyPluginFilesToWorkspaceProject("de.persosim.simulator", sampleConfig.getProject() .getFolder(isRootFolderName),
				"personalization/gtCertificates/CFG.DFLT.EAC.IS", Collections.emptyList());
		GtResourceHelper.copyPluginFilesToWorkspaceProject("de.persosim.simulator", sampleConfig.getProject() .getFolder(atRootFolderName),
				"personalization/gtCertificates/CFG.DFLT.EAC.AT", Collections.emptyList());
		GtResourceHelper.copyPluginFilesToWorkspaceProject("de.persosim.simulator", sampleConfig.getProject() .getFolder(stRootFolderName),
				"personalization/gtCertificates/CFG.DFLT.EAC.ST", Collections.emptyList());


		sampleConfig.saveToProject();

		return sampleConfigProject;
	}

	protected void modifySampleConfigForPerso(SampleConfig sampleConfig)
			throws IOException {
		sampleConfig.setHaveToSaveToProjectAfterPut(false);
		sampleConfig.put("IEA", "IEA", "false");
		sampleConfig.put("MOBILEID", "MOBILEID", "false");

		sampleConfig.put("eID", "CS", "false");
	    sampleConfig.put("eID", "DG16", "false");
	    sampleConfig.put("eID", "DG15", "false");
	    sampleConfig.put("eID", "DG14", "false");
	    sampleConfig.put("eID", "DG12", "false");
	    sampleConfig.put("eID", "DG11", "false");
	    sampleConfig.put("eID", "DG10", "false");
	    sampleConfig.put("eID", "DG19", "false");
	    sampleConfig.put("eID", "DG20", "false");
	    sampleConfig.put("eID", "DG21", "false");
	    sampleConfig.put("eID", "DG22", "false");

	    sampleConfig.put("TAv2", "RSA", "false");
	    sampleConfig.put("TAv2", "AUTH_EXT", "false");

	    sampleConfig.put("CAv3", "CA", "false");

	    sampleConfig.put("eSIGN", "eSIGN", "false");

	    sampleConfig.put("ISO7816", "OddIns", "false");
	    sampleConfig.put("PASSWORDS", "CNG_CAN_AR", "false");

	    sampleConfig.put("RI", "KEY_ID", "0x02");
	    sampleConfig.put("RI", "KEY_ID_AUTH", "true");

	    sampleConfig.put("TR03110", "PSAInfo", "false");


	    sampleConfig.put("ISO7816", "EFATR", "false");
	    sampleConfig.put("ISO7816", "ENV", "false");
	    sampleConfig.put("AuxData", "CMP", "false");

		sampleConfig.put("CAv2", "DH", "false");
		sampleConfig.put("PACE", "CAM", "false");
		sampleConfig.put("PACE", "DH", "false");
	    sampleConfig.put("PACE", "IM", "false");
	    sampleConfig.put("PACE", "STATIC_BINDING", "false");

	    sampleConfig.put("CAPA", "CAPA", "false");

	    sampleConfig.put("PASSWORDS", "MRZ",
				"IDD<<0000000011<<<<<<<<<<<<<<<\n"+
				"8408129F3406304D<<<<<<<<<<<<<4\n"+
				"MUSTERMANN<<ERIKA<<<<<<<<<<<<<");

		//configure certificate locations
		String eac2Certificates = com.secunet.globaltester.protocols.eac2.certificates.Eac2CertificatesFactory.PROTOCOL_NAME;
		sampleConfig.put(eac2Certificates, "USE_CERTS", "USE_GENERATED_CERTS");
		sampleConfig.put(eac2Certificates, "EAC2_CERTS", CertGeneratorBase.CERTIFICATES_DIR + "/generated");
		Files.createDirectories(Paths.get(sampleConfig.getAbsolutePath(eac2Certificates, "EAC2_CERTS")));

		IFolder isRootFolder = sampleConfig.getProject() .getFolder(isRootFolderName);
		Files.createDirectories(Paths.get(isRootFolder.getRawLocationURI()));
		sampleConfig.put(com.secunet.globaltester.protocols.eac2.certificates.Eac2CertificatesFactory.PROTOCOL_NAME, "IS_CVCA_CERT", isRootFolderName + "/CVCA_Cert_01.cvcert");
		sampleConfig.put(com.secunet.globaltester.protocols.eac2.certificates.Eac2CertificatesFactory.PROTOCOL_NAME, "IS_CVCA_KEY", isRootFolderName + "/CVCA_KEY_01.pkcs8");

		IFolder atRootFolder = sampleConfig.getProject().getFolder(atRootFolderName);
		Files.createDirectories(Paths.get(atRootFolder.getRawLocationURI()));
		sampleConfig.put(com.secunet.globaltester.protocols.eac2.certificates.Eac2CertificatesFactory.PROTOCOL_NAME, "AT_CVCA_CERT", atRootFolderName + "/CVCA_Cert_01.cvcert");
		sampleConfig.put(com.secunet.globaltester.protocols.eac2.certificates.Eac2CertificatesFactory.PROTOCOL_NAME, "AT_CVCA_KEY", atRootFolderName + "/CVCA_KEY_01.pkcs8");

		IFolder stRootFolder = sampleConfig.getProject().getFolder(stRootFolderName);
		Files.createDirectories(Paths.get(stRootFolder.getRawLocationURI()));
		sampleConfig.put(com.secunet.globaltester.protocols.eac2.certificates.Eac2CertificatesFactory.PROTOCOL_NAME, "ST_CVCA_CERT", stRootFolderName + "/CVCA_Cert_01.cvcert");
		sampleConfig.put(com.secunet.globaltester.protocols.eac2.certificates.Eac2CertificatesFactory.PROTOCOL_NAME, "ST_CVCA_KEY", stRootFolderName + "/CVCA_KEY_01.pkcs8");

		//copy root certificates/keys
		GtResourceHelper.copyPluginFilesToWorkspaceProject("de.persosim.simulator", sampleConfig.getProject() .getFolder(isRootFolderName),
				"personalization/gtCertificates/CFG.DFLT.EAC.IS", Collections.emptyList());
		GtResourceHelper.copyPluginFilesToWorkspaceProject("de.persosim.simulator", sampleConfig.getProject() .getFolder(atRootFolderName),
				"personalization/gtCertificates/CFG.DFLT.EAC.AT", Collections.emptyList());
		GtResourceHelper.copyPluginFilesToWorkspaceProject("de.persosim.simulator", sampleConfig.getProject() .getFolder(stRootFolderName),
				"personalization/gtCertificates/CFG.DFLT.EAC.ST", Collections.emptyList());
		sampleConfig.setHaveToSaveToProjectAfterPut(true);
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

//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_H/EAC2_ISO7816_H_01.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_H/EAC2_ISO7816_H_04a.gt");

		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_complete_standard_layer6.gtsuite");
		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_H/EAC2_ISO7816_H_02.gt"); // check PIN usability after execution of P suite
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_H.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_I.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_J.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_K.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_L.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_M.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_N.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_O.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_P.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_Q.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/ISO7816_Q/EAC2_ISO7816_Q_20.gt");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_R.gtsuite");

		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer7/testsuite_complete_standard_layer7.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer7/testsuite_DATA_A.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer7/testsuite_DATA_B.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer7/testsuite_DATA_C.gtsuite");
//		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer7/testsuite_EIDDATA_B.gtsuite");

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


	@Override
	public TestResult getChipTestResult() throws InterruptedException, ExecutionException, TimeoutException {
		TestResult chipTestResult = super.getChipTestResult();

		List<String> expectedWarningTestcases = Arrays.asList(
				"EAC2_ISO7816_M_9",
				"EAC2_ISO7816_M_10",
				"EAC2_ISO7816_M_11",
				"EAC2_ISO7816_M_12",
				"EAC2_ISO7816_M_13",
				"EAC2_ISO7816_M_14"
				);

		System.out.println("Test case results:");
		for (SubTestResult current : chipTestResult.subResults){
			String curResString = current.resultString;
			if (curResString == null) {
				continue;
			}
			if (curResString.contains("WARNING") && !expectedWarningTestcases.contains(current.testCaseId)) {
				fail("Testcase "+current.testCaseId+" returned unexpected warning" );
			}

			if (curResString.contains("PASSED") && expectedWarningTestcases.contains(current.testCaseId)) {
				fail("Testcase "+current.testCaseId+" returned unexpected status, WARNING expected but was PASSED" );
			}

		}

		return chipTestResult;
	}

}
