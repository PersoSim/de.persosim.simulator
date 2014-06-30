package de.persosim.simulator.secstatus;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.cardobjects.PasswordAuthObject;

/**
 * This condition can be used to check for past executions of PACE. The used
 * password can be checked as well as the success.
 * 
 * @author mboonk
 * 
 */
@XmlRootElement
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
}
