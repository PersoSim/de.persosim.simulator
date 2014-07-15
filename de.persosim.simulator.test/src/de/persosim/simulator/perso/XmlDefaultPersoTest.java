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

		testPerso = new DefaultPersonalization();
	}

}
