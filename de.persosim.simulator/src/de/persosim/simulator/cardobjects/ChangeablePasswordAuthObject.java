package de.persosim.simulator.cardobjects;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.seccondition.OrSecCondition;
import de.persosim.simulator.seccondition.SecCondition;

/**
 * This class extends a {@link PasswordAuthObject} by functionality which makes
 * it possible to change the PIN after creation.
 * 
 * @author slutters
 * 
 */
public class ChangeablePasswordAuthObject extends PasswordAuthObject {
	
	protected int minLengthOfPasswordInBytes;
	protected int maxLengthOfPasswordInBytes;
	
	private SecCondition pinManagementCondition;
	protected SecCondition changePinCondition;
		
	public ChangeablePasswordAuthObject(AuthObjectIdentifier identifier, byte[] password, String passwordName,
			int minLengthOfPasswordInBytes, int maxLengthOfPasswordInBytes, SecCondition pinManagementCondition,
			SecCondition changePinCondition) {
		
		super(identifier, password, passwordName);
		
		if(minLengthOfPasswordInBytes < 0) {throw new IllegalArgumentException("minimum required length for " + passwordName + " in bytes must be >= 0");}
		if(maxLengthOfPasswordInBytes < minLengthOfPasswordInBytes) {throw new IllegalArgumentException("maximum required length for " + passwordName + " in bytes must be >= minimum required length");}
		
		if(password.length < minLengthOfPasswordInBytes) {throw new IllegalArgumentException(passwordName + " must be at least " + minLengthOfPasswordInBytes + " bytes long but is only " + password.length + " bytes long");}
		if(password.length > maxLengthOfPasswordInBytes) {throw new IllegalArgumentException(passwordName + " must be at most " + maxLengthOfPasswordInBytes + " bytes long but is " + password.length + " bytes long");}
		
		this.minLengthOfPasswordInBytes = minLengthOfPasswordInBytes;
		this.maxLengthOfPasswordInBytes = maxLengthOfPasswordInBytes;
		
		this.pinManagementCondition = pinManagementCondition;
		this.changePinCondition = changePinCondition;
	}
	
	public void setPassword(byte[] newPassword) throws AccessDeniedException {
		if(newPassword == null) {throw new IllegalArgumentException("new " + passwordName + " must not be null");}
		if(!getLifeCycleState().equals(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED) && !getLifeCycleState().isPersonalizationPhase()) {throw new IllegalStateException(passwordName + " must be in state operational activated");}
		
		if (!getLifeCycleState().isPersonalizationPhase()){
			if(newPassword.length < minLengthOfPasswordInBytes) {throw new IllegalArgumentException("new " + passwordName + " must be at least " + minLengthOfPasswordInBytes + " bytes long but is only " + newPassword.length + " bytes long");}
			if(newPassword.length > maxLengthOfPasswordInBytes) {throw new IllegalArgumentException("new " + passwordName + " must be at most " + maxLengthOfPasswordInBytes + " bytes long but is " + newPassword.length + " bytes long");}
				
		}
		
		if (securityStatus == null
				|| securityStatus.checkAccessConditions(getLifeCycleState(), new OrSecCondition(changePinCondition,getPinManagementCondition()))) {
			this.password = newPassword;
		} else {
			throw new AccessDeniedException("Access conditions to change " + passwordName + " not met");
		}
	}
	
	@Override
	public void updateLifeCycleState(Iso7816LifeCycleState state) throws AccessDeniedException {
		if (securityStatus == null
				|| !state.isOperational()
				|| securityStatus.checkAccessConditions(getLifeCycleState(), getPinManagementCondition())) {
			super.updateLifeCycleState(state);
		} else {
			throw new AccessDeniedException("Access conditions to change life cycle state not matched");
		}
	}

	public SecCondition getPinManagementCondition() {
		return pinManagementCondition;
	}
	
	public int getMaxLength(){
		return maxLengthOfPasswordInBytes;
	}
	
	public int getMinLength(){
		return minLengthOfPasswordInBytes;
	}
	
}
