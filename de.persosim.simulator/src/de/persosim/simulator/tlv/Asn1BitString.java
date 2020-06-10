package de.persosim.simulator.tlv;

import java.util.Arrays;

import de.persosim.simulator.utils.BitField;

public class Asn1BitString {

	private BitField bits;

	public Asn1BitString(byte[] encoded) {
		TlvDataObject tlv = TlvDataObjectFactory.createTLVDataObject(encoded);
		if (!TlvConstants.TAG_BIT_STRING.equals(tlv.getTlvTag())) throw new IllegalArgumentException("Tag mismatch");
		
		byte[] tlvValue = tlv.getValueField();
		byte unusedBits = tlvValue[0];
		byte[] bytes = Arrays.copyOfRange(tlvValue, 1, tlvValue.length);
		int bitLength = bytes.length*8-unusedBits;
		boolean[] bitsToStore = new boolean[bitLength];
		
		bitLength--; //used for offset calculations
		for (int i = 0; i < bitsToStore.length; i++) {
			int byteNum = (bitLength-i) / 8;
			int position = (i+unusedBits)%8;
			int bitValue = (bytes[byteNum] >> position) & 1;
			bitsToStore[i] = bitValue > 0;
		}
		
		bits = new BitField(bitsToStore);
	}

	/**
	 * returns the value of the indicated bit
	 * @param i
	 * @return
	 */
	public Object getBit(int i) {
		return bits.getBit(i);
	}

	public Object getNumberOfBits() {
		return bits.getNumberOfBits();
	}
}
