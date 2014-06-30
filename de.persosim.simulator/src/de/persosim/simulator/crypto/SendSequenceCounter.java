package de.persosim.simulator.crypto;

import java.math.BigInteger;
import java.util.Arrays;

import de.persosim.simulator.utils.Utils;

/**
 * @author slutters
 *
 */
public class SendSequenceCounter {
	/* the current value */
	protected BigInteger value;
	
	/* current value must always fit into maxByteLength */
	protected int maxByteLength;
	
	/* current value must always be <= maxValue */
	private BigInteger maxValue;
	
	/*--------------------------------------------------------------------------------*/
	
	public SendSequenceCounter(BigInteger value, BigInteger maxValue) {
		this.setValue(value);
		this.setMaxValue(maxValue);
	}
	
	public SendSequenceCounter(BigInteger maxValue) {
		this(BigInteger.ZERO, maxValue);
	}
	
	public SendSequenceCounter(BigInteger value, int maxByteLength) {
		this.setMaxByteLength(maxByteLength);
		this.setValue(value);
	}
	
	public SendSequenceCounter(int maxByteLength) {
		this(BigInteger.ZERO, maxByteLength);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	private void setMaxValue(BigInteger maxValue) {
		if(maxValue.compareTo(BigInteger.ZERO) <= 0) {throw new IllegalArgumentException("max value must not be smaller than 1");}
		
		this.maxValue = maxValue;
		this.maxByteLength = (int) (Math.ceil(maxValue.bitLength()/8.0));
	}
	
	public void increment() {
		this.value = this.value.add(BigInteger.ONE);
		
		if(this.value.compareTo(this.maxValue) > 0) {
			this.reset();
		}
	}
	
	@Override
	public String toString() {
		return this.value.toString();
	}
	
	private int getCurrentBitLength() {
		int bl;
		
		bl = this.value.bitLength();
		
		if(bl <= 0) {
			return 1;	
		} else{
			return bl;
		}
	}
	
	public int getCurrentByteLength() {
		return (int) Math.ceil(this.getCurrentBitLength()/8.0);
	}
	
	public void reset() {
		this.value = BigInteger.ZERO;
	}
	
	public byte[] toByteArray() {
		byte[] tmp, out;
		
		tmp = Utils.toUnsignedByteArray(this.value);
		
		if(tmp.length < this.maxByteLength) {
			/* we will have to pad the output */
			out = new byte[this.maxByteLength];
			Arrays.fill(out, (byte) 0x00);
			System.arraycopy(tmp, 0, out, out.length - tmp.length, tmp.length);
		} else{
			out = tmp;
		}
		
		return out;
	}

	/**
	 * @return the ssc
	 */
	public BigInteger getValue() {
		return value;
	}

	/**
	 * @param ssc the ssc to set
	 */
	public void setValue(BigInteger ssc) {
		if(ssc.compareTo(BigInteger.ZERO) < 0) {throw new IllegalArgumentException("ssc must not be smaller than 0");}
		if(ssc.compareTo(this.maxValue) > 0) {throw new IllegalArgumentException("ssc must not be larger than max value");}
		
		this.value = ssc;
	}
	
	public void setValue(byte[] newValue) {
		this.setValue(new BigInteger(1, newValue));
	}

	/**
	 * @return the byteLength
	 */
	public int getMaxByteLength() {
		return this.maxByteLength;
	}

	/**
	 * @param maxByteLength the byteLength to set
	 */
	public void setMaxByteLength(int maxByteLength) {
		if(maxByteLength < 1) {throw new IllegalArgumentException("max byte length must not be smaller than 1");}
		
		this.maxByteLength = maxByteLength;
		this.maxValue = ((new BigInteger((new Integer(2)).toString())).pow(maxByteLength * 8)).subtract(BigInteger.ONE);
	}
}
