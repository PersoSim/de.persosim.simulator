package de.persosim.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.junit.Test;

import com.thoughtworks.xstream.io.StreamException;

import de.persosim.simulator.perso.AbstractPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;

public class CommandParserTest {

	
	private String DUMMY_PERSONALIZATION_FILE = "tmp/dummyPerso.xml";

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
		Personalization perso1 = new AbstractPersonalization() {
		};
		PersonalizationFactory.marshal(perso1, DUMMY_PERSONALIZATION_FILE);
		
		Personalization perso = CommandParser.parsePersonalization(DUMMY_PERSONALIZATION_FILE);
		
		assertNotNull(perso);
	}
	
	/**
	 * Negative test case: parse personalization from a non-existing file.
	 * @throws Exception
	 */
	@Test(expected = FileNotFoundException.class)
	public void testParsePersonalization_FileNotFound() throws Exception {
		CommandParser.parsePersonalization("file not found");
	}
	
	/**
	 * Negative test case: parse personalization from an invalid existing file.
	 * @throws Exception
	 */
	@Test(expected = StreamException.class)
	public void testParsePersonalization_InvalidFile() throws Exception {
		CommandParser.parsePersonalization("src/de/persosim/simulator/PersoSimTest.java");
	}
}
