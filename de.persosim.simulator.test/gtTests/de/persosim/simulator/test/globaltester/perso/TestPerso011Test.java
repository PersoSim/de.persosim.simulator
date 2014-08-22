package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.DefaultPersoTestPkiTemplate11;
import de.persosim.simulator.perso.Personalization;

public class TestPerso011Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new DefaultPersoTestPkiTemplate11();
		}
		
		return persoCache;
	}
	
}
