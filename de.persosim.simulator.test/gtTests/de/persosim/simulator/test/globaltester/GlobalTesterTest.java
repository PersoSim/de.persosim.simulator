package de.persosim.simulator.test.globaltester;

import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.persosim.simulator.SocketSimulator;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.test.PersoSimTestCase;

public abstract class GlobalTesterTest extends PersoSimTestCase {

	private static final String GT_SERVER_HOST = "localhost";
	private static final int GT_SERVER_PORT = 6789;
	private static final int GT_SERVER_RESULT_PORT = 6788;
	
	private static final int SIM_PORT = 9876;
	
//	private static final String BASE_PATH = "\\persosim_servermode_tests";
//	private static final String PATH_LOGGING = BASE_PATH + "\\logging";

	private static GtServerConnection gtServer;

	@BeforeClass
	public static void setUpSuite() throws Exception {
		// initialize GT server connection
		gtServer = new GtServerConnection(GT_SERVER_HOST, GT_SERVER_PORT,
				GT_SERVER_RESULT_PORT);
		gtServer.connect();

	}

	@AfterClass
	public static void tearDownSuite() {
		// disconnect GT server connection
		if (gtServer != null) {
			gtServer.closeConnection();
		}
	}

	private SocketSimulator simulator;
	
	@Before
	public void setUp() throws Exception {
		startSimulator();
		configureGtServer();
	}
	
	@After
	public void tearDown(){
		stopSimulator();
	}
	
	private void resetSimulator(){
		stopSimulator();
		startSimulator();
	}
	
	private void stopSimulator(){
		//stop PersoSim Thread
		if (simulator != null) {
			simulator.stop();
			simulator = null;
		}
	}

	
	@Test
	public void testAllApplicableTests() throws Exception {
		gtServer.clearCollectedResults();
		
		Collection<JobDescriptor> suites = getAllApplicableGtTests();
		for (JobDescriptor curSuite : suites) {
			if (curSuite instanceof SimulatorReset){
				resetSimulator();
			} else if (curSuite instanceof GtSuiteDescriptor){
				gtServer.runSuiteAndSaveResults((GtSuiteDescriptor)curSuite);
			}
		}
		
		gtServer.checkAndClearResults(0, 0);
	}

	private void startSimulator() {
		if (simulator == null) {
			simulator = new SocketSimulator(getPersonalization(), SIM_PORT);
		}
		
		if (!simulator.isRunning()) {
			simulator.start();
		}
	}
	
	private void configureGtServer() throws Exception {
		//disable dialogs
		gtServer.setPreferences(GtServerConnection.PREF_QUALIFIER_TESTMANAGER, "PROFILES_SHOW_DIALOG", "false");
		gtServer.setPreferences(GtServerConnection.PREF_QUALIFIER_TESTMANAGER, "INTEGRITY_WARNING_DIALOG", "false");
		
		//set logging options
		gtServer.setPreferences(GtServerConnection.PREF_QUALIFIER_LOGGING, "GT Test - Logging level", "0");
		gtServer.setPreferences(GtServerConnection.PREF_QUALIFIER_LOGGING, "GT Simulator - Logging level", "0");
//		gtServer.setPreferences(GtServerConnection.PREF_QUALIFIER_LOGGING, "manualDirSettings", "true");
//		gtServer.setPreferences(GtServerConnection.PREF_QUALIFIER_LOGGING, "GT Test - Add single logfiles for Testcases", "false");
//		gtServer.setPreferences(GtServerConnection.PREF_QUALIFIER_LOGGING, "GT Test - Logging directory", gtServer.getWorkspaceDir()+PATH_LOGGING);

		//configure profiles
		gtServer.setSupportedProfiles(getSupportedProfiles());
		
		
		//TODO configureGtServer
//		transmitPasswords();
//		configureCertificates();
//		transmitCertificates();
//		generateCertificatesIfNeeded();
	}

	/**
	 * Returns the {@link Personalization} instance to be used with the
	 * GlobalTester test cases
	 */
	public abstract Personalization getPersonalization();

	/**
	 * Returns all GlobalTester profiles that the current personalization
	 * supports.
	 * <p/>
	 * In order to ensure that no tests are missed abstract superclasses are
	 * expected to either don't implement this method at all or implement it in
	 * a way that provides all possible profiles so that subclasses are required
	 * to call the super implementation and restrict the returned Collection.
	 * 
	 * @return
	 */
	public abstract Collection<String> getSupportedProfiles();

	/**
	 * Return a collection of applicable GlobalTester test suites/test cases
	 * represented by their according {@link GtSuiteDescriptor}s. These are
	 * executed by {@link #testAllApplicableTests()} and expected to return
	 * Failures.
	 * 
	 * This must not return an empty List as the number of executed test cases is
	 * expected to be larger than 0.
	 * 
	 * @return
	 */
	public abstract Collection<JobDescriptor> getAllApplicableGtTests();

}