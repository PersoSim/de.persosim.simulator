package de.persosim.simulator.protocols;

import java.security.Key;

import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.certificates.CvPublicKey;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * Implementations of this interface are used to modularize the {@link Tr03110Utils} class.
 * 
 * @author mboonk
 *
 */
public interface Tr03110UtilsProvider {

	/**
	 * This method creates domain parameters from a given key object.
	 * @param key the key to extract domain parameters from
	 * @return the domain parameters or null if the key does not contain supported domain parameters
	 */
	public DomainParameterSet getDomainParameterSetFromKey(Key key);
	
	/**
	 * This method parses a public key as encoded within a CV certificate
	 * @param publicKeyData the encoding of the public key
	 * @return a public key object matching the provided encoding
	 */
	public CvPublicKey parseCvPublicKey(ConstructedTlvDataObject publicKeyData);

}
