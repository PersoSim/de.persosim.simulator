package de.persosim.simulator.crypto.certificates;

import java.util.Date;

import de.persosim.simulator.protocols.ta.CertificateHolderAuthorizationTemplate;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.Utils;


/**
 * This class implements the basic utils usable in the context of certificate operations.
 * 
 * @author slutters
 * 
 */
public class CertificateUtils implements TlvConstants {
	
	/**
	 * This method returns a TLV encoding for a certificate constructed from the provided parameters
	 * @param body the certificate body
	 * @param signature the certificate signature
	 * @param encodeFullKey encode the key including conditional objects
	 * @return the certificate TLV encoding
	 */
	public static ConstructedTlvDataObject encodeCertificate(
			CertificateBody body,
			byte[] signature) {
		
		CertificateRole certificateRole = body.getCertificateRole();
		
		ConstructedTlvDataObject cvCertificateTlv = encodeCertificate(
				body.getCertificateProfileIdentifier(),
				body.getCertificationAuthorityReference(),
				body.getPublicKey().toTlvDataObject(certificateRole.includeConditionalElementsInKeyEncoding()),
				body.getCertificateHolderReference(),
				body.getCertificateHolderAuthorizationTemplate(),
				body.getCertificateEffectiveDate(),
				body.getCertificateExpirationDate(),
				body.getExtensionRepresentation(),
				signature);
		
		return cvCertificateTlv;
	}
	
	/**
	 * This method returns a TLV encoding for a reduced certificate constructed from the provided parameters
	 * @param body the certificate body
	 * @param signature the certificate signature
	 * @return the certificate TLV encoding
	 */
	public static ConstructedTlvDataObject encodeReducedCertificate(
			ReducedCertificateBody body,
			byte[] signature) {
		
		ConstructedTlvDataObject cvCertificateTlv = encodeReducedCertificate(
				body.getCertificateProfileIdentifier(),
				body.getCertificationAuthorityReference(),
				body.getPublicKey().toTlvDataObject(true),
				body.getCertificateHolderReference(),
				body.getExtensionRepresentation(),
				signature);
		
		return cvCertificateTlv;
	}
	
	/**
	 * This method returns the TLV encoding for a certificate constructed from the provided parameters
	 * @param certificateProfileIdentifier the certificate profile identifier
	 * @param certificationAuthorityReference the certification authority reference
	 * @param publicKeyRepresentation the public key representation
	 * @param certificateHolderReference the certificate holder reference
	 * @param certificateHolderAuthorizationTemplate the certificate holder authorization template
	 * @param certificateEffectiveDate the certificate effective date
	 * @param certificateExpirationDate the certificate expiration date
	 * @param certificateExtensions the certificate extensions
	 * @param signature the certificate signature
	 * @return the TLV encoding for a certificate constructed from the provided parameters
	 */
	public static ConstructedTlvDataObject encodeCertificate(
			int certificateProfileIdentifier,
			PublicKeyReference certificationAuthorityReference,
			ConstructedTlvDataObject publicKeyRepresentation,
			PublicKeyReference certificateHolderReference,
			CertificateHolderAuthorizationTemplate certificateHolderAuthorizationTemplate,
			Date certificateEffectiveDate,
			Date certificateExpirationDate,
			ConstructedTlvDataObject certificateExtensions,
			byte[] signature) {
		
		ConstructedTlvDataObject cvCertificateTlv = new ConstructedTlvDataObject(TAG_7F21);
		
		ConstructedTlvDataObject cvCertificateBodyTlv = encodeCertificateBody(
				certificateProfileIdentifier,
				certificationAuthorityReference,
				publicKeyRepresentation,
				certificateHolderReference,
				certificateHolderAuthorizationTemplate,
				certificateEffectiveDate,
				certificateExpirationDate,
				certificateExtensions);
		
		PrimitiveTlvDataObject signatureTlv = new PrimitiveTlvDataObject(TAG_5F37, signature);
		
		cvCertificateTlv.addTlvDataObject(cvCertificateBodyTlv);
		cvCertificateTlv.addTlvDataObject(signatureTlv);
		
		return cvCertificateTlv;
	}
	
