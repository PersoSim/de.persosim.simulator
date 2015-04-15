package de.persosim.simulator.cardobjects;

import de.persosim.simulator.utils.Utils;

/**
 * Identifier using an integer ID for matching authentication objects.
 * @author mboonk
 *
 */
public class AuthObjectIdentifier extends AbstractCardObjectIdentifier {

	int identifier;
	
	public AuthObjectIdentifier() {
	}
	
	public AuthObjectIdentifier(byte[] identifier) {
		this(Utils.getIntFromUnsignedByteArray(identifier));
	}
	
	public AuthObjectIdentifier(int identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public boolean matches(CardObjectIdentifier obj) {
		if (obj instanceof AuthObjectIdentifier){
			return ((AuthObjectIdentifier)obj).getIdentifier() == this.identifier;
		}
		return false;
	}

	public int getIdentifier() {
		return identifier;
	}

}
