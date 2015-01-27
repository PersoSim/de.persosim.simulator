package de.persosim.simulator.tlv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import mockit.Deencapsulation;

import org.junit.Test;

import de.persosim.simulator.exception.ISO7816Exception;
import de.persosim.simulator.utils.Utils;

public class PrimitiveTlvDataObjectTest {
	
	/**
	 * Positive test case: Extract primitive TLV data object from a range out of
	 * a larger byte array. The range is smaller than the length of the byte
	 * array and larger than the minimum range required for the respective
	 * object
	 */
	@Test
	public void testConstructorByteArrayIntInt_LargeRange() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		byte[] tlvExpected = Utils.concatByteArrays(tagExpected,
				lengthExpected, valueExpected);

		/* dummy data to prepend */
		byte[] byteArrayPre = new byte[] { (byte) 0xFF, (byte) 0xFF };
		/* dummy data to append */
		byte[] byteArrayPost = new byte[] { (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF };

		/* build actual byte array */
		byte[] byteArray = Utils.concatByteArrays(byteArrayPre, tlvExpected,
				byteArrayPost);

		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(
				byteArray, byteArrayPre.length, byteArray.length);
		assertArrayEquals("Equals expected byte array representation",
				tlvObject.toByteArray(), tlvExpected);
	}
	
	/**
	 * Positive test case: Extract primitive TLV data object from a range out of
	 * a larger byte array. The range is smaller than the length of the byte
	 * array and matches the minimum range required for the respective object
	 */
	@Test
	public void testConstructorByteArrayIntInt_MinRange() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		byte[] tlvExpected = Utils.concatByteArrays(tagExpected,
				lengthExpected, valueExpected);

		/* dummy data to prepend */
		byte[] byteArrayPre = new byte[] { (byte) 0xFF, (byte) 0xFF };
		/* dummy data to append */
		byte[] byteArrayPost = new byte[] { (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF };

		/* build actual byte array */
		byte[] byteArray = Utils.concatByteArrays(byteArrayPre, tlvExpected,
				byteArrayPost);

		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(
				byteArray, byteArrayPre.length, byteArrayPre.length
						+ tlvExpected.length);
		assertArrayEquals("Equals expected byte array representation",
				tlvObject.toByteArray(), tlvExpected);
	}
	
	/**
	 * Negative test case: Extract primitive TLV data object from a range out of
	 * a larger byte array. The range is smaller than the length of the byte
	 * array and is 1 byte smaller than the minimum range required for the
	 * respective object
	 */
	@Test(expected = ISO7816Exception.class)
	public void testConstructorByteArrayIntInt_TooSmallRange() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		byte[] tlvExpected = Utils.concatByteArrays(tagExpected,
				lengthExpected, valueExpected);

		/* dummy data to prepend */
		byte[] byteArrayPre = new byte[] { (byte) 0xFF, (byte) 0xFF };
		/* dummy data to append */
		byte[] byteArrayPost = new byte[] { (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF };

		/* build actual byte array */
		byte[] byteArray = Utils.concatByteArrays(byteArrayPre, tlvExpected,
				byteArrayPost);

		new PrimitiveTlvDataObject(byteArray, byteArrayPre.length,
				byteArrayPre.length + tlvExpected.length - 1);
	}
	
