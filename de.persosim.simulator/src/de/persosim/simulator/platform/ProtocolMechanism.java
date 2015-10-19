package de.persosim.simulator.platform;

import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecurityEvent;

/**
 * This {@link SecMechanism} is used to communicate all useful information
 * about the currently active protocol.
 * 
 * @author slutters
 *
 */
public class ProtocolMechanism implements SecMechanism {
	
	protected Class<? extends Protocol> currentlyActiveProtocol;
	
	public ProtocolMechanism(Class<? extends Protocol> protocolClass) {
		currentlyActiveProtocol = protocolClass;
	}
	
	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		if(event.equals(SecurityEvent.SECURE_MESSAGING_SESSION_ENDED)) {
			return true;
		} else{
			return false;
		}
	}

	public Class<? extends Protocol> getCurrentlyActiveProtocol() {
		return currentlyActiveProtocol;
	}

}
