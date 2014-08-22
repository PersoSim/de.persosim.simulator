package de.persosim.simulator.tlv;

/**
 * This class implements TLV data objects with primitive encoding, i.e. TLV
 * data objects that in their data field may only contain plain byte sequences
 * if any.
 * 
 * Tag field: All access to this element must be committed through methods
 * provided by this object. If constructed or set the field must not be set
 * directly as reference to the provided object but to a clone. Accordingly any
 * getter must also return a clone of this field.
 * 
 * This behavior e.g. is to prevent anyone from converting primitive encoding to
 * constructed by merely modifying the respective bit.
 * 
 * Length field: References to this object do not need to be protected. When
 * asked for its length field the object first checks whether any length field
 * has been set at all. If a length field has been set it checks whether its
 * encoded value matches the actual length of the value field. If so, the length
 * field is returned as is. Otherwise the default DER encoding of the actual
 * length of the value field is returned. In case an invalid length has been set
 * on purpose, checks can explicitly be disabled. This is flagged within the
 * object. If a flagged object is asked for its length field, it returns the set
 * length field without any checks. If no length field is set, it will be
 * computed and returned as if the flag had not been set.
 * 
 * This behavior saves costs for strict access control on length or value
 * fields.
 * 
 * Tag field: Analogous to the length field, the value field may also be
 * accessed freely. All problems that may arise from this are dealt by the way
 * the length field is determined.
 * 
 * @author slutters
 * 
 */
public class PrimitiveTlvDataObject extends TlvDataObject {
	protected TlvValuePlain tlvValuePlain;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Constructor for a TLV data object with primitive encoding based on a range
	 * from an array of raw bytes.
	 * The defined range must contain at least the whole TLV data object.
	 * The first byte of the range must also be the first byte of the TLV data object.
	 * The last byte of the range is not expected to also be the last byte of the TLV data
	 * object and may lay outside of the array.
	 * 
	 * @param byteArray the array that contains the TLV data object
	 * @param minOffset the first offset of the range to contain the TLV data object (inclusive)
	 * @param maxOffset the first offset of the range to not contain the TLV data object (exclusive).
	 * This offset may no longer be part of the array.
	 */
	public PrimitiveTlvDataObject(byte[] byteArray, int minOffset, int maxOffset) {
		super(byteArray, minOffset, maxOffset);
		
		if(!tlvTag.indicatesEncodingPrimitive()) {throw new IllegalArgumentException("tag must be primitive");}
		
		int currentOffset = minOffset + tlvTag.getLength() + tlvLength.getLength();
		
		tlvValuePlain = new TlvValuePlain(byteArray, currentOffset, currentOffset + tlvLength.getIndicatedLength());
	}
	
	/**
	 * Constructor for a TLV data object with primitive encoding based on an array of raw bytes.
	 * 
	 * @param byteArray the array that contains the TLV data object
	 */
	public PrimitiveTlvDataObject(byte[] byteArray) {
		this(byteArray, 0, byteArray.length);
	}
	
	/**
	 * Constructs an object from pre-fabricated elements explicitly setting a length field.
	 * Length fields only need to be explicitly set if their encoding complies with BER but
	 * not DER encoding rules or their encoding is intentionally damaged.
	 * @param tlvTagInput the tag to be used
	 * @param tlvLengthInput the length to be used (may be null)
	 * @param tlvValuePlainInput the value to be used
	 * @param performValidityChecksInput true: perform validity checks, false: do not perform validity checks
	 */
	public PrimitiveTlvDataObject(TlvTag tlvTagInput, TlvLength tlvLengthInput, TlvValuePlain tlvValuePlainInput, boolean performValidityChecksInput) {
		super(performValidityChecksInput);
		
		if(tlvTagInput == null) {throw new NullPointerException("tag must not be null");}
		if(tlvValuePlainInput == null) {throw new NullPointerException("value must not be null");}
		
		this.setTag(tlvTagInput, performValidityChecksInput);
		this.setValue(tlvValuePlainInput);
		
		if(tlvLengthInput != null) {
			/* length must be set last as its validity check depends on the value already being set */
			this.setLength(tlvLengthInput, performValidityChecksInput);
		}
	}
	
