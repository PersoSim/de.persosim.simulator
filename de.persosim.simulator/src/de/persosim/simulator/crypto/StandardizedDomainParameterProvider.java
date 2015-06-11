package de.persosim.simulator.crypto;

import java.util.Collection;

/**
 * This interface is used to modularize the standard domain parameter
 * implementation. It provides functions that are used to get and use domain
 * parameters.
 * 
 * @author mboonk
 *
 */
public interface StandardizedDomainParameterProvider {

	/**
	 * This returns the collection of domain parameter IDs supported by the
	 * implementation
	 * 
	 * @return the IDs as Integer
	 */
	Collection<Integer> getSupportedDomainParameters();

	/**
	 * This method provides domain parameters for a given ID
	 * 
	 * @param id
	 *            the ID of the domain parameter set
	 * @return the domain parameter set or null if not supported
	 */
	DomainParameterSet getDomainParameterSet(int id);

	/**
	 * This method provides a mapping between the encoded domain parameters and
	 * their integer ID.
	 * 
	 * @param algorithmId
	 *            the encoded domain parameters as a hexadecimal string
	 * @return the domain parameter id or null if not supported
	 */
	Integer getSimplifiedAlgorithm(String algorithmId);
}
