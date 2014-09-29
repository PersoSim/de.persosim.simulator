package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile02;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate02Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new Profile02();
		}
		
		return persoCache;
	}
	
}
