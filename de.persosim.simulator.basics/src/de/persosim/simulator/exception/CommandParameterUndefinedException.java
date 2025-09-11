package de.persosim.simulator.exception;

/**
 * @author slutters
 *
 */
public class CommandParameterUndefinedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CommandParameterUndefinedException() {
		super();
	}
	
	public CommandParameterUndefinedException(String reason) {
		super(reason);
	}

	/**
	 * Throws the JavaCard runtime environment-owned instance of the
	 * ISOException class with the specified reason.
	 * 
	 * @param reason
	 *            the reason for throwing an exception
	 */
	public static void throwIt(String reason) {
		throw new CommandParameterUndefinedException(reason);
	}
	
	public static void throwIt() {
		throw new CommandParameterUndefinedException();
	}
}
