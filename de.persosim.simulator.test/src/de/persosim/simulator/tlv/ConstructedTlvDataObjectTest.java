package de.persosim.simulator.tlv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import mockit.Deencapsulation;

import org.junit.Test;

import de.persosim.simulator.exception.ISO7816Exception;
import de.persosim.simulator.utils.Utils;

public class ConstructedTlvDataObjectTest {
	
	/**
	 * Positive test case: Extract constructed TLV data object from a range
	 * being larger than the minimum range required
	 */
	@Test
	public void testConstructorByteArrayIntIntConstructedTlvDataObjectLargerRangeLargerArray() {
		/* arbitrary but valid constructed TLV data object */
		byte[] tlvExpected = new byte[] { (byte) 0x21, (byte) 0x08,
				(byte) 0x01, (byte) 0x02, (byte) 0xFF, (byte) 0xFF,
				(byte) 0x02, (byte) 0x02, (byte) 0xEE, (byte) 0xEE };

		/* dummy data to prepend */
		byte[] byteArrayPre = new byte[] { (byte) 0xFF, (byte) 0xFF };
		/* dummy data to append */
		byte[] byteArrayPost = new byte[] { (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF };

		/* build actual byte array */
		byte[] byteArray = Utils.concatByteArrays(byteArrayPre, tlvExpected,
				byteArrayPost);

		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(
				byteArray, byteArrayPre.length, byteArray.length);

		assertArrayEquals("Equals expected byte array representation",
				tlvObject.toByteArray(), tlvExpected);
	}
	
	/**
	 * Positive test case: Extract constructed TLV data object from a range
	 * being exactly of the same size as the minimum range required
	 */
	@Test
	public void testConstructorByteArrayIntIntMinRangeLargerArray() {
		/* arbitrary but valid constructed TLV data object */
		byte[] tlvExpected = new byte[] { (byte) 0x21, (byte) 0x08,
				(byte) 0x01, (byte) 0x02, (byte) 0xFF, (byte) 0xFF,
				(byte) 0x02, (byte) 0x02, (byte) 0xEE, (byte) 0xEE };

		/* dummy data to prepend */
		byte[] byteArrayPre = new byte[] { (byte) 0xFF, (byte) 0xFF };
		/* dummy data to append */
		byte[] byteArrayPost = new byte[] { (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF };

		/* build actual byte array */
		byte[] byteArray = Utils.concatByteArrays(byteArrayPre, tlvExpected,
				byteArrayPost);

		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(
				byteArray, byteArrayPre.length, byteArray.length
						- byteArrayPost.length);

		assertArrayEquals("Equals expected byte array representation",
				tlvObject.toByteArray(), tlvExpected);
	}
	
	/**
	 * Negative test case: Extract constructed TLV data object from a range
	 * being 1 byte smaller than the minimum range required
	 */
	@Test(expected = ISO7816Exception.class)
	public void testConstructorByteArrayIntIntSmallerRangeLargerArray() {
		/* arbitrary but valid constructed TLV data object */
		byte[] tlvExpected = new byte[] { (byte) 0x21, (byte) 0x08,
				(byte) 0x01, (byte) 0x02, (byte) 0xFF, (byte) 0xFF,
				(byte) 0x02, (byte) 0x02, (byte) 0xEE, (byte) 0xEE };

		/* dummy data to prepend */
		byte[] byteArrayPre = new byte[] { (byte) 0xFF, (byte) 0xFF };
		/* dummy data to append */
		byte[] byteArrayPost = new byte[] { (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF };

		/* build actual byte array */
		byte[] byteArray = Utils.concatByteArrays(byteArrayPre, tlvExpected,
				byteArrayPost);

		new ConstructedTlvDataObject(byteArray, byteArrayPre.length,
				byteArray.length - byteArrayPost.length - 1);
	}
	
	/**
	 * Positive test case: Construct constructed TLV data object from basic
	 * T-L-V elements
	 */
	@Test
	public void testConstructorTagLengthValueTlvTagTlvLengthTlvValue() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x08 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		byte[] tlvExpected = Utils.concatByteArrays(tagExpected,
				lengthExpected, valueExpected);

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);

		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag,
				length, value);

		assertArrayEquals("Equals expected byte array representation",
				tlvObject.toByteArray(), tlvExpected);
	}
	
	/**
	 * Negative test case: Construct constructed TLV data object from basic
	 * T-L-V elements, V not matching L
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorTagLengthValueLengthNotMatchingValue() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		/* set arbitrary but valid length smaller than actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x07 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		byte[] tlvExpected = Utils.concatByteArrays(tagExpected,
				lengthExpected, valueExpected);

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);

		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag,
				length, value);

		assertArrayEquals("Equals expected byte array representation",
				tlvObject.toByteArray(), tlvExpected);
	}
	
	/**
	 * Negative test case: Construct constructed TLV data object from basic
	 * T-L-V elements, T indicates primitive encoding
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorTagLengthValueWithPrimitiveTag() {
		/* set arbitrary but "valid" primitive tag */
		byte[] tagExpected = new byte[] { (byte) 0x01 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x08 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);

