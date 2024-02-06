package de.persosim.simulator.test.globaltester.perso;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.resources.IProject;
import org.globaltester.base.PreferenceHelper;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.junit.Ignore;
import org.junit.Test;

import com.secunet.globaltester.testcontrol.callback.soap.TestControlCallback.TestResult;

import de.persosim.simulator.perso.DefaultPersoGt;
import de.persosim.simulator.perso.Personalization;

public class PersoSimMigrationCrossover extends DefaultPersoGtCrossover {
	
	@Test
	public void migrate_Rsa_Sha1_1024_Test() throws Exception {
		testMigration(MigrationTarget.RSA_SHA1_1024);
	}
	
	@Test
	public void migrate_Ecdsa_Sha256_P256r1_Test() throws Exception {
		testMigration(MigrationTarget.ECDSA_SHA256_P256r1);
	}
	
	@Test
	public void migrate_Ecdsa_Sha384_P384r1_Test() throws Exception {
		testMigration(MigrationTarget.ECDSA_SHA384_P384r1);
	}
	
	@Test
	public void migrate_Ecdsa_Sha512_SECP521r1_Test() throws Exception {
		testMigration(MigrationTarget.ECDSA_SHA512_SECP521r1);
	}
	
	@Test
	public void migrate_RsaPss_Sha512_1024_Test() throws Exception {
		testMigration(MigrationTarget.RSAPSS_SHA512_1024);
	}
	
	@Override
	public void defaultTest() throws Exception {
		//do not reexecute the testcase from parent class
	}
	
	@Test
	@Ignore
	public void migrateAllTest() throws Exception {
		testMigration(MigrationTarget.values());
	}
	
	public void testMigration(MigrationTarget... migrationTargets) throws Exception {
		if ((migrationTargets==null)|| (migrationTargets.length < 1)) return;
		
		Personalization perso = new DefaultPersoGt();		
		enableSimulator(perso);
		
		IProject sampleConfigProject = importSampleConfig("com.secunet.globaltester.crossover", "configs/Sample_PokeConfig", getNameOfSampleConfigChip() + "_orig_" + migrationTargets[0].name());
		SampleConfig sampleConfig = SampleConfig.getSampleConfigForProject(sampleConfigProject);
		modifySampleConfigForPerso(sampleConfig);
		
		for (int i = 0; i < migrationTargets.length; i++) {
			if (migrationTargets[i]==null) continue; 
			sampleConfig = migrateTo(sampleConfig, migrationTargets[i]);
			checkMigrationResult(sampleConfig);
		}
		
		cleanupChipTest();
		disableSimulator();
	}

	private SampleConfig migrateTo(SampleConfig sampleConfig, MigrationTarget migrationTarget) throws InterruptedException, ExecutionException, IOException, TimeoutException {
		configureMigration(sampleConfig, migrationTarget);
		performMigrationTestcases(sampleConfig);
		return createNewSampleConfigafterMigration(sampleConfig, migrationTarget);
	}
	
	private void configureMigration(SampleConfig sampleConfig, MigrationTarget migrationTarget) {
		PreferenceHelper.setPreferenceValue(com.secunet.globaltester.prove.eac2.Activator.PLUGIN_ID, com.secunet.globaltester.prove.eac2.preferences.PreferenceConstants.P_EPA_GENCERTS_MIG_SIGALG, migrationTarget.sigAlg);
		PreferenceHelper.setPreferenceValue(com.secunet.globaltester.prove.eac2.Activator.PLUGIN_ID, com.secunet.globaltester.prove.eac2.preferences.PreferenceConstants.P_EPA_GENCERTS_MIG_KEYSIZE, migrationTarget.param);
		PreferenceHelper.setPreferenceValue(com.secunet.globaltester.prove.eac2.Activator.PLUGIN_ID, com.secunet.globaltester.prove.eac2.preferences.PreferenceConstants.P_EPA_GENCERTS_MIG_CURVE, migrationTarget.param);
	}

	private void performMigrationTestcases(SampleConfig sampleConfig) throws InterruptedException, ExecutionException, TimeoutException {
		ArrayList<String> testcases = new ArrayList<>();
		testcases.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/generate_data/testsuite_Gen_ALL_Certificate_Sets.gtsuite");
		testcases.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_M.gtsuite");
		testcases.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_N.gtsuite");

		performChipTest(sampleConfig, testcases);	
		TestResult chipResults = getChipTestResult();
		assertEquals("Migration test result", 0, chipResults.overallResult);
	}

