package de.persosim.simulator.cardobjects;

import de.persosim.simulator.exception.FileIdentifierIncorrectValueException;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;


/**
 * Implementation of an ISO7816-4 compliant short identifier.
 * 
 * @author mboonk
 * 
 */
public class ShortFileIdentifier extends AbstractCardObjectIdentifier {

	private int identifier;
	
	public ShortFileIdentifier(int shortFileIdentifier) {
		if ((shortFileIdentifier >= 1 && shortFileIdentifier <= 30) || shortFileIdentifier == -1){
			this.identifier = shortFileIdentifier;
		} else {
			throw new FileIdentifierIncorrectValueException();
		}
	}

	/**
	 * Constructor using a byte array.
	 * 
	 * @param identifier
	 *            to be matched
	 */
	public ShortFileIdentifier(byte [] identifier) {
		this(Utils.getIntFromUnsignedByteArray(identifier));
	}

	public int getShortFileIdentifier() {
		return identifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + identifier;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShortFileIdentifier other = (ShortFileIdentifier) obj;
		if (identifier != other.identifier)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return HexString.encode(Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(identifier))) + " (" + identifier + ")";
	}

}
