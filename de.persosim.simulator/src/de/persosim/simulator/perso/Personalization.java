package de.persosim.simulator.perso;

import java.util.List;

import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.Layer;

/**
 * This interface describes all essential aspects of a simulated SmartCard
 * within the PersoSim architecture.
 * 
 * An instance of this interface is provided to the generic {@link CommandProcessor}
 * during initialization and creates a consistent behavior.
 * 
 * @author amay
 * 
 */
public interface Personalization {

	/**
	 * Returns the list of layers to be used.
	 * 
	 * @return
	 */
	List<Layer> getLayerList();

	public void initialize();
	
}
