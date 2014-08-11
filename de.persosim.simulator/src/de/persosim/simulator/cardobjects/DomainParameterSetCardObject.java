package de.persosim.simulator.cardobjects;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.StandardizedDomainParameters;

/**
 * This object wraps domain parameters for storing them in the object store.
 * They can be retrieved from there using their domain parameter id or additionally associated OIDs.
 * 
 * @author slutters
 *
 */
@XmlRootElement(name="DomainParameterSet")
public class DomainParameterSetCardObject extends AbstractCardObject {
	
	@XmlAnyElement(lax=true)
	protected DomainParameterSet domainParameterSet;
	@XmlElement(name="domainParameterId")
	protected DomainParameterSetIdentifier primaryIdentifier;
	@XmlElementWrapper(name="usage")
	@XmlAnyElement(lax=true)
	protected Collection<CardObjectIdentifier> furtherIdentifiers;
	
	public DomainParameterSetCardObject() {
	}
	
	public DomainParameterSetCardObject(DomainParameterSet domainParameterSet, DomainParameterSetIdentifier identifier) {
		this.primaryIdentifier = identifier;
		this.domainParameterSet = domainParameterSet;
		
		this.furtherIdentifiers = new ArrayList<CardObjectIdentifier>();
	}
	
	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> allIdentifiers = new ArrayList<CardObjectIdentifier>(furtherIdentifiers);
		allIdentifiers.add(primaryIdentifier);
		return allIdentifiers;
	}

	/**
	 * This method adds an additional {@link OidIdentifier} object identifying objects implementing this interface.
	 * Associating one or more {@link OidIdentifier} objects with a {@link DomainParameterSetCardObject} is the preferred way to indicate that the domain parameters provided by {@link DomainParameterSetCardObject} can be used with a given OID.
	 * Querying the object store for a concrete OID represented by an {@link OidIdentifier} will return all {@link DomainParameterSetCardObject} objects with domain parameters that can be used with the respective OID.
	 * @param oidIdentifier additional {@link OidIdentifier} object identifying objects implementing this interface
	 */
	public void addOidIdentifier(OidIdentifier oidIdentifier) {
		furtherIdentifiers.add(oidIdentifier);
	}
	
	public DomainParameterSetIdentifier getPrimaryIdentifier() {
		return primaryIdentifier;
	}

	public DomainParameterSet getDomainParameterSet() {
		return domainParameterSet;
	}
	
	/**
	 * JAXB callback
	 * <p/>
	 * Used to serialize standardized domain parameters only using their id.
	 * @param m
	 */
	@Override
	protected void beforeMarshal(Marshaller m){
		super.beforeMarshal(m);
		
		if ((primaryIdentifier != null)
				&& (primaryIdentifier.getInteger() < StandardizedDomainParameters.NO_OF_STANDARDIZED_DOMAIN_PARAMETERS)) {
			domainParameterSet = null;
		}
	}
	
	/**
	 * JAXB callback
	 * <p/>
	 * Used to initialize standardized domain parameters if only the id is provided
	 * @param u
	 * @param parent
	 */
	@Override
	protected void afterUnmarshal(Unmarshaller u, Object parent) {
		super.afterUnmarshal(u, parent);
		
		if ((domainParameterSet == null)
				&& (primaryIdentifier != null)
				&& (primaryIdentifier.getInteger() <= StandardizedDomainParameters.NO_OF_STANDARDIZED_DOMAIN_PARAMETERS)) {
			domainParameterSet = StandardizedDomainParameters.getDomainParameterSetById(primaryIdentifier.getInteger());
		}
	}

}
