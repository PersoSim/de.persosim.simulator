package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816Lib;


public class CommandApduFactory {

	private CommandApduFactory() {
		// should not be instantiated
	}

	/**
	 * Default factory method. Parses the apdu from the given byte array.
	 * 
	 * @param apdu
	 */
	public static CommandApdu createCommandApdu(byte[] apdu) {
		return createCommandApdu(apdu, null);
	}

	/**
	 * Factory method that parses the apdu from the given byte array and sets
	 * the provided instance as predecessor.
	 * 
	 * @param apdu
	 * @param previousCommandApdu
	 *            the predecessor of this instance
	 */
	public static CommandApdu createCommandApdu(byte[] apdu,
			CommandApdu previousCommandApdu) {
		
		if (Iso7816Lib.isISOInterindustry(apdu)) {
			return new InterindustryCommandApdu(apdu, previousCommandApdu);
		} else {
			if (TR03110VerifySecureMessagingCommandApdu.matches(apdu)){
				return new TR03110VerifySecureMessagingCommandApdu(apdu, previousCommandApdu);
			}
			return new CommandApdu(apdu, previousCommandApdu);
		}
	}

}
