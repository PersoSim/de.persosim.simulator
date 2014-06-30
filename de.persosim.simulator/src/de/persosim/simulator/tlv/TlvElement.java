package de.persosim.simulator.tlv;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.utils.HexString;

/**
 * This class defines basic functionalities any element of a TLV data object
 * (tag, length, value) must provide.
 * 
 * Every TLV element generally is allowed to be constructed from arbitrary data
 * or accept to be set with them. The constructor or respective set-method will
 * check the data and reject invalid or malformed data if not forced to skip the
 * checks. If checks are to be skipped, this is documented within the element.
 * In order to assure valid values and encodings that comply with the respective
 * specifications all elements are required to provide respective check or
 * service methods.
 * 
 * @author slutters
 * 
 */
public abstract class TlvElement implements Iso7816, ValidityChecks {
	
	/**
	 * Returns a byte array representation of this object.
	 * @return a byte array representation of this object
	 */
	public abstract byte[] toByteArray();
	
	/**
	 * Returns the total number of bytes this object occupies, i.e. the actual length independent of what is indicated otherwise.
	 * @return the actual number of bytes this object occupies
	 */
	public abstract int getLength();
	
	@Override
	public String toString() {
		return HexString.encode(this.toByteArray());
	}
	
	/**
	 * Returns whether this object is a valid BER encoding of this element.
	 * This explicitly includes validity checks for potentially existing child elements in recursive data structures.
	 * This method is only supposed to return 'false' iff the object has been damaged intentionally.
	 * As TLV elements are expected to be BER encoded this is equivalent to a general validity check.
	 * If DER encoding is valid, BER encoding by definition is also valid.
	 * @return whether this object contains a valid BER encoding of this type
	 */
	public abstract boolean isValidBerEncoding();
	
	/**
	 * Returns whether this object is a valid DER encoding of this type.
	 * This explicitly includes validity checks for potentially existing child elements in recursive data structures.
	 * This method returns true iff the element is encoded according to DER encoding rules. Otherwise it will return
	 * false, i.e. explicitly if the object has been damaged intentionally.
	 * @return whether this object contains a valid DER encoding of this type
	 */
	public abstract boolean isValidDerEncoding();
	
}
