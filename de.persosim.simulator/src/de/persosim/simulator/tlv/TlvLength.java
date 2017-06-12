package de.persosim.simulator.tlv;

import java.util.Arrays;

import de.persosim.simulator.exception.ISO7816Exception;
import de.persosim.simulator.utils.Utils;

/**
 * This class implements the length field of any TLV data object. A length
 * field may be created or set with any value, i.e. by explicitly bypassing any
 * checks for validity or encoding. This explicitly allows for generating
 * intentionally damaged length fields.
 * 
 * @author slutters
 * 
 */
public class TlvLength extends TlvElement {
	protected byte[] lengthField;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Basic constructor for this object based on a range defined on an array of raw bytes.
	 * 
	 * WARNING: If validity checks are to be skipped, the length will be set to the full range.
	 * This may cause serious problems if the range is also expected to contain a value field
	 * as it is the case during construction of any TLV data object or container from raw byte arrays. 
	 * 
	 * @param lengthFieldInput the byte array that in a certain range contains the TLV length
	 * @param minOffset the first offset of the range to contain the TLV length (inclusive)
	 * @param maxOffset the first offset not to be part of the range to contain the TLV length (exclusive).
	 * This offset may no longer be part of the array.
	 * @param performValidityChecks true: perform validity checks, false: do not perform validity checks
	 */
	public TlvLength(byte[] lengthFieldInput, int minOffset, int maxOffset, boolean performValidityChecks) {
		super();
		
		if(performValidityChecks == PERFORM_VALIDITY_CHECKS) {
			this.setLengthField(lengthFieldInput, minOffset, maxOffset);
		} else{
			this.forceLengthField(Arrays.copyOfRange(lengthFieldInput, minOffset, maxOffset));
		}
	}
	
	/**
	 * Basic constructor for this object based on a range from a byte array.
	 * @param lengthFieldInput the data field that in a certain range contains the TLV object
	 * @param minOffset the first offset of the range to contain the TLV object (inclusive)
	 * @param maxOffset the first offset not to be part of the range to contain the TLV object(exclusive).
	 * This offset may no longer be part of the array.
	 */
	public TlvLength(byte[] lengthFieldInput, int minOffset, int maxOffset) {
		this(lengthFieldInput, minOffset, maxOffset, PERFORM_VALIDITY_CHECKS);
	}
	
	/**
	 * Basic constructor for this object based on a whole byte array.
	 * @param lengthFieldInput the data field that completely contains the TLV object
	 * @param performValidityChecks whether integrity of this object is checked and ok
	 */
	public TlvLength(byte[] lengthFieldInput, boolean performValidityChecks) {
		super();
			
		if(performValidityChecks == PERFORM_VALIDITY_CHECKS) {
			this.setLengthField(lengthFieldInput);
		} else{
			this.forceLengthField(lengthFieldInput);
		}
	}
	
	/**
	 * Basic constructor for this object based on a whole byte array.
	 * @param lengthFieldInput the data field that completely contains the TLV object
	 */
	public TlvLength(byte[] lengthFieldInput) {
		this(lengthFieldInput, PERFORM_VALIDITY_CHECKS);
	}
	
	/**
	 * Basic constructor for this object based on value to be represented by this object.
	 * @param lengthValue the value to be represented by this object
	 * @param safetyOnOff whether integrity of this object is checked and ok
	 */
	public TlvLength(int lengthValue, boolean safetyOnOff) {
		super();
		
		byte[] lengthField = TlvLength.getLengthEncoding(lengthValue);
		
		if(safetyOnOff == PERFORM_VALIDITY_CHECKS) {
			this.setLengthField(lengthField);
		} else{
			this.forceLengthField(lengthField);
		}
	}
	
