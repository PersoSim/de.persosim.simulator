package de.persosim.simulator.protocols.ta;

import de.persosim.simulator.utils.BitField;

/**
 * This class represents authorization information backed by {@link BitField} storage.
 * 
 * @author slutters
 * 
 */
public class Authorization {
	
	protected BitField authorization;

	public Authorization() {
	}
	
	public Authorization(BitField authorization) {
		this.authorization = authorization;
	}

	public BitField getAuthorization() {
		return authorization;
	}

	/**
	 * @return a bit field representation of this object.
	 */
	public BitField getRepresentation() {
		return authorization;
	}
	
}
