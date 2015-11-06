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
 * This class represents an ISO7816-4 compliant elementary file in the object
 * hierarchy on the card
 * 
 * @author mboonk
 *
 */
public class ElementaryFile extends AbstractFile {

	private byte[] content;

	private ShortFileIdentifier shortFileIdentifier;

	private SecCondition readingConditions;

	private SecCondition writingConditions;

	private SecCondition erasingConditions;

	private SecCondition deletionConditions;

	/**
	 * Creates a new {@link ElementaryFile} using both a full file identifier
	 * and a short file identifier.
	 * 
	 * @param fileIdentifier
	 *            used for identification of the file in the object tree
	 * @param shortFileIdentifier
	 *            used for identification of the file in the object tree
	 * @param content
	 *            the initial contents of the file
	 * @param readingConditions
	 *            access restrictions for reading the file contents
	 * @param writingConditions
	 *            access restrictions for updating or writing the file contents
	 * @param erasingConditions
	 *            access restrictions for erasing (setting bytes to zero) of the
	 *            file contents
	 * @param deletionConditions
	 *            access restrictions for deletion (removal from the object
	 *            tree) of the file
	 */
	public ElementaryFile(FileIdentifier fileIdentifier, ShortFileIdentifier shortFileIdentifier, byte[] content,
			SecCondition readingConditions, SecCondition writingConditions, SecCondition erasingConditions,
			SecCondition deletionConditions) {
		this(fileIdentifier, content, readingConditions, writingConditions, erasingConditions, deletionConditions);
		this.shortFileIdentifier = shortFileIdentifier;
	}
	
	/**
	 * Creates a new {@link ElementaryFile} using both a full file identifier
	 * and a short file identifier.
	 * 
	 * @param fileIdentifier
	 *            used for identification of the file in the object tree
	 * @param shortFileIdentifier
	 *            used for identification of the file in the object tree
	 * @param content
	 *            the initial contents of the file
	 * @param readingConditions
	 *            access restrictions for reading the file contents
	 * @param writingConditions
	 *            access restrictions for updating or writing the file contents
	 * @param erasingConditions
	 *            access restrictions for erasing (setting bytes to zero) of the
	 *            file contents
	 */
	public ElementaryFile(FileIdentifier fileIdentifier, ShortFileIdentifier shortFileIdentifier, byte[] content,
			SecCondition readingConditions, SecCondition writingConditions, SecCondition erasingConditions) {
		this(fileIdentifier, shortFileIdentifier, content, readingConditions, writingConditions, erasingConditions, SecCondition.DENIED);
		this.shortFileIdentifier = shortFileIdentifier;
	}
	
	/**
	 * 
	 * Creates a new {@link ElementaryFile} using only a full file identifier.
	 * 
	 * @param fileIdentifier
	 * @param content
	 *            the initial contents of the file
	 * @param readingConditions
	 *            access restrictions for reading the file contents
	 * @param writingConditions
	 *            access restrictions for updating or writing the file contents
	 * @param erasingConditions
	 *            access restrictions for erasing (setting bytes to zero) of the
	 *            file contents
	 * @param deletionConditions
	 *            access restrictions for deletion (removal from the object
	 *            tree) of the file
	 */
	public ElementaryFile(FileIdentifier fileIdentifier, byte[] content, SecCondition readingConditions,
			SecCondition writingConditions, SecCondition erasingConditions, SecCondition deletionConditions) {
		super(fileIdentifier);
		this.content = content;
		this.readingConditions = readingConditions;
		this.writingConditions = writingConditions;
		this.erasingConditions = erasingConditions;
		this.deletionConditions = deletionConditions;
	}

	@Override
	public Collection<CardObject> getChildren() {
		return Collections.emptySet();
	}

	/**
	 * Reads the files internal data.
	 * 
	 * @return stored data as byte array
	 */
	public byte[] getContent() throws AccessDeniedException {
		if (securityStatus.checkAccessConditions(getLifeCycleState(), readingConditions)) {
			return Arrays.copyOf(content, content.length);
		}
		throw new AccessDeniedException("Reading forbidden");
	}

	/**
	 * Replaces the files internal data.
	 * 
	 * @param data
	 *            to be used as a replacement
	 */
	public void update(int offset, byte[] data) throws AccessDeniedException {
		if (securityStatus.checkAccessConditions(getLifeCycleState(), writingConditions)) {
			for (int i = 0; i < data.length; i++) {
				content[i + offset] = data[i];
			}
			return;
		}
		throw new AccessDeniedException("Updating forbidden");
	}

	/**
	 * Completely replaces the files internal data.
	 * 
	 * @param data
	 *            to be used as a replacement
	 */
	public void replace(byte[] data) throws AccessDeniedException {
		if (SecStatus.checkAccessConditions(getLifeCycleState())) {
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
		ConstructedTlvDataObject result = super.getFileControlParameterDataObject();

		result.addTlvDataObject(new PrimitiveTlvDataObject(new TlvTag((byte) 0x80),
				Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(content.length))));

		result.addTlvDataObject(new PrimitiveTlvDataObject(new TlvTag((byte) 0x88),
				Utils.toUnsignedByteArray((byte) shortFileIdentifier.getShortFileIdentifier())));

		return result;
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = super.getAllIdentifiers();
		result.add(shortFileIdentifier);
		return result;
	}

	/**
	 * Removes this file from its parent.
	 * 
	 * @throws AccessDeniedException
	 */
	public void delete() throws AccessDeniedException {
		if (securityStatus.checkAccessConditions(getLifeCycleState(), deletionConditions)) {
			getParent().removeChild(this);
			return;
		}
		throw new AccessDeniedException("The access conditions do not allow deletion of this file.");
	}

	/**
	 * This method erases the indicated content of this file. The sequence of
	 * bytes between the offsets is erased.
	 * 
	 * @param startingOffset
	 *            the first byte to erase
	 * @param endingOffset
	 *            the first byte NOT to erase
	 * @throws AccessDeniedException
	 * @throws {@link
	 *             IllegalArgumentException}, if the given offsets can not be
	 *             used for erasing contents
	 */
	public void erase(int startingOffset, int endingOffset) throws AccessDeniedException {
		if (securityStatus == null || securityStatus.checkAccessConditions(getLifeCycleState(), erasingConditions)) {

			if (startingOffset < 0 | endingOffset > content.length | endingOffset < startingOffset) {
				throw new IllegalArgumentException(
						"The given offset combination (" + startingOffset + "," + endingOffset + ") is not feasible");
			}

			for (int i = startingOffset; i < endingOffset; i++) {
				content[i] = 0;
			}

			return;
		}
		throw new AccessDeniedException("The access conditions do not allow erasing of this files contents.");
	}

	/**
	 * This method erases the content of this file.
	 * 
	 * @throws AccessDeniedException
	 */
	public void erase() throws AccessDeniedException {
		erase(0, content.length);
	}

	/**
	 * This method erases the indicated content of this file. The sequence of
	 * bytes between the startingOffset and the end of the file is erased.
	 * 
	 * @param startingOffset
	 *            the first byte to erase
	 * @throws AccessDeniedException
	 * @throws {@link
	 *             IllegalArgumentException}, if the given offset does not fit
	 *             the file
	 */
	public void erase(int startingOffset) throws AccessDeniedException {
		erase(startingOffset, content.length);
	}
}
