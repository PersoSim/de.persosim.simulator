package de.persosim.simulator.seccondition;

import java.util.Collection;

import de.persosim.simulator.secstatus.CAPAMechanism;
import de.persosim.simulator.secstatus.SecMechanism;

/**
 * This condition can be used to check for past executions of CAPA. The used
 * password can be checked as well as the success.
 */
public class CAPAWithPasswordSecurityCondition extends CAPASecurityCondition {
	String neededPassword;

	public CAPAWithPasswordSecurityCondition() {
	}

	public CAPAWithPasswordSecurityCondition(String passwordName) {
		this.neededPassword = passwordName;
	}

	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		if (super.check(mechanisms)) {
			for (SecMechanism mechanism : mechanisms) {
				if (mechanism instanceof CAPAMechanism
						&& ((CAPAMechanism) mechanism).getUsedPassword().getPasswordName().equals(neededPassword)) {
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
