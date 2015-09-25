package de.persosim.simulator.cardobjects;

import de.persosim.simulator.utils.Utils;

/**
 * An {@link CardObjectIdentifier} that basically relies on an integer
 * representation of its value.
 * <p/>
 * In addition to simple matching to equal values the provided {@link #matches}
 * implementation allows to match any value by providing {@link #MATCHES_ALWAYS}
 * as identifier.
 * 
 * @author slutters
 * 
 */
public abstract class IntegerIdentifier extends AbstractCardObjectIdentifier {
	protected static final int MATCHES_ALWAYS = Integer.MIN_VALUE;
	
	int integer;
	
	public IntegerIdentifier(int integer) {
		this.integer = integer;
	}
	
	public IntegerIdentifier() {
		this(Integer.MIN_VALUE);
	}
	
	public IntegerIdentifier(byte[] idBytes) {
		this(Utils.getIntFromUnsignedByteArray(idBytes));
	}

	@Override
	public boolean matches(CardObjectIdentifier obj) {
		if (obj instanceof IntegerIdentifier) {
			int otherInteger = ((IntegerIdentifier) obj).getInteger();
			if ((otherInteger == integer) || (integer == MATCHES_ALWAYS)) {
				return true;
			}
		}
		return false;
	}
	
	public void setInteger(int integer){
		this.integer = integer;
	}

	public int getInteger() {
		return integer;
	}
	
	public abstract String getNameOfIdentifiedObject();
	
}
