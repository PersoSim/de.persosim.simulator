package de.persosim.simulator.cardobjects;

import java.util.ArrayList;
import java.util.Collection;

public class KeyObject extends AbstractCardObject {
	
	protected KeyIdentifier primaryIdentifier;
	
	protected Collection<CardObjectIdentifier> furtherIdentifiers = new ArrayList<>();
	
	protected boolean privilegedOnly = false;
	
	public KeyIdentifier getPrimaryIdentifier() {
		return primaryIdentifier;
	}
	
	public boolean isPrivilegedOnly() {
		return privilegedOnly;
	}
	
	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> allIdentifiers = new ArrayList<CardObjectIdentifier>();
		allIdentifiers.add(primaryIdentifier);
		allIdentifiers.addAll(furtherIdentifiers);
		return allIdentifiers;
	}

	/**
	 * This method adds an additional {@link OidIdentifier} object identifying objects implementing this interface.
	 * Associating one or more {@link OidIdentifier} objects with a {@link KeyPairObject} is the preferred way to indicate that the key material provided by {@link KeyPairObject} can be used with a given OID.
	 * Querying the object store for a concrete OID represented by an {@link OidIdentifier} will return all {@link KeyPairObject} objects with keys that can be used with the respective OID.
	 * @param oidIdentifier additional {@link OidIdentifier} object identifying objects implementing this interface
	 */
	public void addOidIdentifier(OidIdentifier oidIdentifier) {
		furtherIdentifiers.add(oidIdentifier);
	}



}
