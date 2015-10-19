package de.persosim.simulator.seccondition;

import java.util.Collection;

import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.secstatus.PaceMechanism;
import de.persosim.simulator.secstatus.SecMechanism;

/**
 * This condition can be used to check for past executions of PACE. The used
 * password can be checked as well as the success.
 * 
 * @author mboonk
 * 
 */
public class PaceWithPasswordSecurityCondition extends PaceSecurityCondition {
	PasswordAuthObject neededPassword;

	public PaceWithPasswordSecurityCondition() {
	}
	
	public PaceWithPasswordSecurityCondition(PasswordAuthObject neededPassword) {
		this.neededPassword = neededPassword;
	}

	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		if (super.check(mechanisms)) {
			for (SecMechanism mechanism : mechanisms) {
				if (mechanism instanceof PaceMechanism
						&& ((PaceMechanism) mechanism)
								.getUsedPassword().equals(neededPassword)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + neededPassword + "]";
	}
}
