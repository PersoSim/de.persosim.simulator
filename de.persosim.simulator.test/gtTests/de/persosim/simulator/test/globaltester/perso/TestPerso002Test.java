package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.DefaultPersoTestPkiTemplate02;
import de.persosim.simulator.perso.Personalization;

public class TestPerso002Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new DefaultPersoTestPkiTemplate02();
		}
		
		return persoCache;
	}
	
}
