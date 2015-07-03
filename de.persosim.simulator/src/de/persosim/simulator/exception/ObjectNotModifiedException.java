package de.persosim.simulator.exception;

/**
 * This exception is to be thrown if the tried modification was not possible
 * because of access rights, life cycle state or other security conditions.
 * 
 * @author mboonk
 *
 */
public class ObjectNotModifiedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ObjectNotModifiedException(String message) {
		super(message);
	}

}
