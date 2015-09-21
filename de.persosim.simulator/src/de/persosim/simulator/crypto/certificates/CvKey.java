package de.persosim.simulator.crypto.certificates;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This class represents a Key, public or private. CvKey Objects are
 * deliberately designed to always contain all domain parameters. If keys were
 * to be stored without domain parameters these would still be needed to ensure
 * that the values provided are valid key parameters either during object
 * creation or when the object is actually used. As domain parameters are
 * required in any case they can as well be integrated from the beginning.
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
	
	abstract public KeyPairGenerator getKeyPairGenerator(SecureRandom secRandom) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
	
}
