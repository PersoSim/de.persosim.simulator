package de.persosim.simulator.tlv;

/**
 * This class describes a segment of a {@link TlvPath}. It contains a TlvTag to
 * be searched and an optional occurrence.
 * <p/>
 * If the occurrence is specified this {@link TlvTagIdentifier} identifies the
 * zero-based occurrence of the given tag in its container. Otherwise the first
 * occurrence is identified.
 * 
 * FIXME AMY either start counting occurrences at 1 or rename variable
 * what about "noOfPreviousOccurrences" - getOccurence() e.g. at one place is directly assigned to a variable named "remainingOccurences".
 * 
 * @author amay
 * 
 */
public class TlvTagIdentifier {

	private TlvTag tag;
	private int occurrence = 0;

	/**
	 * Create an identifier matching the given tag (and the first occurrence).
	 * @param tag TlvTag to be matched
	 */
	public TlvTagIdentifier(TlvTag tag) {
		this(tag, 0);
	}

	/**
	 * Create an identifier matching the given tag (and the first occurrence).
	 * @param tag TlvTag to be matched
	 * @param occurrence Occurrence of the given TlvTag
	 */
	public TlvTagIdentifier(TlvTag tag, int occurrence) {
		this.tag = tag;
		this.occurrence = occurrence;
	}

	public TlvTag getTag() {
		return tag;
	}

	public int getOccurrence() {
		return occurrence;
	}
	
	

}
