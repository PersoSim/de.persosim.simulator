package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile03;

public class DefaultPersoTestPkiTemplate03Test extends TestPersoTest {

	@Override
	public void resetPersonalization() {
		persoCache = new Profile03();
	}
	
}
