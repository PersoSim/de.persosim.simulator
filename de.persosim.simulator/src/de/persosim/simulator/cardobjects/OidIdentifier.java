package de.persosim.simulator.cardobjects;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.protocols.Oid;

/**
 * This class implements {@link Oid} objects to be used as identifier.
 * 
 * @author slutters
 *
 */
@XmlRootElement
public class OidIdentifier extends AbstractCardObjectIdentifier {

	@XmlAnyElement(lax=true)
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
