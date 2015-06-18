package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Profile10;

public class DefaultPersoTestPkiTemplate10Test extends TestPersoTest {

	@Override
	public void resetPersonalization() throws AccessDeniedException {
		persoCache = new Profile10();
	}
	
}
