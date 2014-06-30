package de.persosim.simulator.apdumatching;


/**
 * @author slutters
 *
 */
public class ApduMatchResult extends MatchResult {
	public ApduMatchResult(short proposedStatusWord, String additionalInfo) {
		super(proposedStatusWord, additionalInfo);
	}
	
	public ApduMatchResult(short proposedStatusWord) {
		super(proposedStatusWord);
	}
	
	public ApduMatchResult() {
		super();
	}
}
