package de.persosim.simulator.cardobjects;

import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.CertificateUpdateException;

/**
 * This describes a trust point consisting of 2 public keys stored on the card.
 * Implementations will be delivering the root certificates to check chains of
 * certificates.
 * 
 * @author mboonk
 * 
 */
public class TrustPointCardObject extends AbstractCardObject {
	
	CardVerifiableCertificate currentCertificate;
	CardVerifiableCertificate previousCertificate;
	
	TrustPointIdentifier identifier;

	public TrustPointCardObject(TrustPointIdentifier identifier,
			CardVerifiableCertificate currentCertificate) {
		this.identifier = identifier;
		this.currentCertificate = currentCertificate;
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = new HashSet<>();
		result.add(identifier);
		return result;
	}

	/**
	 * @return the current certificate that defines this trustpoint
	 */
	public CardVerifiableCertificate getCurrentCertificate() {
		return currentCertificate;
	}

	/**
	 * @return the previous certificate that defines this trustpoint or null if none set
	 */
	public CardVerifiableCertificate getPreviousCertificate() {
		return previousCertificate;
	}

	/**
	 * Update the trustpoint using a new certificate. This method moves the
	 * current certificate to the previous position and sets the given input as
	 * the new current certificate. Due to the certificate scheduling described
	 * in TR-03110 v2.10 Part 3 2.4 holding two certificates provides for
	 * seamless certificate roll-over.
	 * 
	 * @param newReference
	 * @param newCertificate
	 * @throws CertificateUpdateException
	 */
	public void updateTrustpoint(CardVerifiableCertificate newCertificate) throws CertificateUpdateException {
		//XXX MBK check access rights

		// XXX MBK TR-03105 here should be a test for the CHR (TR-03110 v2.10 Part 2 A.6.2.1)
		//if (previousCertificate != null || !newCertificate.getCertificateHolderReference().equals(currentCertificate.getCertificateHolderReference())){
		//	throw new CertificateUpdateException("The old holder reference does not fit the new one");
		//}
		
		previousCertificate = currentCertificate;
		currentCertificate = newCertificate;
	}
	
	/**
	 * Clears the content of this object, e.g. removes all certificates. This is
	 * only possible if the {@link Iso7816LifeCycleState} allows this
	 * modification.
	 */
	public void clear(){
		if (CardObjectUtils.checkAccessConditions(getLifeCycleState())){
			previousCertificate = null;
			currentCertificate = null;
		}
	}

}
