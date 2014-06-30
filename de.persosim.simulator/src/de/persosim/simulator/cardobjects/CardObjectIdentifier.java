package de.persosim.simulator.cardobjects;

/**
 * Identifier for a CardObject. Primary used for identifying objects within
 * ObjectStore, should correspond to FileIdentifier from ISO7816 or similar
 * concepts.
 * 
 * @author amay
 * 
 */
public interface CardObjectIdentifier {

	/**
	 * Checks whether a given {@link CardObjectIdentifier} fulfills the
	 * requirements expressed by this {@link CardObjectIdentifier}.
	 * <p/>
	 * For concrete identifiers this should implement a simple equals check,
	 * while it is also open for generic matching strategies that are capable of
	 * matching a broad range of {@link CardObjectIdentifier}s.
	 * 
	 * @param obj
	 *            {@link CardObjectIdentifier} to match against
	 * @return true iff the given object matches all criteria of this identifier
	 */
	boolean matches(CardObjectIdentifier obj);

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
