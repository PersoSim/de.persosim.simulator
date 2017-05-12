package de.persosim.simulator.cardobjects;


import de.persosim.simulator.protocols.Protocol;

/**
 * This identifier identifies all {@link AbstractCardObject} elements associated with an implementation of the provided {@link Protocol} class.
 * This can be used e.g. to link cryptographic keys of the same kind exclusively to different protocols.
 * 
 * @author slutters
 * 
 */
public class ProtocolIdentifier extends AbstractCardObjectIdentifier {

	Class<?> protocolType;

	public ProtocolIdentifier(Class<? extends Protocol> type) {
		this.protocolType = type;
	}

	public Class<?> getProtocolType() {
		return protocolType;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((protocolType == null) ? 0 : protocolType.hashCode());
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
		ProtocolIdentifier other = (ProtocolIdentifier) obj;
		if (protocolType == null) {
			if (other.protocolType != null)
				return false;
		} else if (!protocolType.equals(other.protocolType))
			return false;
		return true;
	}

	@Override
	public boolean matches(CardObject obj) {
		for (CardObjectIdentifier curIdentifier : obj.getAllIdentifiers()) {
			if (curIdentifier instanceof ProtocolIdentifier) {
				if (protocolType.isAssignableFrom(((ProtocolIdentifier) curIdentifier).getProtocolType())) {
					return true;
				};
			}
		}
		return false;
	}

}
