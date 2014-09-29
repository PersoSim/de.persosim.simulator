package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile08;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate08Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new Profile08();
		}
		
		return persoCache;
	}
	
}
