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

	@Override
	public BitField getRepresentation() {
		return authorization.concatenate(role.getField());
	}

	/**
	 * Construct the effective Authorization by calculating a logical and on the
	 * {@link BitField} representations of role and authorization.
	 * 
	 * @param authorization
	 * @return
	 */
	public RelativeAuthorization buildEffectiveAuthorization(
			RelativeAuthorization authorization) {
		BitField effectiveRole = authorization.getRole().getField()
				.and(this.role.getField());
		BitField effectiveAuth = authorization.getAuthorization().and(
				this.authorization);
		return new RelativeAuthorization(
				CertificateRole.getFromField(effectiveRole), effectiveAuth);
	}
	
}
