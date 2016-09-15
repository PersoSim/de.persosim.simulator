package de.persosim.simulator.protocols.pace;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.utils.HexString;

/**
 * This class performs the generic, i.e. non key agreement specific parts of generic mapping.
 * 
 * @author slutters
 *
 */
public abstract class GenericMapping implements Mapping {
	
	@Override
	public MappingResult performMapping(DomainParameterSet domainParametersUnmapped, byte[] sNonce, byte[] publicKeyComponentPcd) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeySpecException {
		SecureRandom rng = new SecureRandom();
		
		KeyPair keyPairPiccUnmapped = CryptoUtil.generateKeyPair(domainParametersUnmapped, rng);
		
		System.out.println("keyPairPiccUnmapped private: " + HexString.encode(domainParametersUnmapped.encodePrivateKey(keyPairPiccUnmapped.getPrivate())));
		System.out.println("keyPairPiccUnmapped public : " + HexString.encode(domainParametersUnmapped.encodePublicKey(keyPairPiccUnmapped.getPublic())));
		
		PublicKey publicKeyPcdUnMapped = domainParametersUnmapped.reconstructPublicKey(publicKeyComponentPcd);
		
		byte[] secretPointOfKeyAgreementEncoding = performKeyAgreement(domainParametersUnmapped, keyPairPiccUnmapped.getPrivate(), publicKeyPcdUnMapped);
		
		DomainParameterSet domainParametersMapped = performMappingOfDomainParameters(domainParametersUnmapped, sNonce, secretPointOfKeyAgreementEncoding);
		
//		KeyPair keyPairPiccMapped = CryptoUtil.updateKeyPairToNewDomainParameters(keyPairPiccUnmapped, domainParametersMapped);
		KeyPair keyPairPiccMapped = CryptoUtil.generateKeyPair(domainParametersMapped, rng);
		
		System.out.println("keyPairPiccMapped private  : " + HexString.encode(domainParametersMapped.encodePrivateKey(keyPairPiccMapped.getPrivate())));
		System.out.println("keyPairPiccMapped public   : " + HexString.encode(domainParametersMapped.encodePublicKey(keyPairPiccMapped.getPublic())));
		
		return new MappingResultGm(domainParametersUnmapped, domainParametersMapped, keyPairPiccUnmapped, keyPairPiccMapped);
	}
	
	/**
	 * This method performs a key agreement specified by the implementing class.
	 * 
	 * @param domainParameters the common domain parameters used by the provided keys
	 * @param privKeyPicc the private key
	 * @param pubKeyPcd the public key
	 * @return the result of the key agreement
	 */
	public abstract byte[] performKeyAgreement(DomainParameterSet domainParameters, PrivateKey privKeyPicc, PublicKey pubKeyPcd);
	
	@Override
	public String getMappingName() {
		return "Generic Mapping";
	}
	
}
