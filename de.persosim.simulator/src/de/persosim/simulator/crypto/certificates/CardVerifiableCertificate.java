package de.persosim.simulator.crypto.certificates;

import java.util.Date;

import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.ta.CertificateHolderAuthorizationTemplate;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;


/**
 * This class implements card verifiable certificate as described in TR-03110
 * v2.10 Part 3 Appendix C.
 * 
 * Its TLV data is structured as follows:
 * 
 *  7F21 (CV Certificate)
 *    |- 7F4E (Certificate Body)
 *    |    |- 5F29 (Certificate Profile identifier)
 *    |    |-   42 (Certification Authority Reference)
 *    |    |- 7F49 (Public Key)
 *    |    |- 5F20 (Certificate Holder Reference)
 *    |    |- 7F4C (Certificate Holder Authorization Template)
 *    |    |- 5F25 (Certificate Effective Date)
 *    |    |- 5F24 (Certificate Expiration Date)
 *    |    L-   65 (Certificate Extensions)
 *    L- 5F37 (Signature)
 * 
 * @see CertificateBody
 * @author mboonk, cstroh
 * 
 */
public class CardVerifiableCertificate extends ReducedCardVerifiableCertificate{
	
	
	public CardVerifiableCertificate(CertificateBody body, byte[] signature) {	
		super(body, signature);
	}
	
	/**
	 * Create a certificate object from the TLV-encoding using the domain
	 * parameters from the certificate.
	 * @param certificateData as described in TR-03110 V2.10 part 3, C
	 * @throws CertificateNotParseableException
	 */
	public CardVerifiableCertificate(ConstructedTlvDataObject certificateData) throws CertificateNotParseableException {
		super(certificateData);
	}

		
	@Override
	public CertificateBody parseCertificateBody(ConstructedTlvDataObject certificateBodyData) throws CertificateNotParseableException {
		return new CertificateBody(certificateBodyData);
	}
	
	/**
	 * @return the {@link CertificateHolderAuthorizationTemplate} for this
	 *         certificate
	 */
	public CertificateHolderAuthorizationTemplate getCertificateHolderAuthorizationTemplate() {
		return ((CertificateBody) body).getCertificateHolderAuthorizationTemplate();
	}

	/**
	 * @return the date from which the certificate is valid
	 */
	public Date getEffectiveDate() {
		return ((CertificateBody) body).getCertificateEffectiveDate();
	}
	
	/**
	 * @return the date form which the certificate is no longer valid
	 */
	public Date getExpirationDate() {
		return ((CertificateBody) body).getCertificateExpirationDate();
	}
	
	/**
	 * This method returns the role of this certificate, i.e. either CVCA, DV or
	 * Terminal according to the enums defined by {@link CertificateRole}}
	 * 
	 * @return the role of this certificate
	 */
	public CertificateRole getCertificateRole() {
		return ((CertificateBody) body).getCertificateRole();
	}
	
	@Override
	public ConstructedTlvDataObject getEncoded() {
		ConstructedTlvDataObject encoding = CertificateUtils.encodeCertificate(
				((CertificateBody) body),
				signature);
		
		return encoding;
	}
	
	@Override
	public CertificateBody getBody() {
		return (CertificateBody) body;
	}
	
}
