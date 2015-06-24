package de.persosim.simulator.cardobjects;

import java.security.KeyPair;

/**
 * This object wraps key objects for storing them in the object store.
 * They can be retrieved from there using their key reference id or additionally associated OIDs.
 * 
 * @author slutters
 *
 */
public class KeyPairObject extends KeyObject {
	protected KeyPair keyPair;
	
	
	public KeyPairObject() {
	}
	
	public KeyPairObject(KeyPair keyPair, KeyIdentifier identifier) {
		this(keyPair, identifier, false);
	}
		
	public KeyPairObject(KeyPair keyPair, KeyIdentifier identifier, boolean privilegedOnly) {
		this.primaryIdentifier = identifier;
		this.keyPair = keyPair;
		this.privilegedOnly = privilegedOnly;
	}
	
	public KeyPair getKeyPair() {
		return keyPair;
	}
}
