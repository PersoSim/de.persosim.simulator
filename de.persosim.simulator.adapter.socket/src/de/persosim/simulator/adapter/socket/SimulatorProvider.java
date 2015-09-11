package de.persosim.simulator.adapter.socket;

import org.globaltester.simulator.Simulator;

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
