package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile10;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate10Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new Profile10();
		}
		
		return persoCache;
	}
	
}
