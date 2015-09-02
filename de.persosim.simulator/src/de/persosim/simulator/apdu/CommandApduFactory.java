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
			if (matchesIsoCompatibleProprietaryCommandApdu(apdu)){
				return new IsoCompatibleProprietaryCommandApdu(apdu, previousCommandApdu);
			}
			return new CommandApduImpl(apdu, previousCommandApdu);
		}
	}
	
	public static boolean matchesIsoCompatibleProprietaryCommandApdu(byte [] apdu){
		CommandApdu command = new CommandApduImpl(apdu);
		
//		if ((command.getCla() == (byte) (0x8C & 0xFF) || (command.getCla() == (byte) (0x80 & 0xFF)))) {
//			if((command.getIns() == 0x20) && (command.getP1P2() == (short) (0x8000 & 0xFFFF))) {
//				return true;
//			}
//		}
		
		if(( ((command.getCla() & (byte) 0xE0) == (byte) 0x80) && ((command.getCla() & (byte) 0x0C) != (byte) 0x04) )
				|| ((command.getCla() & (byte) 0x40) == (byte) 0x40)) {
			return true;
		}
		
		return false;
	}
}
