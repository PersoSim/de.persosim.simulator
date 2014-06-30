package de.persosim.simulator.perso;

import org.junit.Before;

/**
 * Test the same tests as XmlPersonalisationTest but with the contents of
 * {@link DefaultPersonalization}
 * 
 * @author amay
 * 
 */
public class XmlDefaultPersoTest extends XmlPersonalisationTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// fill testPerso from defaultPerso
		DefaultPersonalization defaultPerso = new DefaultPersonalization();

		testPerso = new XmlPersonalisation();
		testPerso.setProtocolList(defaultPerso.getProtocolList());
		testPerso.setMf(defaultPerso.getObjectTree());
	}

}
