package de.persosim.simulator.secstatus;

import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.secstatus.SecStatusEventUpdatePropagation;
import de.persosim.simulator.secstatus.SecurityEvent;

/**
 * This {@link UpdatePropagation} is used to propagate store and restore session context events
 * into the {@link SecStatus}.
 * 
 * @author jgoeke
 * 
 */
public final class SecStatusStoreUpdatePropagation extends SecStatusEventUpdatePropagation {

	private final int sessionContextIdentifier;
	
	public SecStatusStoreUpdatePropagation(SecurityEvent event, int sessionContextIdentifier) {
		super(event);
		this.sessionContextIdentifier = sessionContextIdentifier;
	}
	
	@Override
	public Class<? extends UpdatePropagation> getKey() {
		return SecStatusStoreUpdatePropagation.class;
	}

	public int getSessionContextIdentifier() {
		return sessionContextIdentifier;
	}
}
