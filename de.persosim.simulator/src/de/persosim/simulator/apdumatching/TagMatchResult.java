package de.persosim.simulator.apdumatching;


/**
 * @author slutters
 *
 */
public class TagMatchResult extends MatchResult {
	public TagMatchResult(short proposedStatusWord, String additionalInfo) {
		super(proposedStatusWord, additionalInfo);
	}
	
	public TagMatchResult(short proposedStatusWord) {
		super(proposedStatusWord);
	}
	
	public TagMatchResult() {
		super();
	}
}