	/**
	 * Basic constructor for this object based on value to be represented by this object.
	 * @param lengthValue the value to be represented by this object
	 */
	public TlvLength(int lengthValue) {
		this(lengthValue, PERFORM_VALIDITY_CHECKS);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Basic method to set the length field based on a range from an array of raw bytes.
	 * The defined range must contain at least the whole length field.
	 * The first byte of the range must also be the first byte of the length field.
	 * The last byte of the range is not expected to also be the last byte of the length
	 * field and may lay outside of the array.
	 * 
	 * @param lengthFieldInput the byte array that contains the TLV element
	 * @param minOffset the first offset of the range to contain the TLV element (inclusive)
	 * @param maxOffset the first offset not to be part of the range to contain the TLV element (exclusive).
	 * This offset may no longer be part of the array.
	 */
	public void setLengthField(byte[] lengthFieldInput, int minOffset, int maxOffset) {
		if(lengthFieldInput == null) {throw new NullPointerException();}
		if(minOffset < 0) {throw new IllegalArgumentException("min offset must not be less than 0");}
		if(maxOffset < minOffset) {throw new IllegalArgumentException("max offset must not be smaller than min offset");}
		if(maxOffset > lengthFieldInput.length) {throw new IllegalArgumentException("selected array area must not lie outside of data array");}
		if(minOffset == maxOffset) {throw new IllegalArgumentException("selected part of data field must be greater than 0");}
		
		//determine max offset
		int endOffset;
		byte firstLengthByte = lengthFieldInput[minOffset];
		if((firstLengthByte & (byte) 0x80) == (byte) 0x80) {
			/* if most significant bit is '1', i.e. we are not dealing with a 1-Byte length field */
			
			int noOfBytesUsedToIndicateLength = Utils.maskUnsignedByteToInt((byte) (firstLengthByte & (byte) 0x7F)) + 1;
			
			if((noOfBytesUsedToIndicateLength <= 1) || (noOfBytesUsedToIndicateLength > 5)) {
				/* error, unspecified no of length bytes */
				ISO7816Exception.throwIt(SW_6A80_WRONG_DATA);
			}
			
			if((minOffset + noOfBytesUsedToIndicateLength) > maxOffset) {
				ISO7816Exception.throwIt(SW_6A85_NC_INCONSISTENT_WITH_TLV_STRUCTURE, "offset outside data array");
			}
			
			endOffset = minOffset + noOfBytesUsedToIndicateLength;
		} else{
			endOffset = minOffset + 1;
		}
		
		//copy relevant part of input into member lengthField
		lengthField = Arrays.copyOfRange(lengthFieldInput, minOffset, endOffset);
	}
	
	/**
	 * Sets the length field (perform validity checks).
	 * @param lengthFieldInput the length field to be set
	 */
	public void setLengthField(byte[] lengthFieldInput) {
		this.setLengthField(lengthFieldInput, 0, lengthFieldInput.length);
	}
	
	/**
	 * Forces the length field (skip validity checks).
	 * This method allows to set the length field bypassing any checks for content
	 * validity or encoding. The value is set "as is" without any processing.
	 * The only limitation is that the null value is not allowed.
	 * A length field is a basic element of any TLV data object. It may either be
	 * present or not. Creating a length field but setting a null value would be
	 * somewhere in between and hence is not allowed. If no length field is to be
	 * set, no length field needs to be created.
	 * @param lengthFieldInput the length field to be set
	 */
	public void forceLengthField(byte[] lengthFieldInput) {
		if(lengthFieldInput == null) {throw new NullPointerException();}
		this.lengthField = Arrays.copyOf(lengthFieldInput, lengthFieldInput.length);
	}
	
	/**
	 * Returns the length of the value field as indicated by the length field
	 * @return the length of the value field as indicated by the length field
	 */
	public int getIndicatedLength() {
		int lengthNo;
		byte[] actualLength;
		
		actualLength = this.lengthField;
		
		if(this.getLength() == 1) {
			lengthNo = Utils.maskUnsignedByteToInt(actualLength[0]);
		} else{
			actualLength = Arrays.copyOfRange(this.lengthField, 1, this.lengthField.length);
			lengthNo = Utils.getIntFromUnsignedByteArray(actualLength);
		}
		
		return lengthNo;
	}
	
	@Override
	public boolean equals(Object anotherTlvLength) {
		if(anotherTlvLength == null) {return false;}
		
		if (!(anotherTlvLength instanceof TlvLength)) {
			return false;
		}
		
		//TlvLengths are considered equal iff they encode the same length value in the same way
		return Arrays.equals(lengthField, ((TlvLength) anotherTlvLength).lengthField);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		for (int i = 0; i < lengthField.length; i++) {
			hash *= lengthField[i];
		}
		return hash;
	}
	
	@Override
	public byte[] toByteArray() {
		return Arrays.copyOf(lengthField, lengthField.length);
	}
	
	@Override
	public int getLength() {
		return this.lengthField.length;
	}
	
	@Override
	public TlvLength clone() {
		return new TlvLength(this.toByteArray(), SKIP_VALIDITY_CHECKS);
	}
	
	@Override
	public boolean isValidBerEncoding() {
		int lengthFieldLength = lengthField.length;
		
		/* ensure valid length */
		if((lengthFieldLength < 1) || (lengthFieldLength > 5)) {return false;}
		
		/* ensure valid formatting of length */
		if(lengthFieldLength == 1) {
			if((lengthField[0] & ((short) 0x80)) == ((short) 0x80)) {return false;}
		} else{
			if((lengthField[0] & ((short) 0x80)) != ((short) 0x80)) {return false;}
			
			int noOfBytesUsedToIndicateLength = Utils.maskUnsignedByteToInt((byte) (lengthField[0] & (byte) 0x7F)) + 1;
			
			if(noOfBytesUsedToIndicateLength != lengthFieldLength) {return false;}
			if((noOfBytesUsedToIndicateLength < 2) || (noOfBytesUsedToIndicateLength > 5)) {return false;}
		}
		
		return true;
	}
	
	@Override
	public boolean isValidDerEncoding() {
		/* Must be valid BER encoding */
		if(!isValidBerEncoding()) {return false;}
		/* Must be minimum length encoding */
		return getMinNoOfBytesEncodingLength(getIndicatedLength()) == lengthField.length;
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns the minimum length encoding that encodes the provided length value.
	 * The returned byte array complies with DER encoding rules.
	 * @param indicatedLength the length value to be encoded
	 * @return the minimum length encoding that encodes the provided length value
	 */
	public static byte[] getLengthEncoding(int indicatedLength) {
		if(indicatedLength < 0) {throw new NullPointerException("length must not be smaller than 0");}
		
		byte[] temporaryLengthEncoding;
		byte[] finalLengthEncoding;
		byte byteLength;
		
		temporaryLengthEncoding = Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(indicatedLength));
		
		if(indicatedLength <= 127) {
			finalLengthEncoding = temporaryLengthEncoding;
		} else{
			finalLengthEncoding = new byte[temporaryLengthEncoding.length + 1];
			/* for a length input of 32 bit integer byte length will be in range of 0-4 */
			byteLength = (byte) temporaryLengthEncoding.length;
			finalLengthEncoding[0] = (byte) (((byte) 0x80) | byteLength);
			System.arraycopy(temporaryLengthEncoding, 0, finalLengthEncoding, 1, temporaryLengthEncoding.length);
		}
		
		return finalLengthEncoding;
	}
	
	/**
	 * Returns the minimum number of bytes that is needed to encode the given length
	 * @return the minimum number of bytes that is needed to encode the given length
	 */
	public static int getMinNoOfBytesEncodingLength(int indicatedLength) {
		return getLengthEncoding(indicatedLength).length;
	}
	
}