		new ConstructedTlvDataObject(tag, length, value);
	}
	
	/**
	 * Positive test case: Construct constructed TLV data object from basic
	 * T-L-V elements; returned T must not be the same object as provided T
	 */
	@Test
	public void testConstructorTagLengthValueTagNotSameWhenRetrieved() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x08 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);

		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag,
				length, value);

		boolean isSame = tag == tlvObject.getTlvTag();
		assertEquals("Provided tag must mismatch retrieved", isSame, false);
	}
	
	/**
	 * Positive test case: Construct constructed TLV data object from basic
	 * T-L-V elements; stored T must not be the same object as provided T
	 */
	@Test
	public void testConstructorTagLengthValueStoredTNotSameAsProvided() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x08 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);

		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag,
				length, value);
		
		/* set arbitrary but valid constructed tag different from the one defined before */
		byte[] tagExpected2 = new byte[] { (byte) 0x24 };
		
		Deencapsulation.setField(tag, "tagField", tagExpected2);

		boolean isEqual = tag.equals(tlvObject.getTlvTag());
		assertEquals("Provided tag must not be imported directly", isEqual,
				false);
	}
	
	/**
	 * Positive test case:
	 * Construct constructed TLV data object from basic T-L-V elements; returned T must not be the same object as stored T
	 */
	@Test
	public void testConstructorTagLengthValueReturnedTNotSameAsStored() {	
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x08 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0xFF, (byte) 0xFF,
				(byte) 0x02, (byte) 0x02, (byte) 0xEE, (byte) 0xEE };
		
		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);
		
		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag, length, value);
		
		/* set arbitrary but valid constructed tag different from the one defined before */
		byte[] tagExpected2 = new byte[] { (byte) 0x24 };
		
		TlvTag tagMod = tlvObject.getTlvTag();
		Deencapsulation.setField(tagMod, "tagField", tagExpected2);
		
		boolean isEqual = tagMod.equals(tlvObject.getTlvTag());
		assertEquals("Stored tag must not be returned directly", isEqual, false);
	}
	
	/**
	 * Positive test case:
	 * Construct constructed TLV data object from basic T only
	 */
	@Test
	public void testConstructorTag() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		
		TlvTag tag = new TlvTag(tagExpected);
		
		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag);
		
		/* set arbitrary but valid length matching actual length of value (0) */
		byte[] lengthExpected = new byte[] { (byte) 0x00 };

		byte[] tlvExpected = Utils.concatByteArrays(tagExpected,
				lengthExpected);
		
		assertArrayEquals("Equals expected byte array representation", tlvObject.toByteArray(), tlvExpected);
	}
	
	/**
	 * Positive test case:
	 * Add primitive tag
	 */
	@Test
	public void testAddTlvDataObjectTlvDataObjectPrimitiveTag() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		
		TlvTag tag = new TlvTag(tagExpected);
		
		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag);
		
		/* set arbitrary but valid primitive TLV data object */
		byte[] tlvExpected2 = new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0xFF, (byte) 0xFF };
		
		PrimitiveTlvDataObject tlvObject2 = new PrimitiveTlvDataObject(tlvExpected2);
		
		tlvObject.addTlvDataObject(tlvObject2);
		
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected3 = new byte[] { (byte) 0x04 };
		
		byte[] tlvExpected3 = Utils.concatByteArrays(tagExpected, lengthExpected3, tlvExpected2);
		
		assertArrayEquals("Equals expected byte array representation", tlvObject.toByteArray(), tlvExpected3);
	}
	
	/**
	 * Positive test case: Add nested constructed tags
	 */
	@Test
	public void testAddTlvDataObjectTlvDataObjectNestedConstructedTlvDataObject() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		TlvTag tag = new TlvTag(tagExpected);
		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(tag);

		/* set arbitrary but valid primitive TLV data object */
		byte[] tlvExpected2 = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF };
		PrimitiveTlvDataObject tlvObject11 = new PrimitiveTlvDataObject(
				tlvExpected2);

		tlvObject1.addTlvDataObject(tlvObject11);

		ConstructedTlvDataObject tlvObject12 = new ConstructedTlvDataObject(tag);
		PrimitiveTlvDataObject tlvObject121 = new PrimitiveTlvDataObject(
				tlvExpected2);
		PrimitiveTlvDataObject tlvObject122 = new PrimitiveTlvDataObject(
				tlvExpected2);
		tlvObject12.addTlvDataObject(tlvObject121);
		tlvObject12.addTlvDataObject(tlvObject122);
		tlvObject1.addTlvDataObject(tlvObject12);

		ConstructedTlvDataObject tlvObject13 = new ConstructedTlvDataObject(tag);
		tlvObject1.addTlvDataObject(tlvObject13);

		byte[] lengthExpected12 = TlvLength
				.getLengthEncoding(2 * tlvExpected2.length);
		byte[] lengthExpected13 = TlvLength.getLengthEncoding(0);
		byte[] lengthExpected1 = TlvLength.getLengthEncoding(tlvObject11
				.getLength()

				+ tlvObject12.getTlvTag().getLength()
				+ lengthExpected12.length
				+ (2 * tlvExpected2.length)

				+ tagExpected.length + 1 + 0);

		byte[] tlvExpectedX = Utils.concatByteArrays(tagExpected,
				lengthExpected1, tlvExpected2, tagExpected, lengthExpected12,
				tlvExpected2, tlvExpected2, tagExpected, lengthExpected13);

		assertArrayEquals("Equals expected byte array representation",
				tlvObject1.toByteArray(), tlvExpectedX);
	}
	
	/**
	 * Positive test case: Get no of elements in nested constructed TLV data
	 * object
	 */
	@Test
	public void testGetNoOfElementsNestedConstructedTlvDataObject() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		TlvTag tag = new TlvTag(tagExpected);
		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(tag);
		assertEquals("Is expected no of elements",
				tlvObject1.getNoOfElements(), 0);

		/* set arbitrary but valid primitive TLV data object */
		byte[] tlvExpected2 = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF };

		PrimitiveTlvDataObject tlvObject11 = new PrimitiveTlvDataObject(
				tlvExpected2);
		tlvObject1.addTlvDataObject(tlvObject11);
		assertEquals("Is expected no of elements",
				tlvObject1.getNoOfElements(), 1);

		ConstructedTlvDataObject tlvObject12 = new ConstructedTlvDataObject(tag);
		PrimitiveTlvDataObject tlvObject121 = new PrimitiveTlvDataObject(
				tlvExpected2);
		PrimitiveTlvDataObject tlvObject122 = new PrimitiveTlvDataObject(
				tlvExpected2);
		tlvObject12.addTlvDataObject(tlvObject121);
		tlvObject12.addTlvDataObject(tlvObject122);
		tlvObject1.addTlvDataObject(tlvObject12);
		assertEquals("Is expected no of elements",
				tlvObject1.getNoOfElements(), 2);

		ConstructedTlvDataObject tlvObject13 = new ConstructedTlvDataObject(tag);
		tlvObject1.addTlvDataObject(tlvObject13);
		assertEquals("Is expected no of elements",
				tlvObject1.getNoOfElements(), 3);
	}
	
	/**
	 * Positive test case: Get recursive no of elements in nested constructed
	 * TLV data object
	 */
	@Test
	public void testGetNoOfElementsNestedConstructedTlvDataObjectRecursive() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		TlvTag tag = new TlvTag(tagExpected);
		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(tag);
		assertEquals("Is expected no of elements",
				tlvObject1.getNoOfElements(true), 0);

		/* set arbitrary but valid primitive TLV data object */
		byte[] tlvExpected2 = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF };

		PrimitiveTlvDataObject tlvObject11 = new PrimitiveTlvDataObject(
				tlvExpected2);
		tlvObject1.addTlvDataObject(tlvObject11);
		assertEquals("Is expected no of elements",
				tlvObject1.getNoOfElements(true), 1);

		ConstructedTlvDataObject tlvObject12 = new ConstructedTlvDataObject(tag);
		PrimitiveTlvDataObject tlvObject121 = new PrimitiveTlvDataObject(
				tlvExpected2);
		PrimitiveTlvDataObject tlvObject122 = new PrimitiveTlvDataObject(
				tlvExpected2);
		tlvObject12.addTlvDataObject(tlvObject121);
		tlvObject12.addTlvDataObject(tlvObject122);
		tlvObject1.addTlvDataObject(tlvObject12);
		assertEquals("Is expected no of elements",
				tlvObject1.getNoOfElements(true), 4);

		ConstructedTlvDataObject tlvObject13 = new ConstructedTlvDataObject(tag);
		tlvObject1.addTlvDataObject(tlvObject13);
		assertEquals("Is expected no of elements",
				tlvObject1.getNoOfElements(true), 5);
	}
	
	/**
	 * Positive test case: Get path of tag from nested constructed TLV data
	 * object
	 */
	@Test
	public void testGetTagFieldTlvPathNestedConstructedTlvDataObject() {
		/* set arbitrary but valid primitive tags with ascending numbers */
		TlvTag tagP1 = new TlvTag((byte) 0x01);
		TlvTag tagP2 = new TlvTag((byte) 0x02);
		TlvTag tagP3 = new TlvTag((byte) 0x03);

		/* set arbitrary but valid constructed tags with ascending numbers */
		TlvTag tagC1 = new TlvTag((byte) 0x21);
		TlvTag tagC2 = new TlvTag((byte) 0x22);
		TlvTag tagC3 = new TlvTag((byte) 0x23);
		TlvTag tagC4 = new TlvTag((byte) 0x24);

		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(
				tagC1);
		ConstructedTlvDataObject tlvObject11 = new ConstructedTlvDataObject(
				tagC2);
		ConstructedTlvDataObject tlvObject111 = new ConstructedTlvDataObject(
				tagC3);
		ConstructedTlvDataObject tlvObject1111 = new ConstructedTlvDataObject(
				tagC4);

		PrimitiveTlvDataObject tlvObject11111 = new PrimitiveTlvDataObject(
				tagP1);
		PrimitiveTlvDataObject tlvObject11112 = new PrimitiveTlvDataObject(
				tagP2);
		PrimitiveTlvDataObject tlvObject11113 = new PrimitiveTlvDataObject(
				tagP3);

		tlvObject1111.addTlvDataObject(tlvObject11111);
		tlvObject1111.addTlvDataObject(tlvObject11112);
		tlvObject1111.addTlvDataObject(tlvObject11113);

		tlvObject111.addTlvDataObject(tlvObject1111);
		tlvObject11.addTlvDataObject(tlvObject111);
		tlvObject1.addTlvDataObject(tlvObject11);

		TlvPath path = new TlvPath();
		path.add(tagC2);
		path.add(tagC3);
		path.add(tagC4);
		path.add(tagP2);

		assertEquals("Is expected path", tlvObject1.getTlvDataObject(path),
				tlvObject11112);
	}
	
	/**
	 * Positive test case: Adds a TLV data object to a certain path within a
	 * nested constructed TLV data object
	 */
	@Test
	public void testAddTlvDataObjectTlvPathTlvDataObjectNestedConstructedTlvDataObject() {
		/* set arbitrary but valid primitive tags with ascending numbers */
		TlvTag tagP1 = new TlvTag((byte) 0x01);
		TlvTag tagP2 = new TlvTag((byte) 0x02);
		TlvTag tagP3 = new TlvTag((byte) 0x03);

		/* set arbitrary but valid constructed tags with ascending numbers */
		TlvTag tagC1 = new TlvTag((byte) 0x21);
		TlvTag tagC2 = new TlvTag((byte) 0x22);
		TlvTag tagC3 = new TlvTag((byte) 0x23);
		TlvTag tagC4 = new TlvTag((byte) 0x24);

		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(
				tagC1);
		ConstructedTlvDataObject tlvObject11 = new ConstructedTlvDataObject(
				tagC2);
		ConstructedTlvDataObject tlvObject111 = new ConstructedTlvDataObject(
				tagC3);
		ConstructedTlvDataObject tlvObject1111 = new ConstructedTlvDataObject(
				tagC4);

		PrimitiveTlvDataObject tlvObject11111 = new PrimitiveTlvDataObject(
				tagP1);
		PrimitiveTlvDataObject tlvObject11112 = new PrimitiveTlvDataObject(
				tagP2);
		PrimitiveTlvDataObject tlvObject11113 = new PrimitiveTlvDataObject(
				tagP3);

		tlvObject1111.addTlvDataObject(tlvObject11111);
		tlvObject1111.addTlvDataObject(tlvObject11112);

		tlvObject111.addTlvDataObject(tlvObject1111);
		tlvObject11.addTlvDataObject(tlvObject111);
		tlvObject1.addTlvDataObject(tlvObject11);

		TlvPath path = new TlvPath();
		path.add(tagC2);
		path.add(tagC3);
		path.add(tagC4);

		tlvObject1.addTlvDataObject(path, tlvObject11113);

		path.add(tagP3);

		assertEquals("Is expected path", tlvObject1.getTlvDataObject(path),
				tlvObject11113);
	}
	
	/**
	 * Negative test case: Adds a TLV data object to a certain non-existing path
	 * within a nested constructed TLV data object
	 */
	@Test
	public void testAddTlvDataObjectTlvPathTlvDataObjectNonExistingPath() {
		/* set arbitrary but valid primitive tags with ascending numbers */
		TlvTag tagP1 = new TlvTag((byte) 0x01);
		TlvTag tagP2 = new TlvTag((byte) 0x02);
		TlvTag tagP3 = new TlvTag((byte) 0x03);

		/* set arbitrary but valid constructed tags with ascending numbers */
		TlvTag tagC1 = new TlvTag((byte) 0x21);
		TlvTag tagC2 = new TlvTag((byte) 0x22);
		TlvTag tagC3 = new TlvTag((byte) 0x23);
		TlvTag tagC4 = new TlvTag((byte) 0x24);

		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(
				tagC1);
		ConstructedTlvDataObject tlvObject11 = new ConstructedTlvDataObject(
				tagC2);
		ConstructedTlvDataObject tlvObject111 = new ConstructedTlvDataObject(
				tagC3);
		ConstructedTlvDataObject tlvObject1111 = new ConstructedTlvDataObject(
				tagC4);

		PrimitiveTlvDataObject tlvObject11111 = new PrimitiveTlvDataObject(
				tagP1);
		PrimitiveTlvDataObject tlvObject11112 = new PrimitiveTlvDataObject(
				tagP2);
		PrimitiveTlvDataObject tlvObject11113 = new PrimitiveTlvDataObject(
				tagP3);

		tlvObject1111.addTlvDataObject(tlvObject11111);
		tlvObject1111.addTlvDataObject(tlvObject11112);

		tlvObject111.addTlvDataObject(tlvObject1111);
		tlvObject11.addTlvDataObject(tlvObject111);
		tlvObject1.addTlvDataObject(tlvObject11);

		TlvPath path = new TlvPath();
		path.add(tagC2);
		path.add(tagC4);
		path.add(tagC3);

		tlvObject1.addTlvDataObject(path, tlvObject11113);

		assertEquals("Is expected no of elements",
				tlvObject1.getNoOfElements(), 1);
		assertEquals("Is expected no of elements",
				tlvObject11.getNoOfElements(), 1);
		assertEquals("Is expected no of elements",
				tlvObject111.getNoOfElements(), 1);
		assertEquals("Is expected no of elements",
				tlvObject1111.getNoOfElements(), 2);
	}
	
	/**
	 * Positive test case: Remove a TLV data object from a certain path within a
	 * nested constructed TLV data object
	 */
	@Test
	public void testRemoveTlvDataObjectTlvPath() {
		/* set arbitrary but valid primitive tags with ascending numbers */
		TlvTag tagP1 = new TlvTag((byte) 0x01);
		TlvTag tagP2 = new TlvTag((byte) 0x02);
		TlvTag tagP3 = new TlvTag((byte) 0x03);

		/* set arbitrary but valid constructed tags with ascending numbers */
		TlvTag tagC1 = new TlvTag((byte) 0x21);
		TlvTag tagC2 = new TlvTag((byte) 0x22);
		TlvTag tagC3 = new TlvTag((byte) 0x23);
		TlvTag tagC4 = new TlvTag((byte) 0x24);

		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(
				tagC1);
		ConstructedTlvDataObject tlvObject11 = new ConstructedTlvDataObject(
				tagC2);
		ConstructedTlvDataObject tlvObject111 = new ConstructedTlvDataObject(
				tagC3);
		ConstructedTlvDataObject tlvObject1111 = new ConstructedTlvDataObject(
				tagC4);

		PrimitiveTlvDataObject tlvObject11111 = new PrimitiveTlvDataObject(
				tagP1);
		PrimitiveTlvDataObject tlvObject11112 = new PrimitiveTlvDataObject(
				tagP2);
		PrimitiveTlvDataObject tlvObject11113 = new PrimitiveTlvDataObject(
				tagP3);

		tlvObject1111.addTlvDataObject(tlvObject11111);
		tlvObject1111.addTlvDataObject(tlvObject11112);
		tlvObject1111.addTlvDataObject(tlvObject11113);

		tlvObject111.addTlvDataObject(tlvObject1111);
		tlvObject11.addTlvDataObject(tlvObject111);
		tlvObject1.addTlvDataObject(tlvObject11);

		TlvPath path = new TlvPath();
		path.add(tagC2);
		path.add(tagC3);
		path.add(tagC4);
		path.add(tagP2);

		tlvObject1.removeTlvDataObject(path);

		assertEquals("Is expected path", tlvObject1111.getNoOfElements(), 2);
	}
	
	/**
	 * Negative test case: Remove a non-existing TLV data object from a certain
	 * path within a nested constructed TLV data object
	 */
	@Test
	public void testRemoveTlvDataObjectTlvPathNonExistingTlvDataObject() {
		/* set arbitrary but valid primitive tags with ascending numbers */
		TlvTag tagP1 = new TlvTag((byte) 0x01);
		TlvTag tagP2 = new TlvTag((byte) 0x02);
		TlvTag tagP3 = new TlvTag((byte) 0x03);

		/* set arbitrary but valid constructed tags with ascending numbers */
		TlvTag tagC1 = new TlvTag((byte) 0x21);
		TlvTag tagC2 = new TlvTag((byte) 0x22);
		TlvTag tagC3 = new TlvTag((byte) 0x23);
		TlvTag tagC4 = new TlvTag((byte) 0x24);

		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(
				tagC1);
		ConstructedTlvDataObject tlvObject11 = new ConstructedTlvDataObject(
				tagC2);
		ConstructedTlvDataObject tlvObject111 = new ConstructedTlvDataObject(
				tagC3);
		ConstructedTlvDataObject tlvObject1111 = new ConstructedTlvDataObject(
				tagC4);

		PrimitiveTlvDataObject tlvObject11111 = new PrimitiveTlvDataObject(
				tagP1);
		PrimitiveTlvDataObject tlvObject11112 = new PrimitiveTlvDataObject(
				tagP2);

		tlvObject1111.addTlvDataObject(tlvObject11111);
		tlvObject1111.addTlvDataObject(tlvObject11112);

		tlvObject111.addTlvDataObject(tlvObject1111);
		tlvObject11.addTlvDataObject(tlvObject111);
		tlvObject1.addTlvDataObject(tlvObject11);

		TlvPath path = new TlvPath();
		path.add(tagC2);
		path.add(tagC3);
		path.add(tagC4);
		path.add(tagP3);

		tlvObject1.removeTlvDataObject(path);

		assertEquals("Is expected path", tlvObject1111.getNoOfElements(), 2);
	}
	
	/**
	 * Positive test case: Sorts a constructed TLV data object according to DER
	 * encoding rules
	 */
	@Test
	public void testSortComparatorDer() {
		/*
		 * set arbitrary but valid pairs of tags with different tag numbers
		 * representing all combinations of class and encoding
		 */
		TlvTag tagP1 = new TlvTag((byte) 0x01);
		TlvTag tagP2 = new TlvTag((byte) 0x02);
		TlvTag tagP3 = new TlvTag((byte) 0x41);
		TlvTag tagP4 = new TlvTag((byte) 0x42);
		TlvTag tagP5 = new TlvTag((byte) 0x81);
		TlvTag tagP6 = new TlvTag((byte) 0x82);
		TlvTag tagP7 = new TlvTag((byte) 0xC1);
		TlvTag tagP8 = new TlvTag((byte) 0xC2);

		TlvTag tagC1 = new TlvTag((byte) 0x21);

		ConstructedTlvDataObject tlvObjectC1 = new ConstructedTlvDataObject(
				tagC1);
		ConstructedTlvDataObject tlvObjectC2 = new ConstructedTlvDataObject(
				tagC1);

		PrimitiveTlvDataObject tlvObjectP1 = new PrimitiveTlvDataObject(tagP1);
		PrimitiveTlvDataObject tlvObjectP2 = new PrimitiveTlvDataObject(tagP2);
		PrimitiveTlvDataObject tlvObjectP3 = new PrimitiveTlvDataObject(tagP3);
		PrimitiveTlvDataObject tlvObjectP4 = new PrimitiveTlvDataObject(tagP4);
		PrimitiveTlvDataObject tlvObjectP5 = new PrimitiveTlvDataObject(tagP5);
		PrimitiveTlvDataObject tlvObjectP6 = new PrimitiveTlvDataObject(tagP6);
		PrimitiveTlvDataObject tlvObjectP7 = new PrimitiveTlvDataObject(tagP7);
		PrimitiveTlvDataObject tlvObjectP8 = new PrimitiveTlvDataObject(tagP8);

		tlvObjectC1.addTlvDataObject(tlvObjectP8);
		tlvObjectC1.addTlvDataObject(tlvObjectP5);
		tlvObjectC1.addTlvDataObject(tlvObjectP3);
		tlvObjectC1.addTlvDataObject(tlvObjectP7);
		tlvObjectC1.addTlvDataObject(tlvObjectP6);
		tlvObjectC1.addTlvDataObject(tlvObjectP1);
		tlvObjectC1.addTlvDataObject(tlvObjectP4);
		tlvObjectC1.addTlvDataObject(tlvObjectP2);

		tlvObjectC2.addTlvDataObject(tlvObjectP1);
		tlvObjectC2.addTlvDataObject(tlvObjectP2);
		tlvObjectC2.addTlvDataObject(tlvObjectP3);
		tlvObjectC2.addTlvDataObject(tlvObjectP4);
		tlvObjectC2.addTlvDataObject(tlvObjectP5);
		tlvObjectC2.addTlvDataObject(tlvObjectP6);
		tlvObjectC2.addTlvDataObject(tlvObjectP7);
		tlvObjectC2.addTlvDataObject(tlvObjectP8);

		tlvObjectC1.sort(new TlvDataObjectComparatorDer());

		assertArrayEquals("Is expected order of elements",
				tlvObjectC1.toByteArray(), tlvObjectC2.toByteArray());
	}
	
	/**
	 * Positive test case: Test DER encoding validity check on constructed TLV
	 * data object with unsorted but otherwise correctly encoded sub elements.
	 */
	@Test
	public void testIsValidDerEncodingSorting() {
		/*
		 * set arbitrary but valid pairs of tags with different tag numbers
		 * representing all combinations of class and encoding
		 */
		TlvTag tagP1 = new TlvTag((byte) 0x01);
		TlvTag tagP2 = new TlvTag((byte) 0x02);
		TlvTag tagP3 = new TlvTag((byte) 0x41);
		TlvTag tagP4 = new TlvTag((byte) 0x42);
		TlvTag tagP5 = new TlvTag((byte) 0x81);
		TlvTag tagP6 = new TlvTag((byte) 0x82);
		TlvTag tagP7 = new TlvTag((byte) 0xC1);
		TlvTag tagP8 = new TlvTag((byte) 0xC2);

		TlvTag tagC1 = new TlvTag((byte) 0x21);

		ConstructedTlvDataObject tlvObjectC1 = new ConstructedTlvDataObject(
				tagC1);

		PrimitiveTlvDataObject tlvObjectP1 = new PrimitiveTlvDataObject(tagP1);
		PrimitiveTlvDataObject tlvObjectP2 = new PrimitiveTlvDataObject(tagP2);
		PrimitiveTlvDataObject tlvObjectP3 = new PrimitiveTlvDataObject(tagP3);
		PrimitiveTlvDataObject tlvObjectP4 = new PrimitiveTlvDataObject(tagP4);
		PrimitiveTlvDataObject tlvObjectP5 = new PrimitiveTlvDataObject(tagP5);
		PrimitiveTlvDataObject tlvObjectP6 = new PrimitiveTlvDataObject(tagP6);
		PrimitiveTlvDataObject tlvObjectP7 = new PrimitiveTlvDataObject(tagP7);
		PrimitiveTlvDataObject tlvObjectP8 = new PrimitiveTlvDataObject(tagP8);

		tlvObjectC1.addTlvDataObject(tlvObjectP8);
		tlvObjectC1.addTlvDataObject(tlvObjectP5);
		tlvObjectC1.addTlvDataObject(tlvObjectP3);
		tlvObjectC1.addTlvDataObject(tlvObjectP7);
		tlvObjectC1.addTlvDataObject(tlvObjectP6);
		tlvObjectC1.addTlvDataObject(tlvObjectP1);
		tlvObjectC1.addTlvDataObject(tlvObjectP4);
		tlvObjectC1.addTlvDataObject(tlvObjectP2);

		assertEquals("Is expected order of elements",
				tlvObjectC1.isValidDerEncoding(), false);

		tlvObjectC1.sort(new TlvDataObjectComparatorDer());

		assertEquals("Is expected order of elements",
				tlvObjectC1.isValidDerEncoding(), true);
	}
	
	/**
	 * Positive test case: Construct constructed TLV data object from basic
	 * T-L-V elements with L being BER but non-DER encoded. Add another TLV data
	 * object and check for validity of DER encoding, i.e. whether L has been
	 * updated and is DER encoded.
	 */
	@Test
	public void testIsValidDerEncodingImplicitlyModifiedLength() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		/* set arbitrary but valid BER but non-DER length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x81, (byte) 0x08 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);

		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(tag,
				length, value);
		
		TlvTag tag2 = new TlvTag((byte) 0x03);
		PrimitiveTlvDataObject tlvObject2 = new PrimitiveTlvDataObject(tag2);
		
		tlvObject1.addTlvDataObject(tlvObject2);

		assertEquals("Is valid DER encoding",
				tlvObject1.isValidDerEncoding(), true);
	}
	
	/**
	 * Positive test case: Construct constructed TLV data object from basic
	 * T-L-V elements with L being BER but non-DER encoded and check for
	 * validity of DER encoding.
	 */
	@Test
	public void testIsValidDerEncodingBerNonDerLength() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		/* set arbitrary but valid BER but non-DER length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x81, (byte) 0x08 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);

		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(tag,
				length, value);

		assertEquals("Is valid DER encoding",
				tlvObject1.isValidDerEncoding(), false);
	}
	
	/**
	 * Negative test case: Set tag with a primitive tag
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTagTagPrimitiveTag() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x08 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);

		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag,
				length, value);

		/* set arbitrary but "valid" primitive tag */
		byte[] tagExpected2 = new byte[] { (byte) 0x01 };

		TlvTag tag2 = new TlvTag(tagExpected2);
		tlvObject.setTag(tag2);
	}
	
	/**
	 * Negative test case: Forced primitive tag fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testForceTagTlvTagPrimitiveTag() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x08 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);

		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag,
				length, value);

		/* set arbitrary but "valid" primitive tag */
		byte[] tagExpected2 = new byte[] { (byte) 0x01 };

		TlvTag tag2 = new TlvTag(tagExpected2);
		tlvObject.setTag(tag2, false);
	}
	
	/**
	 * Positive test case: TLV data objects equals itself and other objects
	 * generated with the same constructor arguments
	 */
	@Test
	public void testEqualsSameAndSameConstructorArguments() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		/* set arbitrary but valid length matching actual length of value */
		byte[] lengthExpected = new byte[] { (byte) 0x08 };
		/* set arbitrary value consisting of valid chained TLV data objects */
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);

		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(tag,
				length, value);
		ConstructedTlvDataObject tlvObject2 = new ConstructedTlvDataObject(tag,
				length, value);

		assertEquals("Equals self", tlvObject1, tlvObject1);
		assertEquals("Equals same constructor arguments", tlvObject1,
				tlvObject2);
	}
	
	/**
	 * Positive test case: step by step add nested constructed tags and check
	 * for correct computation of length
	 */
	@Test
	public void testAddTlvDataObjectTlvDataObjectLengthCheckForNestedConstructedTlvDataObject() {
		/* set arbitrary but valid constructed tag */
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		TlvTag tag = new TlvTag(tagExpected);
		ConstructedTlvDataObject tlvObject1 = new ConstructedTlvDataObject(tag);
		assertEquals("Equals expected length", tlvObject1.getLengthValue(), 0);

		/* set arbitrary but valid primitive TLV data object */
		byte[] tlvExpected2 = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF };
		PrimitiveTlvDataObject tlvObject11 = new PrimitiveTlvDataObject(
				tlvExpected2);

		tlvObject1.addTlvDataObject(tlvObject11);
		assertEquals("Equals expected length", tlvObject1.getLengthValue(),
				tlvObject11.getLength());

		ConstructedTlvDataObject tlvObject12 = new ConstructedTlvDataObject(tag);
		assertEquals("Equals expected length", tlvObject12.getLengthValue(), 0);

		PrimitiveTlvDataObject tlvObject121 = new PrimitiveTlvDataObject(
				tlvExpected2);
		PrimitiveTlvDataObject tlvObject122 = new PrimitiveTlvDataObject(
				tlvExpected2);
		tlvObject12.addTlvDataObject(tlvObject121);
		tlvObject12.addTlvDataObject(tlvObject122);
		assertEquals("Equals expected length", tlvObject12.getLengthValue(),
				2 * (tlvObject121.getLength()));

		tlvObject1.addTlvDataObject(tlvObject12);
		assertEquals("Equals expected length", tlvObject1.getLengthValue(),
				tlvObject11.getLength() + tlvObject12.getLength());

		ConstructedTlvDataObject tlvObject13 = new ConstructedTlvDataObject(tag);
		tlvObject1.addTlvDataObject(tlvObject13);
		assertEquals(
				"Equals expected length",
				tlvObject1.getLengthValue(),
				tlvObject11.getLength() + tlvObject12.getLength()
						+ tlvObject13.getLength());
	}
	
	/**
	 * Negative test case: the method setValue is getting the tlvDataObjectContainerInput, which is null.
	 */
	@Test(expected=NullPointerException.class)
	public void testSetValue_tlvDataObject_ContainerInput_Is_Null()
	{
		ConstructedTlvDataObject tlvObject = (ConstructedTlvDataObject) TlvDataObjectFactory.createTLVDataObject("21080102FFFF0202EEEE");
		tlvObject.setValue(null);	
	}
	
	/**
	 * Negative test case: the method setTag is getting the tlvTagInput, which is null.
	 */
	@Test(expected=NullPointerException.class)
	public void testSetTag_TlvTag_Input_Is_Null()
	{
		ConstructedTlvDataObject tlvObject = (ConstructedTlvDataObject) TlvDataObjectFactory.createTLVDataObject("21080102FFFF0202EEEE");
		tlvObject.setTag(null, true);
	}
	
	/**
	 * Negative test case: the method ConstructedTlvDataObject is getting tlvTagInput as input, which is null.
	 */
	@Test(expected=NullPointerException.class)
	//FIXME LSG why still test redundant code?
	public void testConstructedTlvDataObject_TlvTagInput_Is_Null()
	{
		byte[] lengthExpected = new byte[] { (byte) 0x08 };
		byte[] valueExpected = new byte[] { (byte) 0x01, (byte) 0x02,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x02, (byte) 0x02,
				(byte) 0xEE, (byte) 0xEE };

		TlvTag tag = null;
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = new TlvDataObjectContainer(valueExpected);
		@SuppressWarnings("unused")
		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag,
				length, value);
	}
	
	/**
	 * Negative test case: the method ConstructedTlvDataObject is getting tlvDateObjectContainer as input, which is null.
	 */
	@Test(expected=NullPointerException.class)
	//FIXME LSG why still test redundant code?
	public void testConstructedTlvDataObject_ContainerInput_Is_Null()
	{
		
		byte[] tagExpected = new byte[] { (byte) 0x21 };
		byte[] lengthExpected = new byte[] { (byte) 0x08 };
		
		TlvTag tag = new TlvTag(tagExpected);
		TlvLength length = new TlvLength(lengthExpected);
		TlvDataObjectContainer value = null;
		@SuppressWarnings("unused")
		ConstructedTlvDataObject tlvObject = new ConstructedTlvDataObject(tag,
				length, value);
	}
	//TODO missing tests
	// modification of child values / update of length field
	// expected methods like testGetLength_ChildLenghtIncreased and testGetLength_ChildLengthDecreased
	// maybe methods like testGetLength_ChildAdded and testGetLength_ChildRemoved, these can be marked with @Ignore and linked in Javadoc to similar methods like testAddTlvDataObject... 
	
}
