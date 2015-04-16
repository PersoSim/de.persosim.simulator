package de.persosim.simulator.adapter.socket;

import de.persosim.simulator.Simulator;
import de.persosim.simulator.perso.Personalization;

/**
 * Implementations of this interface can provide a {@link Simulator}
 * implementation.
 * 
 * @author mboonk
 *
 */
public interface SimulatorProvider {
	
	/**
	 * @return a {@link Simulator} implementation or null if none is available
	 */
	abstract Simulator getSimulator();
}
