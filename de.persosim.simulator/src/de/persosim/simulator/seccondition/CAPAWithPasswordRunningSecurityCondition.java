package de.persosim.simulator.seccondition;

import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.secstatus.CAPAUsedPasswordMechanism;
import de.persosim.simulator.secstatus.SecMechanism;

/**
 * This condition can be used to check for the password of CA+PA.
 */
public class CAPAWithPasswordRunningSecurityCondition implements SecCondition {
	String neededPassword;

	public CAPAWithPasswordRunningSecurityCondition() {
	}

	public CAPAWithPasswordRunningSecurityCondition(String passwordName) {
		this.neededPassword = passwordName;
	}

	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		for (SecMechanism mechanism : mechanisms) {
			if (mechanism instanceof CAPAUsedPasswordMechanism && ((CAPAUsedPasswordMechanism) mechanism)
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
		result.add(CAPAUsedPasswordMechanism.class);
		return result;
	}
}
