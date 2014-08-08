package de.persosim.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import mockit.Deencapsulation;

import org.junit.After;
import org.junit.Test;

import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.test.PersoSimTestCase;

public class PersoSimTest extends PersoSimTestCase {
	
	PersoSim persoSim;
	
	@After
	public void tearDown() {
		if(persoSim != null) {
			Deencapsulation.invoke(persoSim, "stopSimulator");
		}
	}
	
	/**
	 * Positive test case: test implicit setting of a default personalization if no other personalization is explicitly set.
	 */
	@Test
	public void testImplicitSettingOfDefaultPersonalization() {
		persoSim = new PersoSim(new String[0]);
		
		Personalization persoInit = (Personalization) Deencapsulation.getField(persoSim, "currentPersonalization");
		
		assertNull(persoInit);
		
		Deencapsulation.invoke(persoSim, "startSimulator");
		
		Personalization persoStart = (Personalization) Deencapsulation.getField(persoSim, "currentPersonalization");
		
		assertNotNull(persoStart);
	}
	
	/**
	 * Negative test case: check for NullPointerException if PersoSim constructor is called with null argument.
	 */
	@Test(expected = NullPointerException.class)
	public void testPersoSimConstructorNullArgument() {
		persoSim = new PersoSim(null);
	}
	
	/**
	 * Positive test case: test start of socket simulator.
	 * @throws InterruptedException 
	 */
	@Test
	public void testStartSimulator() throws InterruptedException {
		persoSim = new PersoSim(new String[0]);
		
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
		persoSim = new PersoSim(new String[0]);
		
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
		String[] result = PersoSim.parseArgs("");
		
		assertEquals(result.length, 0);
	}
	
	/**
	 * Negative test case: parse arguments from null.
	 */
	@Test(expected = NullPointerException.class)
	public void parseArgsNull() {
		PersoSim.parseArgs(null);
	}
	
	/**
	 * Positive test case: parse arguments from a String containing spaces only at start and end.
	 */
	@Test
	public void parseArgsUntrimmedCoherentString() {
		String arg = "string";
		String[] result = PersoSim.parseArgs(" " + arg + "  ");
		
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
		String[] result = PersoSim.parseArgs(" " + arg1 + "  " + arg2);
		
		assertEquals(result.length, 2);
		assertEquals(result[0], arg1);
		assertEquals(result[1], arg2);
	}
	
	/**
	 * Positive test case: parse personalization from a valid file.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void parsePersonalizationValidFile() throws FileNotFoundException {
		Personalization perso = PersoSim.parsePersonalization("tmp/perso-jaxb.xml");
		
		assertNotNull(perso);
	}
	
	/**
	 * Negative test case: parse personalization from a non-existing file.
	 * @throws FileNotFoundException 
	 */
	@Test(expected = FileNotFoundException.class)
	public void parsePersonalizationFileNotFound() throws FileNotFoundException {
		PersoSim.parsePersonalization("file not found");
	}
	
	/**
	 * Negative test case: parse personalization from an invalid existing file.
	 * @throws FileNotFoundException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void parsePersonalizationInvalidFile() throws FileNotFoundException {
		PersoSim.parsePersonalization("src/de/persosim/simulator/PersoSimTest.java");
	}
	
	/**
	 * Positive test case: check for NullPointerException if PersoSim constructor is called with null argument.
	 */
	@Test
	public void testPersoSimConstructorUnknownArgument() {
		persoSim = new PersoSim(new String[]{"unknownCommand"});
		assertTrue(true);
	}
	
