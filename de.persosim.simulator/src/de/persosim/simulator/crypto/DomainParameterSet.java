package de.persosim.simulator.crypto;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import de.persosim.simulator.tlv.TlvTag;

/**
 * This interface specifies basic domain parameter sets to be used e.g. with the PACE
 * protocol.
 * 
 * @author slutters
 * 
 */
public interface DomainParameterSet {
	
	/**
	 * This method returns the name of the algorithm used for key agreement.
	 * @return the name of the algorithm used for key agreement
	 */
	public abstract String getKeyAgreementAlgorithm();
	
	/**
	 * This method returns the prime to be used for mapping.
	 * @return the prime to be used for mapping
	 */
	public abstract BigInteger getPrime();
	
	/**
	 * This method returns the group order to be used for mapping.
	 * @return the group order to be used for mapping
	 */
	public abstract BigInteger getOrder();
	
	/**
	 * This method updates the {@link KeySpec} of the provided {@link KeyPair} to match the domain parameters of this object.
	 * The first element of the returned array is the private, the second the public key.
	 * @param keyPair the key pair to be updated
	 * @return the updated {@link KeySpec}s
	 */
	public abstract KeySpec[] updateKeySpec(KeyPair keyPair);
	
	/**
	 * This method reconstructs a public key from its byte array representation and adds the missing domain parameters.
	 * @param raw the raw key material
	 * @return the reconstructed public key
	 */
	public abstract PublicKey reconstructPublicKey(byte[] raw);
	
	/**
	 * This method reconstructs a private key from its byte array representation and adds the missing domain parameters.
	 * @param raw the raw key material
	 * @return the reconstructed private key
	 */
	public abstract PrivateKey reconstructPrivateKey(byte[] raw);
	
	/**
	 * This method returns the minimum encoding of the provided public key.
	 * @param publicKey the public key to be encoded
	 * @return the minimum encoding of the provided public key
	 */
	public abstract byte[] encodePublicKey(PublicKey publicKey);
	
	/**
	 * This method returns the tag used to identify the encoding of the public key in the authentication token input data.
	 * @return the tag used to identify the public key encoding
	 */
	public abstract TlvTag getAuthenticationTokenPublicKeyTag();
	
	/**
	 * This method implements the key compression as described in TR-03110 Part
	 * 3 Appendix A.2.2.3. It does NOT return a recoverable compressed variant
	 * of the key.
	 * 
	 * @param publicKey
	 * @return the compressed key
	 * @throws NoSuchAlgorithmException
	 *             if the needed algorithms are not available
	 */
	public abstract byte[] comp(PublicKey publicKey) throws NoSuchAlgorithmException;
	
	/**
	 * Returns an {@link AlgorithmParameterSpec} representing (a subset of) the data stored in this object.
	 * @return an {@link AlgorithmParameterSpec} representation of this object
	 */
	public abstract AlgorithmParameterSpec getKeySpec();
	
	/**
	 * Returns an {@link AlgorithmParameterSpec} object which is as close to this object as possible.
	 * The object may be used e.g. for key generation.
	 * @return an {@link AlgorithmParameterSpec} object which is as close to this object as possible
	 */
	public abstract AlgorithmParameterSpec getAlgorithmParameterSpec();
	
}
