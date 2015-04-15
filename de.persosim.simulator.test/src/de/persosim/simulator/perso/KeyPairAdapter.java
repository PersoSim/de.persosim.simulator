package de.persosim.simulator.perso;

import java.security.KeyPair;

import javax.xml.bind.annotation.adapters.XmlAdapter;
/**
 * @see XmlAdapter
 * @see KeyPair
 * @author amay
 *
 */
public class KeyPairAdapter extends XmlAdapter<KeyPairRepresentation, KeyPair> {

	@Override
	public KeyPairRepresentation marshal(KeyPair keyPair) {
		return new KeyPairRepresentation(keyPair.getPublic(), keyPair.getPrivate());
	}

	@Override
	public KeyPair unmarshal(KeyPairRepresentation representation) {
		return new KeyPair(representation.getPubKey(), representation.getPrivKey());
	}
}