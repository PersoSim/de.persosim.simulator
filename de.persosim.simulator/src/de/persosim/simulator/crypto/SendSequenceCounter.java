package de.persosim.simulator.crypto;

import java.math.BigInteger;

import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class represents a counter to be used in the context of sequence numbers
 * in secure messaging. The counter is limited to positive numbers including 0.
 * The default value is 0, the default increment is 1. Wwhen exceeding the
 * maximum value the counter is reset to 0.
 * 
 * @author slutters
 *
 */
public class SendSequenceCounter {
	
	// the current value
	// 0 <= current value <= maxValue
	protected BigInteger value;
	
	// maximum achievable value
	private BigInteger maxValue;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * This constructor constructs a {@link SendSequenceCounter} object
	 * @param value the value to start counting from
	 * @param maxValue the maximum possible value
	 */
	public SendSequenceCounter(BigInteger value, BigInteger maxValue) {
		if(maxValue.compareTo(BigInteger.ZERO) <= 0) {throw new IllegalArgumentException("max value must not be smaller than 1");}
		
		this.maxValue = maxValue;
		
		if(value.compareTo(BigInteger.ZERO) < 0) {throw new IllegalArgumentException("ssc must not be smaller than 0");}
		if(value.compareTo(this.maxValue) > 0) {throw new IllegalArgumentException("ssc must not be larger than max value");}
		
		this.value = value;	
	}
	
	/**
	 * This constructor constructs a {@link SendSequenceCounter} object
	 * @param maxValue the maximum possible value
	 */
	public SendSequenceCounter(BigInteger maxValue) {
		this(BigInteger.ZERO, maxValue);
	}
	
	/**
	 * This constructor constructs a {@link SendSequenceCounter} object
	 * @param value the value to start counting from
	 * @param maxByteLength the maximum byte length representation of the value
	 */
	public SendSequenceCounter(BigInteger value, int maxByteLength) {
		this(value, ((new BigInteger("2")).pow(maxByteLength * 8)).subtract(BigInteger.ONE));
	}
	
	/**
	 * This constructor constructs a {@link SendSequenceCounter} object
	 * @param maxByteLength the maximum byte length representation of the value
	 */
	public SendSequenceCounter(int maxByteLength) {
		this(BigInteger.ZERO, maxByteLength);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * @return the maxValue
	 */
	public BigInteger getMaxValue() {
		return maxValue;
	}
	
	/**
	 * @return the value
	 */
	public BigInteger getValue() {
		return value;
	}

	/**
	 * @return the byteLength
	 */
	public int getMaxByteLength() {
		return (int) (Math.ceil(maxValue.bitLength()/8.0));
	}
	
	/**
	 * This method increments the current value by 1
	 */
	public void increment() {
		this.value = this.value.add(BigInteger.ONE);
		
		if(this.value.compareTo(this.maxValue) > 0) {
			this.reset();
		}
	}
	
	/**
	 * This method resets the current value to 0
	 */
	public void reset() {
		this.value = BigInteger.ZERO;
	}
	
	/**
	 * This method returns a byte[] representation of the current value padded
	 * to the length of the byte[] representation of the maximum value.
	 * 
	 * @return a byte[] representation of the current value
	 */
	public byte[] toByteArray() {
		byte[] out = Utils.toUnsignedByteArray(this.value);
		return Utils.padWithLeadingZeroes(out, getMaxByteLength());
	}
	
	@Override
	public String toString() {
		return this.value.toString() + " [" + HexString.encode(toByteArray()) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((maxValue == null) ? 0 : maxValue.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SendSequenceCounter other = (SendSequenceCounter) obj;
		if (maxValue == null) {
			if (other.maxValue != null)
				return false;
		} else if (!maxValue.equals(other.maxValue))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
