package de.persosim.simulator.perso;

import org.junit.Before;

import de.persosim.simulator.exception.AccessDeniedException;

public class Profile03Test extends ArtifactPersonalizationTest {

	Personalization perso;
	
	@Before
	public void setUp() throws Exception {
		perso = null;
	}

	@Override
	public Personalization getPerso() throws AccessDeniedException {
		
		if (perso == null) {
			perso = new Profile03();
		}
			
		return perso;
	}

}
