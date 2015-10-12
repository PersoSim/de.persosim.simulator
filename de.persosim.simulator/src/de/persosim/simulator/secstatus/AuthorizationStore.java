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
			this.authorizations.put(currentOid, authorizations.get(currentOid));
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

		//do not add new authorizations on the fly
		if(auth == null) {
			return;
		}
		
		Authorization newAuthorization = auth.buildEffectiveAuthorization(authorization);
		authorizations.put(oid, newAuthorization);
	}
	
	public void updateAuthorization(HashMap<Oid, Authorization> authorizations) {
		for(Oid currentOid:authorizations.keySet()) {
			updateAuthorization(currentOid, authorizations.get(currentOid));
		}
		
		for(Oid currentOid:this.authorizations.keySet()) {
			if(authorizations.get(currentOid) == null) {
				updateAuthorization(currentOid, this.authorizations.remove(currentOid));
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
			
			if(currentAuthOther != currentAuthThis) {
				if(!currentAuthOther.equals(currentAuthThis)) {
					return false;
				}
			}
		}
		
		return true;
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		boolean first = true;
		for(Oid oid:authorizations.keySet()) {
			if(first) {
				first = false;
			} else{
				sb.append("\n");
			}
			sb.append(oid + " --> " + authorizations.get(oid));
		}
		
		return sb.toString();
	}
	
}
