package de.persosim.simulator.exception;

import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public abstract class GeneralException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	protected short statusWord;
	
	public GeneralException(short statusWord, String message) {
		super(message);	
		this.statusWord = statusWord;
	}
	
	public GeneralException(short statusWord) {
		this(statusWord, "");
	}
	
	public short getStatusWord() {
		return this.statusWord;
	}
	
	public String getStatusWordAsString() {
		return HexString.hexifyShort(this.statusWord);
	}
	
	public boolean bearsMessage() {
		return this.getMessage().length() > 0;
	}
	
	public String getTypeOfError() {
		return "general";
	}
}
