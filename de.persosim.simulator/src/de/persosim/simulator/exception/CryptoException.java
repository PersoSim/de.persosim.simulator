package de.persosim.simulator.exception;

/**
 * @author slutters
 *
 */
public class CryptoException extends ProcessingException {
	
	private static final long serialVersionUID = 1L;
	
	public CryptoException(short statusWord, String message) {
		super(statusWord, message);
	}
}
