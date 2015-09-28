package de.persosim.simulator.crypto.certificates;

import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;

public abstract class CertificateExtension {
	
	TaOid objectIdentifier;
	
	public CertificateExtension(TaOid objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}
	
	/**
	 * Get the OID for this extension.
	 * 
	 * @return
	 */
	public TaOid getObjectIdentifier() {
		return objectIdentifier;
	}
	
	abstract public ConstructedTlvDataObject toTlv();
	
	/**
	 * Get the context specific data objects contained in this extension.
	 * 
	 * @return
	 */
	abstract public TlvDataObjectContainer getDataObjects();

}
