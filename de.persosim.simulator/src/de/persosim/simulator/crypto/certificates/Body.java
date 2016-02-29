package de.persosim.simulator.crypto.certificates;

import java.util.List;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * This interface defines the methods that a body of a certificate has to provide.
 * @author cstroh
 *
 */
public interface Body {

	/**
	 * This method returns the certificate profile identifier
	 * @return the certificate profile identifier
	 */
	int getCertificateProfileIdentifier();

	/**
	 * @return the reference to the public key of the certificate authority
	 */
	PublicKeyReference getCertificationAuthorityReference();

	/**
	 * @return the reference to the public key of the certificate holder
	 */
	PublicKeyReference getCertificateHolderReference();

	/**
	 * @return the public key associated with this certificate
	 */
	CvPublicKey getPublicKey();

	/**
	 * @return the extensions this certificate has included
	 */
	List<CertificateExtension> getCertificateExtensions();

	/**
	 * Get the DER-encoded representation of this certificate.
	 * 
	 * @return
	 * 
	 */
	byte[] getEncoded();

	/**
	 * This method returns the TLV encoding of this object
	 * @param withParams include domain parameters in encoding of public key
	 * @return the TLV encoding of this object
	 */
	ConstructedTlvDataObject getTlvEncoding(boolean withParams);

	/**
	 * This method returns the TLV encoding of the certificate extensions
	 * @return the TLV encoding of the certificate extensions
	 */
	ConstructedTlvDataObject getExtensionRepresentation();

}
