package de.persosim.simulator.seccondition;

import java.util.ArrayList;
import java.util.Collection;

import de.persosim.simulator.platform.ProtocolMechanism;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.secstatus.SecMechanism;

public class ProtocolSecCondition implements SecCondition {
	
	protected Class<? extends Protocol> protocol;
	
	public ProtocolSecCondition(Class<? extends Protocol> protocol) {
		this.protocol = protocol;
	}
	
	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		
		for(SecMechanism secMechanism:mechanisms) {
			if(secMechanism instanceof ProtocolMechanism) {
				return protocol.isAssignableFrom(((ProtocolMechanism) secMechanism).getCurrentlyActiveProtocol());
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
