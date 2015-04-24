package de.persosim.simulator.perso;

import org.junit.Before;

public class DefaultPersoGtTest extends PersonalizationTest {

	Personalization perso;
	
	@Before
	public void setUp() throws Exception {
		perso = null;
	}

	@Override
	public Personalization getPerso() {
		
		if (perso == null) {
			perso = new DefaultPersoGt();
		}
			
		return perso;
	}

}
