package de.persosim.simulator.seccondition;

import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.secstatus.PaceMechanism;
import de.persosim.simulator.secstatus.SecMechanism;



/**
 * This condition can be used to check for past executions of PACE.
 * @author mboonk
 *
 */
public class PaceSecurityCondition implements SecCondition {
	
	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		for (SecMechanism mechanism : mechanisms){
			if (mechanism instanceof PaceMechanism){
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
		HashSet<Class<? extends SecMechanism>> result = new HashSet<>();
		result.add(PaceMechanism.class);
		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
