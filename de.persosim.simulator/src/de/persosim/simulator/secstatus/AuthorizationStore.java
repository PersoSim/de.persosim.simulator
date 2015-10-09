package de.persosim.simulator.secstatus;

import java.util.HashMap;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.Authorization;

public class AuthorizationStore {
	
private HashMap<Oid, Authorization> authorizations;
	
	public AuthorizationStore() {
		authorizations = new HashMap<>();
	}
	
	public AuthorizationStore(AuthorizationStore authStore) {
		this();
		updateAuthorization(authStore);
	}

	public AuthorizationStore(HashMap<Oid, Authorization> authorizations) {
		this.authorizations = authorizations;
	}
	
	public Authorization getAuthorization(Oid oid) {
		return authorizations.get(oid);
	}
	
	public void updateAuthorization(Oid oid, Authorization authorization) {
		Authorization auth = authorizations.get(oid);
		Authorization newAuthorization;
		
		if(auth == null) {
			newAuthorization = authorization;
		} else{
			newAuthorization = auth.buildEffectiveAuthorization(authorization);
		}
		
		authorizations.put(oid, newAuthorization);
	}
	
	public void updateAuthorization(AuthorizationStore authStore) {
		Authorization currentAuthorization;
		
		for(Oid currentOid:authStore.authorizations.keySet()) {
			currentAuthorization = authStore.getAuthorization(currentOid);
			updateAuthorization(currentOid, currentAuthorization);
		}
		
	}
	
	@Override
	public AuthorizationStore clone() {
		return new AuthorizationStore(this);
	}
	
}
