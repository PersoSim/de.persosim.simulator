package de.persosim.simulator.secstatus;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.Authorization;

/**
 * This class represents a {@link SecMechanism} storing (accumulated) authorization information.
 * @author slutters
 *
 */
public class AuthorizationMechanism extends AbstractSecMechanism {
	
	private AuthorizationStore authorizationStore;
	
	public AuthorizationMechanism(AuthorizationMechanism authorizationMechanism) {
		authorizationStore = authorizationMechanism.getAuthorizationStore();
	}
	
	public AuthorizationMechanism(AuthorizationStore authorizationStore) {
		this.authorizationStore = authorizationStore.clone();
	}
	
	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		if(event.equals(SecurityEvent.SECURE_MESSAGING_SESSION_ENDED)) {
			return true;
		}
		
		return false;
	}

	public Authorization getAuthorization(Oid oid) {
		return authorizationStore.getAuthorization(oid);
	}
	
	public AuthorizationStore getAuthorizationStore() {
		return authorizationStore.clone();
	}
	
	@Override
	public AuthorizationMechanism clone() {
		return new AuthorizationMechanism(this);
	}
	
	/**
	 * This method returns a copy of this {@link AuthorizationMechanism} updated with the provided {@link Authorization}.
	 * In case the provided mechanism does not contain any authorization registered for the provided OID, the authorization is registered for the specific OID.
	 * In case the provided mechanism contains an authorization registered for the provided OID, this authorization is replaced with the effective authorization resulting from both authorizations available.
	 * @param authorizationStore the authorization information to use for update
	 * @return a copy of the this {@link AuthorizationMechanism} updated with the provided {@link Authorization}
	 */
	public AuthorizationMechanism getUpdatedMechanism(AuthorizationStore authorizationStore) {
		AuthorizationStore originalAuthStore = getAuthorizationStore();
		originalAuthStore.updateAuthorization(authorizationStore);
		return new AuthorizationMechanism(originalAuthStore);
	}
	
}
