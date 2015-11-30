package de.persosim.simulator.protocols;

/**
 * This interface describes the generic functionalities to be implemented by any OID class.
 * 
 * @author slutters
 *
 */
public interface Oid {
	/**
	 * This method returns the byte[] representation of this OID
	 * @return the byte[] representation of this OID
	 */
	public byte[] toByteArray();
	
	/**
	 * This method performs a partial matching of the provided parameters.
	 * If the byte[] representation of this OID starts with the byte[] provided the method will return true, false otherwise
	 * @param oidPrefix the OID prefix to match
	 * @return true iff matching, false otherwise
	 */
	public boolean startsWithPrefix(byte[] oidPrefix);
	
}
