package de.persosim.simulator.securemessaging;

import de.persosim.simulator.processing.UpdatePropagation;

/**
 * This interface describes the behavior of am SM data provider generator able
 * to generate a fully functional {@link SmDataProvider}. Classes implementing
 * this interface are expected to be immutable.
 * 
 * @author slutters
 * 
 */
public interface SmDataProviderGenerator extends UpdatePropagation {
	
	/**
	 * This method generates a fully functional {@link SmDataProvider}.
	 * @return a fully functional {@link SmDataProvider}
	 */
	public SmDataProvider generateSmDataProvider();
	
}
