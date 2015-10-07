package de.persosim.simulator.secstatus;

import java.util.HashMap;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.Authorization;

public class AuthorizationMechanism implements SecMechanism {
	
	protected HashMap<Oid, Authorization> authorizations;
	
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
	
}
