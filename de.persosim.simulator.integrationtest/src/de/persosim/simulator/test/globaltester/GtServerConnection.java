package de.persosim.simulator.test.globaltester;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.globaltester.logging.BasicLogger;

/**
 * Encapsulate ServerMode communication with an existing GlobalTester instance
 * in order to include GlobalTester tests within the existing
 * Unit-test-framework.
 * <p/>
 * This class expects a running GlobalTester instance listening on the provided
 * host/port. It also assumes that this instance is already configured to use
 * the PersoSim simulation as default card reader.
 * 
 * @author amay
 * 
 */
public class GtServerConnection {
	
	public static final String PREF_QUALIFIER_LOGGING = "org.globaltester.logging";
	public static final String PREF_QUALIFIER_SECUREMESSAGING = "com.secunet.globaltester.epassport.securemessaging";
	public static final String PREF_QUALIFIER_EAC2 = "com.secunet.globaltester.prove.eac2";
	public static final String PREF_QUALIFIER_TESTMANAGER = "org.globaltester.testmanager";
	
	private static final Pattern RESULTPATTERN = Pattern.compile(
					".*?(\\d+) testcases \\((\\d+) failures, (\\d+) warnings.*");
	
	private String serverHost;
	private int cmdPort;

	private Socket socket = null;
	private PrintStream out = null;
	private BufferedReader in = null;

	private int collectedErrors = 0;
	private int collectedWarnings = 0;
	private int nrOfExecutedTestCases = 0;

	/**
	 * Constructor that provides connection information to acces GlobalTester
	 * @param host hostname under which the GT is running
	 * @param port port in which GT is listening to accept commands
	 * @param resultPort port on which GT will listen for provided reading results in terminal tests
	 */
	public GtServerConnection(String host, int port, int resultPort) {
		serverHost = host;
		cmdPort = port;
	}
	
	/**
	 * Change the selected working directory on the remote GlobalTester.
	 * 
	 * @param projectName project to select
	 * @throws IOException
	 */
	private void setWorkingProject(String projectName) throws IOException {
		String command = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<changeProject>" + projectName + "</changeProject>";
		transmitCommand(command);
	}
	
	/**
	 * Run a TestSuite (or TestCase) with given name from given project. Test results are
	 * extracted from the server response and accumulated until the next call of
	 * {@link #checkAndClearResults(int, int)} or
	 * {@link #clearCollectedResults()}
	 * 
	 * @param projectName project to search the test suite in
	 * @param testSuiteName filename of the testsuite to execute (without extension)
	 * @throws IOException
	 */
	public void runSuiteAndSaveResults(String projectName, String testSuiteName) throws IOException {
		setWorkingProject(projectName);
		runSuiteAndSaveResults(testSuiteName);
	}

	/**
	 * Run a TestSuite (or TestCase) within the current project. Test results are
	 * extracted from the server response and accumulated until the next call of
	 * {@link #checkAndClearResults(int, int)} or
	 * {@link #clearCollectedResults()}
	 * 
	 * @param testSuiteName filename of the testsuite to execute (without extension)
	 * @throws IOException
	 */
	public void runSuiteAndSaveResults(String testSuiteName) throws IOException {
		String command = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<testsuite>" + testSuiteName + "</testsuite>";
		String result = transmitCommand(command);
		saveResults(result);
	}

	private void saveResults(String result) {
		if (result != null) {
			Matcher matcher = RESULTPATTERN
					.matcher(result);

			if (matcher.matches()) {
				nrOfExecutedTestCases += Integer.parseInt(matcher.group(1));
				collectedErrors += Integer.parseInt(matcher.group(2));
				collectedWarnings += Integer.parseInt(matcher.group(3));
			}

		}
	}

	/**
	 * Compare the accumulated results with expected values and clears the
	 * accumulated results afterwards.
	 * 
	 * @see #checkResults(int, int)
	 * @see #clearCollectedResults()
	 * 
	 * @param expectedErrors
	 * @param expectedWarnings
	 */
	public void checkAndClearResults(int expectedErrors, int expectedWarnings) {
		checkResults(expectedErrors, expectedWarnings);
		clearCollectedResults();
	}

	/**
	 * Reset the accumulated results to 0.
	 */
	public void clearCollectedResults() {
		nrOfExecutedTestCases = 0;
		collectedErrors = 0;
		collectedWarnings = 0;

	}

	public int getCollectedErrors() {
		return collectedErrors;
	}

	public int getCollectedWarnings() {
		return collectedWarnings;
	}

	/**
	 * Compare the accumulated results with expected values.
	 * <p/>
	 * In case of a mismatch between expected and accumulated values an
	 * {@link AssertionError} is thrown.
	 * 
	 * @see #checkResults(int, int)
	 * @see #clearCollectedResults()
	 * 
	 * @param expectedErrors
	 * @param expectedWarnings
	 */
	public void checkResults(int expectedErrors, int expectedWarnings) {
		System.out
				.printf("Executed %5d testcases via GlobalTester Servermode. Found/expected %5d/%-5d errors and %5d/%-5d warnings. ",
						nrOfExecutedTestCases, collectedErrors, expectedErrors,
						collectedWarnings, expectedWarnings);
		if (nrOfExecutedTestCases <= 0) {
			System.out.println("FAIL!");
			assertTrue("No testcases have been executed", nrOfExecutedTestCases > 0);
		} else if (expectedErrors != collectedErrors) {
			System.out.println("FAIL!");
			assertEquals("Unexpected number of errors in testsuite",
					expectedErrors, collectedErrors);
		} else if (expectedWarnings != collectedWarnings) {
			System.out.println("FAIL!");
		assertEquals("Unexpected number of errors in testsuite",
				expectedWarnings, collectedWarnings);
		} else {
			System.out.println("OK!");
		}
	}

