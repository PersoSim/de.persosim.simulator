package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile09;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate09Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new Profile09();
		}
		
		return persoCache;
	}
	
}
