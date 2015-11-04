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