	/**
	 * Initiate the ServerMode connection to the GlobalTester instance.
	 * <p/>
	 * NOTE: the current implementation is not thread safe
	 */
	public boolean connect() {
		if (socket != null) {
			throw new RuntimeException(
					"GTServerConnection is already connected");
		}

		try {
			socket = new Socket(serverHost, cmdPort);


			if (!socket.isConnected()) {
				throw new RuntimeException(
						"GTServerConnection: unable to connect to a GT Server ");
			}
			
			out = new PrintStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return true;
		} catch (IOException e) {
			BasicLogger.logException(this.getClass(), e);
			return false;
		}
	}

	private String transmitCommand(String command) throws IOException {
		out.println(command);
		return in.readLine();
	}

	/**
	 * Close the ServerMode connection to the GlobalTester instance.
	 * <p/>
	 * NOTE: the current implementation is not thread safe
		 
	 * @throws IOException
	 */
	public void closeConnection() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see #runSuiteAndSaveResults(String, String)
	 * @param suite
	 * @throws IOException
	 */
	public void runSuiteAndSaveResults(GtSuiteDescriptor suite) throws IOException {
		runSuiteAndSaveResults(suite.getProject(), suite.getTestSuiteName());
	}

	/**
	 * This method allows setting of preference values.
	 * 
	 * @param qualifier
	 *            Eclipse preference qualifier (i.e. bundle name of scope)
	 * @param key
	 *            key of the preference to set
	 * @param value
	 *            value to store
	 * @return the ServerMode response, this contains either the previous value
	 *         or an error message
	 * @throws IOException
	 */
	public String setPreferences(String qualifier, String key, String value) throws IOException {
		String command = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<setPreferences>"+
						"<preference qualifier=\""+qualifier+"\" key=\""+key+"\"><![CDATA["+ value + "]]></preference>"+
						"</setPreferences>";
		return transmitCommand(command);
		
	}

	/**
	 * Returns the absolute path to the workspace directory used by the remot GT
	 * instance. This path is usable on the (possibly remote) machine.
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getWorkspaceDir() throws IOException {
		String command = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><getWorkspaceDir/>";
		String response = transmitCommand(command);
		Matcher m = Pattern.compile("<workspaceDir>(.*)</workspaceDir>")
				.matcher(response);
		if (m.matches()) {
			return m.group(1);
		} else {

			return "";
		}
	}

	/**
	 * Creates a directory on the remote machine.
	 * <p/>
	 * Be aware that this allows uncontrolled access to the remote file system.
	 * 
	 * @param path
	 * @return the ServerMode response, in case of failure this contains an
	 *         error message
	 * @throws IOException
	 */
	public String mkDir(String path) throws IOException {
		String command = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<mkDir>" + path + "</mkDir>";
		return transmitCommand(command);
	}

	/**
	 * Writes data to the file on the remote machine, possibly creating it.
	 * <p/>
	 * Be aware that this allows uncontrolled access to the remote file system.
	 * 
	 * @param path
	 * @return the ServerMode response, in case of failure this contains an
	 *         error message
	 * @throws IOException
	 */
	public String writeFile(String filename, String content) throws IOException {
		String command = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<writeFile filename=\""+filename+"\">"
				+ content
				+ "</writeFile>";
		return transmitCommand(command);
	}

	/**
	 * Select supported GlobalTester profiles in the remote GT instance.
	 * <p/>
	 * This sets all available profiles to false except those given in supportedProfiles
	 * @param supportedProfiles Collection of profiles supported by this sample.
	 * @throws IOException
	 */
	public void setSupportedProfiles(Collection<String> supportedProfiles) throws IOException {
		HashMap<String, Boolean> profiles = getAllProfilesDisabled();
		
		//set supported profiles
		for (String curSupProfile : supportedProfiles) {
			profiles.put(curSupProfile, true);
		}
		
		//build command
		StringBuffer command = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		command.append("<setPreferences>");
		for (String curProfile : profiles.keySet()) {
			command.append("<preference qualifier=\""+PREF_QUALIFIER_TESTMANAGER+"\" key=\"_profile_"+curProfile+"\">"+profiles.get(curProfile)+"</preference>");			
		}
		command.append("</setPreferences>");
		
		//send to gtServer
		transmitCommand(command.toString());
		
	}

	/**
	 * Returns a HascMap associating false with all known profiles
	 * @return
	 */
	private HashMap<String, Boolean> getAllProfilesDisabled() {
		HashMap<String, Boolean> retVal = new HashMap<String, Boolean>();
						
		for (String curProfile : GtConstants.ALL_PROFILES) {
			retVal.put(curProfile, false);
		}
		return retVal;
	}

}
