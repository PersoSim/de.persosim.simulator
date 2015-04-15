package de.persosim.simulator.perso;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Serializable representation of data required to reconstruct a {@link KeyPair}
 * 
 * @author amay
 *
 */

public class KeyPairRepresentation {

	PublicKey pubKey;
	PrivateKey privKey;
	
	public KeyPairRepresentation() {
	}

	public KeyPairRepresentation(PublicKey pubKey, PrivateKey privKey) {
		super();
		this.pubKey = pubKey;
		this.privKey = privKey;
	}

	public PublicKey getPubKey() {
		return pubKey;
	}

	public void setPubKey(PublicKey pubKey) {
		this.pubKey = pubKey;
	}

	public PrivateKey getPrivKey() {
		return privKey;
	}

	public void setPrivKey(PrivateKey privKey) {
		this.privKey = privKey;
	}
	
	

}
