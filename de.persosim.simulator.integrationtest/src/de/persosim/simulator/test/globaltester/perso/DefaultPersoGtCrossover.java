package de.persosim.simulator.test.globaltester.perso;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.globaltester.base.resources.GtResourceHelper;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.junit.Test;

import com.secunet.globaltester.crossover.DefaultScriptIntegrationTest;
import com.secunet.globaltester.testcontrol.callback.soap.TestControlCallback.TestResult;

import de.persosim.simulator.perso.DefaultPersoGt;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoGtCrossover extends DefaultScriptIntegrationTest{

	@Override
	public IProject getSampleConfigChip() throws Exception {
		IProject sampleConfigProject = super.createSampleConfigChip();
		SampleConfig sampleConfig = new SampleConfig(sampleConfigProject);
		
		//remove unsupported profiles
		
//		sampleConfig.put("TAv2_DATE">false</Parameter>
        

		sampleConfig.put("eID", "CS", "false");
	    sampleConfig.put("eID", "DG19", "false");
	    sampleConfig.put("eID", "DG16", "false");
	    sampleConfig.put("eID", "DG15", "false");
	    sampleConfig.put("eID", "DG14", "false");
	    sampleConfig.put("eID", "DG12", "false");
	    sampleConfig.put("eID", "DG11", "false");
	    sampleConfig.put("eID", "DG21", "false");
	    sampleConfig.put("eID", "DG10", "false");
	    sampleConfig.put("eID", "DG20", "false");
	    
	    sampleConfig.put("TAv2", "RSA", "false");
	    
	    sampleConfig.put("eSIGN", "eSIGN", "false");
        
	    sampleConfig.put("ISO7816", "OddIns", "false");
	    sampleConfig.put("PASSWORDS", "CNG_CAN_AR", "false");
	    
	    sampleConfig.put("RI", "KEY_ID", "0x02");
	    sampleConfig.put("RI", "KEY_ID_AUTH", "true");
		
		
		
		
		sampleConfig.put("CAv2", "DH", "false");
		sampleConfig.put("PACE", "CAM", "false");
		sampleConfig.put("PACE", "DH", "false");
	    sampleConfig.put("PACE", "IM", "false");
	    sampleConfig.put("PACE", "STATIC_BINDING", "false");

		sampleConfig.put("PASSWORDS", "MRZ",
				"IDD<<0000000011<<<<<<<<<<<<<<<\n"+
				"6408125F2010315D<<<<<<<<<<<<<8\n"+
				"MUSTERMANN<<ERIKA<<<<<<<<<<<<<");
		
		String eac2Certificates = com.secunet.globaltester.protocols.eac2.certificates.EacCertificatesFactory.PROTOCOL_NAME;
		sampleConfig.put(eac2Certificates, "USE_CERTS", "USE_GENERATED_CERTS");
		sampleConfig.put(eac2Certificates, "EAC2_CERTS", "certificates/generated");
		Files.createDirectories(Paths.get(sampleConfig.getAbsolutePath(eac2Certificates, "EAC2_CERTS")));

		String isRootFolderName = "certificates/cv/is";
		IFolder isRootFolder = sampleConfigProject.getFolder(isRootFolderName);
		Files.createDirectories(Paths.get(isRootFolder.getRawLocationURI()));
		GtResourceHelper.copyPluginFilesToWorkspaceProject("de.persosim.simulator", isRootFolder, "personalization/gtCertificates/CFG.DFLT.EAC.IS", Collections.emptyList());
		sampleConfig.put(eac2Certificates, "IS_CVCA_CERT", isRootFolderName+"/CVCA_Cert_01.cvcert");
		sampleConfig.put(eac2Certificates, "IS_CVCA_KEY", isRootFolderName+"/CVCA_KEY_01.pkcs8");
		
		String atRootFolderName = "certificates/cv/at";
		IFolder atRootFolder = sampleConfigProject.getFolder(atRootFolderName);
		Files.createDirectories(Paths.get(atRootFolder.getRawLocationURI()));
		GtResourceHelper.copyPluginFilesToWorkspaceProject("de.persosim.simulator", atRootFolder, "personalization/gtCertificates/CFG.DFLT.EAC.AT", Collections.emptyList());
		sampleConfig.put(eac2Certificates, "AT_CVCA_CERT", atRootFolderName+"/CVCA_Cert_01.cvcert");
		sampleConfig.put(eac2Certificates, "AT_CVCA_KEY", atRootFolderName+"/CVCA_KEY_01.pkcs8");

		String stRootFolderName = "certificates/cv/st";
		IFolder stRootFolder = sampleConfigProject.getFolder(stRootFolderName);
		Files.createDirectories(Paths.get(stRootFolder.getRawLocationURI()));
		GtResourceHelper.copyPluginFilesToWorkspaceProject("de.persosim.simulator", stRootFolder, "personalization/gtCertificates/CFG.DFLT.EAC.ST", Collections.emptyList());
		sampleConfig.put(eac2Certificates, "ST_CVCA_CERT", stRootFolderName+"/CVCA_Cert_01.cvcert");
		sampleConfig.put(eac2Certificates, "ST_CVCA_KEY", stRootFolderName+"/CVCA_KEY_01.pkcs8");

		sampleConfig.saveToProject();
		
		return sampleConfigProject;
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

		testcasesChip.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_complete_standard_layer6.gtsuite");
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

}
