package de.persosim.simulator.jaxb;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.persosim.simulator.crypto.Crypto;

public class PublicKeyAdapter extends XmlAdapter<KeyRepresentation, PublicKey> {

	@Override
	public KeyRepresentation marshal(PublicKey key) {
		return new KeyRepresentation(key.getAlgorithm(), key.getEncoded());
	}

	@Override
	public PublicKey unmarshal(KeyRepresentation representation) {

		X509EncodedKeySpec ks = new X509EncodedKeySpec(representation.getEncodedKey());
		try {
			return KeyFactory.getInstance(representation.getAlgorithm(), Crypto.getCryptoProvider()).
					generatePublic(ks);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException
				| NoSuchProviderException e) {
			throw new RuntimeException(
					"Unable to parse public key data with current crypto configuration");
		}
	}
}