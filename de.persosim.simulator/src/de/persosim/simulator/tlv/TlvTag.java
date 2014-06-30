package de.persosim.simulator.tlv;

import java.util.Arrays;

import de.persosim.simulator.exception.ISO7816Exception;
import de.persosim.simulator.utils.Utils;

/**
 * This class implements the tag field of any TLV data object.
 * 
 * @author slutters
 *
 */
public class TlvTag extends TlvElement implements Asn1 {
	
	protected byte[] tagField;
	
	/*--------------------------------------------------------------------------------*/
	
	public TlvTag() {
	}
	
	/**
	 * Constructor for this object based on a range defined on an array of raw bytes.
	 * 
	 * WARNING: If validity checks are to be skipped, the tag will be set to the full range.
	 * This may cause serious problems if the range is also expected to contain a length and
	 * probably also a value field as it is the case during construction of any TLV data object
	 * or container from raw byte arrays. 
	 * 
	 * @param tagFieldInput the byte array that in a certain range contains the TLV tag
	 * @param minOffset the first offset of the range to contain the TLV tag (inclusive)
	 * @param maxOffset the first offset not to be part of the range to contain the TLV tag (exclusive).
	 * @param performValidityChecks true: perform validity checks, false: do not perform validity checks
	 */
	public TlvTag(byte[] tagFieldInput, int minOffset, int maxOffset, boolean performValidityChecks) {
		super();
		
		if(performValidityChecks == PERFORM_VALIDITY_CHECKS) {
			this.setTagField(tagFieldInput, minOffset, maxOffset);
		} else{
			this.forceTagField(Arrays.copyOfRange(tagFieldInput, minOffset, maxOffset));
		}
	}
	
	/**
	 * Constructor for this object based on a range defined on an array of raw bytes.
	 * 
	 * @param tagFieldInput the byte array that in a certain range contains the TLV tag
	 * @param minOffset the first offset of the range to contain the TLV tag (inclusive)
	 * @param maxOffset the first offset not to be part of the range to contain the TLV tag (exclusive).
	 */
	public TlvTag(byte[] tagFieldInput, int minOffset, int maxOffset) {
		this(tagFieldInput, minOffset, maxOffset, PERFORM_VALIDITY_CHECKS);
	}
	
	/**
	 * Constructor for this object based on an array of raw bytes.
	 * 
	 * WARNING: If validity checks are to be skipped, the tag will be set to the full array length.
	 * This may cause serious problems if the range is also expected to contain a length and
	 * probably also a value field as it is the case during construction of any TLV data object
	 * or container from raw byte arrays. 
	 * 
	 * @param tagFieldInput the byte array that contains the TLV tag
	 * @param performValidityChecks true: perform validity checks, false: do not perform validity checks
	 */
	public TlvTag(byte[] tagFieldInput, boolean performValidityChecks) {
		this(tagFieldInput, 0, tagFieldInput.length, performValidityChecks);
	}
	
	/**
	 * Constructor for this object based on an array of raw bytes.
	 * 
	 * @param tagFieldInput the byte array that contains the TLV tag
	 */
	public TlvTag(byte[] tagFieldInput) {
		this(tagFieldInput, PERFORM_VALIDITY_CHECKS);
	}
	
	/**
	 * Constructor for this object based on a short.
	 * 
	 * WARNING: If validity checks are to be skipped, the tag will be set to the full short's length.
	 * This may cause serious problems if the short is also expected to contain a length and
	 * probably also a value field as it is the case during construction of any TLV data object
	 * or container from raw byte arrays. 
	 * 
	 * @param tagFieldInput the short that contains the TLV tag
	 * @param performValidityChecks true: perform validity checks, false: do not perform validity checks
	 */
	public TlvTag(short tagFieldInput, boolean performValidityChecks) {
		this(Utils.toUnsignedByteArray(tagFieldInput), 0, 2, performValidityChecks);
		}
	
	/**
	 * Constructor for this object based on a short.
	 * 
	 * @param tagFieldInput the short that contains the TLV tag
	 */
	public TlvTag(short tagFieldInput) {
		this(tagFieldInput, PERFORM_VALIDITY_CHECKS);
	}
	
	/**
	 * Constructor for this object based on a byte.
	 * 
	 * @param tagFieldInput the byte that contains the TLV tag
	 * @param performValidityChecks true: perform validity checks, false: do not perform validity checks
	 */
	public TlvTag(byte tagFieldInput, boolean performValidityChecks) {
		this(Utils.toUnsignedByteArray(tagFieldInput), 0, 1, performValidityChecks);
	}
	
