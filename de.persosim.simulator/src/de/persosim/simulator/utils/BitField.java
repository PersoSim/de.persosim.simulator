package de.persosim.simulator.utils;

import java.util.Arrays;

import de.persosim.simulator.exception.BitFieldOutOfBoundsException;

/**
 * This class implements a little endian bitfield providing several bitwise
 * logical operations.
 * 
 * @author mboonk
 * 
 */
public class BitField {
	boolean[] storedBits;

	/**
	 * Creates an empty (all zero bits) {@link BitField} of the given length.
	 * @param numberOfBits
	 */
	public BitField(int numberOfBits){
		storedBits = new boolean[numberOfBits];
	}
	
	/**
	 * Creates a {@link BitField} of the given size and sets all additionally
	 * given bits to 1.
	 * 
	 * @param numberOfBits
	 * @param setBits
	 *            this contains the zero based indices of the bits to be set
	 *            to 1
	 */
	public BitField(int numberOfBits, int ... setBits){
		this(numberOfBits);
		for (int bit : setBits){
			if (bit > numberOfBits || bit < 0){
				throw new IllegalArgumentException("The bits to be set must be inside the BitField");
			}
			storedBits[bit] = true;
		}
	}
	
	/**
	 * Create a {@link BitField} from a big endian ordered byte array.
	 * @param numberOfBits
	 * @param bitsToStore
	 * @return
	 */
	public static BitField buildFromBigEndian(int numberOfBits, byte [] bitsToStore){
		BitField result = new BitField(numberOfBits);
		
		boolean [] sourceBits = new boolean [bitsToStore.length*8];
		
		for (int i = 0; i < sourceBits.length; i++){
			sourceBits[i] = ((bitsToStore[i/8] >>> 7-(i%8)) & 0b00000001) == 1;
		}

		int offset = bitsToStore.length*8 - numberOfBits;
		for (int i = offset; i < sourceBits.length; i++){
			result.setBit(numberOfBits - 1 - (i - offset), sourceBits[i]);
		}
		
		return result;
	}

	public static BitField buildFromBigEndian(byte[] bitsToStore) {
		return buildFromBigEndian(bitsToStore.length*8, bitsToStore);
	}
	
	/**
	 * This constructor takes the given byte array and parses it beginning at
	 * element 0 and the LSB of this element up to the element in which the
	 * numberOfBits-1 bit is contained.
	 * 
	 * @param numberOfBits
	 *            to store in the field
	 * @param bitsToStore
	 *            source data
	 */
	public BitField(int numberOfBits, byte[] bitsToStore) {
		this(numberOfBits);
		for (int i = 0; i < numberOfBits; i++) {

			/*
			 * Access the next byte every 8 bits and mask away any potential
			 * sign. Then shift this byte such that the currently interesting
			 * bit is the LSB. Masking the other 7 bits away provides the value
			 * that is added to the internal representation of the bit field.
			 * This inverts bit order per byte and preserves byte order.
			 */
			setBit(i,
					(((bitsToStore[i / 8] & 0xFF) >>> i % 8) & 0b00000001) == 1);
		}
	}
	
	/**
	 * This constructor takes the given byte array and parses it beginning at
	 * element 0 and the LSB of this element up to the end of the given array.
	 * 
	 * @param bitsToStore
	 *            source data
	 */	
	public BitField(byte [] bitsToStore){
		this(8 * bitsToStore.length, bitsToStore);
	}

	/**
	 * This constructor takes the given boolean array and parses it beginning at
	 * element 0 interpreted as LSB using the true/false values directly as bit
	 * values 1/0 respectively.
	 * 
	 * @param bitsToStore
	 *            source data
	 */
	public BitField(boolean[] bitsToStore) {
		storedBits = Arrays.copyOf(bitsToStore, bitsToStore.length);
	}

	/**
	 * This methods concatenates the given {@link BitField} with this object.
	 * 
	 * thisObject|field
	 * 
	 * @param field
	 *            to concatenate with
	 * @return the concatenation of this object with
	 */
	public BitField concatenate(BitField field) {
		boolean[] result = new boolean[field.getNumberOfBits()
				+ getNumberOfBits()];

		System.arraycopy(storedBits, 0, result, 0, getNumberOfBits());
		System.arraycopy(field.storedBits, 0, result, getNumberOfBits(), field.getNumberOfBits());

		return new BitField(result);
	}

	/**
	 * Calculate an bitwise or over this and the given {@link BitField}.
	 * 
	 * @param field
	 * @return a new {@link BitField} containing the result
	 */
	public BitField or(BitField field) {
		boolean[] result = new boolean[Math.max(getNumberOfBits(), field.getNumberOfBits())];

		for (int i = 0; i < result.length; i++) {
			result[i] = getZeroPaddedBit(i) | field.getZeroPaddedBit(i);
		}

		return new BitField(result);
	}

	/**
	 * Calculate an bitwise xor over this and the given {@link BitField}.
	 * 
	 * @param field
	 * @return a new {@link BitField} containing the result
	 */
	public BitField xor(BitField field) {
		boolean[] result = new boolean[Math.max(getNumberOfBits(), field.getNumberOfBits())];

		for (int i = 0; i < result.length; i++) {
			result[i] = getZeroPaddedBit(i) ^ field.getZeroPaddedBit(i);
		}

		return new BitField(result);
	}

