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

	public RelativeAuthorization() {
	}
	
	public RelativeAuthorization(BitField authorization) {
		super(authorization);
	}
	
	public RelativeAuthorization(CertificateRole role, BitField authorization) {
		this(authorization.concatenate(role.getField()));
	}
	
	public CertificateRole getRole() {
		return CertificateRole.getFromMostSignificantBits(authorization);
	}
	
}