	/**
	 * Constructor for this object based on a byte.
	 * 
	 * @param tagFieldInput the byte that contains the TLV tag
	 */
	public TlvTag(byte tagFieldInput) {
		this(tagFieldInput, PERFORM_VALIDITY_CHECKS);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * This method sets the TLV tag based on a raw byte array.
	 * 
	 * The variables minOffset and maxOffset specify a range that is supposed to contain the tag.
	 * When parsing a raw byte array representation of a tag, the exact length of it is previously unknown.
	 * This method will treat all bytes within the specified range as potentially being part of the tag field.
	 * The tag finally will exclusively be created from the bytes that have been identified as being part of a valid
	 * tag field. This explicitly allows for the range to contain more bytes than are actually part of the tag.
	 * This is the exact reason why this method can not be executed without performing validity checks.
	 * In case validity checks are to be omitted, the setting can be forced using the force method.
	 * 
	 * @param tagFieldInput the data field that contains the range containing the tag field
	 * @param minOffset the first offset of the tag field (inclusive)
	 * @param maxOffset the first offset no longer belonging to the range containing the tag field (exclusive)
	 */
	public void setTagField(byte[] tagFieldInput, int minOffset, int maxOffset) {
		if(tagFieldInput == null) {throw new NullPointerException();}
		if(minOffset < 0) {throw new IllegalArgumentException("min offset must not be less than 0");}
		if(maxOffset < minOffset) {throw new IllegalArgumentException("max offset must not be smaller than min offset");}
		if(maxOffset > tagFieldInput.length) {throw new IllegalArgumentException("selected array area must not lie outside of data array");}
		if(minOffset == maxOffset) {throw new IllegalArgumentException("selected part of data field must be greater than 0");}
		
		int currentOff = minOffset;
		byte currentByte = tagFieldInput[currentOff];
		
		/*
		 * Determine Tag
		 */
		boolean isSecondByteOfMultiByteTag = false;
		if(!((byte) (currentByte & (byte) 0x1F) == (byte) 0x1F)) {
			/* if this tag has a short tag, i.e. 1 byte tag field (0 <= tag <= 30) */
			this.tagField = Arrays.copyOfRange(tagFieldInput, currentOff, currentOff + 1);
		} else{
			isSecondByteOfMultiByteTag = true;
			while(true) {
				/* assume tag to be at least one byte longer */
				/* change to next byte */
			currentOff++;
			if(currentOff >= maxOffset) {ISO7816Exception.throwIt(SW_6A85_NC_INCONSISTENT_WITH_TLV_STRUCTURE, "offset outside data array");}
				currentByte = tagFieldInput[currentOff];
			
				if(isSecondByteOfMultiByteTag) {
					isSecondByteOfMultiByteTag = false;
					if(((byte) (currentByte & (byte) 0x7F) == (byte) 0x00)) {
						/* error, second byte must not be 0 in bits 7-1, i.e. indicated tag length would have also fit into smaller tag */
						ISO7816Exception.throwIt(SW_6A80_WRONG_DATA);
					}
			
					if(((byte) (currentByte & (byte) 0x80)) == (byte) 0x00) {
						/* if this is a 2 byte Tag */
						if(((byte) (currentByte & (byte) 0xEF)) <= 30) {
							/* tag number is unsigned as first bit has been checked as not set before */
							/* error, 2 byte tag must not encode a tag number <= 30 */
							/* tag number <= 30 would have also fit into 1-byte tag */
				ISO7816Exception.throwIt(SW_6A80_WRONG_DATA);
			}
				}
				}
				
				if(((byte) (currentByte & (byte) 0x80)) == (byte) 0x00) {
					/* if this is the last byte of the tag */
				
					if(((currentOff - minOffset) + 1) > 3) {
						/* error, tag is longer than the allowed 3 bytes */
					ISO7816Exception.throwIt(SW_6A80_WRONG_DATA);
				}
				
					this.tagField = Arrays.copyOfRange(tagFieldInput, minOffset, currentOff + 1);
					return;
				}
			}
		}
	}
	
	@Override
	public boolean isValidBerEncoding() {
		int tagFieldLength = tagField.length;
		
		/* ensure valid length */
		if((tagFieldLength < 1) || (tagFieldLength > 3)) {return false;};
		
		/* ensure valid formatting of value */
		if(tagFieldLength == 1) {
			if((tagField[0] & ((short) 0x1F)) == ((short) 0x1F)) {return false;};
		} else{
			if((tagField[1] & ((short) 0x7F)) == ((short) 0x00)) {return false;};
			
			for(int i = 1; i < tagFieldLength; i++) {
				if(i == (tagFieldLength - 1)) {
					if((tagField[i] & ((short) 0x80)) == ((short) 0x80)) {return false;};
				} else{
					if((tagField[i] & ((short) 0x80)) != ((short) 0x80)) {return false;};
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean isValidDerEncoding() {
		// No different encoding for a tag is possible, so the checks for BER encoding suffice
		return isValidBerEncoding();
	}
	
	/**
	 * Sets the tag field, validity checks will be performed
	 * @param tagFieldInput the tag field that is supposed to represent the tag from first to last byte
	 */
	public void setTagField(byte[] tagFieldInput) {
		this.setTagField(tagFieldInput, 0, tagFieldInput.length);
	}

	/**
	 * Sets the tag field, validity checks will be performed
	 * @param tagFieldInput the tag field that is supposed to represent the tag from first to last byte
	 */
	public void setTagField(byte tagFieldInput) {
		this.setTagField(Utils.toUnsignedByteArray(tagFieldInput));
	}
	
	/**
	 * Sets the tag field, validity checks will be performed
	 * @param tagFieldInput the tag field that is supposed to represent the tag from first to last byte
	 */
	public void setTagField(short tagFieldInput) {
		this.setTagField(Utils.toUnsignedByteArray(tagFieldInput));
	}
	
	/**
	 * Sets the tag field, validity checks will be skipped
	 * @param tagFieldInput the tag field that is supposed to represent the tag from first to last byte
	 */
	public void forceTagField(byte[] tagFieldInput) {
		if(tagFieldInput == null) {throw new NullPointerException();}
	
		this.tagField = tagFieldInput;
	}
	
	/**
	 * Sets the tag field, validity checks will be skipped
	 * @param tagFieldInput the tag field that is supposed to represent the tag from first to last byte
	 */
	public void forceTagField(byte tagFieldInput) {
		this.forceTagField(Utils.toUnsignedByteArray(tagFieldInput));
	}
	
	/**
	 * Sets the tag field, validity checks will be skipped
	 * @param tagFieldInput the tag field that is supposed to represent the tag from first to last byte
	 */
	public void forceTagField(short tagFieldInput) {
		this.forceTagField(Utils.toUnsignedByteArray(tagFieldInput));
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns the tag number encoded within the tag field
	 * @return the tag number encoded within the tag field
	 */
	public int getIndicatedTagNo() {
		int tagNo, currentOffset;
		
		currentOffset = 0;
		tagNo = 0;
		
		if(((byte) (this.tagField[currentOffset] & (byte) 0x1F)) == (byte) 0x1F) {
			/* if tag length > 1 */
			for(int i = 1; i < this.getLength(); i++) {
				currentOffset++;
				
				tagNo <<= 7;
				tagNo |= (byte) (this.tagField[currentOffset] & (byte) 0x7F);
			}
		} else{
			/* if tag length == 1 */
			tagNo = Utils.maskUnsignedByteToInt((byte) (this.tagField[currentOffset] & (byte) 0x1F));
		}
		
		return tagNo;
	}
	
	/**
	 * Returns whether this tag field indicates primitive encoding (bit 6 of first tag field == 0)
	 * @return whether this tag field indicates primitive encoding
	 */
	public boolean indicatesEncodingPrimitive() {
		return getEncoding(this.tagField) == ENCODING_PRIMITIVE;
	}
	
	/**
	 * Returns whether this tag field indicates constructed encoding (bit 6 of first tag field == 1)
	 * @return whether this tag field indicates constructed encoding
	 */
	public boolean indicatesEncodingConstructed() {
		return getEncoding(this.tagField) == ENCODING_CONSTRUCTED;
	}
	
	/**
	 * Returns whether this tag field indicates the provided class.
	 * Valid classes are:
	 * {@link Asn1#CLASS_UNIVERSAL universal}
	 * {@link Asn1#CLASS_APPLICATION application}
	 * {@link Asn1#CLASS_CONTEXT_SPECIFIC context specific}
	 * {@link Asn1#CLASS_PRIVATE private}
	 * @return whether this tag field indicates the provided class
	 */
	public boolean indicatesClass(byte indicatedClass) {
		return getEncodedClass(this.tagField) == indicatedClass;
	}
	
	public boolean matches(TlvTag anotherTlvTag) {
		if (!Arrays.equals(this.tagField, anotherTlvTag.tagField)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean equals(Object anotherTlvTag) {
		if (!(anotherTlvTag instanceof TlvTag)) {
			return false;
		}
		
		//TlvTags are considered equal iff they encode the same tag value in the same way
		return Arrays.equals(tagField, ((TlvTag) anotherTlvTag).tagField);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		for (int i = 0; i < tagField.length; i++) {
			hash *= tagField[i];
		}
		return hash;
	}

	public byte getEncodedClass() {
		return getEncodedClass(this.tagField);
	}
	
	@Override
	public int getLength() {
		return tagField.length;
	}
	
	@Override
	public byte[] toByteArray() {
		return tagField;
	}
	
	@Override
	public TlvTag clone() {
		return new TlvTag(this.toByteArray(), SKIP_VALIDITY_CHECKS);
	}
	
	/**
	 * Returns the class of the provided tag field
	 * @param tagField the provided tag field
	 * @return the class of the provided tag field
	 */
	public static byte getEncodedClass(byte[] tagField) {
		if(tagField == null) {throw new NullPointerException("tag field must not be null");}
		return (byte) (tagField[0] & CLASS);
	}
	
	/**
	 * Returns the encoding of the provided tag field
	 * @param tagField the provided tag field
	 * @return the encoding of the provided tag field
	 */
	public static byte getEncoding(byte[] tagField) {
		if(tagField == null) {throw new NullPointerException("tag field must not be null");}
		return (byte) (tagField[0] & ENCODING);
	}
	
}
