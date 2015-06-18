package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Profile06;

public class DefaultPersoTestPkiTemplate06Test extends TestPersoTest {

	@Override
	public void resetPersonalization() throws AccessDeniedException {
		persoCache = new Profile06();
	}

}
