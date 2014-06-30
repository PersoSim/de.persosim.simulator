package de.persosim.simulator.apdu;


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

		//FIXME MBK reintegrate check of interindustry apdu
		//if (Iso7816Lib.isISOInterindustry(apdu)) {
			return new InterindustryCommandApdu(apdu, previousCommandApdu);
		//} else {
		//	return new CommandApdu(apdu, previousCommandApdu);
		//}
	}

}
