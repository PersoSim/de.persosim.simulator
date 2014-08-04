package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;

/**
 * This class encapsulates all features of a CommandApdu that are only present
 * if it follows one of the interindustry formats
 * {@link Iso7816#ISO_FORMAT_FIRSTINTERINDUSTRY} or
 * {@link Iso7816#ISO_FORMAT_FURTHERINTERINDUSTRY}
 * 
 * @author amay
 * 
 */
public class InterindustryCommandApdu extends CommandApdu implements IsoSecureMessagingCommandApdu {
	
	/**
	 * Parses the apdu from the given byte array and sets the provided instance as predecessor.
	 * @param apdu
	 * @param previousCommandApdu the predecessor of this instance
	 */
	public InterindustryCommandApdu(byte[] apdu, CommandApdu previousCommandApdu) {
		super(apdu, previousCommandApdu);
	}
	
	public boolean isChaining() {
		return Iso7816Lib.isCommandChainingCLA(header);
	}
	
	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu#getSecureMessaging()
	 */
	@Override
	public byte getSecureMessaging() {
		return Iso7816Lib.getSecureMessagingStatus(header);
	}

	@Override
	public void setSecureMessaging(byte smStatus) {
		header[Iso7816Lib.OFFSET_CLA] = Iso7816Lib.setSecureMessagingStatus(getCla(), smStatus);
	}

	public byte getChannel() {
		return Iso7816Lib.getChannel(getHeader());
	}

	@Override
	public boolean wasSecureMessaging() {
		if(getSecureMessaging() != Iso7816.SM_OFF_OR_NO_INDICATION) {
			return true;
		} else {
			return super.wasSecureMessaging();
		}
	}
}