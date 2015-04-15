package de.persosim.simulator.cardobjects;



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
	
	public PasswordAuthObjectWithRetryCounter(){
	}
	
	public PasswordAuthObjectWithRetryCounter(AuthObjectIdentifier identifier,
			byte [] password, String passwordName, int minLengthOfPasswordInBytes, int maxLengthOfPasswordInBytes,
			int defaultValueRetryCounter){
		
		super(identifier, password, passwordName, minLengthOfPasswordInBytes, maxLengthOfPasswordInBytes);
		
		if(defaultValueRetryCounter < 1) {throw new IllegalArgumentException("initial value of retry counter must be > 0");}
		
		retryCounterDefaultValue = defaultValueRetryCounter;
		retryCounterCurrentValue = retryCounterDefaultValue;
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
