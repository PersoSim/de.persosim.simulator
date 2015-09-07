package de.persosim.simulator.apdu;

/**
 * This interface extends {@link CommandApdu} by adding further functionality
 * relevant in the context of interindustry command APDUs represented by this
 * interface.
 * 
 * @author slutters
 * 
 */
public interface InterindustryCommandApdu extends CommandApdu, IsoSecureMessagingCommandApdu {
	
	/**
	 * This method returns true iff chaining is set for this command APDU, otherwise false is returned
	 * @return true iff chaining is set for this command APDU, otherwise false is returned
	 */
	boolean isChaining();
	
	/**
	 * This method returns the channel which is set for this command APDU
	 * @return the channel which is set for this command APDU
	 */
	byte getChannel();

}