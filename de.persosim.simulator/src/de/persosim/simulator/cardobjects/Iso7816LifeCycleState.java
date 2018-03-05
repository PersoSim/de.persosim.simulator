package de.persosim.simulator.cardobjects;

/**
 * This enumeration is used for the description of card, file and other
 * lifecycle states that are used as defined in ISO7816-4,9,13.
 * 
 * @author mboonk
 * 
 */
public enum Iso7816LifeCycleState {
	CREATION, CREATION_OPERATIONAL_ACTIVATED, CREATION_OPERATIONAL_DEACTIVATED, INITIALISATION, OPERATIONAL_ACTIVATED, OPERATIONAL_DEACTIVATED, TERMINATION, UNDEFINED;

	/**
	 * This returns if this state is one of the operational states.
	 * 
	 * @return
	 */
	public boolean isOperational() {
		return this.equals(OPERATIONAL_ACTIVATED)
				|| this.equals(OPERATIONAL_DEACTIVATED);
	}

	/**
	 * This returns if this state belongs to the personalization phase (all
	 * states before {@link Iso7816LifeCycleState#OPERATIONAL_ACTIVATED})
	 * 
	 * @return true iff the the state is in the personalization phase
	 */
	public boolean isPersonalizationPhase() {
		return this.equals(CREATION) || this.equals(INITIALISATION) || this.equals(CREATION_OPERATIONAL_ACTIVATED) || this.equals(CREATION_OPERATIONAL_DEACTIVATED);
	}

	public boolean isCreation() {
		return this.equals(CREATION) || this.equals(CREATION_OPERATIONAL_ACTIVATED) || this.equals(CREATION_OPERATIONAL_DEACTIVATED);
	}
}
