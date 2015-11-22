package de.persosim.simulator.cardobjects;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.LifeCycleChangeException;

/**
 * This interface describes objects, that have an ISO7816-conform life cycle.
 * 
 * @author mboonk
 * 
 */
public interface Iso7816LifeCycle {
	Iso7816LifeCycleState getLifeCycleState();

	/**
	 * Update the life cycle state of this object.
	 * 
	 * @param state
	 *            the new life cycle state
	 * @throws LifeCycleChangeException
	 *             if the desired state change is not allowed (due to ISO7816
	 *             restrictions on the state machine or access conditions at the
	 *             discretion of the implementing class)
	 * @throws AccessDeniedException 
	 */
	void updateLifeCycleState(Iso7816LifeCycleState state)
			throws AccessDeniedException;

}
