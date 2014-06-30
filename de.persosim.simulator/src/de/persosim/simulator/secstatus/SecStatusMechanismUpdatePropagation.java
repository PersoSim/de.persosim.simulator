package de.persosim.simulator.secstatus;

import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.secstatus.SecStatus.SecContext;

/**
 * Container to store a new mechanism for the SecStatus and propagate it from
 * protocol to the SecStatus.
 * 
 * @author amay
 * 
 */
public class SecStatusMechanismUpdatePropagation extends SecStatusUpdatePropagation {

	private SecContext context;
	private SecMechanism mechanism;

	@Override
	public Class<? extends UpdatePropagation> getKey() {
		return SecStatusMechanismUpdatePropagation.class;
	}

	public SecStatusMechanismUpdatePropagation(SecContext context,
			SecMechanism mechanism) {
		super();
		this.context = context;
		this.mechanism = mechanism;
	}

	public SecContext getContext() {
		return context;
	}

	public SecMechanism getMechanism() {
		return mechanism;
	}

}
