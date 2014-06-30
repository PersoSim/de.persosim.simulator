package de.persosim.simulator.protocols.pace;

import de.persosim.simulator.exception.ProtocolException;

/**
 * @author slutters
 *
 */
public class PaceException extends ProtocolException {
	
	private static final long serialVersionUID = 1L;
	
	public PaceException(short statusWord, String message) {
		super(statusWord, message);
	}
	
	public static void throwIt(short reason) {
		throw new PaceException(reason, "");
	}
	
	public static void throwIt(short reason, String info) {
		throw new PaceException(reason, info);
	}
}
