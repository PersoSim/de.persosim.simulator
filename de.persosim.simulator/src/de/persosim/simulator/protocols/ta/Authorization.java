package de.persosim.simulator.protocols.ta;

import de.persosim.simulator.utils.BitField;

/**
 * @author slutters
 * 
 */
public abstract class Authorization {
	
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
	 * @return the role and relative authorization as a bit field.
	 */
	abstract public BitField getRepresentation();
	
}
