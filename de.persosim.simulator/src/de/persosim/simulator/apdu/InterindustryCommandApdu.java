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
	public boolean chaining;
	public byte secureMessaging;
	public byte channel;

	/**
	 * Parses the apdu from the given byte array and sets the provided instance as predecessor.
	 * @param apdu
	 * @param previousCommandApdu the predecessor of this instance
	 */
	public InterindustryCommandApdu(byte[] apdu, CommandApdu previousCommandApdu) {
		super(apdu, previousCommandApdu);
		chaining = Iso7816Lib.isCommandChainingCLA(apdu);
		secureMessaging = Iso7816Lib.getSecureMessagingStatus(apdu);
		channel = Iso7816Lib.getChannel(apdu);
	}
	
	public boolean isChaining() {
		return chaining;
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu#getSecureMessaging()
	 */
	@Override
	public byte getSecureMessaging() {
		return secureMessaging;
	}

	public byte getChannel() {
		return channel;
	}

	@Override
	public boolean wasSecureMessaging() {
		if(secureMessaging != Iso7816.SM_OFF_OR_NO_INDICATION) {
			return true;
		} else {
			return super.wasSecureMessaging();
		}
	}
}