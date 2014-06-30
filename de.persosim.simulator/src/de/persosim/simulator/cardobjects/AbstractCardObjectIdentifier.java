package de.persosim.simulator.cardobjects;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Abstract super class for all object identifiers, contains the generic
 * matching logic on card objects
 * 
 * @author mboonk
 * 
 */
@XmlTransient
public abstract class AbstractCardObjectIdentifier implements
		CardObjectIdentifier {

	@Override
	public boolean matches(CardObject currentObject) {
		for (CardObjectIdentifier currentIdentifier : currentObject
				.getAllIdentifiers()) {
			if (this.matches(currentIdentifier)) {
				return true;
			}
		}
		return false;
	}

}
