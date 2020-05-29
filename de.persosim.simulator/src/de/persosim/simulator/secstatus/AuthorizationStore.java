package de.persosim.simulator.secstatus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public AuthorizationStore(Map<Oid, Authorization> authorizations) {
		this();
		
		for(Entry<Oid, Authorization> currentEntry:authorizations.entrySet()) {
			this.authorizations.put(currentEntry.getKey(), currentEntry.getValue());
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
	public void updateAuthorization(Map<Oid, Authorization> authorizations) {
		for(Entry<Oid, Authorization> currentEntry:authorizations.entrySet()) {
			updateAuthorization(currentEntry.getKey(), currentEntry.getValue());
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authorizations == null) ? 0 : authorizations.hashCode());
		return result;
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
		
		Authorization currentAuthThis;
		Authorization currentAuthOther;
		
		for(Oid currentOid:authorizations.keySet()) {
			currentAuthThis = getAuthorization(currentOid);
			currentAuthOther = other.getAuthorization(currentOid);
			
			if(!currentAuthThis.equals(currentAuthOther)) {
				return false;
			}
		}
		
		for(Oid currentOid:other.authorizations.keySet()) {
			currentAuthThis = getAuthorization(currentOid);
			currentAuthOther = other.getAuthorization(currentOid);
			
			if(!currentAuthOther.equals(currentAuthThis)) {
				return false;
			}
		}
	
		return true;
	
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		boolean first = true;
		for(Entry<Oid, Authorization> currentEntry:authorizations.entrySet()) {
			if(first) {
				first = false;
			} else{
				sb.append("\n");
			}
			sb.append(currentEntry.getKey() + " --> " + currentEntry.getValue());
		}
		
		return sb.toString();
	}
	
}
