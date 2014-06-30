package de.persosim.simulator.cardobjects;


/**
 * This class implements an identifier for key objects using their key reference.
 * 
 * @author slutters
 *
 */
public class KeyIdentifier extends IntegerIdentifier {

	
	public KeyIdentifier(byte[] idBytes) {
		super(idBytes);
	}
	
	public KeyIdentifier(int keyReference) {
		super(keyReference);
	}
	
	public KeyIdentifier() {
		super();
	}

	@Override
	public boolean matches(CardObjectIdentifier obj) {
		if (obj instanceof KeyIdentifier) {
			return super.matches(obj);
		}
		return false;
	}
	
	public int getKeyReference() {
		return getInteger();
	}

	@Override
	public String getNameOfIdentifiedObject() {
		return "key reference";
	}
	
}
