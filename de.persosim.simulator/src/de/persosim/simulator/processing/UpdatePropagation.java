package de.persosim.simulator.processing;

/**
 * Instances of this interface carry data to update other entities within the
 * card according to result of apdu processing. They are stored within the
 * ProcessingData and referenced by their type.
 * 
 * This provides a loose coupling between entities defining UpdatePropagation
 * types and those who want to provide information within these updates. Also
 * this allows the receiving entities to enforce conditions regarding their
 * updates or to allow further propagation of this information in order to
 * update other entities within the system with the same data.
 * 
 * @author amay
 * 
 */
public interface UpdatePropagation {

	/**
	 * Returns the class used as key of this update.
	 * 
	 * Generally this should be this.getClass(). Under some circumstances it may
	 * be useful to rely on a superclass/super interface as key.
	 * 
	 * Usage of a key that this instance is not "instanceof" is not allowed.
	 * 
	 * @return
	 */
	public Class<? extends UpdatePropagation> getKey();

}
