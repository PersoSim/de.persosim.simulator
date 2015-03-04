package de.persosim.simulator.cardobjects;

import java.util.Collection;

import de.persosim.simulator.crypto.certificates.PublicKeyReference;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.secstatus.SecStatus.SecContext;

/**
 * This interface describes the EF.CVCA as described in TR-03110 v2.10 Part 3
 * Appendix A.6.2.4.
 * 
 * @author mboonk
 * 
 */
public class CvcaFile extends AbstractFile {
	ShortFileIdentifier shortFileIdentifier;

	//XXX MBK maybe instead store references to trust point objects to keep them synched
	PublicKeyReference currentCertificateAuthorityReference;
	PublicKeyReference previousCertificateAuthorityReference;

	Collection<SecCondition> readingConditions;
	Collection<SecCondition> updatingConditions;

	public CvcaFile(FileIdentifier fileIdentifier,
			ShortFileIdentifier shortFileIdentifier,
			Collection<SecCondition> readingConditions,
			Collection<SecCondition> updatingConditions) {
		super(fileIdentifier);
		this.shortFileIdentifier = shortFileIdentifier;
		this.readingConditions = readingConditions;
		this.updatingConditions = updatingConditions;
	}

	/**
	 * Update the CVCA file with a new authority reference
	 * 
	 * @param certificateAuthorityReference
	 * @throws AccessDeniedException
	 *             when writing access is denied because of security conditions
	 */
	public void update(PublicKeyReference certificateAuthorityReference) throws AccessDeniedException {
		for (SecCondition condition : updatingConditions){
			if (condition.check(securityStatus.getCurrentMechanisms(SecContext.APPLICATION, condition.getNeededMechanisms()))){
				previousCertificateAuthorityReference = currentCertificateAuthorityReference;
				currentCertificateAuthorityReference = certificateAuthorityReference;				
				return;
			}
		}
		throw new AccessDeniedException("Updating forbidden");
	}

	/**
	 * @return the most current certificate authority reference
	 * @throws AccessDeniedException
	 */
	public PublicKeyReference getCurrentCertificateAuthorityReference() throws AccessDeniedException {
		for (SecCondition condition : readingConditions){
			if (condition.check(securityStatus.getCurrentMechanisms(SecContext.APPLICATION, condition.getNeededMechanisms()))){
				return currentCertificateAuthorityReference;
			}
		}
		throw new AccessDeniedException("Reading forbidden");
	}

	/**
	 * @return the second certificate authority reference that was set before
	 *         the current one was
	 * @throws AccessDeniedException
	 */
	public PublicKeyReference getPreviousCertificateAuthorityReference() throws AccessDeniedException {
		for (SecCondition condition : readingConditions){
			if (condition.check(securityStatus.getCurrentMechanisms(SecContext.APPLICATION, condition.getNeededMechanisms()))){
				return previousCertificateAuthorityReference;
			}
		}
		throw new AccessDeniedException("Reading forbidden");
	}
}
