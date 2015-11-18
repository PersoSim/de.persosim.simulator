package de.persosim.simulator.cardobjects;

import de.persosim.simulator.seccondition.OrSecCondition;
import de.persosim.simulator.seccondition.SecCondition;

/**
 * This class represents a {@link PasswordAuthObject} extended to provide a retry counter.
 * XXX SLS missing initialization of life cycle state
 * 
 * @author slutters
 *
 */
public class PasswordAuthObjectWithRetryCounter extends ChangeablePasswordAuthObject {
	protected int retryCounterDefaultValue;
	protected int retryCounterCurrentValue;
	
	private SecCondition changePinCondition;
		
	public PasswordAuthObjectWithRetryCounter(AuthObjectIdentifier identifier, byte[] password, String passwordName,
			int minLengthOfPasswordInBytes, int maxLengthOfPasswordInBytes, int defaultValueRetryCounter,
			SecCondition pinManagementCondition, SecCondition changePinCondition) {

		super(identifier, password, passwordName, minLengthOfPasswordInBytes, maxLengthOfPasswordInBytes, pinManagementCondition);
		
		if(defaultValueRetryCounter < 1) {throw new IllegalArgumentException("initial value of retry counter must be > 0");}
		
		retryCounterDefaultValue = defaultValueRetryCounter;
		retryCounterCurrentValue = retryCounterDefaultValue;
		
		this.changePinCondition = changePinCondition;
	}
	
	public void setPassword(byte[] newPassword) {
		if(!getLifeCycleState().equals(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED)) {throw new IllegalStateException(passwordName + " must be in state operational activated");}
		if(newPassword == null) {throw new IllegalArgumentException("new " + passwordName + " must not be null");}
		if(newPassword.length < minLengthOfPasswordInBytes) {throw new IllegalArgumentException("new " + passwordName + " must be at least " + minLengthOfPasswordInBytes + " bytes long but is only " + newPassword.length + " bytes long");}
		if(newPassword.length > maxLengthOfPasswordInBytes) {throw new IllegalArgumentException("new " + passwordName + " must be at most " + maxLengthOfPasswordInBytes + " bytes long but is " + newPassword.length + " bytes long");}
		
		if (securityStatus == null
				|| securityStatus.checkAccessConditions(getLifeCycleState(), new OrSecCondition(changePinCondition,getPinManagementCondition()))) {
			super.setPassword(newPassword);
		} else {
			throw new IllegalStateException("Access conditions to change " + passwordName + " not met");
		}
	}

	public void decrementRetryCounter() {
		if(retryCounterCurrentValue == 0) {
			throw new IllegalStateException(passwordName + " retry counter is not allowed to be decremented below 0");
		} else{
			retryCounterCurrentValue--;
		}
	}
	
	public void resetRetryCounterToDefault() {
		retryCounterCurrentValue = retryCounterDefaultValue;
	}

	public int getRetryCounterCurrentValue() {
		return retryCounterCurrentValue;
	}

	public int getRetryCounterDefaultValue() {
		return retryCounterDefaultValue;
	}
	
}
