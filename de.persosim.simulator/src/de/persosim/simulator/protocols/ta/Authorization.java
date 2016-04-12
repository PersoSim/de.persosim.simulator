package de.persosim.simulator.protocols.ta;

import static org.globaltester.logging.BasicLogger.WARN;
import static org.globaltester.logging.BasicLogger.log;

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
	 * Construct the effective Authorization by calculating a logical and-operation on the
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		return authorization.equals(((Authorization) obj).authorization);
	}
	
	@Override
	public String toString() {
		return authorization.toString();
	}
	
}
