package de.persosim.simulator.cardobjects;

import java.util.Collection;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.ISO7816Exception;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * This class wraps an {@link ElementaryFile} and maintains a counter to track
 * the number of read/write access.
 * 
 * @author amay
 *
 */
public class ElementaryFileReadWriteCounterWrapper extends ElementaryFile implements CardObjectWrapper {

	private int readCounter;
	private int writeCounter;
	private ElementaryFile containedFile;

	/**
	 * Creates a new {@link ElementaryFileReadWriteCounterWrapper} using only a full
	 * file identifier. Sets all access restrictions to denied.
	 * 
	 * @param fileIdentifier
	 *            used for identification of the file in the object tree
	 * @param content
	 *            the initial contents of the file
	 */
	public ElementaryFileReadWriteCounterWrapper() {
		super(null, null);
		readCounter = 0;
		writeCounter = 0;
	}

	@Override
	public void setWrappedObject(CardObject cardObjectToWrap) {
		if (cardObjectToWrap instanceof ElementaryFile) {
			containedFile = (ElementaryFile) cardObjectToWrap;
		}
		
	}

	@Override
	public byte[] getContent() throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		byte[] content = containedFile.getContent();
		if(getLifeCycleState().isOperational()) {
			readCounter++;
		}
		return content;
	}
	
	@Override
	public void update(int offset, byte[] data) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.update(offset, data);
		if(getLifeCycleState().isOperational()) {
			writeCounter++;
		}
	}
	
	
	
	@Override
	public Collection<CardObject> getChildren() {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		return containedFile.getChildren();
	}

	@Override
	public void setReadingConditions(SecCondition readingConditions) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.setReadingConditions(readingConditions);
	}

	@Override
	public void setWritingConditions(SecCondition writingConditions) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.setWritingConditions(writingConditions);
	}

	@Override
	public void setErasingConditions(SecCondition erasingConditions) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.setErasingConditions(erasingConditions);
	}

	@Override
	public void setDeletionConditions(SecCondition deletionConditions) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.setDeletionConditions(deletionConditions);
	}

	@Override
	public void setShortFileIdentifier(ShortFileIdentifier shortFileIdentifier) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.setShortFileIdentifier(shortFileIdentifier);
	}

	@Override
	public void setContent(byte[] content) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.setContent(content);
	}

	@Override
	public void replace(byte[] data) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.replace(data);
	}

	@Override
	public void addChild(CardObject newChild) {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.addChild(newChild);
	}

	@Override
	public ConstructedTlvDataObject getFileControlParameterDataObject() {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		return containedFile.getFileControlParameterDataObject();
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		return containedFile.getAllIdentifiers();
	}

	@Override
	public void delete() throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}

		try {
			containedFile.delete();
			getParent().removeChild(this);
		} catch (AccessDeniedException e) {
			throw e;
		}
	}

	@Override
	public void erase(int startingOffset, int endingOffset) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.erase(startingOffset, endingOffset);
	}

	@Override
	public void erase() throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.erase();
	}

	@Override
	public void erase(int startingOffset) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.erase(startingOffset);
	}

	@Override
	public String toString() {
		if (containedFile == null) {
			return "Uninitialized "+ getClass().getSimpleName();
		}
		
		return containedFile.toString() + " wrapped with ReadWriteCounter";
	}

	@Override
	public ConstructedTlvDataObject getFileManagementDataObject() {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		return containedFile.getFileManagementDataObject();
	}

	@Override
	public void setSecStatus(SecStatus securityStatus) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.setSecStatus(securityStatus);
	}

	@Override
	public Iso7816LifeCycleState getLifeCycleState() {
		if (containedFile == null) {
			return Iso7816LifeCycleState.CREATION;
		}
		
		return containedFile.getLifeCycleState();
	}

	@Override
	public void updateLifeCycleState(Iso7816LifeCycleState state) throws AccessDeniedException {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		containedFile.updateLifeCycleState(state);
	}

	@Override
	public Collection<CardObject> findChildren(CardObjectIdentifier... cardObjectIdentifiers) {
		if (containedFile == null) {
			throw new ISO7816Exception(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "Wraper object not correctly initialized");
		}
		
		return containedFile.findChildren(cardObjectIdentifiers);
	}

	/**
	 * @return true if the file was read at least once.
	 */
	public boolean wasRead() {
		return (readCounter > 0);
	}
	
	/**
	 * @return true if the file was updated at least once.
	 */
	public boolean wasUpdated() {
		return (writeCounter > 0);
	}

	public int getReadCounter() {
		return readCounter;
	}
	
	public int getWriteCounter() {
		return writeCounter;
	}
}
