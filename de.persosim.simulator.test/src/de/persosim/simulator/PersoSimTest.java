package de.persosim.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
	
	//FIXME SLS ensure that these files are located somewhere that is ignored by git (e.g. tmp)
	public static final String DUMMY_PERSONALIZATION_FILE_1 = "dummyPersonalization1.xml";
	public static final String DUMMY_PERSONALIZATION_FILE_2 = "dummyPersonalization2.xml";
	
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
		
		persoSim = new PersoSim(null);
		persoSim.executeUserCommands(PersoSim.CMD_START);
		
		String responseSelect = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, "00A4020C02011C");
		assertEquals(responseSelect, "9000");
		
		String responseReadBinaryExpected = HexString.encode(Arrays.copyOf(EF_CS_CONTENT_1, 4)).toUpperCase();
		String responseReadBinary = sendCommand(persoSim, PersoSim.CMD_SEND_APDU, "00B0000004");
		assertEquals(responseReadBinaryExpected, responseReadBinary.substring(0, responseReadBinary.length() - 4).toUpperCase());
	}
	
	public static String sendCommand(PersoSim persoSimInstance, String... args) throws UnsupportedEncodingException {
//		InputStream	origIn	= System.in;
		PrintStream	origOut	= System.out;
		PrintStream	origErr	= System.err;
		
//		InputStream	stdin = null;
//		String cmdSelect = PersoSim.CMD_SEND_APDU + " 00A4020C02011C";
//		try {
//			stdin = new ByteArrayInputStream(cmdSelect.getBytes("UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println ("Redirect:  Unable to open input stream!");
//		    System.exit (1);
//		}
		
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		PrintStream	stdout = new PrintStream(baos1);
		
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		PrintStream	stderr = new PrintStream(baos2);
		
		System.setOut(stdout);
		System.setErr(stderr);
		
		origOut.print(baos1.toString());
		origOut.flush();
		
		//FIXME SLS reasign stdin and stdout instead of using deencapsulation, deencapsulation is nearly always evil, especially on the DUT
		
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
	
	/**
	 * Negative test case: check for NullPointerException if PersoSim constructor is called with null argument.
	 */
	@Test(expected = NullPointerException.class)
	public void testPersoSimConstructorNullArgument() {
		persoSim = new PersoSim(null);
	}
	
	//FIXME SLS I don't see a testcase that uses the real default perso...
	
	/**
	 * Positive test case: test start of socket simulator.
	 * @throws InterruptedException 
	 */
	@Test
	public void testStartSimulator() throws InterruptedException {
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		Deencapsulation.invoke(persoSim, "startSimulator");
		
		SocketSimulator socketSim = (SocketSimulator) Deencapsulation.getField(persoSim, "simulator");
		
		assertTrue(socketSim.isRunning());
	}
	
	/**
	 * Positive test case: test stop of socket simulator.
	 * @throws InterruptedException 
	 */
	@Test
	public void testStopSimulator() throws InterruptedException {
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		Deencapsulation.invoke(persoSim, "startSimulator");
		
		SocketSimulator socketSim = (SocketSimulator) Deencapsulation.getField(persoSim, "simulator");
		
		assertTrue(socketSim.isRunning());
		Deencapsulation.invoke(persoSim, "stopSimulator");
		
		assertFalse(socketSim.isRunning());
		assertNull(Deencapsulation.getField(persoSim, "simulator"));
	}
	
	/**
	 * Positive test case: parse arguments from an empty String.
	 */
	@Test
	public void parseArgsEmptyString() {
		String[] result = PersoSim.parseCommand("");
		
		assertEquals(result.length, 0);
	}
	
	/**
	 * Negative test case: parse arguments from null.
	 */
	@Test(expected = NullPointerException.class)
	public void parseArgsNull() {
		PersoSim.parseCommand(null);
	}
	
	/**
	 * Positive test case: parse arguments from a String containing spaces only at start and end.
	 */
	@Test
	public void parseArgsUntrimmedCoherentString() {
		String arg = "string";
		String[] result = PersoSim.parseCommand(" " + arg + "  ");
		
		assertEquals(result.length, 1);
		assertEquals(result[0], arg);
	}
	
	/**
	 * Positive test case: parse arguments from a String containing spaces not only at start and end.
	 */
	@Test
	public void parseArgsIncoherentString() {
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
	public void parsePersonalizationValidFile() throws FileNotFoundException, JAXBException {
		Personalization perso = PersoSim.parsePersonalization(DUMMY_PERSONALIZATION_FILE_1);
		
		assertNotNull(perso);
	}
	
	/**
	 * Negative test case: parse personalization from a non-existing file.
	 * @throws FileNotFoundException 
	 * @throws JAXBException 
	 */
	@Test(expected = FileNotFoundException.class)
	public void parsePersonalizationFileNotFound() throws FileNotFoundException, JAXBException {
		PersoSim.parsePersonalization("file not found");
	}
	
	/**
	 * Negative test case: parse personalization from an invalid existing file.
	 * @throws FileNotFoundException 
	 * @throws JAXBException 
	 */
	@Test(expected = JAXBException.class)
	public void parsePersonalizationInvalidFile() throws FileNotFoundException, JAXBException {
		PersoSim.parsePersonalization("src/de/persosim/simulator/PersoSimTest.java");
	}
	
	/**
	 * Positive test case: check for NullPointerException if PersoSim constructor is called with null argument.
	 */
	@Test
	public void testPersoSimConstructorUnknownArgument() {
		persoSim = new PersoSim(new String[]{"unknownCommand"});
		//FIXME SLS this test method must NOT be removed but the assert needs to check something usefull
		assertTrue(true);
	}
	
	/**
	 * Positive test case: test setting of new personalization via user arguments.
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws FileNotFoundException 
	 * @throws JAXBException 
	 */
	@Test
	public void testExecuteUserCommandsCmdLoadPersonalizationValidPersonalization() throws InterruptedException, FileNotFoundException, IllegalArgumentException, JAXBException {
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		Deencapsulation.invoke(persoSim, "startSimulator");
		
		Deencapsulation.invoke(persoSim, "executeUserCommands", new Object[]{new String[]{PersoSim.CMD_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_2}});
		
		String selectApdu = "00A4020C02011C"; 
		String responseSelect = Deencapsulation.invoke(persoSim, "exchangeApdu", selectApdu);
		assertEquals(responseSelect, "9000");
		
		String readBinaryApdu = "00B0000004";
		String responseReadBinary = Deencapsulation.invoke(persoSim, "exchangeApdu", readBinaryApdu);
		
		String expected = HexString.encode(Arrays.copyOf(EF_CS_CONTENT_2, 4)).toUpperCase();
		
		assertEquals(expected, responseReadBinary.substring(0, responseReadBinary.length() - 4).toUpperCase());
	}
	
	/**
	 * Negative test case: test setting of new personalization via user arguments with invalid personalization.
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testExecuteUserCommandsCmdLoadPersonalizationInvalidPersonalization() throws InterruptedException, FileNotFoundException, IllegalArgumentException {
		persoSim = new PersoSim(new String[]{PersoSim.ARG_LOAD_PERSONALIZATION, DUMMY_PERSONALIZATION_FILE_1});
		
		Deencapsulation.invoke(persoSim, "startSimulator");
		
		Deencapsulation.invoke(persoSim, "cmdLoadPersonalization", new Object[]{new String[]{PersoSim.CMD_LOAD_PERSONALIZATION, "non-existing.file"}});
		
		SocketSimulator socketSimPost = (SocketSimulator) Deencapsulation.getField(persoSim, "simulator");
		
		assertNull(socketSimPost);              // SocketSimulator has been stopped and set to null
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
		
		Deencapsulation.invoke(persoSim, "startSimulator");
		
		String hostPre = Deencapsulation.getField(persoSim, "simHost");
		int portPre = Deencapsulation.getField(persoSim, "simPort");
		
		// check that the simulator is actually running on the advertised port
		String selectApdu = "00A4020C02011C"; 
		String responseSelect = Deencapsulation.invoke(persoSim, "exchangeApdu", selectApdu, hostPre, portPre);
		assertEquals(responseSelect, "9000");
		
		int portPostExpected = portPre + 1;
		Deencapsulation.invoke(persoSim, "executeUserCommands", new Object[]{new String[]{PersoSim.CMD_SET_PORT, (new Integer (portPostExpected)).toString()}});
		
		responseSelect = Deencapsulation.invoke(persoSim, "exchangeApdu", selectApdu, hostPre, portPostExpected);
		assertEquals(responseSelect, "9000");
	}

}
