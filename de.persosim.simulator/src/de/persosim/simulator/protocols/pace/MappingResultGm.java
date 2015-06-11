package de.persosim.simulator.protocols.pace;

import java.security.KeyPair;

import de.persosim.simulator.crypto.DomainParameterSet;

public class MappingResultGm extends MappingResult {
	
	// the public key pair according to the unmapped domain parameters
	protected KeyPair keyPairPiccUnmapped;
	
	public MappingResultGm(DomainParameterSet domainParametersUnmapped, DomainParameterSet domainParametersMapped, KeyPair keyPairPiccUnmapped, KeyPair keyPairPiccMapped) {
		super(domainParametersUnmapped, domainParametersMapped, keyPairPiccMapped);
		this.keyPairPiccUnmapped = keyPairPiccUnmapped;
	}
	
	public KeyPair getKeyPairPiccUnmapped() {
		return keyPairPiccUnmapped;
	}
	
	@Override
	public byte[] getMappingResponse() {
		return domainParametersUnmapped.encodePublicKey(keyPairPiccUnmapped.getPublic());
	}

}
