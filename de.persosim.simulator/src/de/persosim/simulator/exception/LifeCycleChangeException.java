package de.persosim.simulator.exception;

import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;

/**
 * This Exception is thrown if the desired state change is not allowed (due to
 * ISO7816 restrictions on the state machine or access conditions at the
 * discretion of the implementing class)
 * 
 * @author mboonk
 * 
 */
public class LifeCycleChangeException extends AccessDeniedException {

	private static final long serialVersionUID = 1L;

	private Iso7816LifeCycleState oldState;
	private Iso7816LifeCycleState newState;
	
	public LifeCycleChangeException(String message, Iso7816LifeCycleState oldState, Iso7816LifeCycleState newState) {
		super(message);
		this.oldState = oldState;
		this.newState = newState;
	}
	
	/**
	 * This returns the state that was current, when the forbidden state change
	 * was tried.
	 * 
	 * @return the current state
	 */
	public Iso7816LifeCycleState getOldState() {
		return oldState;
	}
	
	/**
	 * This returns the state, that the forbidden state change tried to reach.
	 * @return the target state
	 */
	public Iso7816LifeCycleState getNewState() {
		return newState;
	}
	
}
