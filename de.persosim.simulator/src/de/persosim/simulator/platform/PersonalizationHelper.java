package de.persosim.simulator.platform;

import java.util.Collection;
import java.util.HashSet;

import org.globaltester.logging.BasicLogger;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Personalization;

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
	 * This method accepts a {@link Collection} of type {@link Layer} and a
	 * reference type T. It will return the single object of type T if such is
	 * found within the provided {@link Collection}. If there is no such object
	 * null will be returned. If there is more than one object of this type an
	 * IllegalArgumentException will be thrown.
	 * 
	 * @param layers
	 *            the layers to check
	 * @param type
	 *            the type to check for
	 * @return the matching object
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
	
	/**
	 * This method recursively sets the card life cycle state for the provided
	 * {@link CardObject} and all of its children to operational activated
	 * 
	 * @param objectTree
	 *            the root object for which to set the card life cycle state
	 */
	public static void setLifeCycleStates(CardObject objectTree) {
		Collection<CardObject> children = objectTree.getChildren();
		if (children.size() > 0){
			for (CardObject cardObject : children) {
				setLifeCycleStates(cardObject);
				if (cardObject.getLifeCycleState().isPersonalizationPhase()){
					try {
						cardObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
					} catch (AccessDeniedException e) {
						BasicLogger.logException(PersonalizationHelper.class, e, BasicLogger.WARN);
					}	
				}
			}
		}
	}

	
	/**
	 * Returns the file with the given file identifier under the chosen parent object of a given personalization.
	 * @param perso The {@link Personalization}} to search in.
	 * @param fileIdentifier The file identifier of the file to search.
	 * @param df
	 *            the parent object to search in or <code>null</code> if the
	 *            root element should be searched
	 * @return the bytes of the found file
	 * @throws AccessDeniedException
	 */
	public static byte[] getFileFromPerso(Personalization perso, int fileIdentifier, CardObjectIdentifier df) throws AccessDeniedException {
		CardObject parent;
		
		MasterFile mf = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getMasterFile();
		
		if (df != null) {
			Collection<CardObject> parentCandidates = mf.findChildren(df);

			if (parentCandidates.size() != 1) {
				throw new IllegalArgumentException("The chosen parent is ambigous");
			}

			parent = parentCandidates.iterator().next();
		} else {
			parent = mf;
		}
		
		Collection<CardObject> file = parent.findChildren(new FileIdentifier(fileIdentifier));
		
		return ((ElementaryFile) file.iterator().next()).getContent();
	}
}
