package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile03;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate03Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new Profile03();
		}
		
		return persoCache;
	}
	
}
