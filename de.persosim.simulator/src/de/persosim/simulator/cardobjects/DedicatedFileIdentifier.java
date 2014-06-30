package de.persosim.simulator.cardobjects;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Identifier using the name of a dedicated file for matching.
 * 
 * @author mboonk
 * 
 */
@XmlRootElement
public class DedicatedFileIdentifier extends AbstractCardObjectIdentifier {

	@XmlValue
	@XmlJavaTypeAdapter(HexBinaryAdapter.class)
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
