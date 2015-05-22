package de.persosim.simulator.protocols.pace;

import java.security.KeyPair;

import de.persosim.simulator.crypto.DomainParameterSet;

/**
 * This class represents a container for mapping results.
 * This allows for the mapping to be state less.
 * 
 * @author slutters
 *
 */
public abstract class MappingResult {
	
	// the unmapped domain parameters
	protected DomainParameterSet domainParametersUnmapped;
	// the mapped domain parameters
	protected DomainParameterSet domainParametersMapped;
	// the public key pair updated according to the mapped domain parameters
	protected KeyPair keyPairPiccMapped;
	
	/**
	 * This constructor constructs an object container for mapping results.
	 * @param domainParametersUnmapped the unmapped domain parameters
	 * @param domainParametersMapped the mapped domain parameters
	 * @param keyPairPiccMapped the public key pair updated according to the mapped domain parameters
	 */
	public MappingResult(DomainParameterSet domainParametersUnmapped, DomainParameterSet domainParametersMapped, KeyPair keyPairPiccMapped) {
		this.domainParametersUnmapped = domainParametersUnmapped;
		this.domainParametersMapped = domainParametersMapped;
		
		this.keyPairPiccMapped = keyPairPiccMapped;
	}

	public DomainParameterSet getMappedDomainParameters() {
		return domainParametersMapped;
	}
	
	public DomainParameterSet getUnmappedDomainParameters() {
		return domainParametersUnmapped;
	}

	public KeyPair getKeyPairPiccMapped() {
		return keyPairPiccMapped;
	}

	public abstract byte[] getMappingResponse();
	
}
