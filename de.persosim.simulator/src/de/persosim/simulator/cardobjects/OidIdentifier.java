package de.persosim.simulator.cardobjects;

import de.persosim.simulator.protocols.Oid;

/**
 * This class implements {@link Oid} objects to be used as identifier.
 * 
 * @author slutters
 *
 */
public class OidIdentifier extends AbstractCardObjectIdentifier {

	Oid oid;

	public OidIdentifier() {}
	
	public OidIdentifier(Oid oid) {
		this.oid = oid;
	}

	@Override
	public boolean matches(CardObjectIdentifier obj) {
		if (obj instanceof OidIdentifier) {
			return ((OidIdentifier) obj).getOid().startsWithPrefix(oid.toByteArray());
		}
		return false;
	}

	public Oid getOid() {
		return oid;
	}

}
