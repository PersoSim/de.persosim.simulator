package de.persosim.simulator.exception;

/**
 * This {@link Exception} can be thrown if a verification (e.g. comparing hashes) fails.
 * @author mboonk
 *
 */
public class VerificationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public VerificationException(String message) {
		super(message);
	}

}
