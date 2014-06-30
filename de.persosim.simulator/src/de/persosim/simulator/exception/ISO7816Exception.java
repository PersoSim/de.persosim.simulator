package de.persosim.simulator.exception;

/**
 * @author slutters
 *
 */

public class ISO7816Exception extends GeneralException {

	private static final long serialVersionUID = 1L;
	
	protected String info;
	
	public ISO7816Exception(short statusWord, String message) {
		super(statusWord, message);
	}
	
	public static void throwIt(short reason) {
		throw new ISO7816Exception(reason, "");
	}
	
	public static void throwIt(short reason, String info) {
		throw new ISO7816Exception(reason, info);
	}

	/**
	 * @return the info
	 */
	public String getInfo() {
		return this.info;
	}
	
	@Override
	public String getTypeOfError() {
		return "ISO7816";
	}
}
