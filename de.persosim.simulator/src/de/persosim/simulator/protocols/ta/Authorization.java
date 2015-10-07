package de.persosim.simulator.protocols.ta;

import static de.persosim.simulator.utils.PersoSimLogger.log;
import static de.persosim.simulator.utils.PersoSimLogger.WARN;

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
	 * Construct the effective Authorization by calculating a logical and on the
	 * {@link BitField} representations of role and authorization.
	 * 
	 * @param authorization
	 * @return
	 */
	public Authorization buildEffectiveAuthorization(Authorization authorization) {
		BitField newAuthorization = authorization.getAuthorization();
		
		BitField effectiveAuth = newAuthorization.and(this.authorization);
		
		if(this.authorization.getNumberOfBits() != newAuthorization.getNumberOfBits()) {
			log(this.getClass(), "updating authorizations of different length", WARN);
		}
		
		return new Authorization(effectiveAuth);
	}
	
}
