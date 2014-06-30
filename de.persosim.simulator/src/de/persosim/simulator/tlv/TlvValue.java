package de.persosim.simulator.tlv;

/**
 * This class implements the value field of any BER-TLV data object.
 * 
 * @author slutters
 * 
 */
public abstract class TlvValue extends TlvElement {
	
	/**
	 * Returns whether this value field is empty, i.e. its length is 0 bytes.
	 * @return whether this value field is empty
	 */
	public boolean isEmpty() {
		return this.getLength() == 0;
	}
	
}
