package de.persosim.simulator.cardobjects;

import de.persosim.simulator.exception.LifeCycleChangeException;
import de.persosim.simulator.seccondition.SecCondition;

/**
 * This class extends a {@link PasswordAuthObject} by functionality which makes
 * it possible to change the PIN after creation.
 * XXX why not included this functionality in {@link PasswordAuthObject} (where it can be restricted by access rights)?
 * 
 * @author slutters
 * 
 */
public class ChangeablePasswordAuthObject extends PasswordAuthObject {
	
	protected int minLengthOfPasswordInBytes;
	protected int maxLengthOfPasswordInBytes;
	
	private SecCondition pinManagementCondition;
		
	public ChangeablePasswordAuthObject(AuthObjectIdentifier identifier,
			byte [] password, String passwordName, int minLengthOfPasswordInBytes, int maxLengthOfPasswordInBytes, SecCondition pinManagementCondition){
		
		super(identifier, password, passwordName);
		
		if(minLengthOfPasswordInBytes < 0) {throw new IllegalArgumentException("minimum required length for " + passwordName + " in bytes must be >= 0");}
		if(maxLengthOfPasswordInBytes < minLengthOfPasswordInBytes) {throw new IllegalArgumentException("maximum required length for " + passwordName + " in bytes must be >= minimum required length");}
		
		if(password.length < minLengthOfPasswordInBytes) {throw new IllegalArgumentException(passwordName + " must be at least " + minLengthOfPasswordInBytes + " bytes long but is only " + password.length + " bytes long");}
		if(password.length > maxLengthOfPasswordInBytes) {throw new IllegalArgumentException(passwordName + " must be at most " + maxLengthOfPasswordInBytes + " bytes long but is " + password.length + " bytes long");}
		
		this.minLengthOfPasswordInBytes = minLengthOfPasswordInBytes;
		this.maxLengthOfPasswordInBytes = maxLengthOfPasswordInBytes;
		
		this.pinManagementCondition = pinManagementCondition;
	}
	
	public void setPassword(byte[] newPassword) {
		if(!getLifeCycleState().equals(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED)) {throw new IllegalStateException(passwordName + " must be in state operational activated");}
		if(newPassword == null) {throw new IllegalArgumentException("new " + passwordName + " must not be null");}
		if(newPassword.length < minLengthOfPasswordInBytes) {throw new IllegalArgumentException("new " + passwordName + " must be at least " + minLengthOfPasswordInBytes + " bytes long but is only " + newPassword.length + " bytes long");}
		if(newPassword.length > maxLengthOfPasswordInBytes) {throw new IllegalArgumentException("new " + passwordName + " must be at most " + maxLengthOfPasswordInBytes + " bytes long but is " + newPassword.length + " bytes long");}
		
		this.password = newPassword;
	}
	
	@Override
	public void updateLifeCycleState(Iso7816LifeCycleState state) throws LifeCycleChangeException {
		if (securityStatus == null
				|| (!state.equals(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED)
						&& !state.equals(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED))
				|| securityStatus.checkAccessConditions(getLifeCycleState(), getPinManagementCondition())) {
			super.updateLifeCycleState(state);
		} else {
			throw new LifeCycleChangeException("Access conditions to change life cycle state not matched", getLifeCycleState(), state);
		}
	}

	public SecCondition getPinManagementCondition() {
		return pinManagementCondition;
	}
	
}
