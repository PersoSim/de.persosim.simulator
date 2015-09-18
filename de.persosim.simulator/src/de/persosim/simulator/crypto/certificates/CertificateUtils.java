package de.persosim.simulator.crypto.certificates;

import java.util.Calendar;
import java.util.Date;

import de.persosim.simulator.protocols.ta.CertificateHolderAuthorizationTemplate;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.Utils;

public class CertificateUtils implements TlvConstants {
	
	public static ConstructedTlvDataObject encodeFullCertificate(
			int certificateProfileIdentifier,
			PublicKeyReference certificationAuthorityReference,
			ConstructedTlvDataObject publicKeyRepresentation,
			PublicKeyReference certificateHolderReference,
			CertificateHolderAuthorizationTemplate certificateHolderAuthorizationTemplate,
			Date certificateEffectiveDate,
			Date certificateExpirationDate,
			ConstructedTlvDataObject certificateExtensions,
			byte[] signature) {
		
		ConstructedTlvDataObject cvCertificateTlv = encodeFullCertificate(
				certificateProfileIdentifier,
				certificationAuthorityReference,
				publicKeyRepresentation,
				certificateHolderReference,
				certificateHolderAuthorizationTemplate,
				certificateEffectiveDate,
				certificateExpirationDate,
				certificateExtensions,
				signature);
		
		PrimitiveTlvDataObject signatureTlv = new PrimitiveTlvDataObject(TAG_5F37, signature);
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
		
		ConstructedTlvDataObject cvCertificateTlv = new ConstructedTlvDataObject(TAG_7F21);
		ConstructedTlvDataObject certificateBodyTlv = new ConstructedTlvDataObject(TAG_7F4E);
		
		PrimitiveTlvDataObject certificateProfileIdentifierTlv = new PrimitiveTlvDataObject(TAG_5F29, Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(certificateProfileIdentifier)));
		PrimitiveTlvDataObject certificationAuthorityReferenceTlv = new PrimitiveTlvDataObject(TAG_42, certificationAuthorityReference.getBytes());
		ConstructedTlvDataObject publicKeyTlv = publicKeyRepresentation;
		PrimitiveTlvDataObject certificateHolderReferenceTlv = new PrimitiveTlvDataObject(TAG_5F20, certificateHolderReference.getBytes());
		ConstructedTlvDataObject certificateHolderAuthorizationTemplateTlv = encodeCertificateHolderAuthorizationTemplate(certificateHolderAuthorizationTemplate);
		PrimitiveTlvDataObject certificateEffectiveDateTlv = new PrimitiveTlvDataObject(TAG_5F25, encodeDate(certificateEffectiveDate));
		PrimitiveTlvDataObject certificateExpirationDateTlv = new PrimitiveTlvDataObject(TAG_5F24, encodeDate(certificateExpirationDate));
		
		cvCertificateTlv.addTlvDataObject(certificateBodyTlv);
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
		
		return cvCertificateTlv;
	}
	
	public static byte[] encodeDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		year %= 100;
		month++;
		
		byte y2 = (byte) (year%10);
		byte y1 = (byte) ((year-y2)/10);
		
		byte m2 = (byte) (month%10);
		byte m1 = (byte) ((month-m2)/10);
		
		byte d2 = (byte) (day%10);
		byte d1 = (byte) ((day-d2)/10);
		
		return new byte[]{y1, y2, m1, m2, d1, d2};
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
