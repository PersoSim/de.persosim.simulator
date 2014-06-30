package de.persosim.simulator.tlv;

import java.util.Arrays;

/**
 * This class implements the data field for TLV data objects with primitive
 * encoding.
 * 
 * @author slutters
 * 
 */
public class TlvValuePlain extends TlvValue {
	/* The valueField is allowed to be empty if intentionally damaged but must never be null */
	private byte[] valueField;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Constructor for this object based on a range defined on an array of raw bytes.
	 * 
	 * @param valueFieldInput the byte array that in a certain range contains the TLV value
	 * @param minOffset the first offset of the range to contain the TLV value (inclusive)
	 * @param maxOffset the first offset not to be part of the range to contain the TLV value (exclusive).
	 */
	public TlvValuePlain(byte[] valueFieldInput, int minOffset, int maxOffset) {
		if(valueFieldInput == null) {throw new NullPointerException();}
		if(minOffset < 0) {throw new IllegalArgumentException("min offset must not be less than 0");}
		if(maxOffset < minOffset) {throw new IllegalArgumentException("max offset must not be smaller than min offset");}
		if(maxOffset > valueFieldInput.length) {throw new IllegalArgumentException("selected array area must not lie outside of data array");}
		
		if(minOffset == maxOffset) {
			this.valueField = new byte[0];
		} else{
			this.valueField = Arrays.copyOfRange(valueFieldInput, minOffset, maxOffset);
		}
	}
	
	/**
	 * Constructor for this object based on a range defined on an array of raw bytes.
	 * 
	 * @param valueFieldInput the byte array that contains the TLV value
	 */
	public TlvValuePlain(byte[] valueFieldInput) {
		this(valueFieldInput, 0, valueFieldInput.length);
	}
	
	/**
	 * Constructor for this object based on a single byte.
	 * 
	 * @param valueFieldInput the byte that contains the TLV value
	 */
	public TlvValuePlain(byte valueFieldInput) {
		this(new byte[]{valueFieldInput});
	}
	
	/**
	 * Constructor for an empty object of this type.
	 */
	public TlvValuePlain() {
		this(new byte[0]);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Set the value field.
	 * @param valueFieldInpt the value field to be set
	 */
	public void setValueField(TlvValuePlain valueFieldInput) {
		byte[] tlvValueField = valueFieldInput.toByteArray();
		
		this.valueField = Arrays.copyOf(tlvValueField, tlvValueField.length);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public boolean equals(Object anotherTlvValuePlainInput) {
		if(anotherTlvValuePlainInput == null) {return false;}
		
		if (!(anotherTlvValuePlainInput instanceof TlvValuePlain)) {
			return false;
		}
		
		/* TlvValuePlain objects are considered equal iff they encode the same value in the same way */
		return Arrays.equals(this.valueField, ((TlvValuePlain) anotherTlvValuePlainInput).valueField);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		for (int i = 0; i < valueField.length; i++) {
			hash *= valueField[i];
		}
		return hash;
	}
	
	@Override
	public int getLength() {
		return this.valueField.length;
	}
	
	@Override
	public byte[] toByteArray() {
		return Arrays.copyOf(this.valueField, this.valueField.length);
	}
	
	@Override
	public TlvValuePlain clone() {
		return new TlvValuePlain(this.valueField);
	}
	
	@Override
	public boolean isValidBerEncoding() {
		if(valueField == null) {
			return false;
		} else{
			return true;
		}
	}
	
	@Override
	public boolean isValidDerEncoding() {
		return isValidBerEncoding();
	}
	
}
