package de.persosim.simulator.protocols.ca3;

import java.security.InvalidParameterException;

import de.persosim.simulator.protocols.GenericOid;

/**
 * This class implements functionalities for OIDs used in protocols derived from the {@link PsProtocol}.
 */
public abstract class PsOid extends GenericOid implements Ps {
	
	protected String idString;
	
	/**
	 * This constructor constructs a {@link PsOid} based on a byte array representation of a generic PS OID.
	 * @param oidByteArray the byte array representation of a PS OID
	 */
	public PsOid(byte[] byteArrayRepresentation) {
		super(byteArrayRepresentation);
	}
	
	/**
	 * This method returns the message digest identifier as byte.
	 * @return the message digest identifier as byte
	 */
	public byte getMessageDigestIdentifier() {
		return this.oidByteArray[10];
	}
	
	/**
	 * This method returns the name of the message digest as indicated by the OID.
	 * @return the name of the message digest as indicated by the OID
	 */	
	public String getMessageDigestName() {
		switch (this.getMessageDigestIdentifier()) {
		case Psa.SHA_256:
			return "SHA-256";
		default:
			throw new NullPointerException("no message digest specified");
		}
	}
	
	/**
	 * This method returns the size of the message digest.
	 * @return the SIZE of the message digest
	 */	
	public int getMessageDigestSize(){
		switch (this.getMessageDigestIdentifier()) {
		case Psa.SHA_256:
			return 32;
		default:
			throw new NullPointerException("no message digest specified");
		}
	}
	
	/**
	 * This method returns the OID's byte indicating the key agreement
	 * @return the OID's byte indicating the key agreement
	 */
	public byte getKeyAgreementAsByte() {
		return this.oidByteArray[9];
	}
	
	
	/**
	 * This method returns the name of the key agreement.
	 * @return the name of the key agreement
	 */	
	public String getKeyAgreementName() {
		switch (this.getKeyAgreementAsByte()) {
		case Psa.ECDH:
			return "ECDH";
		default:
			throw new InvalidParameterException("no or invalid key agreement selected");
		}
	}
	
}
