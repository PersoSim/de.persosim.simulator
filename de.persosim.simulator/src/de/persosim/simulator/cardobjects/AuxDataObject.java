package de.persosim.simulator.cardobjects;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.HashSet;


import de.persosim.simulator.protocols.ta.AuthenticatedAuxiliaryData;

public abstract class AuxDataObject extends AbstractCardObject {
	OidIdentifier identifier;
	
	public AuxDataObject(){
		
	}
	
	public AuxDataObject(OidIdentifier identifier){
		this.identifier = identifier;
	}
	
	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		HashSet<CardObjectIdentifier> result = new HashSet<>();
		result.add(identifier);
		return result;
	}

	public abstract boolean verify(AuthenticatedAuxiliaryData current) throws AccessDeniedException;
}
