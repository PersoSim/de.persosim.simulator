package de.persosim.simulator.apdumatching;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;

/**
 * XXX provide JavaDoc or remove, this seems to be just a better boolean
 * @author slutters
 *
 */
//FIXME SLS when cleaning up apdumatching package, why not remove this class?
public class MatchResult implements Iso7816 {
	protected short proposedStatusWord;
	protected String additionalInfo;
	
	public MatchResult(short proposedStatusWord, String additionalInfo) {
		this.proposedStatusWord = proposedStatusWord;
		this.additionalInfo = additionalInfo;
	}
	
	public MatchResult(short proposedStatusWord) {
		this(proposedStatusWord, "");
	}
	
	public MatchResult() {
		this(SW_9000_NO_ERROR, "");
	}

	/**
	 * @return the matchResult
	 */
	public boolean isMatch() {
		return !Iso7816Lib.isReportingError(this.proposedStatusWord);
	}

	/**
	 * @return the proposedStatusWord
	 */
	public short getProposedStatusWord() {
		return proposedStatusWord;
	}

	/**
	 * @return the additionalInfo
	 */
	public String getAdditionalInfo() {
		return additionalInfo;
	}
}
