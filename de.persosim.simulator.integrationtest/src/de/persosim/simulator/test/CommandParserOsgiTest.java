package de.persosim.simulator.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.persosim.simulator.CommandParser;
import de.persosim.simulator.CommandParserTest;
import de.persosim.simulator.perso.Personalization;

public class CommandParserOsgiTest extends CommandParserTest {

	/**
	 * Positive test case: parse personalization from a valid perso identifier within an OSGi-Context
	 * @throws Exception
	 */
	@Test
	public void testGetPerso_ValidIdentifier() throws Exception {
		Personalization perso = CommandParser.getPerso("01");
		assertNotNull(perso);
	}
	
	/**
	 * Negative test case: parse personalization with non-existant perso identifier (too large number
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetPerso_IdentifierTooLarge() throws Exception {
		CommandParser.getPerso("15");
	}
}
