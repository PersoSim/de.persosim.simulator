package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;


/**
 * This class represents the proprietary class APDU that is used for the
 * verify-command as described in TR-03110 v2.10 Part 3 B.11.8. using secure
 * messaging and no chaining.
 * 
 * @author mboonk
 * 
 */
public class TR03110VerifySecureMessagingCommandApdu extends CommandApdu implements
		IsoSecureMessagingCommandApdu {
	
	TR03110VerifySecureMessagingCommandApdu(byte[] apdu, CommandApdu previousCommandApdu) {
		super(apdu, previousCommandApdu);
	}
	
	public TR03110VerifySecureMessagingCommandApdu(byte[] apdu) {
		this(apdu, null);
	}

	@Override
	public byte getSecureMessaging() {
		return (byte) ((byte) (super.getCla() & (byte) 0b00001100) >> 2);
	}

	@Override
	public void setSecureMessaging(byte smStatus) {
		header[Iso7816Lib.OFFSET_CLA] = (byte) ((byte) (getCla() & 0b11110011) | smStatus << 2);
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
