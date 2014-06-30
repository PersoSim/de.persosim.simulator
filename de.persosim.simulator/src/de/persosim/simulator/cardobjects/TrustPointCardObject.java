package de.persosim.simulator.cardobjects;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.crypto.certificates.PublicKeyReference;
import de.persosim.simulator.exception.CertificateUpdateException;

/**
 * This describes a trust point consisting of 2 public keys stored on the card.
 * Implementations will be delivering the root certificates to check chains of
 * certificates.
 * 
 * @author mboonk
 * 
 */
@XmlRootElement
public class TrustPointCardObject extends AbstractCardObject {
	
	//FIXME MBK why are the publicKeyReferences duplicated here? They are accessible through the certificates
	@XmlElement
	PublicKeyReference currentPublicKeyReference;
	@XmlElement
	PublicKeyReference previousPublicKeyReference;
	
	@XmlElement
	CardVerifiableCertificate currentCertificate;
	@XmlElement
	CardVerifiableCertificate previousCertificate;
	
	@XmlElement
	TrustPointIdentifier identifier;

	
	public TrustPointCardObject() {
	}

	public TrustPointCardObject(TrustPointIdentifier identifier,
			PublicKeyReference currentReference,
			CardVerifiableCertificate currentCertificate) {
		this.identifier = identifier;
		this.currentCertificate = currentCertificate;
		this.currentPublicKeyReference = currentReference;
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = new HashSet<>();
		result.add(identifier);
		return result;
	}

	/**
	 * @return the reference, that binds a holder to this trustpoints current
	 *         certificate
	 */
	public PublicKeyReference getCurrentPublicKeyReference() {
		return currentPublicKeyReference;
	}

	/**
	 * @return the current certificate that defines this trustpoint
	 */
	public CardVerifiableCertificate getCurrentCertificate() {
		return currentCertificate;
	}

	/**
	 * @return the reference, that binds a holder to this trustpoints previous
	 *         certificate
	 */
	public PublicKeyReference getPreviousPublicKeyReference() {
		return previousPublicKeyReference;
	}

	/**
	 * @return the previous certificate that defines this trustpoint
	 */
	public CardVerifiableCertificate getPreviousCertificate() {
		return previousCertificate;
	}

	/**
	 * Update the trustpoint using a new certificate.
	 * 
	 * XXX MBK add documentation
	 * 
	 * @param newReference
	 * @param newCertificate
	 * @throws CertificateUpdateException 
	 */
	public void updateTrustpoint(PublicKeyReference newReference,
			CardVerifiableCertificate newCertificate) throws CertificateUpdateException {
		//XXX MBK check access rights

		// XXX MBK TR-03105 here should be a test for the CHR (TR-03110 v2.10 Part 2 A.6.2.1)
		//if (previousCertificate != null || !newCertificate.getCertificateHolderReference().equals(currentCertificate.getCertificateHolderReference())){
		//	throw new CertificateUpdateException("The old holder reference does not fit the new one");
		//}
		
		previousCertificate = currentCertificate;
		previousPublicKeyReference = currentPublicKeyReference;
		currentCertificate = newCertificate;
		currentPublicKeyReference = newReference;
	}

}
