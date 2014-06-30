package de.persosim.simulator.exception;

/**
 * This Exception is thrown if the desired state change is not allowed (due to
 * ISO7816 restrictions on the state machine or access conditions at the
 * descretion of the implementing class)
 * 
 * @author mboonk
 * 
 */
public class LifeCycleChangeException extends Exception {

	private static final long serialVersionUID = 1L;

	public LifeCycleChangeException(String message) {
		super(message);
	}
	
}
