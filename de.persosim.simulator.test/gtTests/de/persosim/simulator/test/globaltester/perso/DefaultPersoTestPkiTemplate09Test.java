package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile09;

public class DefaultPersoTestPkiTemplate09Test extends TestPersoTest {

	@Override
	public void resetPersonalization() {
		persoCache = new Profile09();
	}
	
}
