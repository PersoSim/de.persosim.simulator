package de.persosim.simulator.exception;

/**
 * This exception is to be thrown if an object or function is accessed and the
 * access is not allowed.
 * 
 * @author mboonk
 *
 */
public class AccessDeniedException extends Exception {

	public AccessDeniedException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
