package de.persosim.simulator.tlv;

import de.persosim.simulator.utils.HexString;

/**
 * This class is used to generate TLV data objects from byte[] and automatically
 * return the correct type according to the contained tag.
 * 
 * Currently only the types {@link PrimitiveTlvDataObject} and
 * {@link ConstructedTlvDataObject} are supported. Subtypes of these may be
 * added when the need arises.
 * 
 * @author slutters
 * 
 */
public final class TlvDataObjectFactory {
	/**
	 * Should never be instantiated
	 */
	private TlvDataObjectFactory() {
	}
	
	/**
	 * Constructs an object based on basic components.
	 * @param byteArary the byte array that contains the TLV object
	 * @param minOffset the first offset to be used (inclusive)
	 * @param maxOffset the last offset to be used (exclusive)
	 */
	public static TlvDataObject createTLVDataObject(byte[] byteArray, int minOffset, int maxOffset) {
		if(byteArray == null) {throw new IllegalArgumentException("byteArray must not be null");}
		if(minOffset < 0) {throw new IllegalArgumentException("min offset must not be less than 0");}
		if(maxOffset < minOffset) {throw new IllegalArgumentException("max offset must not be smaller than min offset");}
		if(maxOffset > byteArray.length) {throw new IllegalArgumentException("selected array area must not lie outside of data array");}
		if(minOffset == maxOffset) {throw new IllegalArgumentException("selected part of data field must be greater than 0");}
		
		TlvDataObject tlvDataObject;
		
		if((byte) (byteArray[minOffset] & (byte) 0x20) == (byte) 0x20) {
			// isConstructed
			tlvDataObject = new ConstructedTlvDataObject(byteArray, minOffset, maxOffset);
		} else{
			// isPrimitive
			tlvDataObject = new PrimitiveTlvDataObject(byteArray, minOffset, maxOffset);
		}
		
		return tlvDataObject;
	}

	/**
	 * Shortcut to {@link #createTLVDataObject(byte[], int, int)} using the bytes from provided HexString 
	 * @param hexString
	 * @return
	 */
	public static TlvDataObject createTLVDataObject(String hexString) {
		return createTLVDataObject(HexString.toByteArray(hexString));
	}
	
	/**
	 * Shortcut to {@link #createTLVDataObject(byte[], int, int)} using the full byte array
	 * @param bytes
	 * @return
	 */
	public static TlvDataObject createTLVDataObject(byte[] bytes) {
		return createTLVDataObject(bytes, 0, bytes.length);
	}
}
