package de.persosim.simulator.secstatus;

import de.persosim.simulator.processing.UpdatePropagation;

/**
 * This {@link UpdatePropagation} is used to propagate {@link SecurityEvent}s
 * into the {@link SecStatus}.
 * 
 * @author mboonk
 * 
 */
public class SecStatusEventUpdatePropagation extends SecStatusUpdatePropagation {

	protected SecurityEvent event;

	public SecStatusEventUpdatePropagation(SecurityEvent event) {
		this.event = event;
	}

	@Override
	public Class<? extends UpdatePropagation> getKey() {
		return SecStatusEventUpdatePropagation.class;
	}

	public SecurityEvent getEvent() {
		return event;
	}

}
