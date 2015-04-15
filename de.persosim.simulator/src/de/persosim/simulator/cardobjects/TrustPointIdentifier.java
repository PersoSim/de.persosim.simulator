package de.persosim.simulator.cardobjects;


import de.persosim.simulator.protocols.ta.TerminalType;

/**
 * This is the implementation of an identifier for stored trust points using
 * their terminal type.
 * 
 * @author mboonk
 * 
 */
public class TrustPointIdentifier extends AbstractCardObjectIdentifier {

	TerminalType terminalType;

	public TrustPointIdentifier() {
	}
	
	public TrustPointIdentifier(TerminalType terminalType) {
		this.terminalType = terminalType;
	}

	@Override
	public boolean matches(CardObjectIdentifier obj) {
		if (obj instanceof TrustPointIdentifier) {
			if (((TrustPointIdentifier) obj).getTerminalType().equals(
					terminalType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the terminal type used in this identifier
	 */
	public TerminalType getTerminalType() {
		return terminalType;
	}

}
