package de.persosim.simulator.crypto.certificates;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This class represents a Key, public or private to be used in the context of CV certificates.
 * 
 * @author slutters
 * 
 */
public abstract class CvKey implements Key {
	
	private static final long serialVersionUID = 1L;
	
	protected CvOid cvOid;
	protected Key key;
	
	public CvKey(CvOid cvOid, Key key) {
		this.cvOid = cvOid;
		this.key = key;
	}
	
	public CvOid getCvOid() {
		return cvOid;
	}

	@Override
	public String getAlgorithm() {
		return key.getAlgorithm();
	}

	@Override
	public byte[] getEncoded() {
		return key.getEncoded();
	}

	@Override
	public String getFormat() {
		return key.getFormat();
	}
	
	/**
	 * This method returns an already initialized {@link KeyPairGenerator}
	 * capable of generating keys usable for creating an object matching this.
	 * 
	 * @param secRandom source of randomness
	 * @return a KeyPairGenerator for creating matching keys
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 */
	abstract public KeyPairGenerator getKeyPairGenerator(SecureRandom secRandom) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
	
}
