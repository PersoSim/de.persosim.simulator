package de.persosim.simulator.crypto.certificates;

import java.util.Date;

import de.persosim.simulator.protocols.ta.CertificateHolderAuthorizationTemplate;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.Utils;

public class CertificateUtils implements TlvConstants {
	
	public static ConstructedTlvDataObject encodeCertificate(
			CertificateBody body,
			byte[] signature) {
		
		ConstructedTlvDataObject cvCertificateTlv = encodeCertificate(
				body.getCertificateProfileIdentifier(),
				body.getCertificationAuthorityReference(),
				body.getPublicKey().toTlvDataObject(true),
				body.getCertificateHolderReference(),
				body.getCertificateHolderAuthorizationTemplate(),
				body.getCertificateEffectiveDate(),
				body.getCertificateExpirationDate(),
				body.getExtensionRepresentation(),
				signature);
		
		return cvCertificateTlv;
	}
	
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
	
	public static ConstructedTlvDataObject encodeCertificateBody(
			int certificateProfileIdentifier,
			PublicKeyReference certificationAuthorityReference,
			ConstructedTlvDataObject publicKeyRepresentation,
			PublicKeyReference certificateHolderReference,
			CertificateHolderAuthorizationTemplate certificateHolderAuthorizationTemplate,
			Date certificateEffectiveDate,
			Date certificateExpirationDate,
			ConstructedTlvDataObject certificateExtensions) {
		
		ConstructedTlvDataObject certificateBodyTlv = new ConstructedTlvDataObject(TAG_7F4E);
		
		PrimitiveTlvDataObject certificateProfileIdentifierTlv = new PrimitiveTlvDataObject(TAG_5F29, Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(certificateProfileIdentifier)));
		PrimitiveTlvDataObject certificationAuthorityReferenceTlv = new PrimitiveTlvDataObject(TAG_42, certificationAuthorityReference.getBytes());
		ConstructedTlvDataObject publicKeyTlv = publicKeyRepresentation;
		PrimitiveTlvDataObject certificateHolderReferenceTlv = new PrimitiveTlvDataObject(TAG_5F20, certificateHolderReference.getBytes());
		ConstructedTlvDataObject certificateHolderAuthorizationTemplateTlv = encodeCertificateHolderAuthorizationTemplate(certificateHolderAuthorizationTemplate);
		PrimitiveTlvDataObject certificateEffectiveDateTlv = new PrimitiveTlvDataObject(TAG_5F25, Utils.encodeDate(certificateEffectiveDate));
		PrimitiveTlvDataObject certificateExpirationDateTlv = new PrimitiveTlvDataObject(TAG_5F24, Utils.encodeDate(certificateExpirationDate));
		
		certificateBodyTlv.addTlvDataObject(certificateProfileIdentifierTlv);
		certificateBodyTlv.addTlvDataObject(certificationAuthorityReferenceTlv);
		certificateBodyTlv.addTlvDataObject(publicKeyTlv);
		certificateBodyTlv.addTlvDataObject(certificateHolderReferenceTlv);
		certificateBodyTlv.addTlvDataObject(certificateHolderAuthorizationTemplateTlv);
		certificateBodyTlv.addTlvDataObject(certificateEffectiveDateTlv);
		certificateBodyTlv.addTlvDataObject(certificateExpirationDateTlv);
		
		if(certificateExtensions != null) {
			certificateBodyTlv.addTlvDataObject(certificateExtensions);
		}
		
		return certificateBodyTlv;
	}
	
	public static ConstructedTlvDataObject encodeCertificateHolderAuthorizationTemplate(CertificateHolderAuthorizationTemplate certificateHolderAuthorizationTemplate) {
		ConstructedTlvDataObject certificateHolderAuthorizationTemplateTlv = new ConstructedTlvDataObject(TlvConstants.TAG_7F4C);
		PrimitiveTlvDataObject oidTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_06, certificateHolderAuthorizationTemplate.getObjectIdentifier().toByteArray());
		PrimitiveTlvDataObject authorizationTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_53, certificateHolderAuthorizationTemplate.getRelativeAuthorization().getRepresentation().getAsZeroPaddedBigEndianByteArray());
		certificateHolderAuthorizationTemplateTlv.addTlvDataObject(oidTlv);
		certificateHolderAuthorizationTemplateTlv.addTlvDataObject(authorizationTlv);
		return certificateHolderAuthorizationTemplateTlv;
	}
	
}
