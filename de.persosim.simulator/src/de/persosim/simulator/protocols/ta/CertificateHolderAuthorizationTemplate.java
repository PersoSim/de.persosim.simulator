package de.persosim.simulator.protocols.ta;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * This class contains the certificate holder authorization template information
 * as defined in TR-03110 v2.10 Part Appendix C.1.5.
 * 
 * @author mboonk
 * 
 */
@XmlRootElement
public class CertificateHolderAuthorizationTemplate {
	@XmlElement
	TaOid objectIdentifier;
	@XmlElement
	RelativeAuthorization relativeAuthorization;

	public CertificateHolderAuthorizationTemplate(){
	}
	
	public CertificateHolderAuthorizationTemplate(TaOid objectIdentifier,
			RelativeAuthorization relativeAuthorization) {
		this.objectIdentifier = objectIdentifier;
		this.relativeAuthorization = relativeAuthorization;
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
}
