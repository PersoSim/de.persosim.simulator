package de.persosim.simulator.exception;

/**
 * @author slutters
 *
 */
public abstract class ProtocolException extends GeneralException {
	
	private static final long serialVersionUID = 1L;
	
	public ProtocolException(short statusWord, String message) {
		super(statusWord, message);
	}
}
