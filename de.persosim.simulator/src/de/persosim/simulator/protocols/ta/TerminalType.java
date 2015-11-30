package de.persosim.simulator.protocols.ta;

import de.persosim.simulator.protocols.Oid;

/**
 * This defines the terminal types described in TR-3110 v2.10 Part 2.
 * 
 * @author mboonk
 *
 */
public enum TerminalType {
	IS(TaOid.id_IS), AT(TaOid.id_AT), ST(TaOid.id_ST);
	protected Oid oid;

	private TerminalType(Oid oid) {
		this.oid = oid;
	}

	/**
	 * This method returns the OID associated with this terminal type
	 * 
	 * @return the OID associated with this terminal type
	 */
	public Oid getAsOid() {
		return oid;
	}

	/**
	 * Returns the {@link TerminalType} associated with the give OID
	 * 
	 * @param oid
	 *            the {@link Oid} to parse
	 * @return the resulting {@link TerminalType}
	 * @throws IllegalArgumentException
	 *             if the given OID can not be mapped to a terminal type
	 */
	public static TerminalType getFromOid(Oid oid) {
		if (oid.equals(IS.oid)) {
			return IS;
		} else if (oid.equals(AT.oid)) {
			return AT;
		} else if (oid.equals(ST.oid)) {
			return ST;
		}
		throw new IllegalArgumentException("The OID " + oid + " could not be matched to a terminal type");
	}
}
