package de.persosim.simulator.tlv;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import de.persosim.simulator.exception.ISO7816Exception;
import de.persosim.simulator.platform.Iso7816;

/**
 * This class implements basic functionality for the use within TLV data
 * objects. Any TLV data object consists of objects that represent the tag
 * field, followed by a length field and finally a value field, that must be
 * present but may be empty. Checks for the validity of values and conformity of
 * encoding are provided by the elements themselves. Further on the object
 * itself only provides methods to check for the interaction of the elements.
 * Constructors and set methods will reject any invalid or malformed element if
 * not explicitly told otherwise. If so the object accordingly is marked as
 * exempted from checks as a whole.
 * 
 * In order to prevent the object from being presented valid tag fields that are
 * modified by direct access via reference later on, e.g. a primitive encoded
 * TLV data object being converted to a constructed one, a tag must not be
 * changed in any way without being noticed. Any change of this element hence
 * must be committed through methods provided by this object. In order for this
 * to happen the element is not set directly but as a clone of the respective
 * element, eliminating any outside access by reference. Accordingly any get
 * method also returns a clone of this element. On the contrary, these
 * precautions are unnecessary for the length field. As e.g. for constructed TLV
 * data objects the length field may change through changes to sub-objects, the
 * length field, when requested, can not be taken for being up-to-date. Hence
 * any length encoding that may be present must be checked. If no length
 * encoding is present or the length encoding does not match the actual value
 * field, the length field is re-calculated based upon the actual value field.
 * Access to value fields also is not restricted. Validity of these fields is
 * checked on access. While value fields of primitive encoded TLV data objects
 * are always valid, validity of value fields of constructed TLV data objects
 * needs to be checked recursively.
 * 
 * @author slutters
 * 
 */
public abstract class TlvDataObject extends TlvElement implements Iso7816, ValidityChecks {
	
	/* The tag component of any TLV data object 
	 * 
	 * The tag does not have to be immutable. However only methods from within this class hierarchy are allowed
	 * to perform changes as long as they fulfill the following conditions
	 * 1) No construct/set/get method is allowed to permit outside references to the tag.
	 * 2) Tags may only be set or modified if the result matches the tag-class of its class
	 * (e.g. tags within PrimitiveTlvDataObject must always be primitive).
	 */
	protected TlvTag tlvTag;
	
	/* 
	 * The length component of any TLV data object. 
	 * 
	 * The length field for any TLV data object by default is computed based on the length of the
	 * value field. As in BER encoding allow for more than one different but equally valid and hence
	 * no unique representation of the same indicated length the length field for TLV data
	 * objects can be explicitly stated with this variable. This also allows for an explicit
	 * manipulation of this field e.g. for debug or testing purposes.
	 * 
	 * Variable only needs to be set if default DER-encoded length is explicitly to be overridden,
	 * either by a non-DER BER encoding or an intentionally damaged field.
	 */
	protected TlvLength tlvLength;
	
	/* The value component will be set in the various sub classes */
	//TODO define value field within this class, that reduces code duplication within subclasses (esp. constructor redundancy) and requires only a little caution when casting the valuefield within those subclasses
	
	protected boolean performValidityChecks;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Basic constructor for TLV objects
	 * @param performValidityChecks true: perform validity checks, false: do not perform validity checks
	 */
	protected TlvDataObject(boolean performValidityChecksInput) {
		performValidityChecks = performValidityChecksInput;
	}
	
