package de.persosim.simulator.apdu;

public interface InterIndustryCommandApdu extends CommandApdu, IsoSecureMessagingCommandApdu {

	boolean isChaining();

	byte getChannel();

}