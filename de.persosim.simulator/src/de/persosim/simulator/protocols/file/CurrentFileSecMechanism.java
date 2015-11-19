package de.persosim.simulator.protocols.file;

import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecurityEvent;

/**
 * SecMechanism that stores the currently selected file.
 * 
 * @author amay
 *
 */
public class CurrentFileSecMechanism implements SecMechanism {

	private CardFile curFile;

	public CurrentFileSecMechanism(CardFile currentFile) {
		curFile = currentFile;
	}

	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		// no need to be deleted in any case, the only known case when the
		// current file selection gets lost is on reset, but in this case the
		// SecStatus is cleared anyway
		return false;
	}

	public CardFile getCurrentFile() {
		return curFile;
	}

}
