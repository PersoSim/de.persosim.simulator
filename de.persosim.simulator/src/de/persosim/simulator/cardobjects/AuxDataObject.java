package de.persosim.simulator.cardobjects;

import java.util.Collection;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.AuthenticatedAuxiliaryData;

public abstract class AuxDataObject extends AbstractCardObject {
	protected OidIdentifier identifier;
	
	public AuxDataObject(OidIdentifier identifier){
		this.identifier = identifier;
	}
	
	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = super.getAllIdentifiers();
		result.add(identifier);
		return result;
	}

	public Oid getOid(){
		return identifier.getOid();
	}
	
	public abstract boolean verify(AuthenticatedAuxiliaryData current) throws AccessDeniedException;
}
