package de.persosim.simulator.protocols.ta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.crypto.certificates.CertificateExtension;
import de.persosim.simulator.crypto.certificates.PublicKeyReference;

public class TestCardVerifiableCertificate extends CardVerifiableCertificate {

	public TestCardVerifiableCertificate() {
		super(null, new byte[] {});
	}

	public CertificateHolderAuthorizationTemplate chat;
	public Date effectiveDate;
	public Date expirationDate;
	public PublicKeyReference chr;
	public ArrayList<CertificateExtension> extensions;
	
	@Override
	public CertificateHolderAuthorizationTemplate getCertificateHolderAuthorizationTemplate() {
		if (chat != null) return chat;
		return super.getCertificateHolderAuthorizationTemplate();
	}
	
	@Override
	public Date getEffectiveDate() {
		if (effectiveDate != null) return effectiveDate;
		return super.getEffectiveDate();
	}
	
	@Override
	public Date getExpirationDate() {
		if (expirationDate != null) return expirationDate;
		return super.getExpirationDate();
	}
	
	@Override
	public PublicKeyReference getCertificateHolderReference() {
		if (chr != null) return chr;
		return super.getCertificateHolderReference();
	}
	
	@Override
	public List<CertificateExtension> getCertificateExtensions() {
		if (extensions != null) return extensions;
		return super.getCertificateExtensions();
	}

}
