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
	 * @return the OID associated with this terminal type
	 */
	public Oid getAsOid() {
		return oid;
	}
	
}
