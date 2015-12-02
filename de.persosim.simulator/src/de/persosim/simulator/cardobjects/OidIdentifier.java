package de.persosim.simulator.cardobjects;

import de.persosim.simulator.protocols.Oid;

/**
 * This class implements {@link Oid} objects to be used as identifier.
 * <p/>
 * It matches on {@link CardObject}s that contain an OidIdentifier that this
 * {@link #oid} is a prefix of.
 * 
 * @author slutters
 *
 */
public class OidIdentifier extends AbstractCardObjectIdentifier {

	Oid oid;

	public OidIdentifier(Oid oid) {
		this.oid = oid;
	}

	public Oid getOid() {
		return oid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((oid == null) ? 0 : oid.hashCode());
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
		OidIdentifier other = (OidIdentifier) obj;
		if (oid == null) {
			if (other.oid != null)
				return false;
		} else if (!oid.equals(other.oid))
			return false;
		return true;
	}

	@Override
	public boolean matches(CardObject obj) {
		for (CardObjectIdentifier curIdentifier : obj.getAllIdentifiers()) {
			if (curIdentifier instanceof OidIdentifier) {
				if (((OidIdentifier) curIdentifier).getOid().startsWithPrefix(oid.toByteArray())) {
					return true;
				};
			}
		}
		return false;
	}

}
