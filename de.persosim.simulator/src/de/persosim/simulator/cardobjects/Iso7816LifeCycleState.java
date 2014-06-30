package de.persosim.simulator.cardobjects;

/**
 * This enumeration is used for the description of card, file and other
 * lifecycle states that are used as defined in ISO7816-4,9,13.
 * 
 * @author mboonk
 * 
 */
public enum Iso7816LifeCycleState {
	CREATION, INITIALISATION, OPERATIONAL_ACTIVATED, OPERATIONAL_DEACTIVATED, TERMINATION, UNDEFINED
}
