package de.persosim.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import mockit.Deencapsulation;
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
	static PrintStream origErr;
	
	static ByteArrayOutputStream redStdOut;
	static ByteArrayOutputStream redStdErr;
	
	
	
	@Before
	public void setUp() {
		origOut	= System.out;
		origErr = System.err;
		
		MinimumPersonalization perso1 = new MinimumPersonalization(EF_CS_CONTENT_1);
		perso1.writeToFile(DUMMY_PERSONALIZATION_FILE_1);
		
		MinimumPersonalization perso2 = new MinimumPersonalization(EF_CS_CONTENT_2);
		perso2.writeToFile(DUMMY_PERSONALIZATION_FILE_2);
	}
	
	@After
	public void tearDown() {
		if(persoSim != null) {
			persoSim.executeUserCommands(PersoSim.CMD_STOP);
		}
		
		System.setOut(origOut);
		System.setErr(origErr);
	}
	
	public static String sendCommand(PersoSim persoSimInstance, String... args) {
		activateStdOutRedirection();
		
		persoSimInstance.executeUserCommands(args);
		
		String responseBulk = readRedStdOut();
		
		System.setOut(origOut);
		System.setErr(origErr);
		
		return responseBulk;
	}
	
	public static String extractStatusWord(String responseBulk) {
		int startIndex = responseBulk.trim().lastIndexOf("\n");
		String response = "";

		if((startIndex != -1) && (startIndex != responseBulk.length())){
			response = responseBulk.substring(startIndex+3).trim();
		}
		
		return response;
	}
	
	public static void activateStdOutRedirection() {
		redStdOut = new ByteArrayOutputStream();
		PrintStream	stdout = new PrintStream(redStdOut);
		
		System.setOut(stdout);
		origOut.print(redStdOut.toString());
		origOut.flush();
	}
	
	public static void activateStdErrRedirection() {
		redStdErr = new ByteArrayOutputStream();
		PrintStream	stderr = new PrintStream(redStdErr);
		
		System.setErr(stderr);
		origOut.print(redStdErr.toString());
		origOut.flush();
	}
	
	public static String readRedStdOut() {
		String responseBulk = "";
		
		try {
			responseBulk = redStdOut.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// "UTF-8" _is_ valid
			e.printStackTrace();
		}
		
		origOut.print(responseBulk);
		origOut.flush();
		
		return responseBulk;
	}
	
	public static String readRedStdErr() {
		String responseBulk = "";
		
		try {
			responseBulk = redStdErr.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// "UTF-8" _is_ valid
			e.printStackTrace();
		}
		
		origErr.print(responseBulk);
		origErr.flush();
		
		return responseBulk;
	}
	
	//FIXME SLS missing test: launch PersoSimConsole, type an unknown command, hit enter => this should produces a list of available commands (along the existing line that the given command is unknown) and doesn't
	
	/**
	 * Positive test case: check behavior of PersoSim constructor when called with empty argument.
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testPersoSimConstructorEmptyArgument() throws UnsupportedEncodingException {
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
	public void testPersoSimConstructorNullArgument() {
		System.out.println("test002");
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
	public void testExecuteUserCommandsEmptyArgument() {
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
	public void testExecuteUserCommandsExit() {
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
	public void testExecuteUserCommandsUnknownArgument() {
		System.out.println("test019");
		
		persoSim = new PersoSim((String) null);
		
		activateStdOutRedirection();
		
		persoSim.executeUserCommands("unknown");
		
		String responseBulk = readRedStdOut();
		
		String response = responseBulk.trim();
		
		assertTrue(response.startsWith(PersoSim.LOG_UNKNOWN_ARG));
	}
	
	/**
	 * Positive test case: test implicit setting of a default personalization if no other personalization is explicitly set.
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testImplicitSettingOfDefaultPersonalization() throws FileNotFoundException, JAXBException, UnsupportedEncodingException {
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
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		String responseSelect = extractStatusWord(sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
		
		String responseReadBinaryExpected = HexString.encode(Arrays.copyOf(EF_CS_CONTENT_1, 4)).toUpperCase();
		String responseReadBinary = extractStatusWord(sendCommand(persoSim, PersoSim.CMD_SEND_APDU, READ_BINARY_APDU));
		assertEquals(responseReadBinaryExpected, responseReadBinary.substring(0, responseReadBinary.length() - 4).toUpperCase());
	}
	
	/**
	 * Positive test case: test start of socket simulator.
	 * @throws InterruptedException 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testStartSimulator() throws InterruptedException, UnsupportedEncodingException {
		System.out.println("test004");
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		String responseSelect1 = extractStatusWord(sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU));
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		String responseSelect2 = extractStatusWord(sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect2);
		assertTrue(responseSelect1 != responseSelect2);
	}
	
	/**
	 * Positive test case: test stop of socket simulator.
	 * @throws InterruptedException 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testStopSimulator() throws InterruptedException, UnsupportedEncodingException {
		System.out.println("test005");
		persoSim = new PersoSim(PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1);
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		String responseSelect1 = extractStatusWord(sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect1);
		
		persoSim.executeUserCommands(PersoSim.CMD_STOP);
		String responseSelect2 = extractStatusWord(sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU));
		assertTrue(responseSelect1 != responseSelect2);
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
	 * @throws FileNotFoundException 
	 * @throws JAXBException 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testExecuteUserCommandsCmdLoadPersonalizationValidPersonalization() throws InterruptedException, FileNotFoundException, IllegalArgumentException, JAXBException, UnsupportedEncodingException {
		System.out.println("test014");
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		persoSim.executeUserCommands(PersoSim.CMD_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_2);
		
		String responseSelect = extractStatusWord(sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
		
		String responseReadBinary = extractStatusWord(sendCommand(persoSim, PersoSim.CMD_SEND_APDU, READ_BINARY_APDU));
		
		String responseReadBinaryExpected = HexString.encode(Arrays.copyOf(EF_CS_CONTENT_2, 4)).toUpperCase();
		
		assertEquals(responseReadBinaryExpected, responseReadBinary.substring(0, responseReadBinary.length() - 4).toUpperCase());
	}
	
	/**
	 * Negative test case: test setting of new personalization via user arguments with invalid personalization.
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testExecuteUserCommandsCmdLoadPersonalizationInvalidPersonalization() throws InterruptedException, FileNotFoundException, IllegalArgumentException, UnsupportedEncodingException {
		System.out.println("test015");
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		persoSim.executeUserCommands(PersoSim.CMD_LOAD_PERSONALIZATION, "non-existing.file");
		
		String responseSelect = extractStatusWord(sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU));
		assertFalse(responseSelect.equals(SW_NO_ERROR));
	}
	
	/**
	 * Positive test case: test setting of new port via user arguments.
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testExecuteUserCommandsCmdSetPortNo() throws InterruptedException, FileNotFoundException, IllegalArgumentException {
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
		
		responseSelect = Deencapsulation.invoke(persoSim, "exchangeApdu", SELECT_APDU, PersoSim.DEFAULT_SIM_HOST, portPostExpected);
		
		assertEquals(SW_NO_ERROR, responseSelect);
	}

}