	/**
	 * Positive test case: Construct primitive TLV data object from basic T-L-V
	 * elements
	 */
	@Test
	public void testConstructorTagLengthValueBasicTlvElements() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		byte[] tlvExpected = Utils.concatByteArrays(tagExpected,
				lengthExpected, valueExpected);

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = new TlvValuePlain(valueExpected);

		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(tag,
				length, value);
		assertArrayEquals("Equals expected byte array representation",
				tlvObject.toByteArray(), tlvExpected);
	}
	
	/**
	 * Positive test case: Construct primitive TLV data object from basic T-V
	 * elements - L is to be set implicitly
	 */
	@Test
	public void testConstructorTagLengthValueBasicTvElements() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		byte[] tlvExpected = Utils.concatByteArrays(tagExpected,
				lengthExpected, valueExpected);

		TlvTag tag = new TlvTag(tagExpected);
		
		TlvValuePlain value = new TlvValuePlain(valueExpected);

		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(tag,
				value);
		assertArrayEquals("Equals expected byte array representation",
				tlvObject.toByteArray(), tlvExpected);
	}
	
	/**
	 * Negative test case: Construct primitive TLV data object from basic T-L-V
	 * elements, V not matching L
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorTagLengthValue_LengthMismatch() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length greater than actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0C };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = new TlvValuePlain(valueExpected);

		new PrimitiveTlvDataObject(tag, length, value);
	}
	
	/**
	 * Negative test case: Construct primitive TLV data object from basic T-L-V
	 * elements, T indicates constructed encoding
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorTagLengthValue_ConstructedTag() {
		/* set arbitrary but "valid" constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0xA0 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = new TlvValuePlain(valueExpected);

		new PrimitiveTlvDataObject(tag, length, value);
	}
	
	/**
	 * Construct primitive TLV data object from basic T-L-V elements; returned T
	 * must not be the same object as provided T
	 */
	@Test
	public void testGetTlvTag_DifferentFromConstructor() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = new TlvValuePlain(valueExpected);

		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(tag,
				length, value);

		assertNotSame(tag, tlvObject.getTlvTag());
	}
	
	/**
	 * Construct primitive TLV data object from basic T-L-V elements; stored T
	 * must not be the same object as provided T, thus later modifications on
	 * the parameter T should NOT be reflected by the TlvDataObject
	 */
	@Test
	public void testConstructorTagLengthValue_TagImmutableFromOutside() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = new TlvValuePlain(valueExpected);

		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(tag,
				length, value);

		/*
		 * set arbitrary but valid primitive tag different than the one defined
		 * above
		 */
		byte[] tagExpectedNew = new byte[] { (byte) 0x08 };

		Deencapsulation.setField(tag, "tagField", tagExpectedNew);

		assertNotEquals(tag, tlvObject.getTlvTag());
	}
	
	/**
	 * After setting tag through setTlvTag() stored tag
	 * must not be the same object as provided parameter, thus later modifications on
	 * the parameter should NOT be reflected by the TlvDataObject
	 */
	@Test
	public void testSetTag_TagImmutableFromOutside() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = new TlvValuePlain(valueExpected);

		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(tag,
				length, value);

		TlvTag newTag = new TlvTag(new byte[] { (byte) 0x08 });
		tlvObject.setTag(newTag);

		/*
		 * set arbitrary but valid primitive tag different than the one defined
		 * above
		 */
		byte[] tagExpectedNew = new byte[] { (byte) 0x05 };
		Deencapsulation.setField(newTag, "tagField", tagExpectedNew);

		assertNotEquals(newTag, tlvObject.getTlvTag());
	}
	
	/**
	 * Negative test case: Set tag with a constructed tag
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTag_ConstructedTag() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = new TlvValuePlain(valueExpected);

		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(tag,
				length, value);

		/* set arbitrary but valid constructed tag */
		byte[] tagExpectedNew = new byte[] { (byte) 0x28 };

		TlvTag tag2 = new TlvTag(tagExpectedNew);
		tlvObject.setTag(tag2);
	}
	
	/**
	 * Positive test case: TLV data objects equals itself and other objects
	 * generated with the same constructor arguments
	 */
	@Test
	public void testEquals() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		byte[] tlvExpected = Utils.concatByteArrays(tagExpected,
				lengthExpected, valueExpected);

		PrimitiveTlvDataObject tlvObject1 = new PrimitiveTlvDataObject(
				tlvExpected);
		PrimitiveTlvDataObject tlvObject2 = new PrimitiveTlvDataObject(
				tlvExpected);

		assertEquals("Equals self", tlvObject1, tlvObject1);
		assertEquals("Equals same constructor arguments", tlvObject1,
				tlvObject2);
	}
	
	/**
	 * Positive test case: Set new value and have object automatically correct length
	 */
	@Test
	public void testGetLength_ModifiedValueLength() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected1 = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected1 = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		byte[] tlvExpected1 = Utils.concatByteArrays(tagExpected,
				lengthExpected1, valueExpected1);

		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(
				tlvExpected1);
		
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected2 = new byte[] { (byte) 0x04 };
		/* set arbitrary value */
		byte[] valueExpected2 = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00 };
		TlvValuePlain tlvValue2 = new TlvValuePlain(valueExpected2);
		
		tlvObject.setValue(tlvValue2);
		
		byte[] tlvExpected2 = Utils.concatByteArrays(tagExpected,
				lengthExpected2, valueExpected2);
		
		assertArrayEquals("Equals expected byte array representation", tlvObject.toByteArray(),
				tlvExpected2);
	}
	
	/**
	 * Positive test case: Set new length with alternative BER but non-DER encoding 
	 */
	@Test
	public void testSetLengthTlv_AlternateBerEncoding() {
		/* set arbitrary but valid primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected1 = new byte[] { (byte) 0x0A };
		/* set arbitrary value */
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04 };

		byte[] tlvExpected1 = Utils.concatByteArrays(tagExpected,
				lengthExpected1, valueExpected);

		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(
				tlvExpected1);
		
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected2 = new byte[] { (byte) 0x81, (byte) 0x0A };
		
		TlvLength tlvLength2 = new TlvLength(lengthExpected2);
		
		tlvObject.setLength(tlvLength2);
		
		byte[] tlvExpected2 = Utils.concatByteArrays(tagExpected,
				lengthExpected2, valueExpected);
		
		assertArrayEquals("Equals expected byte array representation", tlvObject.toByteArray(),
				tlvExpected2);
	}

	/**
	 * Negative test case: tlvtaginput must not be null. 
	 */
	//FIXME LSG why still test redundant code?
	@Test(expected=NullPointerException.class)
	public void testConstructor_TagNull()
	{
		byte[] lengthExpected = new byte[] { (byte) 0x0A };
		byte[] valueExpected = new byte[] { (byte) 0x04};
		
		TlvTag tag = null; //remove
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = new TlvValuePlain(valueExpected);
		@SuppressWarnings("unused") //FIXME LSG don't assign the result and you won'T need to suppress a warning
		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(tag,length,value,true);
	}
	
	/**
	 * Negative test case: tlvlengthinput must not be null. 
	 */
	//FIXME LSG why still test redundant code?
	@Test(expected=NullPointerException.class)
	public void testTlvTagLength_Equals_Null()
	{
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		byte[] lengthExpected = new byte[] { (byte) 0x04};
		
		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = null;
		@SuppressWarnings("unused") //FIXME LSG don't assign the result and you won'T need to suppress a warning
		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(tag,length,value,false);
	}
	
	/**
	 * Negative test case: In the setTag method the tlvtaginput must not be null. 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetTag_TlvTagInput_Equals_Null()
	{
		//FIXME LSG use TlvDataObjectFactory instead of the following lines!!! and check the input you need (use a minimal input)
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		byte[] lengthExpected = new byte[] { (byte) 0x0A};
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04};
		
		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = new TlvValuePlain(valueExpected);
		
		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(tag,length,value,true);
		tag = null;
		tlvObject.setTag(tag, true);
	}
	
	/**
	 * Negative test case: In the setValue method the tlvvalueplaininput must not be null.
	 */
	@Test(expected=NullPointerException.class)
	public void testSetValue_TlvValuePlainInput_Equals_Null()
	{
		//FIXME LSG use TlvDataObjectFactory instead of the following lines!!! and check the input you need (use a minimal input)
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		byte[] lengthExpected = new byte[] { (byte) 0x0A};
		byte[] valueExpected = new byte[] { (byte) 0x04, (byte) 0x00,
				(byte) 0x7F, (byte) 0x00, (byte) 0x07, (byte) 0x02,
				(byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x04};
		
		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvValuePlain value = new TlvValuePlain(valueExpected);
		
		PrimitiveTlvDataObject tlvObject = new PrimitiveTlvDataObject(tag,length,value,true);
		value = null;
		tlvObject.setValue(value);
	}

	//TODO missing tests
	// modification of value / update of length field (according to package doc this should also work if the existing value is modified and not only if a new value is set)
}
