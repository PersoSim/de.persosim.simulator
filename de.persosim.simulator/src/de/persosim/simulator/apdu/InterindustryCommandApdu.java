package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.utils.Utils;

/**
 * This class encapsulates all features of a CommandApdu that are only present
 * if it follows one of the interindustry formats
 * {@link Iso7816#ISO_FORMAT_FIRSTINTERINDUSTRY} or
 * {@link Iso7816#ISO_FORMAT_FURTHERINTERINDUSTRY}
 * 
 * @author amay
 * 
 */
public class InterindustryCommandApdu extends CommandApduImpl implements IsoSecureMessagingCommandApdu {
	
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

	@Override
	public CommandApdu rewrapApdu(byte newSmStatus, byte[] commandData) {
		byte [] newApdu = Utils.concatByteArrays(header, commandData);
		newApdu[Iso7816.OFFSET_CLA] = Iso7816Lib.setSecureMessagingStatus(newApdu[Iso7816.OFFSET_CLA], newSmStatus);
		return new InterindustryCommandApdu(newApdu, this);
	}
}