	/**
	 * This method returns the TLV encoding for a certificate constructed from the provided parameters
	 * @param certificateProfileIdentifier the certificate profile identifier
	 * @param certificationAuthorityReference the certification authority reference
	 * @param publicKeyRepresentation the public key representation
	 * @param certificateHolderReference the certificate holder reference
	 * @param certificateExtensions the certificate extensions
	 * @param signature the certificate signature
	 * @return the TLV encoding for a certificate constructed from the provided parameters
	 */
	public static ConstructedTlvDataObject encodeReducedCertificate(
			int certificateProfileIdentifier,
			PublicKeyReference certificationAuthorityReference,
			ConstructedTlvDataObject publicKeyRepresentation,
			PublicKeyReference certificateHolderReference,
			ConstructedTlvDataObject certificateExtensions,
			byte[] signature) {
		
		ConstructedTlvDataObject cvCertificateTlv = new ConstructedTlvDataObject(TAG_7F21);
		
		ConstructedTlvDataObject cvCertificateBodyTlv = encodeReducedCertificateBody(
				certificateProfileIdentifier,
				certificationAuthorityReference,
				publicKeyRepresentation,
				certificateHolderReference,
				certificateExtensions);
		
		PrimitiveTlvDataObject signatureTlv = new PrimitiveTlvDataObject(TAG_5F37, signature);
		
		cvCertificateTlv.addTlvDataObject(cvCertificateBodyTlv);
		cvCertificateTlv.addTlvDataObject(signatureTlv);
		
		return cvCertificateTlv;
	}
	
	/**
	 * This method returns the TLV encoding for a certificate body constructed from the provided parameters
	 * @param certificateProfileIdentifier the certificate profile identifier
	 * @param certificationAuthorityReference the certification authority reference
	 * @param publicKeyRepresentation the public key representation
	 * @param certificateHolderReference the certificate holder reference
	 * @param certificateHolderAuthorizationTemplate the certificate holder authorization template
	 * @param certificateEffectiveDate the certificate effective date
	 * @param certificateExpirationDate the certificate expiration date
	 * @param certificateExtensions the certificate extensions
	 * @return the TLV encoding for a certificate constructed from the provided parameters
	 */
	public static ConstructedTlvDataObject encodeCertificateBody(
			int certificateProfileIdentifier,
			PublicKeyReference certificationAuthorityReference,
			ConstructedTlvDataObject publicKeyRepresentation,
			PublicKeyReference certificateHolderReference,
			CertificateHolderAuthorizationTemplate certificateHolderAuthorizationTemplate,
			Date certificateEffectiveDate,
			Date certificateExpirationDate,
			ConstructedTlvDataObject certificateExtensions) {
		
		ConstructedTlvDataObject certificateBodyTlv = encodeBodyUptoCHR(certificateProfileIdentifier,
				certificationAuthorityReference, publicKeyRepresentation, certificateHolderReference);
		
		ConstructedTlvDataObject certificateHolderAuthorizationTemplateTlv = encodeCertificateHolderAuthorizationTemplate(certificateHolderAuthorizationTemplate);
		PrimitiveTlvDataObject certificateEffectiveDateTlv = new PrimitiveTlvDataObject(TAG_5F25, Utils.encodeDate(certificateEffectiveDate));
		PrimitiveTlvDataObject certificateExpirationDateTlv = new PrimitiveTlvDataObject(TAG_5F24, Utils.encodeDate(certificateExpirationDate));

		certificateBodyTlv.addTlvDataObject(certificateHolderAuthorizationTemplateTlv);
		certificateBodyTlv.addTlvDataObject(certificateEffectiveDateTlv);
		certificateBodyTlv.addTlvDataObject(certificateExpirationDateTlv);
		
		addExtensionsToBody(certificateBodyTlv, certificateExtensions);
		
		return certificateBodyTlv;
	}
	
