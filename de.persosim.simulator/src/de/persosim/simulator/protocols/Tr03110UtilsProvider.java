package de.persosim.simulator.protocols;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;

import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * Implementations of this interface are used to modularize the {@link Tr03110Utils} class.
 * 
 * @author mboonk
 *
 */
public interface Tr03110UtilsProvider {

	/**
	 * This method parses a given TLV-encoded public key using the domain
	 * parameters form a given public key. This is usually the key of a trust
	 * point.
	 * 
	 * @param publicKeyData
	 *            the TLV data to parse
	 * @param trustPointPublicKey
	 *            the key containing domain parameters or null if the public key
	 *            data contains domain parameters
	 * @return the public key or null if the key data is not supported
	 * @throws GeneralSecurityException
	 */
	PublicKey parsePublicKey(ConstructedTlvDataObject publicKeyData,
			PublicKey trustPointPublicKey) throws GeneralSecurityException;

	/**
	 * This method creates domain parameters from a given key object.
	 * @param key the key to extract domain parameters from
	 * @return the domain parameters or null if the key does not contain supported domain parameters
	 */
	DomainParameterSet getDomainParameterSetFromKey(Key key);

}
