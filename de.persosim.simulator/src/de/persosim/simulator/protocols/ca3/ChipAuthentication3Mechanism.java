package de.persosim.simulator.protocols.ca3;

import java.security.KeyPair;
import java.security.PublicKey;

import de.persosim.simulator.protocols.ca.CaOid;
import de.persosim.simulator.protocols.ca.ChipAuthenticationMechanism;
import de.persosim.simulator.utils.Serialized;
import de.persosim.simulator.utils.Serializer;

public class ChipAuthentication3Mechanism extends ChipAuthenticationMechanism {
	
	private Serialized<KeyPair> ephemeralKeyPairPicc;
	
	public ChipAuthentication3Mechanism(CaOid caOid, int keyReference, PublicKey uncompressedPublicKey, KeyPair ephemeralKeyPairPicc) {
		super(caOid, keyReference, uncompressedPublicKey);
		this.ephemeralKeyPairPicc = Serializer.serialize(ephemeralKeyPairPicc);
	}

	public KeyPair getEphemeralKeyPairPicc() {
		return Serializer.deserialize(ephemeralKeyPairPicc);
	}

}
