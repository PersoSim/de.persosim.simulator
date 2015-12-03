package de.persosim.simulator.cardobjects;

import de.persosim.simulator.utils.Utils;

/**
 * Identifier using an integer ID for matching authentication objects.
 * @author mboonk
 *
 */
public class AuthObjectIdentifier extends AbstractCardObjectIdentifier {

	int identifier;
	
	public AuthObjectIdentifier(byte[] identifier) {
		this(Utils.getIntFromUnsignedByteArray(identifier));
	}
	
	public AuthObjectIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public int getIdentifier() {
		return identifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + identifier;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthObjectIdentifier other = (AuthObjectIdentifier) obj;
		if (identifier != other.identifier)
			return false;
		return true;
	}

}
