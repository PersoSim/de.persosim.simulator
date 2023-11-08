package de.persosim.simulator.secstatus;

import de.persosim.simulator.cardobjects.PasswordAuthObject;

/**
 * This {@link SecMechanism} is used to communicate password used for CA+PA.
 */
public class CAPAUsedPasswordMechanism extends AbstractSecMechanism {

	private PasswordAuthObject usedPassword;

	public CAPAUsedPasswordMechanism(PasswordAuthObject usedPassword) {
		this.usedPassword = usedPassword;
	}

	/**
	 * @return the password, that was used to execute CA+PA
	 */
	public PasswordAuthObject getUsedPassword() {
		return usedPassword;
	}

	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		return true;
	}

}
