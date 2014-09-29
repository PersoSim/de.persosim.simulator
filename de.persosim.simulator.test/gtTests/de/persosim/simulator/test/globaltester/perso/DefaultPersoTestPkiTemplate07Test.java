package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile07;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate07Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new Profile07();
		}
		
		return persoCache;
	}
	
}
