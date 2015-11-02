package de.persosim.simulator.cardobjects;

import java.util.Collection;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.secstatus.SecStatus;

/**
 * This interface represents the highest level of abstraction for objects on the
 * card. See inheritance hierarchy for available object types.
 * 
 * Classes implementing this interface MUST have EXACTLY one constructor.
 * This constructor MUST accept a {@link SecStatus} as first parameter.
 * 
 * @author amay
 * 
 */
public interface CardObject extends Iso7816LifeCycle {

	/**
	 * @return parent object of this object or null if unknown or root object
	 */
	CardObject getParent();

	/**
	 * @return children of this CardObject, possibly empty but should not be
	 *         null
	 */
	Collection<CardObject> getChildren();
	
	/**
	 * @return all identifiers contained in this object
	 */
	Collection<CardObjectIdentifier> getAllIdentifiers();

	/**
	 * Set the SecStatus that shall be used when verifying access conditions. It
	 * is expected that this {@link SecStatus} is forwarded to all children.
	 * <p/>
	 * Note: This method shall be restricted through the LifeCycle of the
	 * implementing Object.
	 * 
	 * @param securityStatus
	 * @throws AccessDeniedException 
	 */
	public void setSecStatus(SecStatus securityStatus) throws AccessDeniedException;
	
	/**
	 * Build a Collection containing all children of this object that match all given {@link CardObjectIdentifier}.
	 * 
	 * @param identifier
	 *            to match the {@link CardObject}s with
	 * @return a {@link Collection} containing all children that match the given
	 *         identifier. May be empty if no matching child was found.
	 */
	public Collection<CardObject> findChildren(CardObjectIdentifier... cardObjectIdentifiers);
	
	
	/**
	 * Remove child from the collection.
	 * 
	 * If the given element is not a child nothing will be done at all.
	 * 
	 * @param child
	 *        element to remove from the collection
	 * @return the removed child or null if none removed
	 * @throws AccessDeniedException 
	 */
	public CardObject removeChild(CardObject child) throws AccessDeniedException;
}
