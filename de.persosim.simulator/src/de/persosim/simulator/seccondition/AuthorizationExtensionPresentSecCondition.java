package de.persosim.simulator.seccondition;

import java.util.ArrayList;
import java.util.Collection;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.Authorization;
import de.persosim.simulator.secstatus.AuthorizationMechanism;
import de.persosim.simulator.secstatus.SecMechanism;

/**
 * {@link SecCondition} that ensures that a given AuthorizationExtenension is present in {@link AuthorizationMechanism} 
 * @author amay
 *
 */
public class AuthorizationExtensionPresentSecCondition implements SecCondition {
	
	protected Oid oid;
	public AuthorizationExtensionPresentSecCondition(Oid oid) {
		this.oid = oid;
	}
	
	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {

		for(SecMechanism secMechanism:mechanisms) {
			if(secMechanism instanceof AuthorizationMechanism) {
				AuthorizationMechanism authMechanism = (AuthorizationMechanism) secMechanism;
				Authorization auth = authMechanism.getAuthorization(oid);
				return auth != null;
			}
		}
		
		return false;
		
	}

	@Override
	public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
		Collection<Class<? extends SecMechanism>> mechanisms = new ArrayList<>();
		
		mechanisms.add(AuthorizationMechanism.class);
		
		return mechanisms;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + oid + "]";
	}

}
