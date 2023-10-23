package de.persosim.simulator.protocols.ta;

import de.persosim.simulator.crypto.certificates.CertificateUtils;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.BitField;

/**
 * This class contains the certificate holder authorization template information
 * as defined in TR-03110 v2.10 Part Appendix C.1.5.
 * 
 * @author mboonk
 * 
 */
public class CertificateHolderAuthorizationTemplate {
	RelativeAuthorization relativeAuthorization;
	TerminalType terminalType;
	
	public CertificateHolderAuthorizationTemplate(TerminalType terminalType,
			RelativeAuthorization relativeAuthorization) {
		this.relativeAuthorization = relativeAuthorization;
		this.terminalType = terminalType;
	}
	
	public CertificateHolderAuthorizationTemplate(ConstructedTlvDataObject chatData)
			throws CertificateNotParseableException {
		TlvDataObject tlvOID = chatData.getTlvDataObject(TlvConstants.TAG_06);
		TlvDataObject tlvTag53 = chatData.getTlvDataObject(TlvConstants.TAG_53);
		if (tlvOID == null || tlvTag53 == null)
			throw new CertificateNotParseableException("invalid CHAT");
		Oid objectIdentifier = new GenericOid(tlvOID.getValueField());
		PrimitiveTlvDataObject relativeAuthorizationData = (PrimitiveTlvDataObject) tlvTag53;
		CertificateRole role = CertificateRole.getFromMostSignificantBits(relativeAuthorizationData.getValueField()[0]);
		BitField authorization = BitField.buildFromBigEndian(relativeAuthorizationData.getLengthValue() * 8 - 2,
				relativeAuthorizationData.getValueField());
		relativeAuthorization = new RelativeAuthorization(role, authorization);

		// check if oid and relative authorization fit together
		terminalType = TerminalType.getFromOid(objectIdentifier);
		int authBits = getRelativeAuthorization().getAuthorization().getNumberOfBits();

		if ((terminalType.equals(TerminalType.AT) && authBits != 40)
				|| ((terminalType.equals(TerminalType.IS) || terminalType.equals(TerminalType.ST)) && authBits != 8)) {
			throw new CertificateNotParseableException("invalid combination of OID and terminal type");
		}
	}

	public RelativeAuthorization getRelativeAuthorization() {
		return relativeAuthorization;
	}
	
	/**
	 * Extract the terminalType from this objects OID.
	 * 
	 * @return the terminal type stored
	 */
	public TerminalType getTerminalType() {
		return terminalType;
	}
	
	public ConstructedTlvDataObject toTlv() {
		return CertificateUtils.encodeCertificateHolderAuthorizationTemplate(this);
	}
	
}
