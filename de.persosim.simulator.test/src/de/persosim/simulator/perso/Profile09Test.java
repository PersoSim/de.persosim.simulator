package de.persosim.simulator.perso;

import org.junit.Before;

public class Profile09Test extends PersonalizationTest {

	Personalization perso;
	
	@Before
	public void setUp() throws Exception {
		perso = null;
	}

	@Override
	public Personalization getPerso() {
		
		if (perso == null) {
			perso = new Profile09();
		}
			
		return perso;
	}

}
