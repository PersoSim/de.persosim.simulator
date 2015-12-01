package de.persosim.simulator.cardobjects;

import java.util.Arrays;

/**
 * Identifier using the name of a dedicated file for matching.
 * 
 * @author mboonk
 * 
 */
public class DedicatedFileIdentifier extends AbstractCardObjectIdentifier {

	byte [] fileName;
	
	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            to match on
	 */
	public DedicatedFileIdentifier(byte [] fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the files name/aid as a byte array
	 */
	public byte[] getDedicatedFileName() {
		return Arrays.copyOf(fileName, fileName.length);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fileName);
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
		DedicatedFileIdentifier other = (DedicatedFileIdentifier) obj;
		if (!Arrays.equals(fileName, other.fileName))
			return false;
		return true;
	}

}
