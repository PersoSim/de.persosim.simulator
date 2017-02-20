package de.persosim.simulator.cardobjects;

import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * Identifies for a {@link CardFile} using the identifier stored in the file
 * itself.
 * 
 * @author mboonk
 * 
 */
public class FileIdentifier extends AbstractCardObjectIdentifier {

	private int identifier;
	
	/**
	 * Default constructor.
	 * 
	 * @param identifier
	 *            to be matched
	 */
	public FileIdentifier(int identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Constructor using a byte array.
	 * 
	 * @param identifier
	 *            to be matched
	 */
	public FileIdentifier(byte [] identifier) {
		this.identifier = Utils.getIntFromUnsignedByteArray(identifier);
	}
	
	/**
	 * @return the file identifier
	 */
	public int getFileIdentifier() {
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
		FileIdentifier other = (FileIdentifier) obj;
		if (identifier != other.identifier)
			return false;
		return true;
	}
	
	
	
	@Override
	public String toString() {
		return "0x"+HexString.encode(Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(identifier)))+" ("+identifier+")";
	}
	
}
