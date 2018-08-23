package de.persosim.simulator.cardobjects;

import java.util.Collection;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.seccondition.SecCondition;
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
	private SecCondition createFiles;
	
	public DedicatedFile(FileIdentifier fileIdentifier, DedicatedFileIdentifier dedicatedFileName) {
		this(fileIdentifier, dedicatedFileName, SecCondition.ALLOWED);
	}
	
	public DedicatedFile(FileIdentifier fileIdentifier, DedicatedFileIdentifier dedicatedFileName, SecCondition createFilesAccess){
		super(fileIdentifier);
		this.dedicatedFileName = dedicatedFileName;
		this.createFiles = createFilesAccess;
	}
	
	@Override
	public void addChild(CardObject newChild) throws AccessDeniedException {
		if (securityStatus == null || securityStatus.checkAccessConditions(getLifeCycleState(), createFiles)){
			super.addChild(newChild);
		} else {
			throw new AccessDeniedException("The access conditions for add file were not fulfilled");
		}
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

	@Override
	public String toString() {
		return "DedicatedFile [dedicatedFileName=" + dedicatedFileName + ", createFiles=" + createFiles + "]";
	}
	
}
