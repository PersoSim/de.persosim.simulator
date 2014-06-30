package de.persosim.simulator.jaxb;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.persosim.simulator.crypto.Crypto;

/**
 * @see XmlAdapter
 * @see PrivateKey
 * @author amay
 *
 */
public class PrivateKeyAdapter extends XmlAdapter<KeyRepresentation, PrivateKey> {

	@Override
	public KeyRepresentation marshal(PrivateKey key) {
		if (key == null) return null;
		return new KeyRepresentation(key.getAlgorithm(), key.getEncoded());
	}

	@Override
	public PrivateKey unmarshal(KeyRepresentation representation) {

		PKCS8EncodedKeySpec  ks = new PKCS8EncodedKeySpec (representation.getEncodedKey());
		try {
			PrivateKey pk = KeyFactory.getInstance(representation.getAlgorithm(), Crypto.getCryptoProvider()).
					generatePrivate(ks);
			return pk;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException
				| NoSuchProviderException e) {
			throw new RuntimeException(
					"Unable to parse private key data with current crypto configuration");
		}
	}
}