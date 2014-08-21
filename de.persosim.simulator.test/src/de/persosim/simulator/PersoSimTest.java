package de.persosim.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.perso.DefaultPersoTestPki;
import de.persosim.simulator.perso.MinimumPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

//FIXME SLS generaly use method names with a separating _ between the name of the method to test and the special circumstances that are tested
//FIXME SLS @Test methods can simply thro Exception instead of a bunch of narroy subclasses. These methods are not intended to be used by any caller that would complain about that
public class PersoSimTest extends PersoSimTestCase {
	
	PersoSim persoSim;
	
	@Mocked DefaultPersoTestPki defaultPersoTestPki;
	
	public static final byte[] EF_CS_CONTENT_1 = HexString.toByteArray("FF010203");
	public static final byte[] EF_CS_CONTENT_2 = HexString.toByteArray("FF030201");
	
	public static final String DUMMY_PERSONALIZATION_FILE_1 = "tmp/dummyPersonalization1.xml";
	public static final String DUMMY_PERSONALIZATION_FILE_2 = "tmp/dummyPersonalization2.xml";
	
	public static final String SELECT_APDU = "00A4020C02011C";
	public static final String READ_BINARY_APDU = "00B0000004";
	public static final String SW_NO_ERROR = "9000";
	
	static PrintStream	origOut;
	static ByteArrayOutputStream redStdOut;
	
	
	
	@Before
	public void setUp() {
		origOut	= System.out;
		
		MinimumPersonalization perso1 = new MinimumPersonalization(EF_CS_CONTENT_1);
		perso1.writeToFile(DUMMY_PERSONALIZATION_FILE_1);
		
		MinimumPersonalization perso2 = new MinimumPersonalization(EF_CS_CONTENT_2);
		perso2.writeToFile(DUMMY_PERSONALIZATION_FILE_2);
	}
	
	@After
	public void tearDown() {
		if(persoSim != null) {
			persoSim.executeUserCommands(PersoSim.CMD_STOP); //FIXME SLS why not call persoSim.stopSimulator directly? (and why is it not public?)
		}
		
		System.setOut(origOut);
	}
	
	/**
	 * This method extracts the status word from an APDU response String.
	 * @param responseBulk the bulk APDU response
	 * @return the status word
	 */
	public static String extractStatusWord(String responseBulk) {
		return responseBulk.substring(responseBulk.length() - 4);
	}
	
	/**
	 * This method extracts the data response from an APDU response String.
	 * @param responseBulk the bulk APDU response
	 * @return the data
	 */
	public static String extractResponse(String responseBulk) {
		return responseBulk.substring(0, responseBulk.length() - 4).trim();
	}
	
	/**
	 * This method exchanges APDUs with a simulator running on default host and port.
	 * @param cmdApdu the APDU to be sent
	 * @return the APDU response
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private String exchangeApdu(String cmdApdu) throws UnknownHostException, IOException {
		return exchangeApdu(cmdApdu, PersoSim.DEFAULT_SIM_PORT);
	}
	
	/**
	 * This method exchanges APDUs with a simulator running on localhost at the provided port.
	 * @param cmdApdu the APDU to be sent
	 * @param port the port to contact the simulator
	 * @return the APDU response
	 * @throws IOException
	 */
	private String exchangeApdu(String cmdApdu, int port) throws IOException {
		cmdApdu = cmdApdu.replaceAll("\\s", ""); // remove any whitespace
		
		Socket socket = null;
		String respApdu = null;
		
		try {
			socket = new Socket(PersoSim.DEFAULT_SIM_HOST, port);

			PrintStream out = new PrintStream(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			out.println(cmdApdu);
			out.flush();
			
			respApdu = in.readLine();
		} catch (IOException e) {
			throw e; //FIXME SLS isn't this catch block obsolete?
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					return "socket close failure";
				}
			}
		}

