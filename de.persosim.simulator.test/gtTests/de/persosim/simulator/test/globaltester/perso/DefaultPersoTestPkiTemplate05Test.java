package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile05;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate05Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new Profile05();
		}
		
		return persoCache;
	}
	
}
