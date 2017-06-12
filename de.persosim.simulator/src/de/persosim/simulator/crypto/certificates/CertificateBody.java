package de.persosim.simulator.crypto.certificates;

import java.security.PublicKey;
import java.util.Date;
import java.util.List;

import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.exception.NotParseableException;
import de.persosim.simulator.protocols.Tr03110Utils;
import de.persosim.simulator.protocols.ta.CertificateHolderAuthorizationTemplate;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;


/**
 * This class implements the body of a card verifiable certificate as described
 * in TR-03110 v2.10 Part 3 Appendix C.
 * 
 * @author mboonk, cstroh
 * 
 */
public class CertificateBody extends ReducedCertificateBody {
	protected CertificateHolderAuthorizationTemplate certificateHolderAuthorizationTemplate;
	protected Date certificateEffectiveDate;
	protected Date certificateExpirationDate;
	
	public CertificateBody(
			int certificateProfileIdentifier,
			PublicKeyReference certificationAuthorityReference,
			CvPublicKey publicKey,
			PublicKeyReference certificateHolderReference,
			CertificateHolderAuthorizationTemplate certificateHolderAuthorizationTemplate,
			Date certificateEffectiveDate,
			Date certificateExpirationDate,
			List<CertificateExtension> certificateExtensions) {
		super(certificateProfileIdentifier, certificationAuthorityReference, publicKey, certificateHolderReference,
				certificateExtensions);
		this.certificateHolderAuthorizationTemplate = certificateHolderAuthorizationTemplate;
		this.certificateEffectiveDate = certificateEffectiveDate;
		this.certificateExpirationDate = certificateExpirationDate;
	}
	
	/**
	 * Create a certificate object from the TLV-encoding using the domain
	 * parameters from the certificate.
	 * @param certificateData as described in TR-03110 V2.10 part 3, C
	 * @throws CertificateNotParseableException
	 */
	public CertificateBody(ConstructedTlvDataObject certificateData) throws CertificateNotParseableException {
		this(certificateData, null);
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
	public CertificateBody(ConstructedTlvDataObject certificateBodyData, PublicKey currentPublicKey) throws CertificateNotParseableException {
		super(certificateBodyData, currentPublicKey);
		
		//Certificate Holder Authorization Template (CHAT)
		certificateHolderAuthorizationTemplate = new CertificateHolderAuthorizationTemplate((ConstructedTlvDataObject) certificateBodyData.getTlvDataObject(TlvConstants.TAG_7F4C));
		
		//Certificate Expiration Date
		//Certificate Effective Date
		try {
			certificateExpirationDate = Tr03110Utils.parseDate(((PrimitiveTlvDataObject) certificateBodyData.getTlvDataObject(TlvConstants.TAG_5F24)).getValueField(), false);
			certificateEffectiveDate = Tr03110Utils.parseDate(((PrimitiveTlvDataObject) certificateBodyData.getTlvDataObject(TlvConstants.TAG_5F25)).getValueField(), false);
		} catch (NotParseableException | IllegalArgumentException e) {
			throw new CertificateNotParseableException("The date could not be parsed: " + e.getMessage());
		}
		
		if (certificateExpirationDate.before(certificateEffectiveDate)){
			throw new CertificateNotParseableException("The certificates expiration date is before the effective date");
		}
	}
	
	/**
	 * This method returns the certificate effective date
	 * @return the certificate effective date
	 */
	public Date getCertificateEffectiveDate() {
		return certificateEffectiveDate;
	}
	
	/**
	 * This method returns the certificate expiration date
	 * @return the certificate expiration date
	 */
	public Date getCertificateExpirationDate() {
		return certificateExpirationDate;
	}
	
	/**
	 * @return the {@link CertificateHolderAuthorizationTemplate} for this
	 *         certificate
	 */
	public CertificateHolderAuthorizationTemplate getCertificateHolderAuthorizationTemplate() {
		return certificateHolderAuthorizationTemplate;
	}

	@Override
	public byte[] getEncoded() {
		return getTlvEncoding(getCertificateRole().includeConditionalElementsInKeyEncoding()).toByteArray();
	}

	@Override
	public ConstructedTlvDataObject getTlvEncoding(boolean withParams) {
		return CertificateUtils.encodeCertificateBody(
				certificateProfileIdentifier,
				certificationAuthorityReference,
				publicKey.toTlvDataObject(withParams),
				certificateHolderReference,
				certificateHolderAuthorizationTemplate,
				certificateEffectiveDate,
				certificateExpirationDate,
				getExtensionRepresentation());
	}
	/**
	 * This method returns the role of this certificate, i.e. either CVCA, DV or
	 * Terminal according to the enums defined by {@link CertificateRole}}
	 * 
	 * @return the role of this certificate
	 */
	public CertificateRole getCertificateRole() {
		return certificateHolderAuthorizationTemplate.getRelativeAuthorization().getRole();
	}
	
}
