package de.persosim.simulator.jaxb;

import java.security.Key;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Serializable representation of data required to reconstruct a {@link Key}
 * 
 * @author amay
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyRepresentation {

	@XmlAttribute
	String algorithm;
	
	@XmlValue
	@XmlJavaTypeAdapter(HexBinaryAdapter.class)
	byte[] encodedKey;

	public KeyRepresentation() {
	}
	
	public KeyRepresentation(String algorithm, byte[] encodedKey) {
		this.algorithm = algorithm;
		this.encodedKey = encodedKey;
	}
	
	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public byte[] getEncodedKey() {
		return encodedKey;
	}

	public void setEncodedKey(byte[] encodedKey) {
		this.encodedKey = encodedKey;
	}

}
