package de.persosim.simulator.exception;

/**
 * This exception is meant to channel exceptions resulting from cryptographic operations.
 * As this product is shipped with a crypto provider supporting all required operations
 * any exception like NoSuchAlgorithmException or InvalidPaddingException strongly indicates
 * that the wrong crypto provider is being used. This is considered as a configuration error
 * on RuntimeException level.
 * 
 * @author slutters
 *
 */
public class CryptoException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public CryptoException(Throwable cause) {
		super("operation NOT supported by crypto provider", cause);
	}
	
	public CryptoException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
