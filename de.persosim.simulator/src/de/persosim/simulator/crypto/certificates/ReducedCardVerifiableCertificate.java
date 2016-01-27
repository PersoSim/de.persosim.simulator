package de.persosim.simulator.crypto.certificates;

import java.util.List;

import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;


/**
 * This class implements card verifiable certificate as described in TR-03110
 * v2.10 Part 3 Appendix C.<br>
 * 
 * Its TLV data is structured as follows:<br>
 * 
 *  7F21 (CV Certificate)<br>
 *    |- 7F4E (Certificate Body)<br>
 *    |    |- 5F29 (Certificate Profile identifier)<br>
 *    |    |-   42 (Certification Authority Reference)<br>
 *    |    |- 7F49 (Public Key)<br>
 *    |    |- 5F20 (Certificate Holder Reference)<br>
 *    |    L-   65 (Certificate Extensions)<br>
 *    L- 5F37 (Signature)
 * 
 * @see ReducedCertificateBody
 * @author mboonk, cstroh
 * 
 */
public class ReducedCardVerifiableCertificate {
	
	protected Body body;
	protected byte[] signature;
	
	public ReducedCardVerifiableCertificate(ReducedCertificateBody body, byte[] signature) {	
		this.body = body;
		this.signature = signature;
	}
	
	/**
	 * Create a certificate object from the TLV-encoding using the domain
	 * parameters from the certificate.
	 * @param certificateData as described in TR-03110 V2.10 part 3, C
	 * @throws CertificateNotParseableException
	 */
	public ReducedCardVerifiableCertificate(ConstructedTlvDataObject certificateData) throws CertificateNotParseableException {
		ConstructedTlvDataObject certificateBodyData = (ConstructedTlvDataObject) certificateData.getTlvDataObject(TlvConstants.TAG_7F4E);
		
		//Body
		body = parseCertificateBody(certificateBodyData);
		
		//Signature
		PrimitiveTlvDataObject signatureData = (PrimitiveTlvDataObject) certificateData.getTlvDataObject(TlvConstants.TAG_5F37);
		signature = signatureData.getValueField();
	}
	
	/**
	 * This method parses and returns the certificate body
	 * @param certificateBodyData the certificate body to parse
	 * @return the parsed certificate body
	 * @throws CertificateNotParseableException
	 */
	protected ReducedCertificateBody parseCertificateBody(ConstructedTlvDataObject certificateBodyData) throws CertificateNotParseableException {
		return new ReducedCertificateBody(certificateBodyData);
	}
	
	/**
	 * @return the certificate profile identifier
	 */
	public int getCertificateProfileIdentifier() {
		return body.getCertificateProfileIdentifier();
	}
	
	/**
	 * @return the reference to the public key of the certificate authority
	 */
	public PublicKeyReference getCertificationAuthorityReference() {
		return body.getCertificationAuthorityReference();
	}
	
	/**
	 * @return the reference to the public key of the certificate holder
	 */
	public PublicKeyReference getCertificateHolderReference() {
		return body.getCertificateHolderReference();
	}
	
	/**
	 * @return the public key associated with this certificate
	 */
	public CvPublicKey getPublicKey() {
		return body.getPublicKey();
	}

	@Override
	public String toString() {
		return "CardVerifiableCertificate [certificationAuthorityReference="
				+ body.getCertificationAuthorityReference()
				+ ", certificateHolderReference=" + body.getCertificateHolderReference()
				+ "]";
	}
	
	/**
	 * Get the DER-encoded representation of this certificate.
	 * 
	 * @return the DER-encoded representation of this certificate
	 * 
	 */
	public ConstructedTlvDataObject getEncoded() {
		ConstructedTlvDataObject encoding = CertificateUtils.encodeReducedCertificate(
				((ReducedCertificateBody) body),
				signature);
		
		return encoding;
	}
	
	/**
	 * This method returns the certificate extensions
	 * @return the certificate extensions
	 */
	public List<CertificateExtension> getCertificateExtensions() {
		return body.getCertificateExtensions();
	}
	
	/**
	 * This method returns the certificate body
	 * @return the certificate body
	 */
	public ReducedCertificateBody getBody() {
		return (ReducedCertificateBody) body;
	}
	
	/**
	 * This method returns the signature of this certificate
	 * @return the signature of this certificate
	 */
	public byte[] getSignature() {
		return signature;
	}
	
}
