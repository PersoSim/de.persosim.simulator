package de.persosim.simulator.exception;

/**
 * This exception is to be thrown when an update of a trust point does not
 * finish because of restrictions or errors.
 * 
 * @author mboonk
 * 
 */
public class CertificateUpdateException extends Exception {

	public CertificateUpdateException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
