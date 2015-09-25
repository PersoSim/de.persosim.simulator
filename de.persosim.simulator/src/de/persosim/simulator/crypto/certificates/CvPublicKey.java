package de.persosim.simulator.crypto.certificates;

import java.security.PublicKey;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;

/**
 * This class represents a public key to be used in the context of CV certificates.
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
	
	/**
	 * This method returns whether this object represents a fully usable or only partial basic key
	 * @return true if the key is complete, i.e. fully usable, false otherwise
	 */
	public abstract boolean isComplete();

}
