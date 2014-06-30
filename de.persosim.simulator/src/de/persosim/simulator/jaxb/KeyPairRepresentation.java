package de.persosim.simulator.jaxb;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Serializable representation of data required to reconstruct a {@link KeyPair}
 * 
 * @author amay
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyPairRepresentation {

	@XmlElement
	@XmlJavaTypeAdapter(PublicKeyAdapter.class)
	PublicKey pubKey;
	@XmlElement
	@XmlJavaTypeAdapter(PrivateKeyAdapter.class)
	PrivateKey privKey;
	
	public KeyPairRepresentation() {
	}

	public KeyPairRepresentation(PublicKey pubKey, PrivateKey privKey) {
		super();
		this.pubKey = pubKey;
		this.privKey = privKey;
	}

	public PublicKey getPubKey() {
		return pubKey;
	}

	public void setPubKey(PublicKey pubKey) {
		this.pubKey = pubKey;
	}

	public PrivateKey getPrivKey() {
		return privKey;
	}

	public void setPrivKey(PrivateKey privKey) {
		this.privKey = privKey;
	}
	
	

}
