package de.persosim.simulator.protocols.ca3;

import java.security.PrivateKey;
import java.security.PublicKey;

import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyObject;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.utils.HexString;

/**
 * This object stores the key material needed for the execution of the PSA part
 * of the chip authentication version 3.
 * 
 * @author jgoeke
 *
 */
public class KeyObjectIcc extends KeyObject {
	private PublicKey publicKeyICC;
	private PrivateKey privateKeyICC1;
	private PrivateKey privateKeyICC2;
	private PublicKey groupManagerPublicKey;
	private DomainParameterSet domainParameters;

	public KeyObjectIcc (PrivateKey privateKeyICC1, PrivateKey privateKeyICC2, PublicKey publicKeyICC, PublicKey groupManagerPublicKey, DomainParameterSet domainParameters, KeyIdentifier identifier) {
		this(privateKeyICC1, privateKeyICC2, publicKeyICC, groupManagerPublicKey, domainParameters, identifier, false);
	}
	public KeyObjectIcc (PrivateKey privateKeyICC1, PrivateKey privateKeyICC2, PublicKey publicKeyICC, PublicKey groupManagerPublicKey, DomainParameterSet domainParameters, KeyIdentifier identifier, boolean privilegedOnly) {
		if(publicKeyICC == null) {throw new IllegalArgumentException("ICC public key must not be null");}
		if(groupManagerPublicKey == null) {throw new IllegalArgumentException("group manager public key must not be null");}
		if(domainParameters == null) {throw new IllegalArgumentException("domain parameters must not be null");}
		if(identifier == null) {throw new IllegalArgumentException("key identifier must not be null");}
		
		this.primaryIdentifier = identifier;
		this.privilegedOnly = privilegedOnly;
		this.privateKeyICC1 = privateKeyICC1;
		this.privateKeyICC2 = privateKeyICC2;
		this.publicKeyICC = publicKeyICC;
		this.domainParameters = domainParameters;
		this.groupManagerPublicKey = groupManagerPublicKey;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("ICC key object:");
		sb.append("\nprimary key identifier is  : " + primaryIdentifier);
		sb.append("\npublic key ICC is          : " + HexString.encode(domainParameters.encodePublicKey(publicKeyICC)));
		
		if(privateKeyICC1 != null) {
			sb.append("\nprivate key ICC1 is        : " + HexString.encode(domainParameters.encodePrivateKey(privateKeyICC1)));
		} else{
			sb.append("\nno private key ICC1 available");
		}
		
		if(privateKeyICC2 != null) {
			sb.append("\nprivate key ICC2 is        : " + HexString.encode(domainParameters.encodePrivateKey(privateKeyICC2)));
		} else{
			sb.append("\nno private key ICC2 available");
		}
		
		sb.append("\ngroup manager public key is: " + HexString.encode(domainParameters.encodePublicKey(groupManagerPublicKey)));
		sb.append("\ndomain parameter set is    :\n" + domainParameters);
		sb.append("\nkey is privileged only     : " + privilegedOnly);
		
		return sb.toString();
	}

	public PublicKey getPublicKeyICC() {
		return publicKeyICC;
	}

	public PublicKey getGroupManagerPublicKey() {
		return groupManagerPublicKey;
	}

	public PrivateKey getPrivateKeyICC1() {
		return privateKeyICC1;
	}

	public PrivateKey getPrivateKeyICC2() {
		return privateKeyICC2;
	}

	public DomainParameterSet getPsDomainParameters() {
		return domainParameters;
	}
}
