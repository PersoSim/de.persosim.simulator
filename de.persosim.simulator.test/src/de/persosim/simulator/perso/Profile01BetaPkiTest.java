package de.persosim.simulator.perso;

import java.io.File;

import org.junit.Before;

import de.persosim.simulator.exception.AccessDeniedException;

public class Profile01BetaPkiTest extends ArtifactPersonalizationTest {

	Personalization perso;
	
	@Before
	public void setUp() throws Exception {
		perso = null;
	}

	@Override
	public Personalization getPerso() throws AccessDeniedException {
		
		if (perso == null) {
			perso = new Profile01BetaPki();
		}
			
		return perso;
	}

	@Override
	protected String getArtifactFolder() {
		return super.getArtifactFolder() + "Beta-PKI" + File.separator;
	}
	
}
