package de.persosim.simulator.secstatus;

import de.persosim.simulator.protocols.ta.Authorization;

/**
 * This class represents a {@link SecMechanism} storing authorization information with update functionality.
 * @author slutters
 *
 */
public class EffectiveAuthorizationMechanism extends ConfinedAuthorizationMechanism  {
	
	public EffectiveAuthorizationMechanism(AuthorizationStore authorizationStore) {
		super(authorizationStore);
	}

	/**
	 * This method returns a copy of this {@link EffectiveAuthorizationMechanism} updated with the provided {@link Authorization}.
	 * In case the provided mechanism does not contain any authorization registered for the provided OID, the authorization is registered for the specific OID.
	 * In case the provided mechanism contains an authorization registered for the provided OID, this authorization is replaced with the effective authorization resulting from both authorizations available.
	 * @param authorizationStore the authorization information to use for update
	 * @return a copy of the this {@link EffectiveAuthorizationMechanism} updated with the provided {@link Authorization}
	 */
	public EffectiveAuthorizationMechanism getUpdatedMechanism(AuthorizationStore authorizationStore) {
		AuthorizationStore originalAuthStore = getAuthorizationStore();
		originalAuthStore.updateAuthorization(authorizationStore);
		return new EffectiveAuthorizationMechanism(originalAuthStore);
	}
	
}
