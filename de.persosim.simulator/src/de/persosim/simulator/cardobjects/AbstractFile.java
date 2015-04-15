package de.persosim.simulator.cardobjects;

import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.Utils;

/**
 * Abstract super class for files. Contains the generic file identifier.
 * 
 * @author mboonk
 * 
 */
public abstract class AbstractFile extends AbstractCardObject implements
		CardFile {

	protected FileIdentifier fileIdentifier;

	/**
	 * Default Constructor for JAXB.
	 * 
	 */
	public AbstractFile() {
	}
	
	/**
	 * Default Constructor.
	 * 
	 * @param fileIdentifier
	 */
	public AbstractFile(FileIdentifier fileIdentifier) {
		this.fileIdentifier = fileIdentifier;
	}

	@Override
	public ConstructedTlvDataObject getFileControlParameterDataObject() {
		ConstructedTlvDataObject result = new ConstructedTlvDataObject(new TlvTag(Iso7816.TAG_FILE_CONTROL_PARAMETERS_TEMPLATE));
		//TODO MBK implement FCP template according to ISO7816-4 Table 12
		result.addTlvDataObject(new PrimitiveTlvDataObject(new TlvTag(
				(byte) 0x83), Utils.toUnsignedByteArray(fileIdentifier
				.getFileIdentifier())));
		return result;
	}

	@Override
	public ConstructedTlvDataObject getFileManagementDataObject() {
		//IMPL return FMD template as described in ISO7816-4 Table 12
		ConstructedTlvDataObject result = new ConstructedTlvDataObject(new TlvTag(Iso7816.TAG_FILE_MANAGEMENT_DATA_TEMPLATE));
		return result;
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		HashSet<CardObjectIdentifier> result = new HashSet<>();
		result.add(fileIdentifier);
		return result;
	}

}
