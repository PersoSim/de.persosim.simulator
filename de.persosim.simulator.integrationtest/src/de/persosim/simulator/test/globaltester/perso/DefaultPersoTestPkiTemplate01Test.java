package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile01;

public class DefaultPersoTestPkiTemplate01Test extends TestPersoTest{

	@Override
	public void resetPersonalization() {
		persoCache = new Profile01();
	}
	
}
