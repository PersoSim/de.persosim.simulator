package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.DefaultPersoTestPkiTemplate04;
import de.persosim.simulator.perso.Personalization;

public class TestPerso004Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new DefaultPersoTestPkiTemplate04();
		}
		
		return persoCache;
	}
	
}
