package de.persosim.simulator.perso;

import java.util.List;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.Layer;
import de.persosim.simulator.protocols.Protocol;

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
	 * Returns the root element of the object tree.
	 */
	MasterFile getObjectTree();

	/**
	 * Returns the list of activated protocols.
	 * <p/>
	 * The protocols contained in this List are required to be already
	 * initialized and ready to be added to a {@link CardStateAccessor} and
	 * used afterwards
	 * 
	 * @return
	 */
	List<Protocol> getProtocolList();
	
	/**
	 * Returns the list of layers to be used.
	 * 
	 * @return
	 */
	List<Layer> getLayerList();

}
