package de.persosim.simulator.tlv;

/**
 * This class describes a segment of a {@link TlvPath}. It contains a TlvTag to
 * be searched and an optional occurrence.
 * <p/>
 * If the occurrence is specified this {@link TlvTagIdentifier} identifies the
 * zero-based occurrence of the given tag in its container. Otherwise the first
 * occurrence is identified.
 * 
 * @author amay
 * 
 */
public class TlvTagIdentifier {

	private TlvTag tag;
	private int noOfPreviousOccurrences = 0;

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
	 * @param noOfPreviousOccurrences number of previous occurrences of the given TlvTag
	 */
	public TlvTagIdentifier(TlvTag tag, int noOfPreviousOccurrences) {
		this.tag = tag;
		this.noOfPreviousOccurrences = noOfPreviousOccurrences;
	}

	public TlvTag getTag() {
		return tag;
	}

	public int getNoOfPreviousOccurrences() {
		return noOfPreviousOccurrences;
	}
	
	

}
