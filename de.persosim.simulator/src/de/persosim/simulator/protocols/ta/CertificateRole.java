package de.persosim.simulator.protocols.ta;

import de.persosim.simulator.utils.BitField;

/**
 * This enum describes the certificate roles as defined in TR-03110 v2.10 Part
 * 3. Depending on the used {@link TerminalType} different meanings for the DV
 * and TERMINAL roles emerge:<br/>
 * <br/>
 * <p>
 * Inspection Systems (IS):<br/>
 * DV_TYPE_1 = official domestic DV<br/>
 * DV_TYPE_2 = official foreign DV<br/>
 * TERMINAL = Inspection System<br/>
 * </p>
 * <p>
 * Authentication Terminals (AT):<br/>
 * DV_TYPE_1 = official domestic DV<br/>
 * DV_TYPE_2 = non-official/foreign DV<br/>
 * TERMINAL = Authentication Terminal<br/>
 * </p>
 * <p>
 * Signature Terminals (ST):<br/>
 * DV_TYPE_1 = Accreditation DV<br/>
 * DV_TYPE_2 = Certification service provider DV<br/>
 * TERMINAL = Signature Terminal<br/>
 * </p>
 * 
 * @author mboonk
 * 
 */
public enum CertificateRole {
	CVCA(new BitField(2, new byte[] { 0b11 }), true), DV_TYPE_1(new BitField(2,
			new byte[] { 0b10 }), false), DV_TYPE_2(new BitField(2,
			new byte[] { 0b01 }), false), TERMINAL(
			new BitField(2, new byte[] { 0b00 }), false);

	private BitField value;
	private boolean includeConditionalElementsInKeyEncoding;

	/**
	 * Parse a {@link BitField} and return the fitting {@link CertificateRole}.
	 * The field must be equal to the one defining the role.
	 * 
	 * @param field
	 *            the bit field to parse
	 * @return the fitting {@link CertificateRole} or null
	 */
	public static CertificateRole getFromField(BitField field) {
		for (CertificateRole role : CertificateRole.values()) {
			if (field.equals(role.getField())) {
				return role;
			}
		}
		return null;
	}

	/**
	 * Parse a byte for a {@link CertificateRole}.
	 * 
	 * @param data
	 *            the byte to parse
	 * @return the {@link CertificateRole} as defined in the two MSB
	 */
	public static CertificateRole getFromMostSignificantBits(byte data) {
		byte masked = (byte) ((data & 0b11000000) >>> 6);
		switch (masked) {
		case 0b11:
			return CVCA;
		case 0b10:
			return DV_TYPE_1;
		case 0b01:
			return DV_TYPE_2;
		case 0b00:
			return TERMINAL;
		}
		return null;
	}
	
	/**
	 * Parse a {@link BitField} for a {@link CertificateRole}.
	 * 
	 * @param field
	 *            the {@link BitField} to parse
	 * @return the {@link CertificateRole} as defined in the two MSB
	 */
	public static CertificateRole getFromMostSignificantBits(BitField field) {
		BitField role = new BitField(new boolean [] { field.getBit(field.getNumberOfBits()-2),  field.getBit(field.getNumberOfBits()-1) });
		return CertificateRole.getFromField(role);
	}

	CertificateRole(BitField value, boolean includeConditionalElementsInKeyEncoding) {
		this.value = value;
		this.includeConditionalElementsInKeyEncoding = includeConditionalElementsInKeyEncoding;
	}

	/**
	 * @return the {@link BitField} describing the role
	 * @see CertificateRole
	 */
	public BitField getField() {
		return value;
	}

	public boolean includeConditionalElementsInKeyEncoding() {
		return includeConditionalElementsInKeyEncoding;
	}
	
}
