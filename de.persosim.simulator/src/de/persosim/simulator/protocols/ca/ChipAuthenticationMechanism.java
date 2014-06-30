package de.persosim.simulator.protocols.ca;

import java.security.PublicKey;

import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecurityEvent;

/**
 * This {@link SecMechanism} is used to communicate all useful information
 * collected while executing chip authentication.
 * 
 * @author slutters
 *
 */
public class ChipAuthenticationMechanism implements SecMechanism {
	
	CaOid caOid;
	int keyReference;
	PublicKey uncompressedTerminalEphemeralPublicKey;
	
	public ChipAuthenticationMechanism(CaOid caOid, int keyReference, PublicKey uncompressedPublicKey) {
		this.caOid = caOid;
		this.keyReference = keyReference;
		this.uncompressedTerminalEphemeralPublicKey = uncompressedPublicKey;
	}
	
	public CaOid getCaOid() {
		return caOid;
	}

	public int getKeyReference() {
		return keyReference;
	}

	public PublicKey getUncompressedTerminalEphemeralPublicKey() {
		return uncompressedTerminalEphemeralPublicKey;
	}

	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		return true;
	}

}
