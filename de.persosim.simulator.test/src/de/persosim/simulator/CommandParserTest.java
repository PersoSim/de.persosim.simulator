package de.persosim.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;
import de.persosim.simulator.perso.PersonalizationImpl;

public class CommandParserTest {

	
	
	private String DUMMY_PERSONALIZATION_FOLDER = new File("").getAbsolutePath() +  "/tmp";
	private String DUMMY_PERSONALIZATION_FILE = DUMMY_PERSONALIZATION_FOLDER + "/dummyPerso"+CommandParser.PERSO_FILE_POSTFIX;
	
	@Before
	public void setUp(){
		File folder = new File(DUMMY_PERSONALIZATION_FOLDER);
		if (!folder.exists()) {
			folder.mkdirs();			
		}
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
	 * Negative test case: parse personalization from a perso identifier without OSGi-Context
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetPerso_ValidIdentifier() throws Exception {
		CommandParser.getPerso("01");
	}
	
	/**
	 * Positive test case: parse personalization from a valid file.
	 * @throws Exception
	 */
	@Test
	public void testGetPerso_ValidFile() throws Exception {
		Personalization perso1 = new PersonalizationImpl();
		PersonalizationFactory.marshal(perso1, DUMMY_PERSONALIZATION_FILE);
		
		Personalization perso = CommandParser.getPerso(DUMMY_PERSONALIZATION_FILE);
		
		assertNotNull(perso);
	}
	
	/**
	 * Negative test case: parse personalization from a non-existing file.
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetPerso_FileNotFound() throws Exception {
		CommandParser.getPerso("file not found");
	}
	
	/**
	 * Negative test case: parse personalization from an invalid existing file.
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetPerso_InvalidFile() throws Exception {
		CommandParser.getPerso("src/de/persosim/simulator/PersoSimTest.java");
	}
}
