package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.DefaultPersoTestPkiTemplate09;
import de.persosim.simulator.perso.Personalization;

public class TestPerso009Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new DefaultPersoTestPkiTemplate09();
		}
		
		return persoCache;
	}
	
}
