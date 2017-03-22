package de.persosim.simulator.protocols;

import java.security.Key;
import java.security.PublicKey;

import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.certificates.CvPublicKey;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;

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

	
	/**
	 * This method encodes a public key as described in TR03110 Part 3 Appendix D.3
	 * @param oid the {@link Oid} to store in the encoded Key
	 * @param pk the {@link PublicKey} to encode
	 * @param includeConditionalObjects whether to store conditional data
	 * @return tlv data containing the encoded key
	 */
	public TlvDataObjectContainer encodePublicKey(Oid oid, PublicKey pk, boolean includeConditionalObjects);

}
