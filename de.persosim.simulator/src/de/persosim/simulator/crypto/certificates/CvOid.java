package de.persosim.simulator.crypto.certificates;

import java.security.Signature;

import de.persosim.simulator.protocols.GenericOid;

/**
 * This interface can be implemented by OID classes to indicate their capability
 * of being used for public private key signing and verifying operations in the
 * context of CV certificates.
 * 
 * @author slutters
 * 
 */
public interface CvOid extends GenericOid {
	
	/**
	 * This method returns the signature algorithm suitable e.g. for
	 * instantiating a {@link Signature} object for generating or verifying
	 * signatures.
	 * 
	 * @return a signature algorithm
	 */
	public String getSignatureString();
	
}
