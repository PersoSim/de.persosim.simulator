package de.persosim.simulator.crypto.certificates;

import java.security.PublicKey;
import java.util.Date;
import java.util.List;

import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.ta.CertificateHolderAuthorizationTemplate;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;


/**
 * This class implements card verifiable certificate as described in TR-03110
 * v2.10 Part 3 Appendix C.
 * 
 * @see CertificateBody
 * @author mboonk
 * 
 */
public class CardVerifiableCertificate {
	
	CertificateBody body;
	protected byte[] signature;
	
	public CardVerifiableCertificate(CertificateBody body, byte[] signature) {	
		this.body = body;
		this.signature = signature;
	}
	
	/**
	 * Create a certificate object from the TLV-encoding using the domain
	 * parameters from the certificate.
	 * @param certificateData as described in TR-03110 V2.10 part 3, C
	 * @throws CertificateNotParseableException
	 */
	public CardVerifiableCertificate(ConstructedTlvDataObject certificateData) throws CertificateNotParseableException {
		ConstructedTlvDataObject certificateBodyData = (ConstructedTlvDataObject) certificateData.getTlvDataObject(TlvConstants.TAG_7F4E);
		
		//Body
		body = parseCertificateBody(certificateBodyData);
		
		//Signature
		PrimitiveTlvDataObject signatureData = (PrimitiveTlvDataObject) certificateData.getTlvDataObject(TlvConstants.TAG_5F37);
		signature = signatureData.getValueField();
	}

	/**
	 * Create a certificate object from the TLV-encoding using the domain
	 * parameters from the given public key if the certificate does not contain
	 * them.
	 * 
	 * @param certificateBodyData as described in TR-03110 V2.10 part 3, C
	 * @param currentPublicKey the public key to be used
	 * @throws CertificateNotParseableException
	 */
	public CardVerifiableCertificate(ConstructedTlvDataObject certificateData, PublicKey currentPublicKey) throws CertificateNotParseableException {
		this(certificateData);
		
		getPublicKey().updateKey(currentPublicKey);
	}
	
	/**
	 * This method parses and returns the certificate body
	 * @param certificateBodyData the certificate body to parse
	 * @return the parsed certificate body
	 * @throws CertificateNotParseableException
	 */
	public CertificateBody parseCertificateBody(ConstructedTlvDataObject certificateBodyData) throws CertificateNotParseableException {
		return new CertificateBody(certificateBodyData);
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
	
	/**
	 * @return the {@link CertificateHolderAuthorizationTemplate} for this
	 *         certificate
	 */
	public CertificateHolderAuthorizationTemplate getCertificateHolderAuthorizationTemplate() {
		return body.getCertificateHolderAuthorizationTemplate();
	}

	/**
	 * @return the date from which the certificate is valid
	 */
	public Date getEffectiveDate() {
		return body.getCertificateEffectiveDate();
	}
	
	/**
	 * @return the date form which the certificate is no longer valid
	 */
	public Date getExpirationDate() {
		return body.getCertificateExpirationDate();
	}

	@Override
	public String toString() {
		return "CardVerifiableCertificate [certificationAuthorityReference="
				+ body.getCertificationAuthorityReference()
				+ ", certificateHolderReference=" + body.certificateHolderReference
				+ "]";
	}
	
	/**
	 * Get the DER-encoded representation of this certificate.
	 * 
	 * @return the DER-encoded representation of this certificate
	 * 
	 */
	public ConstructedTlvDataObject getEncoded() {
		ConstructedTlvDataObject encoding = CertificateUtils.encodeCertificate(
				body,
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
	public CertificateBody getBody() {
		return body;
	}
	
	/**
	 * This method returns the signature of this certificate
	 * @return the signature of this certificate
	 */
	public byte[] getSignature() {
		return signature;
	}
	
}
