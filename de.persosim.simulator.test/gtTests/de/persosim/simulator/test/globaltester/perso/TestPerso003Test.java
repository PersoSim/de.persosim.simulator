package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.DefaultPersoTestPkiTemplate03;
import de.persosim.simulator.perso.Personalization;

public class TestPerso003Test extends TestPersoTest {

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new DefaultPersoTestPkiTemplate03();
		}
		
		return persoCache;
	}
	
}
