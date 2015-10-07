package de.persosim.simulator.secstatus;

import java.util.HashMap;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.Authorization;

public class AuthorizationMechanism implements SecMechanism {
	
	private HashMap<Oid, Authorization> authorizations;
	
	public AuthorizationMechanism(HashMap<Oid, Authorization> authorizations) {
		this.authorizations = authorizations;
	}
	
	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		if(event.equals(SecurityEvent.SECURE_MESSAGING_SESSION_ENDED)) {
			return true;
		}
		
		return false;
	}

	public Authorization getAuthorization(Oid oid) {
		return authorizations.get(oid);
	}
	
	public AuthorizationMechanism getUpdatedMechanism(Oid oid, Authorization authorization) {
		return getUpdatedMechanism(this, oid, authorization);
	}
	
	/**
	 * This method returns a copy of the provided {@link AuthorizationMechanism} updated with the also provided {@link Authorization}.
	 * In case the provided mechanism does not contain any authorization registered for the provided OID, the authorization is registered for the specific OID.
	 * In case the provided mechanism contains an authorization registered for the provided OID, this authorization is replaced with the effective authorization resulting from both authorizations available.
	 * @param authMechanism the mechanism to update
	 * @param oid the OID to register the authorization
	 * @param authorization the authorization to update
	 * @return a copy of the provided {@link AuthorizationMechanism} updated with the also provided {@link Authorization}
	 */
	public static AuthorizationMechanism getUpdatedMechanism(AuthorizationMechanism authMechanism, Oid oid, Authorization authorization) {
		HashMap<Oid, Authorization> newAuthorizations = new HashMap<>(authMechanism.authorizations);
		
		Authorization auth = authMechanism.authorizations.get(oid);
		Authorization newAuthorization;
		
		if(auth == null) {
			newAuthorization = authorization;
		} else{
			newAuthorization = auth.buildEffectiveAuthorization(authorization);
		}
		
		newAuthorizations.put(oid, newAuthorization);
		
		return new AuthorizationMechanism(newAuthorizations);
	}
	
}
