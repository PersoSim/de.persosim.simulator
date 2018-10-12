package de.persosim.simulator.secstatus;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.Authorization;

/**
 * This class represents a {@link SecMechanism} storing unchangeable authorization information which are provided in PACE.
 * @author jkoch
 *
 */
public class ConfinedAuthorizationMechanism extends AbstractSecMechanism {
	
	private AuthorizationStore authorizationStore;
	
	public ConfinedAuthorizationMechanism(ConfinedAuthorizationMechanism authorizationMechanism) {
		authorizationStore = authorizationMechanism.getAuthorizationStore();
	}
	
	public ConfinedAuthorizationMechanism(AuthorizationStore authorizationStore) {
		this.authorizationStore = new AuthorizationStore(authorizationStore);
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
		return new AuthorizationStore(authorizationStore);
	}
	
	@Override
	public ConfinedAuthorizationMechanism clone() {
		return new ConfinedAuthorizationMechanism(this);
	}

}
