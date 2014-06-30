package de.persosim.simulator.utils;

/**
 * Instances of this interface are recognized by the system as source of
 * different kinds of information (e.g. log messages, processing data or
 * UpdatePropagations)
 * 
 * @author amay
 * 
 */
public interface InfoSource {

	/**
	 * Provide a user readable string that allows a user to identify the source
	 * of a message. Currently no further restrictions are defined but maybe
	 * later the exact format of the returned String might be specified in more
	 * detail.
	 * 
	 * @return user readable String identifying the implementing Class as source
	 */
	public String getIDString();

}
