package de.persosim.simulator.statemachine;


/**
 * Generic super class for Sinelabore generated statemachine code. This
 * implements methods required by {@link StateMachine} interface and not created
 * by code generation.
 * 
 * @author amay
 * 
 */
public abstract class AbstractStateMachine implements StateMachine {
	private boolean initialized = false;
	
	@Override
	public void init() {
		reset();
		initialized  = true;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}
	
	@Override
	public void reset() {
		reInitialize();
		processEvent((byte) 0xFF); // handle the first transition
	}
}
