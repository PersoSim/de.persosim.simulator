package de.persosim.simulator.cardobjects;

/**
 * Abstract super class for all object identifiers, contains the generic
 * matching logic on card objects which evaluates to true if the object has a
 * {@link CardObjectIdentifier} associated on which this{@link #equals(Object)}
 * evaluates to true.
 * <p/>
 * Note: This implies that all subclasses need to define correct implementations
 * for the {@link #hashCode()} and {@link #equals(Object)} methods.
 * 
 * @author mboonk
 * 
 */
public abstract class AbstractCardObjectIdentifier implements
		CardObjectIdentifier {

	@Override
	public boolean matches(CardObject currentObject) {
		for (CardObjectIdentifier currentIdentifier : currentObject
				.getAllIdentifiers()) {
			if (this.equals(currentIdentifier)) {
				return true;
			}
		}
		return false;
	}

}
