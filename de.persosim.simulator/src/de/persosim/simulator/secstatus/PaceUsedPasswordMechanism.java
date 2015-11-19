package de.persosim.simulator.secstatus;

import de.persosim.simulator.cardobjects.PasswordAuthObject;

/**
 * This {@link SecMechanism} is used to communicate password used for PACE.
 * 
 * @author cstroh
 * 
 */
public class PaceUsedPasswordMechanism implements SecMechanism {

	private PasswordAuthObject usedPassword;

	public PaceUsedPasswordMechanism(PasswordAuthObject usedPassword){
		this.usedPassword = usedPassword;
	}
	
	/**
	 * @return the password, that was used to execute PACE
	 */
	public PasswordAuthObject getUsedPassword() {
		return usedPassword;
	}

	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		return true;
	}

}
