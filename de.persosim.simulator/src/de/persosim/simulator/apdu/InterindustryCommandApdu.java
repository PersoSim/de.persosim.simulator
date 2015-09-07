package de.persosim.simulator.apdu;

//FIXME SLS Interfaces need JavaDoc for all methods
public interface InterindustryCommandApdu extends CommandApdu, IsoSecureMessagingCommandApdu {

	boolean isChaining();

	byte getChannel();

}