	/**
	 * Constructs an object from pre-fabricated elements explicitly setting a length field.
	 * Length fields only need to be explicitly set if their encoding complies with BER but
	 * not DER encoding rules or their encoding is intentionally damaged.
	 * @param tlvTagInput the tag to be used
	 * @param tlvLengthInput the length to be used
	 * @param tlvValuePlainInput the value to be used
	 */
	public PrimitiveTlvDataObject(TlvTag tlvTagInput, TlvLength tlvLengthInput, TlvValuePlain tlvValuePlainInput) {
		this(tlvTagInput, tlvLengthInput, tlvValuePlainInput, PERFORM_VALIDITY_CHECKS);
	}
	
	/**
	 * Constructs an object from pre-fabricated elements. Length field is implicitly set
	 * according to DER encoding rules by default.
	 * @param tlvTagInput the tag to be used
	 * @param tlvValuePlainInput the value to be used
	 * @param performValidityChecksInput true: perform validity checks, false: do not perform validity checks
	 */
	public PrimitiveTlvDataObject(TlvTag tlvTagInput, TlvValuePlain tlvValuePlainInput, boolean performValidityChecksInput) {
		this(tlvTagInput, null, tlvValuePlainInput, performValidityChecksInput);
	}
	
	/**
	 * Constructs an object from pre-fabricated elements. Length field is implicitly set
	 * according to DER encoding rules by default.
	 * @param tlvTagInput the tag to be used
	 * @param tlvValuePlainInput the value to be used
	 */
	public PrimitiveTlvDataObject(TlvTag tlvTagInput, TlvValuePlain tlvValuePlainInput) {
		this(tlvTagInput, tlvValuePlainInput, PERFORM_VALIDITY_CHECKS);
	}
	
	/**
	 * Constructs an object from a pre-fabricated tag and a provided array of raw value bytes
	 * @param tlvTagInput the tag to be used
	 * @param tlvValuePlainInput the raw value bytes to be used
	 */
	public PrimitiveTlvDataObject(TlvTag tlvTagInput, byte[] tlvValuePlainInput) {
		this(tlvTagInput, new TlvValuePlain(tlvValuePlainInput));
	}
	
	/**
	 * Creates an empty object containing only a tag and length of zero.
	 * @param tlvTagInput the tag to be used
	 */
	public PrimitiveTlvDataObject(TlvTag tlvTagInput) {
		this(tlvTagInput, new TlvValuePlain());
	}
	
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public void setTag(TlvTag tlvTagInput, boolean performValidityChecksInput) {
		if(tlvTagInput == null) {throw new NullPointerException("tag must not be null");}
		
		if(!tlvTagInput.indicatesEncodingPrimitive()) {throw new IllegalArgumentException("tag must be primitive");}
		
		performValidityChecks = performValidityChecksInput;
		
		if(performValidityChecks) {
			if(!tlvTagInput.isValidBerEncoding()) {throw new IllegalArgumentException("tag must be valid BER encoding");};
		}
		
		/*
		 * TLV tag must be cloned to eliminate outside access to this object.
		 * The tag must only be set by methods offered by this class e.g. to
		 * prevent setting the primitive tag to be a constructed tag.
		 */
		tlvTag = tlvTagInput.clone();
	}
	
	@Override
	public byte[] getValueField() {
		return tlvValuePlain.toByteArray();
	}

	@Override
	public int getNoOfValueBytes() {
		return tlvValuePlain.getLength();
	}
	
	/**
	 * Sets the value field of this object.
	 * Method does not override a super class method due to different signatures required for this method
	 * in both subclasses for primitive and constructed encoding.
	 * @param tlvValuePlainInput the value to be set
	 */
	public void setValue(byte[] tlvValuePlainInput) {
		setValue(new TlvValuePlain(tlvValuePlainInput));
	}
	
	/**
	 * Sets the value field of this object.
	 * Method does not override a super class method due to different signatures required for this method
	 * in both subclasses for primitive and constructed encoding.
	 * @param tlvValuePlainInput the value to be set
	 */
	public void setValue(TlvValuePlain tlvValuePlainInput) {
		if(tlvValuePlainInput == null) {throw new NullPointerException("value must not be null");}	
		tlvValuePlain = tlvValuePlainInput;
	}

	@Override
	public TlvValuePlain getTlvValue() {
		return tlvValuePlain;
	}
	
	@Override
	public boolean isValidBerEncoding() {
		if(!super.isValidBerEncoding()) {return false;}
		
		return tlvTag.indicatesEncodingPrimitive();
	}
	
}
