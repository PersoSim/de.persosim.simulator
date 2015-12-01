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

	/**
	 * @return the terminal type used in this identifier
	 */
	public TerminalType getTerminalType() {
		return terminalType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((terminalType == null) ? 0 : terminalType.hashCode());
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
		TrustPointIdentifier other = (TrustPointIdentifier) obj;
		if (terminalType != other.terminalType)
			return false;
		return true;
	}

}
