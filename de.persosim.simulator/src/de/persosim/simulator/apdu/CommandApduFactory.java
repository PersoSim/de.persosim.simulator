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
			if (matchesTR03110Verify(apdu)){
				return new TR03110VerifySecureMessagingCommandApdu(apdu, previousCommandApdu);
			}
			return new CommandApduImpl(apdu, previousCommandApdu);
		}
	}
	
	private static boolean matchesTR03110Verify(byte [] apdu){
		CommandApdu command = new CommandApduImpl(apdu);
		if ((command.getCla() == (byte) (0x8c & 0xFF) || (command.getCla() == (byte) (0x80 & 0xFF)))
				&& command.getIns() == 0x20
				&& command.getP1P2() == (short) (0x8000 & 0xFFFF)
				){
			return true;
		}
		return false;
	}
}
