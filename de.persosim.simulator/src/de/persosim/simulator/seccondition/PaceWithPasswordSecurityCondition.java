package de.persosim.simulator.seccondition;

import java.util.Collection;

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
	String neededPassword;

	public PaceWithPasswordSecurityCondition() {
	}
	
	public PaceWithPasswordSecurityCondition(String passwordName) {
		this.neededPassword = passwordName;
	}

	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		if (super.check(mechanisms)) {
			for (SecMechanism mechanism : mechanisms) {
				if (mechanism instanceof PaceMechanism
						&& ((PaceMechanism) mechanism)
								.getUsedPassword().getPasswordName().equals(neededPassword)) {
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
