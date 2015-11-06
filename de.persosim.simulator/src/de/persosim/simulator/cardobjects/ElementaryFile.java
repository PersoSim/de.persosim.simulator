package de.persosim.simulator.cardobjects;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.Utils;

/**
 * This class represents an ISO7816-4 compliant elementary file in the object hierarchy on the card
 * @author mboonk
 *
 */
public class ElementaryFile extends AbstractFile {

	private byte[] content;
	
	private ShortFileIdentifier shortFileIdentifier;

	private SecCondition readingConditions;
	
	private SecCondition writingConditions;
	
	@SuppressWarnings("unused")  //IMPL MBK implement the ISO7816 erase functionality for files
	private SecCondition erasingConditions;
	
	/**
	 * Default constructor fur JAXB usage.
	 */
	public ElementaryFile(){
		
	}
			
	public ElementaryFile(FileIdentifier fileIdentifier,
			ShortFileIdentifier shortFileIdentifier, byte[] content, SecCondition readingConditions, SecCondition writingConditions, SecCondition erasingConditions) {
		super(fileIdentifier);
		this.shortFileIdentifier = shortFileIdentifier;
		this.content = content;
		this.readingConditions = readingConditions;
		this.writingConditions = writingConditions;
		this.erasingConditions = erasingConditions;
	}

	@Override
	public Collection<CardObject> getChildren() {
		return Collections.emptySet();
	}

	/**
	 * Reads the files internal data.
	 * @return stored data as byte array
	 */
	public byte[] getContent() throws AccessDeniedException {
		if (securityStatus == null || securityStatus.checkAccessConditions(getLifeCycleState(), readingConditions)){
			return Arrays.copyOf(content, content.length);
		}
		throw new AccessDeniedException("Reading forbidden");
	}

	/**
	 * Replaces the files internal data.
	 * @param data to be used as a replacement
	 */
	public void update(int offset, byte[] data) throws AccessDeniedException {
		if (securityStatus == null || securityStatus.checkAccessConditions(getLifeCycleState(), writingConditions)){
			for(int i = 0; i < data.length; i++){
				content[i + offset] = data[i];
			}
			return;
		}
		throw new AccessDeniedException("Updating forbidden");
	}

	/**
	 * Completely replaces the files internal data.
	 * @param data to be used as a replacement
	 */
	public void replace(byte[] data) throws AccessDeniedException {
		if (SecStatus.checkAccessConditions(getLifeCycleState())){
			content = Arrays.copyOf(data, data.length);
			return;
		}
		throw new AccessDeniedException("Updating forbidden");
	}

	@Override
	public void addChild(CardObject newChild) {
	}

	@Override
	public ConstructedTlvDataObject getFileControlParameterDataObject() {
		ConstructedTlvDataObject result = super
				.getFileControlParameterDataObject();

		result.addTlvDataObject(new PrimitiveTlvDataObject(new TlvTag(
				(byte) 0x80), Utils.removeLeadingZeroBytes(Utils
				.toUnsignedByteArray(content.length))));

		result.addTlvDataObject(new PrimitiveTlvDataObject(new TlvTag(
				(byte) 0x88), Utils
				.toUnsignedByteArray((byte) shortFileIdentifier
						.getShortFileIdentifier())));

		return result;
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = super.getAllIdentifiers();
		result.add(shortFileIdentifier);
		return result;
	}


}
