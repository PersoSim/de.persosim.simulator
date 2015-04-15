package de.persosim.simulator.cardobjects;

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
