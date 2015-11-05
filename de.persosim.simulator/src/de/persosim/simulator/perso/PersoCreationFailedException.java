package de.persosim.simulator.perso;

/**
 * This {@link RuntimeException} is used when the instantiation of a
 * personalization failes.
 * 
 * @author mboonk
 *
 */
public class PersoCreationFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PersoCreationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersoCreationFailedException(String message) {
		super(message);
	}
}
