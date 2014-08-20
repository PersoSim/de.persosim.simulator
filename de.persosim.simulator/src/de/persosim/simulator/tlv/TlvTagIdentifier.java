package de.persosim.simulator.tlv;

/**
 * This class describes a segment of a {@link TlvPath}. It contains a TlvTag to
 * be searched and an optional occurence.
 * <p/>
 * If the occurence is specified this {@link TlvTagIdentifier} identifies the
 * zero-based occurence of the given tag in its container. Otherwise the first
 * occurence is identified.
 * 
 * @author amay
 * 
 */
public class TlvTagIdentifier {

	private TlvTag tag;
	private int occurence = 0;

	/**
	 * Create an identifier matching the given tag (and the first occurence).
	 * @param tag TlvTag to be matched
	 */
	public TlvTagIdentifier(TlvTag tag) {
		this(tag, 0);
	}

	/**
	 * Create an identifier matching the given tag (and the first occurence).
	 * @param tag TlvTag to be matched
	 * @param occurence Occurence of the given TlvTag
	 */
	public TlvTagIdentifier(TlvTag tag, int occurence) {
		this.tag = tag;
		this.occurence = occurence;
	}

	public TlvTag getTag() {
		return tag;
	}

	public int getOccurence() {
		return occurence;
	}
	
	

}
