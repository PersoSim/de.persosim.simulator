package de.persosim.simulator.cardobjects;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import de.persosim.simulator.utils.HexString;

/**
 * This class represents a {@link PasswordAuthObject} extended to provide a
 * reset retry counter.
 */
public class PasswordAuthObjectWithResetRetryCounter extends AbstractCardObject implements AuthObject {

	AuthObjectIdentifier identifier;
	byte[] password;
	protected String passwordName;

	protected int resetRetryCounterDefaultValue;
	protected int resetRetryCounterCurrentValue;

	public PasswordAuthObjectWithResetRetryCounter() {
	}

	public PasswordAuthObjectWithResetRetryCounter(AuthObjectIdentifier identifier, byte[] password,
			String passwordName, int defaultValueResetRetryCounter) {

		if (identifier == null) {
			throw new IllegalArgumentException("identifier must not be null");
		}
		if (password == null) {
			throw new IllegalArgumentException("password must not be null");
		}
		if (passwordName == null) {
			throw new IllegalArgumentException("password name must not be null");
		}

		this.identifier = identifier;
		this.password = password;
		this.passwordName = passwordName;
		resetRetryCounterDefaultValue = defaultValueResetRetryCounter;
	}

	public byte[] getPassword() {
		return Arrays.copyOf(password, password.length);
	}

	public String getPasswordName() {
		return passwordName;
	}

	public int getPasswordIdentifier() {
		return identifier.getIdentifier();
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = super.getAllIdentifiers();
		result.add(identifier);
		return result;
	}

	public void decrementRetryCounter() {
		if (resetRetryCounterCurrentValue == 0) {
			throw new IllegalStateException(
					passwordName + " reset retry counter is not allowed to be decremented below 0");
		} else {
			resetRetryCounterCurrentValue--;
		}
	}

	public int getRetryCounterCurrentValue() {
		return resetRetryCounterCurrentValue;
	}

	public int getRetryCounterDefaultValue() {
		return resetRetryCounterDefaultValue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("password ").append(passwordName).append(" is ").append(HexString.encode(password)).append(" (")
				.append((new String(password, StandardCharsets.UTF_8)).toString()).append(")");
		return sb.toString();
	}

}
