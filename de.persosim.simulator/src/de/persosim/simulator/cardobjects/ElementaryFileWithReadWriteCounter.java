package de.persosim.simulator.cardobjects;

import de.persosim.simulator.exception.AccessDeniedException;

/**
 * This class represents an ISO7816-4 compliant elementary file in the object
 * hierarchy on the card and maintains a counter to track the number of reading
 * access.
 * 
 * @author cstroh
 *
 */
public class ElementaryFileWithReadWriteCounter extends ElementaryFile {

	private int readCounter;
	private int writeCounter;

	/**
	 * Creates a new {@link ElementaryFileWithReadWriteCounter} using only a full
	 * file identifier. Sets all access restrictions to denied.
	 * 
	 * @param fileIdentifier
	 *            used for identification of the file in the object tree
	 * @param content
	 *            the initial contents of the file
	 */
	public ElementaryFileWithReadWriteCounter(FileIdentifier fileIdentifier, byte[] content) {
		super(fileIdentifier, content);
		readCounter = 0;
		writeCounter = 0;
	}

	/**
	 * Creates a new {@link ElementaryFileWithReadWriteCounter} using both a full file identifier
	 * and a short file identifier. Sets all access restrictions to denied.
	 * 
	 * @param fileIdentifier
	 *            used for identification of the file in the object tree
	 * @param shortFileIdentifier
	 *            used for identification of the file in the object tree
	 * @param content
	 *            the initial contents of the file
	 */
	public ElementaryFileWithReadWriteCounter(FileIdentifier fileIdentifier, ShortFileIdentifier shortFileIdentifier,
			byte[] content) {
		super(fileIdentifier, shortFileIdentifier, content);
		readCounter = 0;
		writeCounter = 0;
	}

	@Override
	public byte[] getContent() throws AccessDeniedException {
		byte[] content = super.getContent();
		if(getLifeCycleState().isOperational()) {
			readCounter++;
		}
		return content;
	}
	
	@Override
	public void update(int offset, byte[] data) throws AccessDeniedException {
		super.update(offset, data);
		if(getLifeCycleState().isOperational()) {
			readCounter++;
		}
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