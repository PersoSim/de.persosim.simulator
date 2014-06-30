package de.persosim.simulator.exception;

/**
 * @author slutters
 *
 */
public class CardException extends GeneralException {
	
	private static final long serialVersionUID = 1L;

	public CardException(short statusWord, String message) {
		super(statusWord, message);
	}
}
