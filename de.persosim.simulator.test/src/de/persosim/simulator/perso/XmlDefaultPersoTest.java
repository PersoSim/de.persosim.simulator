package de.persosim.simulator.perso;

import org.junit.Before;

/**
 * Test the same tests as {@link XmlPersonalizationTest} but with the contents of
 * {@link DefaultPersonalization}
 * 
 * @author amay
 * 
 */
public class XmlDefaultPersoTest extends XmlPersonalizationTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();

		testPerso = new DefaultPersonalization();
	}

}
