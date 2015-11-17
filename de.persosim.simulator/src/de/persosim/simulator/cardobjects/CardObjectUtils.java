package de.persosim.simulator.cardobjects;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.util.Collection;
import java.util.LinkedList;

import de.persosim.simulator.exception.ProcessingException;
import de.persosim.simulator.platform.Iso7816;

public class CardObjectUtils {

	/**
	 * Perform a breadth-first-search beginning from the given search root and
	 * return the first CardObject that matches all identifiers.
	 * 
	 * @param searchRoot
	 *            object the search is started on
	 * @param identifiers
	 *            set of identifiers that are required to match on the returned
	 *            element
	 * @return first found element that matches all identifiers or
	 *         {@link NullCardObject} if no matching object can be found
	 */
	public static CardObject findObject(CardObject searchRoot, CardObjectIdentifier... identifiers) {
		LinkedList<CardObject> objectsToSearch = new LinkedList<>();

		objectsToSearch.add(searchRoot);

		while (!objectsToSearch.isEmpty()) {
			CardObject curElem = objectsToSearch.removeFirst();

			if (matches(curElem, identifiers)) {
				// current elem is the first match
				return curElem;
			} else {
				// add children to the list
				for (CardObject curChild : curElem.getChildren()) {
					objectsToSearch.addLast(curChild);
				}
			}
		}

		// no matching element found
		return new NullCardObject();
	}
	
	/**
	 * This method returns the only existing child {@link CardObject} of parent
	 * parameter, that match all provided {@link CardObjectIdentifier}.
	 * <p/>
	 * It is expected that exactly one CardObject is returned (meaning that the
	 * given Set of Identifiers is unambiguous). If no or more matching elements
	 * are found an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param parent CardObject whose children should be searched
	 * @param cardObjectIdentifier set of identifiers that are required to match on the returned element
	 * @return the one and only child element of parent that matches all provided identifiers
	 * @throws IllegalArgumentException if none or several matching children are found
	 * 
	 */
	public static CardObject getSpecificChild(CardObject parent, CardObjectIdentifier... cardObjectIdentifier) {

		Collection<CardObject> cardObjects = parent.findChildren(cardObjectIdentifier);
		
		// assume that selection is not ambiguous and can be performed implicitly
		switch (cardObjects.size()) {
		case 0:
			throw new ProcessingException(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "no matching selection found");
			
		case 1:
			CardObject matchingCardObject = cardObjects.iterator().next();
			log(CardObjectUtils.class, "selected " + matchingCardObject, DEBUG);
			return matchingCardObject;

		default:
			throw new ProcessingException(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "selection is ambiguous, more identifiers required");
		}
	}

	/**
	 * Check whether a given CardObject matches all identifiers
	 * 
	 * @param obj
	 *            CardObject to be checked
	 * @param cardObjectIdentifiers
	 *            set of identifiers that are required to match
	 * @return true iff all identifiers match on obj
	 */
	public static boolean matches(CardObject obj, CardObjectIdentifier... cardObjectIdentifiers) {
		for (CardObjectIdentifier cardObjectIdentifier : cardObjectIdentifiers) {
			if (!cardObjectIdentifier.matches(obj)) {
				return false;
			}
		}
		return true;
	}

}