	private SampleConfig createNewSampleConfigafterMigration(SampleConfig sampleConfig,
			MigrationTarget migrationTarget) throws IOException {
		
		IProject afterMigSampleConfigProject = importSampleConfig("com.secunet.globaltester.crossover", "configs/Sample_PokeConfig", getNameOfSampleConfigChip() + "_" + migrationTarget.name());
		SampleConfig newSampleConfig = SampleConfig.getSampleConfigForProject(afterMigSampleConfigProject);
		
		modifySampleConfigForPerso(newSampleConfig);
		
		// adjust profiles according to migration target
		newSampleConfig.setHaveToSaveToProjectAfterPut(false);
		newSampleConfig.put("TAv2", "RSA", MigrationType.RSA.equals(migrationTarget.type));
		newSampleConfig.put("TAv2", "ECDSA", MigrationType.ECDSA.equals(migrationTarget.type));
		
		//import certificates from migration
		Path sourceCertIs = Paths.get(sampleConfig.getAbsolutePath(com.secunet.globaltester.testscripts.bsi.tr03105_part3_3.ics.TR03105ProtocolFactory.PROTOCOL_NAME, "LINK_CERT_13a"));
		Files.copy(sourceCertIs, Paths.get(newSampleConfig.getAbsolutePath(eac2Certs, "IS_CVCA_CERT")), StandardCopyOption.REPLACE_EXISTING);
		Path sourceKeyIs = Paths.get(sampleConfig.getAbsolutePath(com.secunet.globaltester.testscripts.bsi.tr03105_part3_3.ics.TR03105ProtocolFactory.PROTOCOL_NAME, "CVCA_KEY_13a_PRIV"));
		Files.copy(sourceKeyIs, Paths.get(newSampleConfig.getAbsolutePath(eac2Certs, "IS_CVCA_KEY")), StandardCopyOption.REPLACE_EXISTING);
		Path sourceCertAt = Paths.get(sampleConfig.getAbsolutePath(com.secunet.globaltester.testscripts.bsi.tr03105_part3_3.ics.TR03105ProtocolFactory.PROTOCOL_NAME, "LINK_CERT_13b"));
		Files.copy(sourceCertAt, Paths.get(newSampleConfig.getAbsolutePath(eac2Certs, "AT_CVCA_CERT")), StandardCopyOption.REPLACE_EXISTING);
		Path sourceKeyAt = Paths.get(sampleConfig.getAbsolutePath(com.secunet.globaltester.testscripts.bsi.tr03105_part3_3.ics.TR03105ProtocolFactory.PROTOCOL_NAME, "CVCA_KEY_13b_PRIV"));
		Files.copy(sourceKeyAt, Paths.get(newSampleConfig.getAbsolutePath(eac2Certs, "AT_CVCA_KEY")), StandardCopyOption.REPLACE_EXISTING);
		Path sourceCertSt = Paths.get(sampleConfig.getAbsolutePath(com.secunet.globaltester.protocols.eac2.certificates.EacCertificatesFactory.PROTOCOL_NAME, "EAC2_CERTS")).resolve("cvca_root_st.cvcert");
		Files.copy(sourceCertSt, Paths.get(newSampleConfig.getAbsolutePath(eac2Certs, "ST_CVCA_CERT")), StandardCopyOption.REPLACE_EXISTING);
		Path sourceKeySt = Paths.get(sampleConfig.getAbsolutePath(com.secunet.globaltester.protocols.eac2.certificates.EacCertificatesFactory.PROTOCOL_NAME, "EAC2_CERTS")).resolve("CVCA_KEY_ST_00.pkcs8");
		Files.copy(sourceKeySt, Paths.get(newSampleConfig.getAbsolutePath(eac2Certs, "ST_CVCA_KEY")), StandardCopyOption.REPLACE_EXISTING);	
				
		newSampleConfig.saveToProject();
		newSampleConfig.setHaveToSaveToProjectAfterPut(true);
		return newSampleConfig;
	}

	private void checkMigrationResult(SampleConfig sampleConfig) throws InterruptedException, ExecutionException, TimeoutException {
		ArrayList<String> testcases = new ArrayList<>();
		testcases.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/generate_data/testsuite_Gen_ALL_Certificate_Sets.gtsuite");
		testcases.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_K.gtsuite");
		testcases.add("GT Scripts BSI TR03105 Part 3.3/TestSuites/Layer6/testsuite_ISO7816_J.gtsuite");
		
		performChipTest(sampleConfig, testcases);
		TestResult results = getChipTestResult();
		assertEquals("Chip test result", 0, results.overallResult);
	}

}
