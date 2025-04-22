package de.persosim.simulator.protocols.pin;

import java.lang.reflect.Field;

import de.persosim.simulator.cardobjects.AbstractCardObject;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.seccondition.SecCondition;

public class TestAuthObject extends PasswordAuthObjectWithRetryCounter {

	public TestAuthObject(AuthObjectIdentifier identifier, byte[] password, String passwordName,
			int minLengthOfPasswordInBytes, int maxLengthOfPasswordInBytes, int defaultValueRetryCounter,
			SecCondition pinManagementCondition, SecCondition changePinCondition, SecCondition unblockPinCondition,
			SecCondition resetPinCondition) {
		super(identifier, password, passwordName, minLengthOfPasswordInBytes, maxLengthOfPasswordInBytes,
				defaultValueRetryCounter, pinManagementCondition, changePinCondition, unblockPinCondition,
				resetPinCondition);
	}

	public TestAuthObject(AuthObjectIdentifier identifier, byte[] password, String passwordName,
			int minLengthOfPasswordInBytes, int maxLengthOfPasswordInBytes, int defaultValueRetryCounter,
			SecCondition pinManagementCondition, SecCondition changePinCondition, SecCondition unblockPinCondition,
			SecCondition resetPinCondition, int maxResetRetryCounterValue) {
		super(identifier, password, passwordName, minLengthOfPasswordInBytes, maxLengthOfPasswordInBytes,
				defaultValueRetryCounter, pinManagementCondition, changePinCondition, unblockPinCondition,
				resetPinCondition, maxResetRetryCounterValue);
	}

	public void setRetryCounterCurrentValue(int i) {
		retryCounterCurrentValue = i;
	}

	public void setResetRetryCounterCurrentValue(int i) {
		resetRetryCounterCurrentValue = i;
	}

	public void setLifeCycleState(Iso7816LifeCycleState newState)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Field f = AbstractCardObject.class.getDeclaredField("lifeCycleState");
		f.setAccessible(true);
		f.set(this, newState);
	}

}
