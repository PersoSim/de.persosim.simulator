package de.persosim.simulator.platform;

import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.exception.LifeCycleChangeException;
import de.persosim.simulator.utils.PersoSimLogger;

/**
 * This class provides various helper methods which may be useful when operating
 * on/with personalizations.
 * 
 * @author slutters
 *
 */
public class PersonalizationHelper {
	
	/**
	 * This method accepts a {@link Collection} of type {@link Layer} and a
	 * reference type T. It will return a {@link Collection} of type T which is
	 * an intersecting set of the provided {@link Collection} containing all of
	 * its elements matching the provided type T. The returned
	 * {@link Collection} will never be null but may be empty.
	 * 
	 * @param layers
	 *            the layers to check
	 * @param type
	 *            the type to check for
	 * @return the intersecting set
	 */
	public static <T> Collection<T> getCompatibleLayers(Collection<Layer> layers, Class<T> type){
		HashSet<T> result = new HashSet<>();
		
		for (Layer layer : layers) {
			if (type.isAssignableFrom(layer.getClass())){
				@SuppressWarnings("unchecked")
				T casted = (T)layer;
				result.add(casted);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param layers
	 * @param type
	 * @return
	 */
	public static <T> T getUniqueCompatibleLayer(Collection<Layer> layers, Class<T> type) {
		Collection<T> compatibleLayers = PersonalizationHelper.getCompatibleLayers(layers, type);
		if(compatibleLayers.size() <= 1) {
			if(compatibleLayers.size() == 1) {
				return compatibleLayers.iterator().next();
			} else{
				return null;
			}
		} else{
			throw new IllegalArgumentException("more than 1 matching layers found");
		}
	}
	
	public static void setLifeCycleStates(CardObject objectTree) {
		Collection<CardObject> children = objectTree.getChildren();
		if (children.size() > 0){
			for (CardObject cardObject : children) {
				setLifeCycleStates(cardObject);
				if (cardObject.getLifeCycleState().isPersonalizationPhase()){
					try {
						cardObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
					} catch (LifeCycleChangeException e) {
						PersoSimLogger.logException(PersonalizationHelper.class, e, PersoSimLogger.WARN);
					}	
				}
			}
		}
	}
}
