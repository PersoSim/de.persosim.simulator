package de.persosim.simulator.tlv;

import java.util.Comparator;

import de.persosim.simulator.utils.Utils;

/**
 * This class implements a comparator for sorting TLV data objects in DER-TLV
 * order. Sorting is performed based on the sorting of tags. In detail tags
 * again are sorted based on their indicated class and tag number.
 * 
 * !Attention!: Sorting is performed without checking either explicitly or
 * implicitly for valid DER encoding of the objects to be sorted. Checks for
 * valid DER encoding must be performed separately. Sorting however will also
 * work correctly for valid BER but not DER encoded TLV data objects.
 * 
 * @author slutters
 * 
 */
public class TlvDataObjectComparatorDer implements Comparator<TlvDataObject> {
	
	@Override
	public int compare(TlvDataObject tlvdo1, TlvDataObject tlvdo2) {
		TlvTag tlvTag1, tlvTag2;
		
		tlvTag1 = tlvdo1.getTlvTag();
		tlvTag2 = tlvdo2.getTlvTag();
		
		return compare(tlvTag1, tlvTag2);
	}
	
	/**
	 * Performs Comparator's sorting based on tags
	 * @param tlvTag1 tag 1
	 * @param tlvTag2 tag 2
	 * @return the Comparator's compare result
	 */
	private int compare(TlvTag tlvTag1, TlvTag tlvTag2) {
		short class1 = Utils.maskUnsignedByteToShort(tlvTag1.getEncodedClass());
		short class2 = Utils.maskUnsignedByteToShort(tlvTag2.getEncodedClass());
		short classDiff = (short) (class1 - class2);
		
		if(classDiff != 0) {
			return classDiff;
		} else{
			return tlvTag1.getIndicatedTagNo() - tlvTag2.getIndicatedTagNo();
		}
	}
	
}
