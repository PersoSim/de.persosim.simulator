package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.utils.Utils;


/**
 * This class represents the proprietary class APDU that is used for the
 * verify-command as described in TR-03110 v2.10 Part 3 B.11.8. using secure
 * messaging and no chaining.
 * 
 * @author mboonk
 * 
 */
public class IsoCompatibleProprietaryCommandApdu extends CommandApduImpl implements
		IsoSecureMessagingCommandApdu {
	
	IsoCompatibleProprietaryCommandApdu(byte[] apdu, CommandApdu previousCommandApdu) {
		super(apdu, previousCommandApdu);
	}

	@Override
	public byte getSecureMessaging() {
		return (byte) ((byte) (super.getCla() & (byte) 0b00001100) >> 2);
	}

	@Override
	public boolean wasSecureMessaging() {
		if(getSecureMessaging() != Iso7816.SM_OFF_OR_NO_INDICATION) {
			return true;
		} else {
			if (getPredecessor() instanceof IsoSecureMessagingCommandApdu) {
				return ((IsoSecureMessagingCommandApdu)getPredecessor()).wasSecureMessaging();
			} else {
				return false;
			}
		}
	}

	@Override
	public CommandApdu rewrapApdu(byte newSmStatus, byte[] commandData) {
		byte [] newApdu = Utils.concatByteArrays(header, commandData);
		newApdu[Iso7816.OFFSET_CLA] = (byte) ((byte) (getCla() & 0b11110011) | newSmStatus << 2);
		return new IsoCompatibleProprietaryCommandApdu(newApdu, this);
	}

}
