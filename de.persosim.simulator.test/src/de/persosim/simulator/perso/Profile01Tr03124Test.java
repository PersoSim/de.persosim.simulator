package de.persosim.simulator.perso;

import java.io.File;

import org.junit.Before;

import de.persosim.simulator.exception.AccessDeniedException;

public class Profile01Tr03124Test extends ArtifactPersonalizationTest {

	Personalization perso;
	
	@Before
	public void setUp() throws Exception {
		perso = null;
	}

	@Override
	public Personalization getPerso() throws AccessDeniedException {
		
		if (perso == null) {
			perso = new Profile01Tr03124();
		}
			
		return perso;
	}

	@Override
	protected String getArtifactFolder() {
		return super.getArtifactFolder() + "TR-03124" + File.separator;
	}
	
}