	/**
	 * Constructor for TLV objects
	 * @param dataField the byte array that in a certain range contains the TLV data object
	 * @param minOffset the first offset of the range to contain the TLV data object (inclusive)
	 * @param maxOffset the first offset not to be part of the range to contain the TLV data object (exclusive).
	 */
	public TlvDataObject(byte[] dataField, int minOffset, int maxOffset) {
		if(dataField == null) {throw new NullPointerException();}
		if(minOffset < 0) {throw new IllegalArgumentException("min offset must not be less than 0");}
		if(maxOffset < minOffset) {throw new IllegalArgumentException("max offset must not be smaller than min offset");}
		if(maxOffset > dataField.length) {throw new IllegalArgumentException("selected array area must not lie outside of data array");}
		if(minOffset == maxOffset) {throw new IllegalArgumentException("selected part of data field must be greater than 0");}
		
		performValidityChecks = PERFORM_VALIDITY_CHECKS;
		
		/*
		 * Determine Tag
		 */
		int currentOffset = minOffset;
		
		tlvTag = new TlvTag(dataField, currentOffset, maxOffset);
		
		/*
		 * Determine Length
		 */
		currentOffset += getNoOfTagBytes();
		tlvLength = new TlvLength(dataField, currentOffset, maxOffset);
		
		int indicatedLength = tlvLength.getIndicatedLength();
		
		/*
		 * Determine Value
		 */
		currentOffset += tlvLength.getLength();
		
		if((currentOffset + indicatedLength) > maxOffset) {
			/* error, length indicated by TLV would exceed expected length */
			ISO7816Exception.throwIt(SW_6A85_NC_INCONSISTENT_WITH_TLV_STRUCTURE, "offset outside data array");
		}
		
		/* The actual value is set by the sub-class constructors */
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Set the tag of this object.
	 * If validity checks are to be skipped the tag may be set to any arbitrary value.
	 * In any case the tag's indicated class must match the class of the TLV data object.
	 * If the indicated class of a TLV object is to be changed the object must be rebuild as object of the respective class.
	 * @param tlvTag the tag
	 * @param performValidityChecksInput true: perform validity checks, false: do not perform validity checks
	 */
	public abstract void setTag(TlvTag tlvTag, boolean performValidityChecksInput);
	
	/**
	 * Set the tag of this object.
	 * @param tlvTag the tag
	 */
	public void setTag(TlvTag tlvTag) {
		setTag(tlvTag, PERFORM_VALIDITY_CHECKS);
	}
	
	/**
	 * Set the length of this object
	 * @param tlvLengthInput the length
	 * @param performValidityChecksInput true: perform validity checks, false: do not perform validity checks
	 */
	public void setLength(TlvLength tlvLengthInput, boolean performValidityChecksInput) {
		if(tlvLengthInput == null) {throw new NullPointerException("length must not be null");}
		
		performValidityChecks = performValidityChecksInput;
		
		if(performValidityChecks) {
			if(!tlvLengthInput.isValidBerEncoding()) {throw new IllegalArgumentException("new length must be valid BER encoding");}
			if(tlvLengthInput.getIndicatedLength() != this.getNoOfValueBytes()) {throw new IllegalArgumentException("new length must match length of present value field");}
		}
		
		this.tlvLength = tlvLengthInput;
	}
	
	/**
	 * Set the length of this object
	 * @param tlvLengthInput the length
	 */
	public void setLength(TlvLength tlvLengthInput) {
		setLength(tlvLengthInput, PERFORM_VALIDITY_CHECKS);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * @return the noOfTagBytes
	 */
	public int getNoOfTagBytes() {
		return tlvTag.getLength();
	}
	
	/**
	 * @return the noOfLengthBytes
	 */
	public int getNoOfLengthBytes() {
		return getTlvLength().getLength();
	}
	
	/**
	 * @return the no of bytes occupied by the value field
	 */
	public abstract int getNoOfValueBytes();
	
	@Override
	public int getLength() {
		return getNoOfTagBytes() + getNoOfLengthBytes() + getNoOfValueBytes();
	}
	
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream outputStream;
		
		outputStream = new ByteArrayOutputStream();
		
		try {
			/* tag can be accessed directly */
			outputStream.write(tlvTag.toByteArray());
			/* length must be accessed by getter in case there is a valid override */
			outputStream.write(getTlvLength().toByteArray());
			/* value must be accessed by getter as values are only specified by sub classes */
			outputStream.write(getTlvValue().toByteArray());
		} catch (IOException e) {
			logException(this.getClass(), e, DEBUG);
		}

		return outputStream.toByteArray();
	}
	
	/**
	 * Returns the tag number encoded within the tag field
	 * @return the tag number encoded within the tag field
	 */
	public int getTagNo() {
		return tlvTag.getIndicatedTagNo();
	}
	
	/**
	 * Returns the length of the value field as indicated by the length field
	 * @return the length of the value field as indicated by the length field
	 */
	public int getLengthValue() {
		return getTlvLength().getIndicatedLength();
	}
	
	/**
	 * @return the valueField
	 */
	public abstract byte[] getValueField();

	/**
	 * The tag returned is a clone of the original object.
	 * This is necessary for the following reasons:
	 * The type of this object allows changes in general.
	 * These changes may be valid for themselves while being
	 * invalid as part of the TLV data object represented
	 * by this class.
	 * 
	 * @return the tlvTag
	 */
	public TlvTag getTlvTag() {
		return tlvTag.clone();
	}

	/**
	 * Returns the length field for this TLV data object. If not explicitly set
	 * the length field is always returned as a live computation based on the
	 * actual length of the current value field encoded according to DER
	 * encoding rules by default. If explicitly set the length field will only
	 * be returned as is if either the encoded value matches the actual length
	 * of the value field or if validity checks have been explicitly disabled.
	 * Otherwise the method will discard the length field and deal with it in
	 * the same way as if it had never been set.
	 * 
	 * @return the tlvLength
	 */
	public TlvLength getTlvLength() {
		if(tlvLength == null) {
			/* A TLV length field has NOT been explicitly set */
			return new TlvLength(getTlvValue().getLength());
		} else{
			/* A TLV length field has been explicitly set */
			int indicatedLength = tlvLength.getIndicatedLength();
			
			if(indicatedLength == getTlvValue().getLength()) {
				/* The length indicated by the length field matches the actual length of the value field */
				return tlvLength;
			} else{
				/* The length indicated by the length field does NOT match the actual length of the value field */
				if(performValidityChecks) {
					/* discard invalid length field */
					tlvLength = null;
					return new TlvLength(getTlvValue().getLength());
				} else{
					return tlvLength;
				}
			}
		}
	}
	
	/**
	 * @return the tlvValue
	 */
	public abstract TlvValue getTlvValue();
	
	@Override
	public boolean isValidBerEncoding() {
		/* first all T-L-V elements must be of valid encoding for themselves */
		if(!(getTlvTag().isValidBerEncoding() && getTlvLength().isValidBerEncoding() && getTlvValue().isValidBerEncoding())) {return false;}
		
		/* then encoded length must match actual length */
		if(getTlvLength().getIndicatedLength() != getTlvValue().getLength()) {return false;}
		
		return true;
	}
	
	@Override
	public boolean isValidDerEncoding() {
		if(!isValidBerEncoding()) {return false;}
		
		//TODO provide a negative testcase for this method that checks behavior for a mismatch between length encoded in getTlvLEngth() and actual length of value
		return getTlvTag().isValidDerEncoding() && getTlvLength().isValidDerEncoding() && getTlvValue().isValidDerEncoding();
	}
	
	/**
	 * This method matches this tag against a provided tag
	 * @param otherTlvTag the tlv tag to compare with
	 * @return whether this tag matches the provided tag
	 */
	public boolean matches(TlvTag otherTlvTag) {
		return tlvTag.matches(otherTlvTag);
	}
	
	/**
	 * This method matches this tag against the tag of a provided TLVDataObject
	 * @param the TLVDataObject with the tag to be matched against
	 * @return whether this tag matches the provided tag
	 */
	public boolean matches(TlvDataObject otherTLVDataObject) {
		return matches(otherTLVDataObject.getTlvTag());
	}
	
	@Override
	public String toString() {
		StringBuilder sb;
		TlvValue tlvValue;
		
		sb = new StringBuilder();
		
		sb.append(this.tlvTag.toString());
		sb.append("|");
		sb.append(this.getTlvLength().toString());
		
		tlvValue = this.getTlvValue();
		
		if(!tlvValue.isEmpty()) {
			sb.append("|");
			sb.append(this.getTlvValue().toString());
		}
		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object anotherTlvDataObject) {
		if(anotherTlvDataObject == null) {return false;}
		
		if (!(anotherTlvDataObject instanceof TlvDataObject)) {
			return false;
		}
		
		//TlvDataObjects are considered equal iff they encode the same T-L-V combination in the same way
		return Arrays.equals(toByteArray(), ((TlvDataObject) anotherTlvDataObject).toByteArray());
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		byte[] byteArray = toByteArray();
		for (int i = 0; i < byteArray.length; i++) {
			hash *= byteArray[i];
		}
		return hash;
	}
	
	public void setPerformValidityChecksTo(boolean performValidityChecksInput) {
		performValidityChecks = performValidityChecksInput;
	}
	
	/**
	 * Returns a neatly indented and line wrapped version of the provided {@link TlvDataObject}
	 * @param obj
	 */
	public static String dumpTlvObject(TlvDataObject obj){
		if (obj == null) return "";

		String inputStr = obj.toString();
		StringBuilder sb = new StringBuilder();
		
		String indent = "";
		
		for (int i = 0, n = inputStr.length(); i < n; i++) {
		    char curChar = inputStr.charAt(i);
		    switch (curChar) {
			case '(':
				indent += "    ";
				break;
			case ')':
				indent = indent.substring(4);
				break;
			case '[':
				sb.append("\n");
				sb.append(indent);
				break;
			case ']':	
				break;

			default:
				sb.append(curChar);
				break;
			}
		}
		
		return sb.toString();
	}
}
