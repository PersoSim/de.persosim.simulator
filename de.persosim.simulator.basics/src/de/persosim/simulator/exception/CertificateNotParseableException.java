package de.persosim.simulator.exception;

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

	public CertificateNotParseableException(String message, Exception e) {
		super(message, e);
	}

}
