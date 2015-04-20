package de.persosim.simulator.perso;

import org.junit.Before;

public class Profile05Test extends PersonalizationTest {

	Personalization perso;
	
	@Before
	public void setUp() throws Exception {
		perso = null;
	}

	@Override
	public Personalization getPerso() {
		
		if (perso == null) {
			perso = new Profile05();
		}
			
		return perso;
	}

}
