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
import java.util.Arrays;

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
			persoSim.stopSimulator();
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
	 * @return the trimmed data read from redirected System.out
	 * @throws UnsupportedEncodingException 
	 */
	public static String readRedStdOut() throws UnsupportedEncodingException {
		String responseBulk = "";
		
		responseBulk = redStdOut.toString("UTF-8").trim();
		
		origOut.print(responseBulk);
		origOut.flush();
		
		return responseBulk;
	}
	
	/**
	 * Positive test case: check behavior of PersoSim constructor when called with empty argument.
	 * @throws Exception 
	 */
	@Test
	public void testPersoSimConstructor_EmptyArgument() throws Exception {
		activateStdOutRedirection();
		
		persoSim = new PersoSim(new String[]{});
		
		String response = readRedStdOut();
		
		assertEquals(PersoSim.LOG_NO_OPERATION, response);
	}
	
	/**
	 * Positive test case: check how PersoSim constructor handles null argument.
	 * @throws Exception 
	 */
	@Test
	public void testPersoSimConstructor_NullArgument() throws Exception {
		activateStdOutRedirection();
		
		persoSim = new PersoSim((String) null);
		
		String response = readRedStdOut();
		
		assertEquals(PersoSim.LOG_NO_OPERATION, response);
	}
	
	/**
	 * Positive test case: check how PersoSim command line handles empty argument.
	 * @throws Exception 
	 */
	@Test
	public void testExecuteUserCommands_EmptyArgument() throws Exception {
		persoSim = new PersoSim((String) null);
		
		activateStdOutRedirection();
		
		persoSim.executeUserCommands("");
		
		String response = readRedStdOut();
		
		assertEquals(PersoSim.LOG_NO_OPERATION, response);
	}
	
	/**
	 * Positive test case: check how PersoSim command line handles exit command.
	 * @throws Exception
	 */
	@Test
	public void testExecuteUserCommands_Exit() throws Exception {
		persoSim = new PersoSim((String) null);
		
		activateStdOutRedirection();
		
		persoSim.executeUserCommands(PersoSim.CMD_EXIT);
		
		String response = readRedStdOut();
		
		assertEquals(PersoSim.LOG_SIM_EXIT, response);
	}
	
	/**
	 * Positive test case: check how PersoSim command line handles an unknown argument.
	 * @throws Exception
	 */
	@Test
	public void testExecuteUserCommands_UnknownArgument() throws Exception {
		persoSim = new PersoSim((String) null);
		
		activateStdOutRedirection();
		
		persoSim.executeUserCommands("unknown");
		
		String response = readRedStdOut();
		
		assertTrue(response.startsWith(PersoSim.LOG_UNKNOWN_ARG));
	}
	
	/**
	 * Positive test case: test implicit setting of a default personalization if no other personalization is explicitly set.
	 * @throws Exception
	 */
	@Test
	public void testImplicitSettingOfDefaultPersonalization() throws Exception {
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
		persoSim.cmdStartSimulator(new ArrayList<String>(Arrays.asList(new String[]{PersoSim.CMD_START}))); //FIXME SLS why not call persoSim.startSimulator() directly? see other occurences of persoSim.cmd within this file
		
		String responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
		
		String responseReadBinaryExpected = (HexString.encode(EF_CS_CONTENT_1)).toUpperCase();
		String responseReadBinary = (extractResponse(exchangeApdu(READ_BINARY_APDU))).toUpperCase();
		assertEquals(responseReadBinaryExpected, responseReadBinary);
	}
	
	/**
	 * Positive test case: test start of socket simulator.
	 * @throws Exception
	 */
	@Test
	public void testStartSimulator() throws Exception {
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		boolean caughtIoException = false;
		try {
			exchangeApdu(SELECT_APDU);
		} catch (IOException e) {
			caughtIoException = true;
		}
		
		assertTrue(caughtIoException);
		
		persoSim.cmdStartSimulator(new ArrayList<String>(Arrays.asList(new String[]{PersoSim.CMD_START})));
		
		String responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
	}
	
	/**
	 * Positive test case: test stop of socket simulator.
	 * @throws Exception 
	 */
	@Test
	public void testStopSimulator() throws Exception {
		persoSim = new PersoSim(PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1);
		
		persoSim.cmdStartSimulator(new ArrayList<String>(Arrays.asList(new String[]{PersoSim.CMD_START})));
		
		String responseSelect1 = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect1);
		
		persoSim.cmdStopSimulator(new ArrayList<String>(Arrays.asList(new String[]{PersoSim.CMD_STOP})));
		
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
	public void testParseCommand_UntrimmedCoherentString() {
		String arg = "string";
		String[] result = PersoSim.parseCommand(" " + arg + "  ");
		
		assertEquals(result.length, 1);
		assertEquals(result[0], arg);
	}
	
	/**
	 * Positive test case: parse arguments from a String containing spaces not only at start and end.
	 */
	@Test
	public void testParseCommand_IncoherentString() {
		String arg1 = "string1";
		String arg2 = "string 2";
		String[] result = PersoSim.parseCommand(" " + arg1 + "  " + arg2);
		
		assertEquals(result.length, 2);
		assertEquals(result[0], arg1);
		assertEquals(result[1], arg2);
	}
	
	/**
	 * Positive test case: parse personalization from a valid file.
	 * @throws Exception
	 */
	@Test
	public void testParsePersonalization_ValidFile() throws Exception {
		Personalization perso = PersoSim.parsePersonalization(DUMMY_PERSONALIZATION_FILE_1);
		
		assertNotNull(perso);
	}
	
	/**
	 * Negative test case: parse personalization from a non-existing file.
	 * @throws Exception
	 */
	@Test(expected = FileNotFoundException.class)
	public void testParsePersonalization_FileNotFound() throws Exception {
		PersoSim.parsePersonalization("file not found");
	}
	
	/**
	 * Negative test case: parse personalization from an invalid existing file.
	 * @throws Exception
	 */
	@Test(expected = JAXBException.class)
	public void testParsePersonalization_InvalidFile() throws Exception {
		PersoSim.parsePersonalization("src/de/persosim/simulator/PersoSimTest.java");
	}
	
	/**
	 * Positive test case: check behavior of PersoSim constructor when called with unknown argument.
	 */
	@Test
	public void testPersoSimConstructor_UnknownArgument() {
		persoSim = new PersoSim(new String[]{"unknownCommand"});
		assertNotNull(persoSim);
	}
	
	/**
	 * Positive test case: test setting of new personalization via user arguments.
	 * @throws Exception
	 */
	@Test
	public void testExecuteUserCommandsCmdLoadPersonalization_ValidPersonalization() throws Exception {
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		persoSim.cmdStartSimulator(new ArrayList<String>(Arrays.asList(new String[]{PersoSim.CMD_START})));
		
		persoSim.cmdLoadPersonalization(new ArrayList<String>(Arrays.asList(new String[]{PersoSim.CMD_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_2})));
		
		String responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
		
		String responseReadBinary = (extractResponse(exchangeApdu(READ_BINARY_APDU))).toUpperCase();
		
		String responseReadBinaryExpected = (HexString.encode(EF_CS_CONTENT_2)).toUpperCase();
		
		assertEquals(responseReadBinaryExpected, responseReadBinary);
	}
	
	/**
	 * Negative test case: test setting of new personalization via user arguments with argument referencing existing file containing invalid personalization.
	 * @throws Exception 
	 */
	@Test
	public void testExecuteUserCommandsCmdLoadPersonalization_InvalidPersonalizationFile() throws Exception {
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
		
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, "src/de/persosim/simulator/PersoSimTest.java"});
		
		persoSim.startSimulator();
		
		String responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
		
		String responseReadBinaryExpected = (HexString.encode(EF_CS_CONTENT_1)).toUpperCase();
		String responseReadBinary = (extractResponse(exchangeApdu(READ_BINARY_APDU))).toUpperCase();
		assertEquals(responseReadBinaryExpected, responseReadBinary);
	}
	
	/**
	 * Negative test case: test setting of new personalization via user arguments with argument referencing non existing file.
	 * @throws Exception
	 */
	@Test
	public void testExecuteUserCommandsCmdLoadPersonalization_FileNotFound() throws Exception {
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
		
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, "non-existing.file"});
		
		persoSim.startSimulator();
		
		String responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
		
		String responseReadBinaryExpected = (HexString.encode(EF_CS_CONTENT_1)).toUpperCase();
		String responseReadBinary = (extractResponse(exchangeApdu(READ_BINARY_APDU))).toUpperCase();
		assertEquals(responseReadBinaryExpected, responseReadBinary);
	}
	
	/**
	 * Positive test case: test setting of new port via user arguments.
	 * @throws Exception
	 */
	@Test
	//FIXME SLS no need to test setting of the port through a userCommand
	public void testExecuteUserCommandsCmdSetPortNo() throws Exception {
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(PersoSim.CMD_SEND_APDU);
		args.add(SELECT_APDU);
		String responseSelect = persoSim.cmdSendApdu(args);
		assertEquals(SW_NO_ERROR, responseSelect);
		
		int portPostExpected = PersoSim.DEFAULT_SIM_PORT + 1;
		
		persoSim.cmdSetPortNo(new ArrayList<String>(Arrays.asList(new String[]{PersoSim.CMD_SET_PORT, (new Integer (portPostExpected)).toString()})));
		
		responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU, portPostExpected));
		
		assertEquals(SW_NO_ERROR, responseSelect);
	}

}