		return respApdu;
	}
	
	/**
	 * This method activates redirection of System.out.
	 */
	public static void activateStdOutRedirection() {
		redStdOut = new ByteArrayOutputStream();
		PrintStream	stdout = new PrintStream(redStdOut);
		
		System.setOut(stdout);
		origOut.print(redStdOut.toString());
		origOut.flush();
	}
	
	/**
	 * This method reads from redirected System.out.
	 * @return the data read from redirected System.out
	 */
	public static String readRedStdOut() {
		String responseBulk = "";
		
		try {
			responseBulk = redStdOut.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// "UTF-8" _is_ valid //FIXME SLS then simply throw this exception furhter, we are in a unit test here any unexpected behavior shall lead to failing tests instead of asimple log
			e.printStackTrace();
		}
		
		origOut.print(responseBulk);
		origOut.flush();
		
		return responseBulk;
	}
	
	/**
	 * Positive test case: check behavior of PersoSim constructor when called with empty argument.
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testPersoSimConstructor_EmptyArgument() throws UnsupportedEncodingException {
		System.out.println("test001");
		activateStdOutRedirection();
		
		persoSim = new PersoSim(new String[]{});
		
		String responseBulk = readRedStdOut();
		
		String response = responseBulk.trim();
		
		assertEquals(PersoSim.LOG_NO_OPERATION, response);
	}
	
	/**
	 * Positive test case: check how PersoSim constructor handles null argument.
	 */
	@Test
	public void testPersoSimConstructor_NullArgument() {
		System.out.println("test002"); //FIXME SLS remove all these useless printlns (they don't relate the output to any specific test method, for debugging running the tests on-by-one is more effective)
		activateStdOutRedirection();
		
		persoSim = new PersoSim((String) null);
		
		String responseBulk = readRedStdOut();
		
		String response = responseBulk.trim();
		
		assertEquals(PersoSim.LOG_NO_OPERATION, response);
	}
	
	/**
	 * Positive test case: check how PersoSim command line handles empty argument.
	 */
	@Test
	public void testExecuteUserCommands_EmptyArgument() {
		System.out.println("test017");
		
		persoSim = new PersoSim((String) null);
		
		activateStdOutRedirection();
		
		persoSim.executeUserCommands("");
		
		String responseBulk = readRedStdOut();
		
		String response = responseBulk.trim();
		
		assertEquals(PersoSim.LOG_NO_OPERATION, response);
	}
	
	/**
	 * Positive test case: check how PersoSim command line handles exit command.
	 */
	@Test
	public void testExecuteUserCommands_Exit() {
		System.out.println("test018");
		
		persoSim = new PersoSim((String) null);
		
		activateStdOutRedirection();
		
		persoSim.executeUserCommands(PersoSim.CMD_EXIT);
		
		String responseBulk = readRedStdOut();
		
		String response = responseBulk.trim();
		
		assertEquals(PersoSim.LOG_SIM_EXIT, response);
	}
	
	/**
	 * Positive test case: check how PersoSim command line handles an unknown argument.
	 */
	@Test
	public void testExecuteUserCommands_UnknownArgument() {
		System.out.println("test019");
		
		persoSim = new PersoSim((String) null);
		
		activateStdOutRedirection();
		
		persoSim.executeUserCommands("unknown");
		
		String responseBulk = readRedStdOut();
		
		String response = responseBulk.trim(); //FIXME SLS I see this quite offen, why isn't this handled within readRedStdOut()?
		
		assertTrue(response.startsWith(PersoSim.LOG_UNKNOWN_ARG));
	}
	
	/**
	 * Positive test case: test implicit setting of a default personalization if no other personalization is explicitly set.
	 * @throws JAXBException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	@Test
	public void testImplicitSettingOfDefaultPersonalization() throws JAXBException, UnknownHostException, IOException {
		System.out.println("test003");
		// prepare the mock
		new NonStrictExpectations() {
			{
				defaultPersoTestPki.getObjectTree();
				result = new MinimumPersonalization(EF_CS_CONTENT_1).getObjectTree();
			}
			
			{
				defaultPersoTestPki.getProtocolList();
				result = new MinimumPersonalization(EF_CS_CONTENT_1).getProtocolList();
			}
		};
		
		persoSim = new PersoSim((String) null);
		persoSim.executeUserCommands(PersoSim.CMD_START); //FIXME SLS again, execute the corresponding methods directly instead of indirectly through some kind of String parsing method. Check all calls to executeUserCommands 
		
		String responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
		
		String responseReadBinaryExpected = (HexString.encode(EF_CS_CONTENT_1)).toUpperCase();
		String responseReadBinary = (extractResponse(exchangeApdu(READ_BINARY_APDU))).toUpperCase();
		assertEquals(responseReadBinaryExpected, responseReadBinary);
	}
	
	/**
	 * Positive test case: test start of socket simulator.
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	@Test
	public void testStartSimulator() throws InterruptedException, UnknownHostException, IOException {
		System.out.println("test004");
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		boolean caughtIoException = false;
		try {
			exchangeApdu(SELECT_APDU);
		} catch (IOException e) {
			caughtIoException = true;
		}
		
		assertTrue(caughtIoException);
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		String responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
	}
	
	/**
	 * Positive test case: test stop of socket simulator.
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	@Test
	public void testStopSimulator() throws InterruptedException, UnknownHostException, IOException {
		System.out.println("test005");
		persoSim = new PersoSim(PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1);
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		String responseSelect1 = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect1);
		
		persoSim.executeUserCommands(PersoSim.CMD_STOP);
		
		boolean caughtIoException = false;
		
		try {
			exchangeApdu(SELECT_APDU);
		} catch (IOException e) {
			caughtIoException = true;
		}
		
		assertTrue(caughtIoException);
	}
	
	/**
	 * Positive test case: parse arguments from an empty String.
	 */
	@Test
	public void testParseCommandEmptyString() {
		System.out.println("test006");
		String[] result = PersoSim.parseCommand("");
		
		assertEquals(result.length, 0);
	}
	
	/**
	 * Negative test case: parse arguments from null.
	 */
	@Test(expected = NullPointerException.class)
	public void testParseCommandNull() {
		System.out.println("test007");
		PersoSim.parseCommand(null);
	}
	
	/**
	 * Positive test case: parse arguments from a String containing spaces only at start and end.
	 */
	@Test
	public void testParseCommandUntrimmedCoherentString() {
		System.out.println("test008");
		String arg = "string";
		String[] result = PersoSim.parseCommand(" " + arg + "  ");
		
		assertEquals(result.length, 1);
		assertEquals(result[0], arg);
	}
	
	/**
	 * Positive test case: parse arguments from a String containing spaces not only at start and end.
	 */
	@Test
	public void testParseCommandIncoherentString() {
		System.out.println("test009");
		String arg1 = "string1";
		String arg2 = "string 2";
		String[] result = PersoSim.parseCommand(" " + arg1 + "  " + arg2);
		
		assertEquals(result.length, 2);
		assertEquals(result[0], arg1);
		assertEquals(result[1], arg2);
	}
	
	/**
	 * Positive test case: parse personalization from a valid file.
	 * @throws FileNotFoundException 
	 * @throws JAXBException 
	 */
	@Test
	public void testParsePersonalizationValidFile() throws FileNotFoundException, JAXBException {
		System.out.println("test010");
		Personalization perso = PersoSim.parsePersonalization(DUMMY_PERSONALIZATION_FILE_1);
		
		assertNotNull(perso);
	}
	
	/**
	 * Negative test case: parse personalization from a non-existing file.
	 * @throws FileNotFoundException 
	 * @throws JAXBException 
	 */
	@Test(expected = FileNotFoundException.class)
	public void testParsePersonalizationFileNotFound() throws FileNotFoundException, JAXBException {
		System.out.println("test011");
		PersoSim.parsePersonalization("file not found");
	}
	
	/**
	 * Negative test case: parse personalization from an invalid existing file.
	 * @throws FileNotFoundException 
	 * @throws JAXBException 
	 */
	@Test(expected = JAXBException.class)
	public void testParsePersonalizationInvalidFile() throws FileNotFoundException, JAXBException {
		System.out.println("test012");
		PersoSim.parsePersonalization("src/de/persosim/simulator/PersoSimTest.java");
	}
	
	/**
	 * Positive test case: check behavior of PersoSim constructor when called with null argument.
	 */
	//FIXME SLS this test does not test a null argument as stated in the JavaDoc
	@Test
	public void testPersoSimConstructorUnknownArgument() {
		System.out.println("test013");
		persoSim = new PersoSim(new String[]{"unknownCommand"});
		assertNotNull(persoSim);
	}
	
	/**
	 * Positive test case: test setting of new personalization via user arguments.
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws JAXBException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	@Test
	public void testExecuteUserCommandsCmdLoadPersonalization_ValidPersonalization() throws InterruptedException, IllegalArgumentException, JAXBException, UnknownHostException, IOException {
		System.out.println("test014");
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		persoSim.executeUserCommands(PersoSim.CMD_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_2);
		
		String responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
		
		String responseReadBinary = (extractResponse(exchangeApdu(READ_BINARY_APDU))).toUpperCase();
		
		String responseReadBinaryExpected = (HexString.encode(EF_CS_CONTENT_2)).toUpperCase();
		
		assertEquals(responseReadBinaryExpected, responseReadBinary);
	}
	
	/**
	 * Negative test case: test setting of new personalization via user arguments with invalid personalization. //FIXME SLS be a little more detailed here, I see at least two different types of invalid personalization: File does not exist vs. File does not encode a perso, please test both
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	@Test
	public void testExecuteUserCommandsCmdLoadPersonalizationInvalidPersonalization() throws InterruptedException, IllegalArgumentException, UnknownHostException, IOException {
		System.out.println("test015");
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		persoSim.executeUserCommands(PersoSim.CMD_LOAD_PERSONALIZATION, "non-existing.file");
		
		
		//FIXME SLS this check can be achieved by expectation in the @Test annotation instead of this bulky construct here
		boolean caughtIoException = false;
		
		try {
			exchangeApdu(SELECT_APDU);
		} catch (IOException e) {
			caughtIoException = true;
		}
		
		assertTrue(caughtIoException);
	}
	
	/**
	 * Positive test case: test setting of new port via user arguments.
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws IOException 
	 */
	@Test
	public void testExecuteUserCommandsCmdSetPortNo() throws InterruptedException, IllegalArgumentException, IOException {
		System.out.println("test016");
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(PersoSim.CMD_SEND_APDU);
		args.add(SELECT_APDU);
		String responseSelect = persoSim.cmdSendApdu(args);
		assertEquals(SW_NO_ERROR, responseSelect);
		
		int portPostExpected = PersoSim.DEFAULT_SIM_PORT + 1;
		persoSim.executeUserCommands(PersoSim.CMD_SET_PORT, (new Integer (portPostExpected)).toString());
		
		responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU, portPostExpected));
		
		assertEquals(SW_NO_ERROR, responseSelect);
	}

}
