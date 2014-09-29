package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile01;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate01Test extends TestPersoTest{

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new Profile01();
		}
		
		return persoCache;
	}
	
}
