package de.persosim.simulator.protocols.ca;

import java.security.PublicKey;

import de.persosim.simulator.secstatus.AbstractSecMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecurityEvent;
import de.persosim.simulator.utils.Serialized;
import de.persosim.simulator.utils.Serializer;

/**
 * This {@link SecMechanism} is used to communicate all useful information
 * collected while executing chip authentication.
 * 
 * @author slutters
 *
 */
public class ChipAuthenticationMechanism extends AbstractSecMechanism {
	
	CaOid caOid;
	int keyReference;
	Serialized<PublicKey> uncompressedTerminalEphemeralPublicKey;
	
	public ChipAuthenticationMechanism(CaOid caOid, int keyReference, PublicKey uncompressedPublicKey) {
		this.caOid = caOid;
		this.keyReference = keyReference;
		this.uncompressedTerminalEphemeralPublicKey = Serializer.serialize(uncompressedPublicKey);
	}
	
	public CaOid getCaOid() {
		return caOid;
	}

	public int getKeyReference() {
		return keyReference;
	}

	public PublicKey getUncompressedTerminalEphemeralPublicKey() {
		return Serializer.deserialize(uncompressedTerminalEphemeralPublicKey);
	}

	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		return true;
	}

}
