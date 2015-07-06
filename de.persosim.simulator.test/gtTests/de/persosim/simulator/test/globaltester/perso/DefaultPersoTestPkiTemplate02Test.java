package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile02;

public class DefaultPersoTestPkiTemplate02Test extends TestPersoTest {

	@Override
	public void resetPersonalization() {
		persoCache = new Profile02();
	}
	
}
