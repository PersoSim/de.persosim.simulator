package de.persosim.simulator.test.globaltester.perso;

import de.persosim.simulator.perso.DefaultPersoTestPkiTemplate01;
import de.persosim.simulator.perso.Personalization;

//FIXME SLS following comments apply to all new Persos (without having looked at them in detail)
//FIXME SLS name of test class should be similar to that of the class under test
public class TestPerso001Test extends TestPersoTest{

	@Override
	public Personalization getPersonalization() {
		if(persoCache == null) {
			persoCache = new DefaultPersoTestPkiTemplate01();
		}
		
		return persoCache;
	}
	
}
