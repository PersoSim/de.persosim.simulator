package de.persosim.simulator.crypto.certificates;

import java.security.PublicKey;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;

/**
 * This class represents a public key.
 * 
 * @author slutters
 *
 */
public abstract class CvPublicKey extends CvKey implements PublicKey, TlvConstants {
	
	private static final long serialVersionUID = 1L;
	
	public CvPublicKey(CvOid cvOid, PublicKey publicKey) {
		super(cvOid, publicKey);
	}
	
	public abstract ConstructedTlvDataObject toTlvDataObject(boolean includeConditionalObjects);
	
	public abstract boolean isComplete();

}
