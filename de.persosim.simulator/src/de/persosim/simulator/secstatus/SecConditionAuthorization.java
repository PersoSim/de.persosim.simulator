package de.persosim.simulator.secstatus;

import java.util.ArrayList;
import java.util.Collection;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.Authorization;

public class SecConditionAuthorization implements SecCondition {
	
	protected Oid oid;
	protected int bit;
	
	public SecConditionAuthorization(Oid oid, int bit) {
		this.oid = oid;
		this.bit = bit;
	}
	
	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {

		for(SecMechanism secMechanism:mechanisms) {
			if(secMechanism instanceof AuthorizationMechanism) {
				AuthorizationMechanism authMechanism = (AuthorizationMechanism) secMechanism;
				Authorization auth = authMechanism.getAuthorization(oid);
				
				return auth.getAuthorization().getBit(bit);
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

}