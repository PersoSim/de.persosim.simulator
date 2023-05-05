package de.persosim.simulator.seccondition;

import java.util.Collection;
import java.util.Collections;

import de.persosim.simulator.secstatus.SecMechanism;

/**
 * This class implements a {@link SecCondition} always returning false.
 * 
 * @author mboonk
 * 
 */
public final class NeverSecCondition implements SecCondition {
	
	public NeverSecCondition() {
	}
	
	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		return false;
	}
	
	@Override
	public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
		return Collections.emptySet();
	}

}
