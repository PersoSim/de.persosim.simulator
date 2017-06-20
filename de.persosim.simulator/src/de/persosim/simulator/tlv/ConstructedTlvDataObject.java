package de.persosim.simulator.tlv;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * This class implements BER-TLV data objects with constructed encoding, i.e.
 * TLV data objects that in their data field again may contain an arbitrarily
 * large number of TLV data objects.
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
 * encoded value matches the actual length of the value field (This is
 * recursively computed solely based on the actual length of any (sub-)
 * element). If so, the length field is returned as is. Otherwise the default
 * DER encoding of the actual length of the value field is returned. In case an
 * invalid length has been set on purpose, checks can explicitly be disabled.
 * This is flagged within the object. If a flagged object is asked for its
 * length field, it returns the set length field without any checks. If no
 * length field is set, it will be computed and returned as if the flag had not
 * been set.
 * 
 * This behavior saves costs for strict access control on length or value
 * fields.
 * 
 * Value field: Analogous to the length field, the value field may also be
 * accessed freely. All problems that may arise from this are dealt by the way
 * the length field is determined. Considering the possible complexity of
 * constructed TLV data objects, the costs for ensuring restrictive access
 * control would exceed any benefit from it.
 * 
 * @author slutters
 * 
 */
public class ConstructedTlvDataObject extends TlvDataObject implements TlvDataStructure {
	protected TlvDataObjectContainer tlvDataObjectContainer;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Constructor for a TLV data object with constructed encoding based on a range
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
	public ConstructedTlvDataObject(byte[] byteArray, int minOffset, int maxOffset) {
		super(byteArray, minOffset, maxOffset);
		
		if(!tlvTag.indicatesEncodingConstructed()) {throw new IllegalArgumentException("tag must be constructed");}
		
		int minOffsetSub = minOffset + tlvTag.getLength() + tlvLength.getLength();
		int maxOffsetSub = (minOffsetSub + tlvLength.getIndicatedLength());
		
		tlvDataObjectContainer = new TlvDataObjectContainer(byteArray, minOffsetSub, maxOffsetSub);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((tlvDataObjectContainer == null) ? 0 : tlvDataObjectContainer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConstructedTlvDataObject other = (ConstructedTlvDataObject) obj;
		if (tlvDataObjectContainer == null) {
			if (other.tlvDataObjectContainer != null)
				return false;
		} else if (!tlvDataObjectContainer.equals(other.tlvDataObjectContainer))
			return false;
		return true;
	}

	/**
	 * Constructor for a TLV data object with constructed encoding based on a fixed array of raw bytes.
	 * The defined byte array must contain exactly the whole TLV data object.
	 * 
	 * @param byteArray the array that contains the TLV data object
	 */
	public ConstructedTlvDataObject(byte[] byteArray) {
		this(byteArray, 0, byteArray.length);
		
		if(this.getLength() != byteArray.length) {
			throw new IllegalArgumentException("input data is longer than actual TLV data object");
		}
	}
	
	/**
	 * Constructs an object from pre-fabricated elements explicitly setting a length field.
	 * Length fields only need to be explicitly set if their encoding complies with BER but
	 * not DER encoding rules or their encoding is intentionally damaged.
	 * @param tlvTagInput the tag to be used
	 * @param tlvLengthInput the length to be used
	 * @param tlvDataObjectContainerInput the data to be used
	 * @param performValidityChecksInput true: perform validity checks, false: do not perform validity checks
	 */
	public ConstructedTlvDataObject(TlvTag tlvTagInput, TlvLength tlvLengthInput, TlvDataObjectContainer tlvDataObjectContainerInput, boolean performValidityChecksInput) {
		super(performValidityChecksInput);
		
		this.setTag(tlvTagInput, performValidityChecksInput);
		this.setValue(tlvDataObjectContainerInput);
		
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
	 * @param tlvDataObjectContainerInput the data to be used
	 */
	public ConstructedTlvDataObject(TlvTag tlvTagInput, TlvLength tlvLengthInput, TlvDataObjectContainer tlvDataObjectContainerInput) {
		this(tlvTagInput, tlvLengthInput, tlvDataObjectContainerInput, PERFORM_VALIDITY_CHECKS);
	}
	
	/**
	 * Constructs an object from pre-fabricated elements. Length field is implicitly set
	 * according to DER encoding rules by default.
	 * @param tlvTagInput the tag to be used
	 * @param tlvDataObjectContainerInput the value to be used
	 * @param performValidityChecksInput true: perform validity checks, false: do not perform validity checks
	 */
	public ConstructedTlvDataObject(TlvTag tlvTagInput, TlvDataObjectContainer tlvDataObjectContainerInput, boolean performValidityChecksInput) {
		this(tlvTagInput, null, tlvDataObjectContainerInput, performValidityChecksInput);
	}
	
	/**
	 * Constructs an object from pre-fabricated elements. Length field is implicitly set
	 * according to DER encoding rules by default.
	 * @param tlvTagInput the tag to be used
	 * @param tlvDataObjectContainerInput the value to be used
	 */
	public ConstructedTlvDataObject(TlvTag tlvTagInput, TlvDataObjectContainer tlvDataObjectContainerInput) {
		this(tlvTagInput, null, tlvDataObjectContainerInput, PERFORM_VALIDITY_CHECKS);
	}
	
	/**
	 * Constructs an object from pre-fabricated elements. Length field is implicitly set
	 * according to DER encoding rules by default.
	 * @param tlvTagInput the tag to be used
	 * @param tlvDataObjectInputs the value elemets to be used
	 */
	public ConstructedTlvDataObject(TlvTag tlvTagInput, TlvDataObject... tlvDataObjectInputs) {
		this(tlvTagInput, new TlvDataObjectContainer(tlvDataObjectInputs));
	}
	
	/**
	 * Constructs an object from pre-fabricated elements. Length field is implicitly set
	 * according to DER encoding rules by default.
	 * @param tlvTagInput the tag to be used
	 */
	public ConstructedTlvDataObject(TlvTag tlvTagInput) {
		this(tlvTagInput, new TlvDataObjectContainer());
	}
	
	/*--------------------------------------------------------------------------------*/

	@Override
	public void setTag(TlvTag tlvTagInput, boolean performValidityChecksInput) {
		if(tlvTagInput == null) {throw new IllegalArgumentException("tag must not be null");}
		if(!tlvTagInput.indicatesEncodingConstructed()) {throw new IllegalArgumentException("tag must be constructed");}
		
		performValidityChecks = performValidityChecksInput;
		if(performValidityChecks && (!tlvTagInput.isValidBerEncoding())) {throw new IllegalArgumentException("tag must be valid BER encoding");}
		
		/*
		 * TLV tag must be cloned to eliminate outside access to this object.
		 * The tag must only be set by methods offered by this class e.g. to
		 * prevent setting the primitive tag to be a constructed tag.
		 */
		tlvTag = tlvTagInput.clone();
	}
	
	/**
	 * @return the tlvDataObjectContainer
	 */
	public TlvDataObjectContainer getTlvDataObjectContainer() {
		return tlvDataObjectContainer;
	}
	
	/*--------------------------------------------------------------------------------*/
	/*------------------------- Inherited from TLVDataStructure ----------------------*/
	/*--------------------------------------------------------------------------------*/

	@Override
	public TlvDataObject getTlvDataObject(TlvPath path) {
		return tlvDataObjectContainer.getTlvDataObject(path);
	}

	@Override
	public TlvDataObject getTlvDataObject(TlvTag tlvTag) {
		return tlvDataObjectContainer.getTlvDataObject(tlvTag);
	}

	@Override
	public TlvDataObject getTlvDataObject(TlvTagIdentifier tagIdentifier) {
		return tlvDataObjectContainer.getTlvDataObject(tagIdentifier);
	}
	
	@Override
	public TlvDataObject getTlvDataObject(TlvPath path, int index) {
		return tlvDataObjectContainer.getTlvDataObject(path, index);
	}

	@Override
	public boolean containsTlvDataObject(TlvTag tagField) {
		return tlvDataObjectContainer.containsTlvDataObject(tagField);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public void addTlvDataObject(TlvPath path, TlvDataObject tlvDataObject) {
		tlvDataObjectContainer.addTlvDataObject(path, tlvDataObject);
	}
	
	@Override
	public void addTlvDataObject(TlvDataObject... tlvDataObject) {
		tlvDataObjectContainer.addTlvDataObject(tlvDataObject);
	}

	@Override
	public void removeTlvDataObject(TlvPath path) {
		tlvDataObjectContainer.removeTlvDataObject(path);
	}

	@Override
	public void removeTlvDataObject(TlvTagIdentifier tagIdentifier) {
		tlvDataObjectContainer.removeTlvDataObject(tagIdentifier);
	}

	@Override
	public void removeTlvDataObject(TlvTag tlvTag) {
		tlvDataObjectContainer.removeTlvDataObject(tlvTag);
	}
	
	@Override
	public void sort(Comparator<TlvDataObject> comparator) {
		tlvDataObjectContainer.sort(comparator);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public int getNoOfElements(boolean recursive) {
		return tlvDataObjectContainer.getNoOfElements(recursive);
	}
	
	@Override
	public int getNoOfElements() {
		return tlvDataObjectContainer.getNoOfElements();
	}
	
	/*--------------------------------------------------------------------------------*/
	/*------------------------- Inherited from TLVDataObject -------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public int getNoOfValueBytes() {
		return tlvDataObjectContainer.getLength();
	}

	@Override
	public byte[] getValueField() {
		return tlvDataObjectContainer.toByteArray();
	}
	
	/*--------------------------------------------------------------------------------*/

	@Override
	public Iterator<TlvDataObject> iterator() {
		return tlvDataObjectContainer.iterator();
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Sets the value field of this object.
	 * Method does not override a super class method due to different signatures required for this method
	 * in both subclasses for primitive and constructed encoding.
	 * @param tlvDataObjectContainerInput the value to be set
	 */
	public void setValue(TlvDataObjectContainer tlvDataObjectContainerInput) {
		if(tlvDataObjectContainerInput == null) {throw new IllegalArgumentException("value must not be null");}
		tlvDataObjectContainer = tlvDataObjectContainerInput;
	}

	@Override
	public TlvValue getTlvValue() {
		return tlvDataObjectContainer;
	}
	
	@Override
	public boolean isValidBerEncoding() {
		if(!super.isValidBerEncoding()) {return false;}
		
		return tlvTag.indicatesEncodingConstructed();
	}

	/**
	 * Add all provided {@link TlvDataObject}s to this container.
	 * @param newTlvDataObjects
	 */
	public void addAll(Collection<? extends TlvDataObject> newTlvDataObjects) {
		for (TlvDataObject curTlvDataObject : newTlvDataObjects) {
			addTlvDataObject(curTlvDataObject);
		}
	}
	
}
