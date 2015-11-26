package de.persosim.simulator.crypto.certificates;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;


/**
 * This class implements the basic functionalities of any certificate extension.
 * 
 * @author slutters
 * 
 */
public abstract class CertificateExtension {
	
	protected Oid objectIdentifier;
	
	public CertificateExtension(Oid objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}
	
	/**
	 * Get the OID for this extension.
	 * 
	 * @return the OID for this extension
	 */
	public Oid getObjectIdentifier() {
		return objectIdentifier;
	}
	
	abstract public ConstructedTlvDataObject toTlv();
	
	/**
	 * Get the context specific data objects contained in this extension.
	 * 
	 * @return the context specific data objects contained in this extension
	 */
	abstract public TlvDataObjectContainer getDataObjects();

}