	/**
	 * Positive test case: test setting of new personalization via command line or user arguments.
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testCmdLoadPersonalizationValidPersonalization() throws InterruptedException, FileNotFoundException, IllegalArgumentException {
		persoSim = new PersoSim(new String[0]);
		
		Deencapsulation.invoke(persoSim, "startSimulator");
		
		SocketSimulator socketSimPre = (SocketSimulator) Deencapsulation.getField(persoSim, "simulator");
		
		Personalization persoPre = PersoSim.parsePersonalization("tmp/perso-jaxb.xml");
		Deencapsulation.invoke(persoSim, "cmdLoadPersonalization", new Object[]{new String[]{PersoSim.CMD_LOAD_PERSONALIZATION, "tmp/perso-jaxb.xml"}});
		
		SocketSimulator socketSimPost = (SocketSimulator) Deencapsulation.getField(persoSim, "simulator");
		
		assertNotNull(socketSimPost);              // SocketSimulator has been stopped and recreated
		assertTrue(socketSimPost != socketSimPre); // SocketSimulator has been stopped and recreated
		assertTrue(socketSimPost.isRunning());     // new SocketSimulator is actually running
		
		Personalization persoPost = PersoSim.parsePersonalization("tmp/perso-jaxb.xml");
		
		assertNotNull(persoPost);
		assertTrue(persoPost != persoPre); // Personalization has changed
	}
	
	/**
	 * Negative test case: test setting of new personalization via command line or user arguments with invalid personalization.
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testCmdLoadPersonalizationInvalidPersonalization() throws InterruptedException, FileNotFoundException, IllegalArgumentException {
		persoSim = new PersoSim(new String[0]);
		
		Deencapsulation.invoke(persoSim, "startSimulator");
		
		Deencapsulation.invoke(persoSim, "cmdLoadPersonalization", new Object[]{new String[]{PersoSim.CMD_LOAD_PERSONALIZATION, "non-existing.file"}});
		
		SocketSimulator socketSimPost = (SocketSimulator) Deencapsulation.getField(persoSim, "simulator");
		
		assertNull(socketSimPost);              // SocketSimulator has been stopped and set to null
	}
	
	/**
	 * Positive test case: test setting of new port via command line or user arguments.
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testCmdSetPortNo() throws InterruptedException, FileNotFoundException, IllegalArgumentException {
		persoSim = new PersoSim(new String[0]);
		
		Deencapsulation.invoke(persoSim, "startSimulator");
		
		SocketSimulator socketSimPre = (SocketSimulator) Deencapsulation.getField(persoSim, "simulator");
		
		int portPre = Deencapsulation.getField(persoSim, "simPort");
		int portPostExpected = portPre + 1;
		Deencapsulation.invoke(persoSim, "cmdSetPortNo", new Object[]{new String[]{PersoSim.CMD_SET_PORT, (new Integer (portPostExpected)).toString()}});
		
		SocketSimulator socketSimPost = (SocketSimulator) Deencapsulation.getField(persoSim, "simulator");
		
		assertNotNull(socketSimPost);              // SocketSimulator has been stopped and recreated
		assertTrue(socketSimPost != socketSimPre); // SocketSimulator has been stopped and recreated
		assertTrue(socketSimPost.isRunning());     // new SocketSimulator is actually running
		
		int portPost = Deencapsulation.getField(persoSim, "simPort");
		
		assertEquals(portPostExpected, portPost); // Port has changed
	}
	
	/**
	 * Positive test case: test setting of new host via command line or user arguments.
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testCmdSetHost() throws InterruptedException, FileNotFoundException, IllegalArgumentException {
		persoSim = new PersoSim(new String[0]);
		
		Deencapsulation.invoke(persoSim, "startSimulator");
		
		SocketSimulator socketSimPre = (SocketSimulator) Deencapsulation.getField(persoSim, "simulator");
		
		String hostPre = Deencapsulation.getField(persoSim, "simHost");
		String hostPostExpected = new String(hostPre);
		Deencapsulation.invoke(persoSim, "cmdSetHostName", new Object[]{new String[]{PersoSim.CMD_SET_HOST, (hostPostExpected)}});
		
		SocketSimulator socketSimPost = (SocketSimulator) Deencapsulation.getField(persoSim, "simulator");
		
		assertNotNull(socketSimPost);              // SocketSimulator has been stopped and recreated
		assertTrue(socketSimPost != socketSimPre); // SocketSimulator has been stopped and recreated
		assertTrue(socketSimPost.isRunning());     // new SocketSimulator is actually running
		
		String hostPost = Deencapsulation.getField(persoSim, "simHost");
		
		assertTrue(hostPost != hostPre); // Host has changed
	}

}
