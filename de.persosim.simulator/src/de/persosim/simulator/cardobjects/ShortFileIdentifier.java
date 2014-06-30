package de.persosim.simulator.cardobjects;

import javax.xml.bind.annotation.XmlValue;

/**
 * Implementation of an ISO7816-4 compliant short identifier.
 * 
 * @author mboonk
 * 
 */
public class ShortFileIdentifier extends AbstractCardObjectIdentifier {

	@XmlValue
	private int identifier;
	
	public ShortFileIdentifier(){
	}

	public ShortFileIdentifier(int shortFileIdentifier) {
		if ((shortFileIdentifier >= 1 && shortFileIdentifier <= 30) || shortFileIdentifier == -1){
			this.identifier = shortFileIdentifier;
		} else {
			throw new FileIdentifierIncorrectValueException();
		}
	}

	@Override
	public boolean matches(CardObjectIdentifier identifier) {
		if (identifier instanceof ShortFileIdentifier) {
			return ((ShortFileIdentifier) identifier).getShortFileIdentifier() == this.identifier;
		}
		return false;
	}

	public int getShortFileIdentifier() {
		return identifier;
	}

}
