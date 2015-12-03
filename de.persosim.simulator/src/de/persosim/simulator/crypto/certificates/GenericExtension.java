package de.persosim.simulator.crypto.certificates;

import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvDataObjectFactory;

/**
 * This class implements card verifiable certificate extensions as described
 * in TR-03110 v2.10 Part 3 Appendix C.3.
 * 
 * @author mboonk
 * 
 */
public class GenericExtension extends CertificateExtension {
	TlvDataObjectContainer dataObjects;

	public GenericExtension(ConstructedTlvDataObject extensionData) {
		super(new GenericOid(extensionData.getTlvDataObject(TlvConstants.TAG_06).getValueField()));
		
		dataObjects = new TlvDataObjectContainer();
		boolean firstIgnored = false;
		for (TlvDataObject object : extensionData.getTlvDataObjectContainer()){
			//ignore the first object to get only the context specific data objects
			if (firstIgnored){
				byte [] objectData = object.toByteArray();
				dataObjects.addTlvDataObject(TlvDataObjectFactory.createTLVDataObject(objectData, 0, objectData.length));
			}
			else
				firstIgnored = true;
		}
	}
	
	public GenericExtension(TaOid objectIdentifier, TlvDataObjectContainer dataObjects) {
		super(objectIdentifier);
		
		this.dataObjects = dataObjects;
	}

	@Override
	public TlvDataObjectContainer getDataObjects() {
		TlvDataObjectContainer result = new TlvDataObjectContainer(dataObjects.toByteArray());
		return result;
	}
	
	@Override
	public ConstructedTlvDataObject toTlv() {
		ConstructedTlvDataObject extension = new ConstructedTlvDataObject(TlvConstants.TAG_73);
		
		PrimitiveTlvDataObject oidTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_06, objectIdentifier.toByteArray());
		extension.addTlvDataObject(oidTlv);
		
		for(TlvDataObject tlvDataObject:dataObjects) {
			extension.addTlvDataObject(tlvDataObject);
		}
		
		return extension;
	}
	
}