	/**
	 * This method returns the TLV encoding for a certificate body constructed from the provided parameters
	 * @param certificateProfileIdentifier the certificate profile identifier
	 * @param certificationAuthorityReference the certification authority reference
	 * @param publicKeyRepresentation the public key representation
	 * @param certificateHolderReference the certificate holder reference
	 * @param certificateHolderAuthorizationTemplate the certificate holder authorization template
	 * @param certificateEffectiveDate the certificate effective date
	 * @param certificateExpirationDate the certificate expiration date
	 * @param certificateExtensions the certificate extensions
	 * @return the TLV encoding for a certificate constructed from the provided parameters
	 */
	public static ConstructedTlvDataObject encodeReducedCertificateBody(
			int certificateProfileIdentifier,
			PublicKeyReference certificationAuthorityReference,
			ConstructedTlvDataObject publicKeyRepresentation,
			PublicKeyReference certificateHolderReference,
			ConstructedTlvDataObject certificateExtensions) {
		
		ConstructedTlvDataObject certificateBodyTlv = encodeBodyUptoCHR(certificateProfileIdentifier,
				certificationAuthorityReference, publicKeyRepresentation, certificateHolderReference);
		
		addExtensionsToBody(certificateBodyTlv, certificateExtensions);
		
		return certificateBodyTlv;
	}
	
	/**
	 * This method returns the TLV encoding for a certificate body up to the certificate holder reference
	 * due to common parameters of reduced and full body
	 * @param certificateProfileIdentifier the certificate profile identifier
	 * @param certificationAuthorityReference the certification authority reference
	 * @param publicKeyRepresentation the public key representation
	 * @param certificateHolderReference the certificate holder reference
	 * @return
	 */
	private static ConstructedTlvDataObject encodeBodyUptoCHR(int certificateProfileIdentifier,
			PublicKeyReference certificationAuthorityReference,
			ConstructedTlvDataObject publicKeyRepresentation,
			PublicKeyReference certificateHolderReference) {
		ConstructedTlvDataObject certificateBodyTlv = new ConstructedTlvDataObject(TAG_7F4E);
		
		PrimitiveTlvDataObject certificateProfileIdentifierTlv = new PrimitiveTlvDataObject(TAG_5F29, Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(certificateProfileIdentifier)));
		PrimitiveTlvDataObject certificationAuthorityReferenceTlv = new PrimitiveTlvDataObject(TAG_42, certificationAuthorityReference.getBytes());
		ConstructedTlvDataObject publicKeyTlv = publicKeyRepresentation;
		PrimitiveTlvDataObject certificateHolderReferenceTlv = new PrimitiveTlvDataObject(TAG_5F20, certificateHolderReference.getBytes());
		
		certificateBodyTlv.addTlvDataObject(certificateProfileIdentifierTlv);
		certificateBodyTlv.addTlvDataObject(certificationAuthorityReferenceTlv);
		certificateBodyTlv.addTlvDataObject(publicKeyTlv);
		certificateBodyTlv.addTlvDataObject(certificateHolderReferenceTlv);
		
		return certificateBodyTlv;
	}
	
	/**
	 * This method adds certificate extensions to a given TLV encoding of a certificate body 
	 * @param certificateBodyTlv
	 * @param certificateExtensions
	 * @return
	 */
	private static ConstructedTlvDataObject addExtensionsToBody(ConstructedTlvDataObject certificateBodyTlv, ConstructedTlvDataObject certificateExtensions) {
		if(certificateExtensions != null) {
			certificateBodyTlv.addTlvDataObject(certificateExtensions);
		}
		
		return certificateBodyTlv;
	}
	
	/**
	 * This method returns the TLV encoding for a certificate holder authorization template
	 * @param certificateHolderAuthorizationTemplate the certificate holder authorization template to encode
	 * @return the TLV encoding for a certificate holder authorization template
	 */
	public static ConstructedTlvDataObject encodeCertificateHolderAuthorizationTemplate(CertificateHolderAuthorizationTemplate certificateHolderAuthorizationTemplate) {
		ConstructedTlvDataObject certificateHolderAuthorizationTemplateTlv = new ConstructedTlvDataObject(TlvConstants.TAG_7F4C);
		PrimitiveTlvDataObject oidTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_06, certificateHolderAuthorizationTemplate.getObjectIdentifier().toByteArray());
		PrimitiveTlvDataObject authorizationTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_53, certificateHolderAuthorizationTemplate.getRelativeAuthorization().getAuthorization().getAsZeroPaddedBigEndianByteArray());
		certificateHolderAuthorizationTemplateTlv.addTlvDataObject(oidTlv);
		certificateHolderAuthorizationTemplateTlv.addTlvDataObject(authorizationTlv);
		return certificateHolderAuthorizationTemplateTlv;
	}
	
}
