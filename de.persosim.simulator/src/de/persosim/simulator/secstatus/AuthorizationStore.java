package de.persosim.simulator.secstatus;

import java.util.HashMap;
import java.util.Iterator;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.Authorization;

/**
 * This class provides an updatable container for storing authorization information.
 * Authorization information is stored within this object backed by a HashMap mapping
 * OIDs to objects of type {@link Authorization}.
 * 
 * @author slutters
 *
 */
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
	
	/**
	 * This method updates the authorization information stored within this
	 * object. If authorization information is already registered for the
	 * provided OID, it will be updated to the effective authorization resulting
	 * from present and provided authorization information, see
	 * {@link Authorization#buildEffectiveAuthorization(Authorization)}} for
	 * details.
	 * 
	 * @param oid
	 *            the OID for which authorization information is to be updated
	 * @param authorization
	 *            the information authorization to be used for the update
	 */
	private void updateAuthorization(Oid oid, Authorization authorization) {
		Authorization auth = authorizations.get(oid);

		//do not add new authorizations on the fly
		if(auth == null) {
			return;
		}
		
		Authorization newAuthorization = auth.buildEffectiveAuthorization(authorization);
		authorizations.put(oid, newAuthorization);
	}
	
	/**
	 * This method updates the authorization information stored within this
	 * object. If authorization information is already registered for one of the
	 * provided OIDs, it will be updated to the effective authorization
	 * resulting from present and provided authorization information, see
	 * {@link Authorization#buildEffectiveAuthorization(Authorization)}} for
	 * details. If the set of provided information lacks information that is
	 * registered within this object, the information is also removed from this
	 * object.
	 * 
	 * @param authorizations the authorization information to be used for the update
	 */
	public void updateAuthorization(HashMap<Oid, Authorization> authorizations) {
		for(Oid currentOid:authorizations.keySet()) {
			updateAuthorization(currentOid, authorizations.get(currentOid));
		}
		
		Iterator<Oid> iter = this.authorizations.keySet().iterator();
		
		while (iter.hasNext()){
			Oid currentOid = iter.next();
			if(authorizations.get(currentOid) == null) {
				iter.remove();
			}
		}
	}
	
	/**
	 * This method updates the authorization information stored within this
	 * object. See {@link #updateAuthorization(HashMap)} for details.
	 * 
	 * @param authStore the authorization information to be used for the update
	 */
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
