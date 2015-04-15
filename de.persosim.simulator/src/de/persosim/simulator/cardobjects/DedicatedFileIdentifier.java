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

	public DedicatedFileIdentifier() {
	}
	
	/**
	 * Default constructor.
	 * 
	 * @param name
	 *            to match on
	 */
	public DedicatedFileIdentifier(byte [] fileName) {
		this.fileName = fileName;
	}

	@Override
	public boolean matches(CardObjectIdentifier obj) {
		if (obj instanceof DedicatedFileIdentifier) {
			return Arrays.equals(fileName,
					((DedicatedFileIdentifier) obj).getDedicatedFileName());
		}
		return false;
	}

	/**
	 * @return the files name/aid as a byte array
	 */
	public byte[] getDedicatedFileName() {
		return Arrays.copyOf(fileName, fileName.length);
	}

}
