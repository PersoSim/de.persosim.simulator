package de.persosim.simulator.cardobjects;

import java.security.KeyPair;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.secstatus.SecStatus;

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
		
	public KeyPairObject(KeyPair keyPair, KeyIdentifier identifier, boolean privilegedOnly) {
		this.primaryIdentifier = identifier;
		this.keyPair = keyPair;
		this.privilegedOnly = privilegedOnly;
	}
	
	public KeyPairObject(KeyPair keyPair, KeyIdentifier identifier) {
		this(keyPair, identifier, false);
	}
	
	public KeyPairObject(KeyPair keyPair, KeyIdentifier identifier, boolean privilegedOnly, CardObjectIdentifier... furtherIdentifiers) {
		this(keyPair, identifier, privilegedOnly);
		
		if(furtherIdentifiers != null) {
			for(CardObjectIdentifier coi: furtherIdentifiers) {
				this.furtherIdentifiers.add(coi);
			}
		}
	}
	
	public KeyPairObject(KeyPair keyPair, KeyIdentifier identifier, CardObjectIdentifier... furtherIdentifiers) {
		this(keyPair, identifier, false, furtherIdentifiers);
	}
	
	public KeyPair getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(KeyPair newKeyPair) throws AccessDeniedException {
		if (SecStatus.checkAccessConditions(getLifeCycleState())) {
			keyPair = newKeyPair;
			return;
		}
		throw new AccessDeniedException("Setting a new keyPair forbidden");
	}
}
