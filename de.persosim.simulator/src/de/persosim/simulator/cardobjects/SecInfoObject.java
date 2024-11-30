package de.persosim.simulator.cardobjects;

import java.util.Collection;
import java.util.LinkedHashSet;

import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.tlv.TlvDataObject;

public class SecInfoObject extends AbstractCardObject {

	protected OidIdentifier primaryIdentifier;

	protected LinkedHashSet<SecInfoPublicity> publicity = new LinkedHashSet<>();

	protected TlvDataObject secInfoContent;

	public SecInfoObject(OidIdentifier identifier) {
		primaryIdentifier = identifier;
	}

	public OidIdentifier getPrimaryIdentifier() {
		return primaryIdentifier;
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = super.getAllIdentifiers();
		result.add(primaryIdentifier);
		return result;
	}

	public TlvDataObject getSecInfoContent() {
		return secInfoContent;
	}

	public Collection<SecInfoPublicity> getPublicity() {
		return publicity;
	}

	public void setSecInfoContent(TlvDataObject mobileEidTypeInfo) {
		secInfoContent = mobileEidTypeInfo;
	}

	public void addPublicity(SecInfoPublicity publicity) {
		this.publicity.add(publicity);
	}

}
