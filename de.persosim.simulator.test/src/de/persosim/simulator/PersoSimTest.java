package de.persosim.simulator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.xml.bind.JAXBException;

import mockit.Mocked;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.persosim.simulator.perso.DefaultPersoTestPki;
import de.persosim.simulator.perso.MinimumPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class PersoSimTest extends PersoSimTestCase {
	
	PersoSim persoSim;
	
	@Mocked DefaultPersoTestPki defaultPersoTestPki;
	
	public static final byte[] EF_CS_CONTENT = HexString.toByteArray("FF01020304");
	
	public static final String DUMMY_PERSONALIZATION_FILE = "tmp/dummyPersonalization1.xml";
	
	public static final String SELECT_APDU = "00A4020C02011C";
	public static final String READ_BINARY_APDU = "00B0000005";
	public static final String SW_NO_ERROR = "9000";
	
	static PrintStream	origOut;
	static ByteArrayOutputStream redStdOut;
	
	
	
	@Before
	public void setUp() {
		origOut	= System.out;
		
		MinimumPersonalization perso1 = new MinimumPersonalization(EF_CS_CONTENT);
		perso1.writeToFile(DUMMY_PERSONALIZATION_FILE);
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
		return exchangeApdu(cmdApdu, Simulator.DEFAULT_SIM_PORT);
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
			socket = new Socket(Simulator.DEFAULT_SIM_HOST, port);

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
		
		assertTrue(response.contains(CommandParser.LOG_NO_OPERATION));
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
		
		assertTrue(response.contains(CommandParser.LOG_NO_OPERATION));
	}
	
	/**
	 * Positive test case: check how PersoSim command line handles empty argument.
	 * @throws Exception 
	 */
	@Test
	public void testExecuteUserCommands_EmptyArgument() throws Exception {
		persoSim = new PersoSim((String) null);
		
		activateStdOutRedirection();
		
		CommandParser.executeUserCommands(persoSim, "");
		
		String response = readRedStdOut();

		assertTrue(response.contains(CommandParser.LOG_NO_OPERATION));
	}
	
	/**
	 * Positive test case: check how PersoSim command line handles exit command.
	 * @throws Exception
	 */
	@Test
	public void testExecuteUserCommands_Exit() throws Exception {
		persoSim = new PersoSim((String) null);
		
		activateStdOutRedirection();
		
		CommandParser.executeUserCommands(persoSim, CommandParser.CMD_EXIT);
		
		String response = readRedStdOut();

		assertTrue(response.contains(PersoSim.LOG_SIM_EXIT));
	}
	
	/**
	 * Positive test case: check how PersoSim command line handles an unknown argument.
	 * @throws Exception
	 */
	@Test
	public void testExecuteUserCommands_UnknownArgument() throws Exception {
		persoSim = new PersoSim((String) null);
		
		activateStdOutRedirection();
		
		CommandParser.executeUserCommands(persoSim, "unknown");
		
		String response = readRedStdOut();
		
		assertTrue(response.contains(CommandParser.LOG_UNKNOWN_ARG));
	}
	
	/**
	 * Positive test case: test implicit setting of a default personalization if no other personalization is explicitly set.
	 * @throws Exception
	 */
	@Test
	public void testImplicitSettingOfMinimumPersonalization() throws Exception {
		persoSim = new PersoSim();
		persoSim.startSimulator();
		
		byte [] response = persoSim.processCommand(HexString.toByteArray(SELECT_APDU));
		assertArrayEquals(Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR), response);
		
		byte[] responseReadBinaryExpected = Utils.concatByteArrays(MinimumPersonalization.DEFAULT_EF_CA_VALUE, Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR));
		
		response = persoSim.processCommand(HexString.toByteArray(READ_BINARY_APDU));
		assertArrayEquals(responseReadBinaryExpected, response);
	}
	
	/**
	 * Positive test case: test start of socket simulator.
	 * @throws Exception
	 */
	@Test
	public void testStartSimulator() throws Exception {
		persoSim = new PersoSim();
		
		byte [] response = persoSim.processCommand(HexString.toByteArray(SELECT_APDU));
		
		assertArrayEquals(Utils.toUnsignedByteArray(Iso7816.SW_6F00_UNKNOWN), response);
		
		persoSim.startSimulator();
		
		response = persoSim.processCommand(HexString.toByteArray(SELECT_APDU));
		
		assertArrayEquals(Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR), response);
	}
	
	/**
	 * Positive test case: second execution of startSimulator() shouldn't do anything, especially not fail or throw an exception.
	 * @throws Exception
	 */
	@Test
	public void testStartSimulator_twice() throws Exception {
		persoSim = new PersoSim(new String[]{CommandParser.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE});
		
		persoSim.startSimulator();
		assertTrue(persoSim.startSimulator());
		
		//ensure that the simulator is responding
		byte [] response = persoSim.processCommand(HexString.toByteArray(SELECT_APDU));
		assertArrayEquals(Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR), response);
	}
	
	/**
	 * Positive test case: test stop of socket simulator.
	 * @throws Exception 
	 */
	@Test(expected = ConnectException.class)
	public void testStopSimulator() throws Exception {
		persoSim = new PersoSim(CommandParser.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE);
		
		persoSim.startSimulator();
		
		String responseSelect1 = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect1);
		
		persoSim.stopSimulator();
		
		exchangeApdu(SELECT_APDU);
	}
	
	/**
	 * Positive test case: parse arguments from an empty String.
	 */
	@Test
	public void testParseCommandEmptyString() {
		String[] result = CommandParser.parseCommand("");
		
		assertEquals(result.length, 0);
	}
	
	/**
	 * Negative test case: parse arguments from null.
	 */
	@Test(expected = NullPointerException.class)
	public void testParseCommandNull() {
		System.out.println("test007");
		CommandParser.parseCommand(null);
	}
	
	/**
	 * Positive test case: parse arguments from a String containing spaces only at start and end.
	 */
	@Test
	public void testParseCommand_UntrimmedCoherentString() {
		String arg = "string";
		String[] result = CommandParser.parseCommand(" " + arg + "  ");
		
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
		String[] result = CommandParser.parseCommand(" " + arg1 + "  " + arg2);
		
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
		Personalization perso = PersoSim.parsePersonalization(DUMMY_PERSONALIZATION_FILE);
		
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
	 * Positive test case: test setting of new personalization.
	 * @throws Exception
	 */
	@Test
	public void testLoadPersonalization_ValidPersonalization() throws Exception {
		persoSim = new PersoSim();
		
		persoSim.startSimulator();
		
		persoSim.loadPersonalization(DUMMY_PERSONALIZATION_FILE);

		byte [] response = persoSim.processCommand(HexString.toByteArray(SELECT_APDU));
		assertArrayEquals(Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR), response);
		
		response = persoSim.processCommand(HexString.toByteArray(READ_BINARY_APDU));
		
		byte [] responseReadBinaryExpected = Utils.concatByteArrays(EF_CS_CONTENT, Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR));
		
		assertArrayEquals(responseReadBinaryExpected, response);
	}
	
	/**
	 * Negative test case: test setting of new personalization via user arguments with argument referencing existing file containing invalid personalization.
	 * @throws Exception 
	 */
	@Test
	public void testArgLoadPersonalization_InvalidPersonalizationFile() throws Exception {		
		persoSim = new PersoSim(new String[]{CommandParser.ARG_LOAD_PERSONALIZATION, "src/de/persosim/simulator/PersoSimTest.java"});
		
		assertFalse(persoSim.startSimulator());
	}
	
	/**
	 * Negative test case: test setting of new personalization via user arguments with argument referencing non existing file.
	 * @throws Exception
	 */
	@Test
	public void testArgLoadPersonalization_FileNotFound() throws Exception {
		persoSim = new PersoSim(new String[]{CommandParser.ARG_LOAD_PERSONALIZATION, "non-existing.file"});
		
		assertFalse(persoSim.startSimulator());
	}
	
	/**
	 * Positive test case: test setting of new port via user arguments.
	 * @throws Exception
	 */
	@Test
	@Ignore
	//FIXME MBK move this test to the proper position in the SocketAdapter test package
	public void testExecuteUserCommandsCmdSetPortNo() throws Exception {
		persoSim = new PersoSim();
		persoSim.startSimulator();
		
		
		
		String responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU));
		assertEquals(SW_NO_ERROR, responseSelect);
		
		int portPostExpected = Simulator.DEFAULT_SIM_PORT + 1;
		
		CommandParser.setPort(persoSim, (new Integer (portPostExpected)).toString());
		persoSim.restartSimulator();
		
		responseSelect = extractStatusWord(exchangeApdu(SELECT_APDU, portPostExpected));
		
		assertEquals(SW_NO_ERROR, responseSelect);
	}

}
