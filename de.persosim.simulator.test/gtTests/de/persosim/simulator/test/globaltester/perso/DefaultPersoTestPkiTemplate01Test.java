package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.DefaultPersoTestPkiTemplate01;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate01Test extends TestPersoTest{

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new DefaultPersoTestPkiTemplate01();
		}
		
		return persoCache;
	}
	
}
