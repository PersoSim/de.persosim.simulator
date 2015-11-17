package de.persosim.simulator.cardobjects;

import java.util.LinkedList;

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
