package de.persosim.simulator.secstatus;

import java.util.HashMap;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.Authorization;

public class AuthorizationStore {
	
private HashMap<Oid, Authorization> authorizations;
	
	public AuthorizationStore() {
		authorizations = new HashMap<>();
	}
	
	public AuthorizationStore(HashMap<Oid, Authorization> authorizations) {
		this();
		
		for(Oid currentOid:authorizations.keySet()) {
			authorizations.put(currentOid, authorizations.get(currentOid));
		}
	}
	
	public AuthorizationStore(AuthorizationStore authStore) {
		this(authStore.authorizations);
	}
	
	public Authorization getAuthorization(Oid oid) {
		return authorizations.get(oid);
	}
	
	private void updateAuthorization(Oid oid, Authorization authorization) {
		Authorization auth = authorizations.get(oid);
		Authorization newAuthorization;
		
		if(auth == null) {
			newAuthorization = authorization.getMinimumAuthorization();
		} else{
			newAuthorization = auth.buildEffectiveAuthorization(authorization);
		}
		
		authorizations.put(oid, newAuthorization);
	}
	
	public void updateAuthorization(HashMap<Oid, Authorization> authorizations) {
		for(Oid currentOid:authorizations.keySet()) {
			updateAuthorization(currentOid, authorizations.get(currentOid));
		}
		
		for(Oid currentOid:this.authorizations.keySet()) {
			if(authorizations.get(currentOid) == null) {
				updateAuthorization(currentOid, this.authorizations.get(currentOid).getMinimumAuthorization());
			}
		}
	}
	
	public void updateAuthorization(AuthorizationStore authStore) {
		updateAuthorization(authStore.authorizations);
	}
	
	@Override
	public AuthorizationStore clone() {
		return new AuthorizationStore(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthorizationStore other = (AuthorizationStore) obj;
		
		Authorization currentAuthThis, currentAuthOther;
		
		for(Oid currentOid:authorizations.keySet()) {
			currentAuthThis = getAuthorization(currentOid);
			currentAuthOther = other.getAuthorization(currentOid);
			
			if(currentAuthThis != currentAuthOther) {
				if(!currentAuthThis.equals(currentAuthOther)) {
					return false;
				}
			}
		}
		
		for(Oid currentOid:other.authorizations.keySet()) {
			currentAuthThis = getAuthorization(currentOid);
			currentAuthOther = other.getAuthorization(currentOid);
			
			if(currentAuthThis != currentAuthOther) {
				if(!currentAuthThis.equals(currentAuthOther)) {
					return false;
				}
			}
		}
		
		return true;
		
	}
	
}
