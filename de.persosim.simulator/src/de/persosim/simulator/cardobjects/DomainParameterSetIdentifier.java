package de.persosim.simulator.cardobjects;

/**
 * This class implements an identifier for domain parameters using their domain parameter id.
 * 
 * @author slutters
 *
 */
public class DomainParameterSetIdentifier extends IntegerIdentifier {
	
	public DomainParameterSetIdentifier(int domainParameterId) {
		super(domainParameterId);
	}
	
	public DomainParameterSetIdentifier() {
		super();
	}

	public DomainParameterSetIdentifier(byte[] idBytes) {
		super(idBytes);
	}public int getDomainParameterId() {
		return getInteger();
	}

	@Override
	public String getNameOfIdentifiedObject() {
		return "domain parameter id";
	}

}
