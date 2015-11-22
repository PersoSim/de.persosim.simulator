package de.persosim.simulator.seccondition;

import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.secstatus.PaceUsedPasswordMechanism;
import de.persosim.simulator.secstatus.SecMechanism;

/**
 * This condition can be used to check for the password of PACE.
 * 
 * @author cstroh
 * 
 */
public class PaceWithPasswordRunningSecurityCondition implements SecCondition {
	String neededPassword;

	public PaceWithPasswordRunningSecurityCondition() {
	}
	
	public PaceWithPasswordRunningSecurityCondition(String passwordName) {
		this.neededPassword = passwordName;
	}

	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		for (SecMechanism mechanism : mechanisms) {
			if (mechanism instanceof PaceUsedPasswordMechanism
					&& ((PaceUsedPasswordMechanism) mechanism)
							.getUsedPassword().getPasswordName().equals(neededPassword)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + neededPassword + "]";
	}

	@Override
	public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
		HashSet<Class<? extends SecMechanism>> result = new HashSet<>();
		result.add(PaceUsedPasswordMechanism.class);
		return result;
	}
}