	/**
	 * Calculate an bitwise and over this and the given {@link BitField}.
	 * 
	 * @param field
	 * @return a new {@link BitField} containing the result
	 */
	public BitField and(BitField field) {
		boolean[] result = new boolean[Math.max(getNumberOfBits(), field.getNumberOfBits())];

		for (int i = 0; i < result.length; i++) {
			result[i] = getZeroPaddedBit(i) & field.getZeroPaddedBit(i);
		}

		return new BitField(result);
	}

	/**
	 * Calculate an bitwise not over this {@link BitField}.
	 * 
	 * @return a new {@link BitField} containing the result
	 */
	public BitField not() {
		boolean[] result = new boolean [this.getNumberOfBits()];

		for (int i = 0; i < result.length; i++) {
			result[i] = ! getZeroPaddedBit(i);
		}

		return new BitField(result);
	}

	private boolean getZeroPaddedBit(int index) {
		if (index >= storedBits.length) {
			return false;
		}
		return storedBits[index];
	}

	public int getNumberOfBits() {
		return storedBits.length;
	}

	public boolean getBit(int index) {
		if (0 <= index && index < storedBits.length) {
			return storedBits[index];
		} else {
			throw new BitFieldOutOfBoundsException();
		}
	}
	
	/**
	 * This method creates a new instance with one flipped bit.
	 * @param index the bit to flip
	 * @return
	 */
	public BitField flipBit(int index){
		BitField result = new BitField(this.storedBits);
		result.setBit(index, !result.getBit(index));
		return result;
	}
	
	/**
	 * This method creates a new instance with one changed bit.
	 * @param index the bit to set
	 * @return
	 */
	public BitField forceBit(int index, boolean value){
		BitField result = new BitField(this.storedBits);
		result.setBit(index, value);
		return result;
	}
	
	private void setBit(int index, boolean value) {
		if (0 <= index && index < storedBits.length) {
			storedBits[index] = value;
		} else {
			throw new BitFieldOutOfBoundsException();
		}
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(storedBits);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BitField other = (BitField) obj;
		if (!Arrays.equals(storedBits, other.storedBits))
			return false;
		return true;
	}

	/**
	 * This method creates a byte array representation of the bitfield. It
	 * contains the LSB of the field as the LSB of the element 0, bit 9 as LSB
	 * of element 1 etc.. After the internal bit field is exhausted, missing
	 * bits will be padded to 0.
	 * 
	 * @return
	 */
	public byte[] getAsZeroPaddedByteArray() {
		int length = getNumberOfBits() / 8;
		if (getNumberOfBits() % 8 > 0) {
			length++;
		}
		byte[] result = new byte[length];
		for (int i = 0; i < length * 8; i++) {
			result[i / 8] |= (byte) (getZeroPaddedBit(i) ? 0x80 : 0);

			if ((i + 1) % 8 != 0){
				result[i / 8] = (byte) (result[i / 8] >> 1);
				result[i / 8] &= 0x7F;	
			} 

		}
		return result;
	}
	
	/**
	 * This method creates a padded big endian byte array representation of this {@link BitField}
	 * @return a padded big endian byte array representation of this {@link BitField}
	 */
	public byte[] getAsZeroPaddedBigEndianByteArray() {
		int length = getNumberOfBits() / 8;
		if (getNumberOfBits() % 8 > 0) {
			length++;
		}
		byte[] result = new byte[length];
		for (int i = 0; i < getNumberOfBits(); i++) {
			byte temp = (byte) ((byte) (getBit(i) ? 1 : 0) << (i % 8));
			result[result.length - 1 - (i / 8)] |= temp;
		}
		return result;
	}
	
	@Override
	public String toString() {
		return getStringRepresentation(START_WITH_MOST_SIGNIFICANT_BIT);
	}
	
	public static boolean START_WITH_MOST_SIGNIFICANT_BIT = true;
	public static boolean START_WITH_LEAST_SIGNIFICANT_BIT = false;
	
	/**
	 * This method returns a String representation of this object consisting of
	 * {0, 1}. The String starts in reading direction either with the most or
	 * least significant bit. Either use
	 * {@link #START_WITH_MOST_SIGNIFICANT_BIT} or
	 * {@link #START_WITH_LEAST_SIGNIFICANT_BIT} as parameter.
	 * 
	 * @param startWithMostSignificantBit
	 *            start with most or least significant bit
	 * @return the String representation of this object
	 */
	public String getStringRepresentation(boolean startWithMostSignificantBit) {
		StringBuilder sb = new StringBuilder(getNumberOfBits());
		int noOfRemainingBits = getNumberOfBits();
		
		int effectiveIndex, bitsToCount;
		for(int i=0; i<getNumberOfBits(); i++) {
			
			if(startWithMostSignificantBit) {
				effectiveIndex = (getNumberOfBits() - 1) - i;
				bitsToCount = noOfRemainingBits;
			} else{
				effectiveIndex = i;
				bitsToCount = i;
			}
			
			if((i > 0) && ((bitsToCount % 8) == 0)) {
				sb.append(" ");
			}
			
			if(getBit(effectiveIndex)) {
				sb.append("1");
			} else{
				sb.append("0");
			}
			noOfRemainingBits--;
		}
		return sb.toString();
	}
	
}
