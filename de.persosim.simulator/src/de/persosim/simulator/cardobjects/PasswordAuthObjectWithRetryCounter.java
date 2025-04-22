package de.persosim.simulator.cardobjects;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.seccondition.OrSecCondition;
import de.persosim.simulator.seccondition.SecCondition;

/**
 * This class represents a {@link PasswordAuthObject} extended to provide a
 * retry counter and a reset retry counter.
 */
public class PasswordAuthObjectWithRetryCounter extends ChangeablePasswordAuthObject {
	protected int retryCounterDefaultValue;
	protected int retryCounterCurrentValue;

	protected SecCondition unblockPinCondition;
	protected SecCondition resetPinCondition;

	public PasswordAuthObjectWithRetryCounter(AuthObjectIdentifier identifier, byte[] password, String passwordName,
			int minLengthOfPasswordInBytes, int maxLengthOfPasswordInBytes, int defaultValueRetryCounter,
			SecCondition pinManagementCondition, SecCondition changePinCondition, SecCondition unblockPinCondition,
			SecCondition resetPinCondition) {

		this(identifier, password, passwordName, minLengthOfPasswordInBytes, maxLengthOfPasswordInBytes,
				defaultValueRetryCounter, pinManagementCondition, changePinCondition, unblockPinCondition,
				resetPinCondition, -1);
	}

	public PasswordAuthObjectWithRetryCounter(AuthObjectIdentifier identifier, byte[] password, String passwordName,
			int minLengthOfPasswordInBytes, int maxLengthOfPasswordInBytes, int defaultValueRetryCounter,
			SecCondition pinManagementCondition, SecCondition changePinCondition, SecCondition unblockPinCondition,
			SecCondition resetPinCondition, int maxResetRetryCounterValue) {

		super(identifier, password, passwordName, minLengthOfPasswordInBytes, maxLengthOfPasswordInBytes,
				pinManagementCondition, changePinCondition, maxResetRetryCounterValue);

		if (defaultValueRetryCounter < 1) {
			throw new IllegalArgumentException("initial value of retry counter must be > 0");
		}

		retryCounterDefaultValue = defaultValueRetryCounter;
		retryCounterCurrentValue = retryCounterDefaultValue;

		this.unblockPinCondition = unblockPinCondition;
		this.resetPinCondition = resetPinCondition;
	}

	@Override
	public void setPassword(byte[] newPassword) throws AccessDeniedException {
		super.setPassword(newPassword);
		resetRetryCounterToDefault();
	}

	public void decrementRetryCounter() {
		if (retryCounterCurrentValue == 0) {
			throw new IllegalStateException(passwordName + " retry counter is not allowed to be decremented below 0");
		} else {
			retryCounterCurrentValue--;
		}
	}

	public void resetRetryCounterToDefault() throws AccessDeniedException {
		if (securityStatus == null
				|| securityStatus.checkAccessConditions(getLifeCycleState(),
						new OrSecCondition(unblockPinCondition, getPinManagementCondition()))
				|| (securityStatus.checkAccessConditions(getLifeCycleState(), resetPinCondition)
						&& retryCounterCurrentValue > 0)
				|| (securityStatus.checkAccessConditions(getLifeCycleState(),
						new OrSecCondition(changePinCondition, getPinManagementCondition()))
						&& retryCounterCurrentValue > 0)) {
			retryCounterCurrentValue = retryCounterDefaultValue;
		} else {
			throw new AccessDeniedException("Access conditions to unblock " + passwordName + " not met");
		}
	}

	public int getRetryCounterDefaultValue() {
		return retryCounterDefaultValue;
	}

	public int getRetryCounterCurrentValue() {
		return retryCounterCurrentValue;
	}
}
