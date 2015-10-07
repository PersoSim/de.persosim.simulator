package de.persosim.simulator.protocols.ta;

import de.persosim.simulator.utils.BitField;

/**
 * This contains the relative authorization used for terminal authentication as
 * described in TR-03110 v2.10 Part 3 Appendix C.4
 * 
 * @author mboonk
 * 
 */
public class RelativeAuthorization extends Authorization {
	
	protected CertificateRole role;

	public RelativeAuthorization() {
	}
	
	public RelativeAuthorization(CertificateRole role, BitField authorization) {
		super(authorization);
		this.role = role;
	}
	
	public CertificateRole getRole() {
		return role;
	}
	
	public BitField getRepresentation() {
		return authorization.concatenate(role.getField());
	}

	@Override
	public RelativeAuthorization buildEffectiveAuthorization(Authorization authorization) {
		if (authorization instanceof RelativeAuthorization){
			BitField effectiveRole = ((RelativeAuthorization) authorization).getRole().getField().and(this.role.getField());
			BitField effectiveAuth = super.buildEffectiveAuthorization(authorization).getAuthorization(); 
			return new RelativeAuthorization(CertificateRole.getFromField(effectiveRole), effectiveAuth);	
		}
		
		throw new IllegalArgumentException("parameter must be of type " + RelativeAuthorization.class.getName());
	}
	
}
