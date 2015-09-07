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
			return new InterindustryCommandApduImpl(apdu, previousCommandApdu);
		} else {
			if (matchesIsoCompatibleProprietaryCommandApdu(apdu)){
				return new IsoCompatibleProprietaryCommandApdu(apdu, previousCommandApdu);
			}
			return new CommandApduImpl(apdu, previousCommandApdu);
		}
	}
	
	public static boolean matchesIsoCompatibleProprietaryCommandApdu(byte [] apdu){
		CommandApdu command = new CommandApduImpl(apdu);
		
		
		if ((command.getCla() == (byte) 0x8C || (command.getCla() == (byte) 0x80))) {
			if((command.getIns() == (byte) 0x20) && (command.getP1P2() == (short) 0x8000)) {
				// Verify command as defined by BSI TR 03110 (with parameters not
				// allowed by ISO back than) used in AuxDataVerification
				return true;
			}
			
			if((command.getIns() == (byte) 0x2A) && (command.getP1P2() == (short) 0xAEAC)) {
				// PSO CDS as defined by BSI TR 03110 used in PSM and PSC
				return true;
			}
		}
		
		return false;
	}
}
