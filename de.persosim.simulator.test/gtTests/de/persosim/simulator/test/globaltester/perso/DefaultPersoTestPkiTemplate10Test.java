package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.DefaultPersoTestPkiTemplate10;
import de.persosim.simulator.perso.Personalization;

public class DefaultPersoTestPkiTemplate10Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new DefaultPersoTestPkiTemplate10();
		}
		
		return persoCache;
	}
	
}
