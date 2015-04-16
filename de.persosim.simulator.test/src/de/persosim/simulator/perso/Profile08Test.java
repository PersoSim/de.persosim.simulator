package de.persosim.simulator.perso;

import org.junit.Before;

public class Profile08Test extends XmlPersonalizationTest {


	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		defaultnpa = new DefectListNpaUnmarshallerCallback();

		testPerso = new Profile08();
	}

}
