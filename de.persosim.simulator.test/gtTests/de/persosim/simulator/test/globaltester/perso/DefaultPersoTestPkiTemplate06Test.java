package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile06;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate06Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new Profile06();
		}
		
		return persoCache;
	}
	
}
