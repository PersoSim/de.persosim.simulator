package de.persosim.simulator.cardobjects;

import de.persosim.simulator.perso.Personalization;

/**
 * This interface allows wrapping existing CardObjects. See inheritance
 * hierarchy for available object types.
 * 
 * This can be usefull to add additional behavior to exisitng objects in
 * existing {@link Personalization}s without the need to define new Objects and
 * copy content/state.
 * 
 * @author amay
 * 
 */
public interface CardObjectWrapper extends CardObject {

	/**
	 * Set the object to be wrapped.
	 * 
	 * Removing that object from its tree and adding the wrapper appropriately
	 * shall be handled externaly.
	 * 
	 * @param cardObjectToWrap
	 */
	void setWrappedObject(CardObject cardObjectToWrap);

}
