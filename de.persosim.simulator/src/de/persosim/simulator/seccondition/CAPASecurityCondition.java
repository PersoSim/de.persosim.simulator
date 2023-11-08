package de.persosim.simulator.seccondition;

import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.secstatus.CAPAMechanism;
import de.persosim.simulator.secstatus.SecMechanism;

/**
 * This condition can be used to check for past executions of CA+PA.
 *
 */
public class CAPASecurityCondition implements SecCondition {

	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		for (SecMechanism mechanism : mechanisms) {
			if (mechanism instanceof CAPAMechanism) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
		HashSet<Class<? extends SecMechanism>> result = new HashSet<>();
		result.add(CAPAMechanism.class);
		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
