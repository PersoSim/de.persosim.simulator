package de.persosim.simulator.exception;

/**
 * @author slutters
 *
 */
public class ProcessingException extends GeneralException {
	
	private static final long serialVersionUID = 1L;
	
	public ProcessingException(short statusWord, String message) {
		super(statusWord, message);
	}
	
	public static void throwIt(short reason) {
		throw new ProcessingException(reason, "");
	}
	
	public static void throwIt(short reason, String info) {
		throw new ProcessingException(reason, info);
	}
}
