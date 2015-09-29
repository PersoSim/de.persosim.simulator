package de.persosim.simulator.protocols.ta;

import de.persosim.simulator.crypto.certificates.CertificateUtils;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.BitField;

/**
 * This class contains the certificate holder authorization template information
 * as defined in TR-03110 v2.10 Part Appendix C.1.5.
 * 
 * @author mboonk
 * 
 */
public class CertificateHolderAuthorizationTemplate {
	TaOid objectIdentifier;
	RelativeAuthorization relativeAuthorization;

	public CertificateHolderAuthorizationTemplate(){
	}
	
	public CertificateHolderAuthorizationTemplate(TaOid objectIdentifier,
			RelativeAuthorization relativeAuthorization) {
		this.objectIdentifier = objectIdentifier;
		this.relativeAuthorization = relativeAuthorization;
	}
	
	public CertificateHolderAuthorizationTemplate(ConstructedTlvDataObject chatData) throws CertificateNotParseableException {

		objectIdentifier = new TaOid(chatData.getTlvDataObject(TlvConstants.TAG_06).getValueField());
		PrimitiveTlvDataObject relativeAuthorizationData = (PrimitiveTlvDataObject) chatData.getTlvDataObject(TlvConstants.TAG_53);
		CertificateRole role = CertificateRole.getFromMostSignificantBits(relativeAuthorizationData.getValueField()[0]);
		BitField authorization = BitField.buildFromBigEndian(relativeAuthorizationData.getLengthValue() * 8 - 2, relativeAuthorizationData.getValueField());
		relativeAuthorization = new RelativeAuthorization(role, authorization);
		
		//check if oid and relative authorization fit together
		TerminalType type = getTerminalType();
		int authBits = getRelativeAuthorization().getRepresentation().getNumberOfBits();
		
		if ((type.equals(TerminalType.AT) && authBits != 40) || ((type.equals(TerminalType.IS) || type.equals(TerminalType.ST)) && authBits != 8)){
			throw new CertificateNotParseableException("invalid combination of OID and terminal type");
		}
		
	}

	public TaOid getObjectIdentifier() {
		return objectIdentifier;
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
		if (objectIdentifier.equals(TaOid.id_IS)) {
			return TerminalType.IS;
		} else if (objectIdentifier.equals(TaOid.id_AT)) {
			return TerminalType.AT;
		} else if (objectIdentifier.equals(TaOid.id_ST)) {
			return TerminalType.ST;
		}
		return null;
	}
	
	public ConstructedTlvDataObject toTlv() {
		return CertificateUtils.encodeCertificateHolderAuthorizationTemplate(this);
	}
	
}
