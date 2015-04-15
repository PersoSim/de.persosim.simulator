package de.persosim.simulator.protocols.ta;

import de.persosim.simulator.utils.BitField;

/**
 * This contains the relative authorization used for terminal authentication as
 * described in TR-03110 v2.10 Part 3 Appendix C.4
 * 
 * @author mboonk
 * 
 */
public class RelativeAuthorization {
	CertificateRole role;
	BitField authorization;

	public RelativeAuthorization() {
	}
	
	public RelativeAuthorization(CertificateRole role, BitField authorization) {
		this.role = role;
		this.authorization = authorization;
	}

	public CertificateRole getRole() {
		return role;
	}

	public BitField getAuthorization() {
		return authorization;
	}

	/**
	 * @return the role and relative authorization as a bit field.
	 */
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
