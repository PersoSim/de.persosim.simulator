package de.persosim.simulator.exception;

import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;

/**
 * This exception is thrown when unrecoverable errors occur while parsing a
 * {@link CardVerifiableCertificate} encoding.
 * 
 * @author mboonk
 * 
 */
public class CertificateNotParseableException extends Exception {

	private static final long serialVersionUID = 1L;

	public CertificateNotParseableException(String message) {
		super(message);
	}

}
