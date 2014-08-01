package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816;


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

	private byte sm;
	
	TR03110VerifySecureMessagingCommandApdu(byte[] apdu, CommandApdu previousCommandApdu) {
		super(apdu, previousCommandApdu);
		sm = (byte) ((byte) (super.getCla() & (byte) 0b00001100) >> 2);
	}
	
	@Override
	public byte getCla() {
		// proprietary class, no chaining
		byte cla = (byte) 0b10000000;
		// first interindustry style secure messaging bits
		cla = (byte) (cla | (sm << 2));
		return cla; 
	}
	
	@Override
	public byte getSecureMessaging() {
		return sm;
	}

	@Override
	public void setSecureMessaging(byte smStatus) {
		sm = smStatus;
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
