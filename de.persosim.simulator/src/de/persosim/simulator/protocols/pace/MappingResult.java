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
public class MappingResult {
	
	// the mapped domain parameters
	protected DomainParameterSet domainParameterSet;
	// the public key pair updated according to the mapped domain parameters
	protected KeyPair keyPair;
	// the response data to be sent (may be empty)
	protected byte[] mappingResponse;
	
	/**
	 * This constructor constructs an object container for mapping results.
	 * @param domainParams the mapped domain parameters
	 * @param keys the public key pair updated according to the mapped domain parameters
	 * @param mappingResponseData the response data to be sent (may be empty)
	 */
	public MappingResult(DomainParameterSet domainParams, KeyPair keys, byte[] mappingResponseData) {
		domainParameterSet = domainParams;
		keyPair = keys;
		
		if(mappingResponseData == null) {
			mappingResponse = new byte[0];
		} else{
			mappingResponse = mappingResponseData;
		}
	}
	
	/**
	 * This constructor constructs an object container for mapping results.
	 * @param domainParams the mapped domain parameters
	 * @param keys the public key pair updated according to the mapped domain parameters
	 */
	public MappingResult(DomainParameterSet domainParams, KeyPair keys) {
		this(domainParams, keys, null);
	}

	public DomainParameterSet getDomainParameterSet() {
		return domainParameterSet;
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public byte[] getMappingResponse() {
		return mappingResponse;
	}
	
}
