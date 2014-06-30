package de.persosim.simulator.cardobjects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Identifies for a {@link CardFile} using the identifier stored in the file
 * itself.
 * 
 * @author mboonk
 * 
 */
@XmlRootElement
public class FileIdentifier extends AbstractCardObjectIdentifier {

	@XmlValue
	private int identifier;
	
	/**
	 * Default constructor for usage with JAXB.
	 * 
	 */
	public FileIdentifier() {
	}
	
	/**
	 * Default constructor.
	 * 
	 * @param identifier
	 *            to be matched
	 */
	public FileIdentifier(int identifier) {
		this.identifier = identifier;
	}

	@Override
	public boolean matches(CardObjectIdentifier object) {
		if (object instanceof FileIdentifier) {
			return ((FileIdentifier) object).getFileIdentifier() == identifier;
		}
		return false;
	}
	
	/**
	 * @return the file identifier
	 */
	public int getFileIdentifier() {
		return identifier;
	}
}
