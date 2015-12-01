package de.persosim.simulator.cardobjects;

/**
 * Identifier for a CardObject. Primary used for identifying objects within
 * trees, should correspond to FileIdentifier from ISO7816 or similar
 * concepts.
 * 
 * Implementing objects are expected to be immutable (at least wrt to the
 * methods defined here).
 * 
 * @author amay
 * 
 */
public interface CardObjectIdentifier {

	/**
	 * Checks whether a given {@link CardObject} resp. one of its
	 * {@link CardObjectIdentifier}s fulfills the requirements expressed by this
	 * {@link CardObjectIdentifier}.
	 * 
	 * @see #matches(CardObjectIdentifier)
	 * 
	 * @param obj
	 *            {@link CardObject} to match against
	 * @return true iff the given object matches all criteria of this identifier
	 */
	boolean matches(CardObject currentObject);

}
