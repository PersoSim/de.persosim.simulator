package de.persosim.simulator.securemessaging;

import de.persosim.simulator.secstatus.SecMechanism;

/**
 * This interface describes the behavior of a SM data provider generator able
 * to generate a fully functional {@link SmDataProvider}. Classes implementing
 * this interface are expected to be immutable.
 * 
 * @author slutters
 * 
 */
public interface SmDataProviderGenerator extends SecMechanism {
	
	/**
	 * This method generates a fully functional {@link SmDataProvider}.
	 * @return a fully functional {@link SmDataProvider}
	 */
	public SmDataProvider generateSmDataProvider();
	
}
