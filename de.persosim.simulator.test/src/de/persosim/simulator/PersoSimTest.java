package de.persosim.simulator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.Profile01;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class PersoSimTest extends PersoSimTestCase {
	
	PersoSim persoSim;
	
	public static final String SELECT_APDU = "00A4020C02011C";
	public static final String SW_NO_ERROR = "9000"; //FIXME why this constant?
	
	static PrintStream	origOut;
	static ByteArrayOutputStream redStdOut;
	
	
	
	@Before
	public void setUp() throws FileNotFoundException {
		origOut	= System.out;
	}
	
	@After
	public void tearDown() {
		if(persoSim != null) {
			persoSim.stopSimulator();
		}
		
		System.setOut(origOut);
	}
	
	/**
	 * This method returns the default personalization. Currently we use
	 * Profile01 as no specific requirements are known. This might change in the
	 * future.
	 * 
	 * @return the default personalization
	 * @throws FileNotFoundException
	 */
	public Personalization getDefaultPerso() throws FileNotFoundException {
		return new Profile01();
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
		
		activateStdOutRedirection();
		
		CommandParser.executeUserCommands("");
		
		String response = readRedStdOut();

		assertTrue(response.contains(CommandParser.LOG_NO_OPERATION));
	}
	
	/**
	 * Positive test case: check how PersoSim command line handles an unknown argument.
	 * @throws Exception
	 */
	@Test
	public void testExecuteUserCommands_UnknownArgument() throws Exception {
		
		activateStdOutRedirection();
		
		CommandParser.executeUserCommands("unknown");
		
		String response = readRedStdOut();
		
		assertTrue(response.contains(CommandParser.LOG_UNKNOWN_ARG));
	}
	
	/**
	 * Positive test case: test start of socket simulator.
	 * @throws Exception
	 */
	@Test
	public void testStartSimulator() throws Exception {
		persoSim = new PersoSim();
		
		byte [] response = persoSim.processCommand(HexString.toByteArray(SELECT_APDU));
		
		assertArrayEquals(Utils.toUnsignedByteArray((short)(Iso7816.SW_6F00_UNKNOWN+0x85)), response);
		
		persoSim.loadPersonalization(getDefaultPerso());
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
		persoSim = new PersoSim();
		
		persoSim.loadPersonalization(getDefaultPerso());
		persoSim.startSimulator();
		assertTrue(persoSim.startSimulator());
		
		//ensure that the simulator is still responding
		byte [] response = persoSim.processCommand(HexString.toByteArray(SELECT_APDU));
		assertArrayEquals(Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR), response);
	}
	
	/**
	 * Positive test case: test stop of socket simulator.
	 * @throws Exception 
	 */
	@Test
	public void testStopSimulator() throws Exception {
		persoSim = new PersoSim();
		
		persoSim.loadPersonalization(getDefaultPerso());
		persoSim.startSimulator();
		
		byte [] responseSelect = persoSim.processCommand(HexString.toByteArray(SELECT_APDU));
		assertArrayEquals(Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR), responseSelect);
		
		persoSim.stopSimulator();
		
		responseSelect = persoSim.processCommand(HexString.toByteArray(SELECT_APDU));
		assertArrayEquals(Utils.toUnsignedByteArray((short)(Iso7816.SW_6F00_UNKNOWN+0x85)), responseSelect);
	}
	
	/**
	 * Positive test case: check behavior of PersoSim constructor when called with unknown argument.
	 */
	@Test
	public void testPersoSimConstructor_UnknownArgument() {
		persoSim = new PersoSim(new String[]{"unknownCommand"});
		assertNotNull(persoSim);
	}

}
