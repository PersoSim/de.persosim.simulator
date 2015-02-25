package de.persosim.simulator.adapter.socket;

import de.persosim.simulator.Simulator;

public interface SimulatorProvider {
	/**
	 * @return a {@link Simulator} implementation or null if none is available
	 */
	abstract Simulator getSimulator();
}
