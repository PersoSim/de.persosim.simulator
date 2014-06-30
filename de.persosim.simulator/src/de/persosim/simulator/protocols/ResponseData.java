package de.persosim.simulator.protocols;

/**
 * This class is a container that combines a status word with a detailed note
 * why this status word has been selected or what it is to indicate if the
 * status word itself is ambiguous.
 * 
 * If additionally to a status word there is response data to be transmitted
 * this can be achieved by extending this class to also store this data. Both
 * classes, i.e. this and the extended one could offer a function
 * getResponseApdu(). This class would generate a response APDU solely with the
 * status word while the extending class would also incorporate the additional
 * data. The method getStatusWord may become obsolete then.
 * 
 * @author slutters
 */
public class ResponseData {
	
	protected short statusWord;
	protected String response;
	
	public ResponseData(short sw, String note) {
		statusWord = sw;
		response = note;
	}

	public short getStatusWord() {
		return statusWord;
	}

	public String getResponse() {
		return response;
	}
	
}
