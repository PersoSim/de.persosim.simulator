package de.persosim.simulator.crypto.certificates;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;

import de.persosim.simulator.exception.CarParameterInvalidException;
import de.persosim.simulator.tlv.TlvDataObject;

/**
 * This class represents a public key reference as described in TR-03110 v2.10
 * Part 3 A.6.1.
 * 
 * @author mboonk
 * 
 */
public class PublicKeyReference {

	/**
	 * 2 characters long ISO 3166-1 ALPHA-2 encoded code
	 */
	@XmlAttribute
	String countryCode = "";

	/**
	 * up to 9 characters long
	 */
	@XmlAttribute
	String holderMnemonic = "";

	/**
	 * 5 characters long
	 */
	@XmlAttribute
	String sequenceNumber = "";

	public PublicKeyReference() {
	}
	
	public PublicKeyReference(TlvDataObject publicKeyReferenceData) throws CarParameterInvalidException{
		byte [] referenceData = publicKeyReferenceData.getValueField();
		try{
			countryCode = new String(Arrays.copyOfRange(referenceData, 0, 2));
			holderMnemonic = new String(Arrays.copyOfRange(referenceData, 2, referenceData.length - 5));
			sequenceNumber = new String(Arrays.copyOfRange(referenceData, referenceData.length - 5, referenceData.length));
		} catch (ArrayIndexOutOfBoundsException e){
			throw new CarParameterInvalidException("Invalid CAR encoding");
		}
	}
	
	public PublicKeyReference(String countryCode, String holderMnemonic,
			String sequenceNumber) throws CarParameterInvalidException {
		super();
		if (countryCode.length() != 2) {
			throw new CarParameterInvalidException("Country code invalid");
		}
		if (holderMnemonic.length() > 9 || holderMnemonic.length() < 0) {
			throw new CarParameterInvalidException("Holder mnemonic invalid");
		}
		if (sequenceNumber.length() != 5) {
			throw new CarParameterInvalidException("Sequence number invalid");
		}

		this.countryCode = countryCode;
		this.holderMnemonic = holderMnemonic;
		this.sequenceNumber = sequenceNumber;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getHolderMnemonic() {
		return holderMnemonic;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * @return the byte array representation of this public key reference
	 */
	public byte[] getBytes() {
		return (countryCode + holderMnemonic + sequenceNumber).getBytes();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((countryCode == null) ? 0 : countryCode.hashCode());
		result = prime * result
				+ ((holderMnemonic == null) ? 0 : holderMnemonic.hashCode());
		result = prime * result
				+ ((sequenceNumber == null) ? 0 : sequenceNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PublicKeyReference other = (PublicKeyReference) obj;
		if (countryCode == null) {
			if (other.countryCode != null)
				return false;
		} else if (!countryCode.equals(other.countryCode))
			return false;
		if (holderMnemonic == null) {
			if (other.holderMnemonic != null)
				return false;
		} else if (!holderMnemonic.equals(other.holderMnemonic))
			return false;
		if (sequenceNumber == null) {
			if (other.sequenceNumber != null)
				return false;
		} else if (!sequenceNumber.equals(other.sequenceNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return toString("[", "|", "]");
	}
	
	public String toUnformattedString() {
		return toString("", "", "");
	}
	
	public String toString(String deliminatorOpen, String separator, String deliminatorClose) {
		return deliminatorOpen + getCountryCode() + separator + getHolderMnemonic() + separator + getSequenceNumber() + deliminatorClose;
	}
	
}
