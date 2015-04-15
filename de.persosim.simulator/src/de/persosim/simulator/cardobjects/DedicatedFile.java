package de.persosim.simulator.cardobjects;

import java.util.Collection;


import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;


/**
 * Implementation for an ISO7816 DF in the file hierarchy.
 * 
 * @author mboonk
 * 
 */
public class DedicatedFile extends AbstractFile {

	protected DedicatedFileIdentifier dedicatedFileName;

	public DedicatedFile() {
		
	}
	
	public DedicatedFile(FileIdentifier fileIdentifier, DedicatedFileIdentifier dedicatedFileName) {
		super(fileIdentifier);
		this.dedicatedFileName = dedicatedFileName;
	}

	@Override
	public ConstructedTlvDataObject getFileControlParameterDataObject() {
		ConstructedTlvDataObject result = super.getFileControlParameterDataObject();
		result.addTlvDataObject(new PrimitiveTlvDataObject(new TlvTag((byte)0x84), dedicatedFileName.getDedicatedFileName()));
		return result;
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = super.getAllIdentifiers();
		result.add(dedicatedFileName);
		return result;
	}

}
