package de.persosim.simulator.perso;

import java.security.Key;

/**
 * Serializable representation of data required to reconstruct a {@link Key}
 * 
 * @author amay
 *
 */

public class KeyRepresentation {

	String algorithm;
	byte[] encodedKey;

	public KeyRepresentation() {
	}
	
	public KeyRepresentation(String algorithm, byte[] encodedKey) {
		this.algorithm = algorithm;
		this.encodedKey = encodedKey;
	}
	
	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public byte[] getEncodedKey() {
		return encodedKey;
	}

	public void setEncodedKey(byte[] encodedKey) {
		this.encodedKey = encodedKey;
	}

}
