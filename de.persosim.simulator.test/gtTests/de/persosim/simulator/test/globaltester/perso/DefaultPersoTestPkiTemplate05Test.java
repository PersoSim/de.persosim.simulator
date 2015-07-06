package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.Profile05;

public class DefaultPersoTestPkiTemplate05Test extends TestPersoTest {

	@Override
	public void resetPersonalization() {
		persoCache = new Profile05();
	}
	
}
