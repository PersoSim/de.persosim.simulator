package de.persosim.simulator.secstatus;

import java.util.ArrayList;
import java.util.Collection;

import de.persosim.simulator.platform.ProtocolMechanism;
import de.persosim.simulator.protocols.Protocol;

public class SecConditionProtocol implements SecCondition {
	
	protected Class<? extends Protocol> protocol;
	
	public SecConditionProtocol(Class<? extends Protocol> protocol) {
		this.protocol = protocol;
	}
	
	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		
		for(SecMechanism secMechanism:mechanisms) {
			if(secMechanism instanceof ProtocolMechanism) {
				return protocol.isAssignableFrom(((ProtocolMechanism) secMechanism).getCurrentlyActiveProtocol().getClass());
			}
		}
		return false;
	}

	@Override
	public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
		Collection<Class<? extends SecMechanism>> mechanisms = new ArrayList<>();
		
		mechanisms.add(ProtocolMechanism.class);
		
		return mechanisms;
	}

}
