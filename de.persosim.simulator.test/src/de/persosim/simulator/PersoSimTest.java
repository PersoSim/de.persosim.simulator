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

import mockit.Deencapsulation; //FIXME SLS why is this import needed at all
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
	
	@Before
	public void setUp() {
		
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
	}
	
	public static String sendCommand(PersoSim persoSimInstance, String... args) throws UnsupportedEncodingException {
		PrintStream	origOut	= System.out;
		PrintStream	origErr	= System.err;
		
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		PrintStream	stdout = new PrintStream(baos1);
		
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		PrintStream	stderr = new PrintStream(baos2);
		
		System.setOut(stdout);
		System.setErr(stderr);
		
		origOut.print(baos1.toString());
		origOut.flush();
		
		persoSimInstance.executeUserCommands(args);
		
		String responseSelectBulk = baos1.toString("UTF-8");
		origOut.print(responseSelectBulk);
		origOut.flush();
		
		int startIndex = responseSelectBulk.trim().lastIndexOf("\n");
		String responseSelect = "";

		if((startIndex != -1) && (startIndex != responseSelectBulk.length())){
			responseSelect = responseSelectBulk.substring(startIndex+3).trim();
		}
		
		System.setOut(origOut);
		System.setErr(origErr);
		
		return responseSelect;
	}
	
	//FIXME SLS missing test: launch PersoSimConsole, hit enter => this produces a NPE and shouldn't
	//FIXME SLS missing test: launch PersoSimConsole, type exit, hit enter => this produces a list of available commands and shouldn't
	//FIXME SLS missing test: launch PersoSimConsole, type an unknown command, hit enter => this should produces a list of available commands (along the existing line that the given command is unknown) and doesn't
	
	/**
	 * Positive test case: test implicit setting of a default personalization if no other personalization is explicitly set.
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testImplicitSettingOfDefaultPersonalization() throws FileNotFoundException, JAXBException, UnsupportedEncodingException {
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
		
		String responseSelect = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU);
		assertEquals(SW_NO_ERROR, responseSelect);
		
		String responseReadBinaryExpected = HexString.encode(Arrays.copyOf(EF_CS_CONTENT_1, 4)).toUpperCase();
		String responseReadBinary = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, READ_BINARY_APDU);
		assertEquals(responseReadBinaryExpected, responseReadBinary.substring(0, responseReadBinary.length() - 4).toUpperCase());
	}
	
	/**
	 * Positive test case: check for NullPointerException if PersoSim constructor is called with null argument.
	 */
	@Test
	public void testPersoSimConstructorNullArgument() {
		persoSim = new PersoSim((String) null);
		assertNotNull(persoSim);
	}
	
	/**
	 * Positive test case: test start of socket simulator.
	 * @throws InterruptedException 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testStartSimulator() throws InterruptedException, UnsupportedEncodingException {
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		String responseSelect1 = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU);
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		String responseSelect2 = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU);
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
		persoSim = new PersoSim(PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1);
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		String responseSelect1 = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU);
		assertEquals(SW_NO_ERROR, responseSelect1);
		
		persoSim.executeUserCommands(PersoSim.CMD_STOP);
		String responseSelect2 = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU);
		assertTrue(responseSelect1 != responseSelect2);
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
		PersoSim.parseCommand(null);
	}
	
	/**
	 * Positive test case: parse arguments from a String containing spaces only at start and end.
	 */
	@Test
	public void testParseCommandUntrimmedCoherentString() {
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
		PersoSim.parsePersonalization("file not found");
	}
	
	/**
	 * Negative test case: parse personalization from an invalid existing file.
	 * @throws FileNotFoundException 
	 * @throws JAXBException 
	 */
	@Test(expected = JAXBException.class)
	public void testParsePersonalizationInvalidFile() throws FileNotFoundException, JAXBException {
		PersoSim.parsePersonalization("src/de/persosim/simulator/PersoSimTest.java");
	}
	
	/**
	 * Positive test case: check behavior of PersoSim constructor when called with null argument.
	 */
	@Test
	public void testPersoSimConstructorUnknownArgument() {
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
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		persoSim.executeUserCommands(PersoSim.CMD_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_2);
		
		String responseSelect = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU);
		assertEquals(SW_NO_ERROR, responseSelect);
		
		String responseReadBinary = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, READ_BINARY_APDU);
		
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
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		persoSim.executeUserCommands(PersoSim.CMD_START);
		persoSim.executeUserCommands(PersoSim.CMD_LOAD_PERSONALIZATION, "non-existing.file");
		
		String responseSelect = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, SELECT_APDU);
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
