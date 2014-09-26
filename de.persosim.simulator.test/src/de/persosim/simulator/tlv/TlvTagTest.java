package de.persosim.simulator.tlv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.persosim.simulator.exception.ISO7816Exception;
import de.persosim.simulator.utils.Utils;

public class TlvTagTest {

	/**
	 * Positive test case: Extract 1 byte tag from a range out of a larger byte
	 * array. The range is smaller than the length of the byte array and larger
	 * than the minimum range required for the respective tag
	 */
	@Test
	public void testConstructor_1ByteTagLargerRange() {
		byte tagExpected = Asn1.INTEGER;
		byte[] byteArray = new byte[] { 0x00, 0x00, tagExpected, 0x00, 0x00,
				0x00, 0x00 };

		TlvTag tagExtracted = new TlvTag(byteArray, 2, byteArray.length);
		assertArrayEquals("Equals expected byte array representation",
				tagExtracted.toByteArray(), new byte[] { tagExpected });
	}
	
	/**
	 * Positive test case: Extract 2 byte tag from a range out of a larger byte
	 * array. The range is smaller than the length of the byte array and larger
	 * than the minimum range required for the respective tag
	 */
	@Test
	public void testConstructor_2ByteTag() {
		/* set arbitrary but valid 2-byte tag */
		byte[] tagExpected = new byte[] { (byte) 0x7F, (byte) 0x4C };
		/* append dummy data */
		byte[] byteArray = new byte[] { (byte) 0xFF, (byte) 0xFF };
		/* append tag */
		byteArray = Utils.concatByteArrays(byteArray, tagExpected);
		/* append dummy data */
		byteArray = Utils.concatByteArrays(byteArray, new byte[] { (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF });

		TlvTag tagExtracted = new TlvTag(byteArray, 2, byteArray.length);
		assertArrayEquals("Equals expected byte array representation",
				tagExtracted.toByteArray(), tagExpected);
	}
	
	/**
	 * Positive test case: Extract 2 byte tag with minimum tag number allowed for the respective tag
	 */
	@Test
	public void testConstructor_2ByteTagMinLength() {
		/* set arbitrary but valid 2-byte tag with tag number 31 */
		byte[] tagExpected = new byte[] { (byte) 0x1F, (byte) 0x1F };

		TlvTag tagExtracted = new TlvTag(tagExpected, 0, tagExpected.length);
		assertArrayEquals("Equals expected byte array representation",
				tagExpected, tagExtracted.toByteArray());
	}
	
	/**
	 * Positive test case:
	 * Extract 2 byte tag from a range out of a larger byte array.
	 * The range is smaller than the length of the byte array and
	 * matches the minimum range required for the respective
	 * tag 
	 */
	@Test
	public void testConstructor_2ByteTagMinRange() {
		/* set arbitrary but valid 2-byte tag */
		byte[] tagExpected = new byte[]{(byte) 0x7F, (byte) 0x4C};
		/* append dummy data */
		byte[] byteArray = new byte[]{(byte) 0xFF, (byte) 0xFF};
		/* append tag */
		byteArray = Utils.concatByteArrays(byteArray, tagExpected);
		/* append dummy data */
		byteArray = Utils.concatByteArrays(byteArray, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
		
		TlvTag tagExtracted = new TlvTag(byteArray, 2, 4);
		assertArrayEquals("Equals expected byte array representation", tagExtracted.toByteArray(), tagExpected);
	}
	
	/**
	 * Negative test case: Extract 2 byte tag from a range out of a larger byte
	 * array. The range is smaller than the length of the byte array and 1 byte
	 * smaller than the minimum range required for the respective tag
	 */
	@Test(expected = ISO7816Exception.class)
	public void testConstructor_RangeTooShort() {
		/* set arbitrary but valid 2-byte tag */
		byte[] tagExpected = new byte[] { (byte) 0x7F, (byte) 0x4C };
		/* append dummy data */
		byte[] byteArray = new byte[] { (byte) 0xFF, (byte) 0xFF };
		/* append tag */
		byteArray = Utils.concatByteArrays(byteArray, tagExpected);
		/* append dummy data */
		byteArray = Utils.concatByteArrays(byteArray, new byte[] { (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF });

		new TlvTag(byteArray, 2, 3);
	}
	
	/**
	 * Positive test case: Extract 3 byte tag from a range out of a larger byte
	 * array. The range is smaller than the length of the byte array and matches
	 * the minimum range required for the respective tag
	 */
	@Test
	public void testConstructor_3ByteTag() {
		/* set arbitrary but valid 3-byte tag */
		byte[] tagExpected = new byte[] { (byte) 0x1F, (byte) 0x81, (byte) 0x00 };
		/* append dummy data */
		byte[] byteArray = new byte[] { (byte) 0xFF, (byte) 0xFF };
		/* append tag */
		byteArray = Utils.concatByteArrays(byteArray, tagExpected);
		/* append dummy data */
		byteArray = Utils.concatByteArrays(byteArray, new byte[] { (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF });

		TlvTag tagExtracted = new TlvTag(byteArray, 2, byteArray.length);
		assertArrayEquals("Equals expected byte array representation",
				tagExtracted.toByteArray(), tagExpected);
	}
	
	/**
	 * Negative test case: Extract 3 byte tag with invalid length encoding from
	 * a byte array. Encoded length is zero within otherwise valid three byte tag.
	 */
	@Test(expected = ISO7816Exception.class)
	public void testConstructor_3ByteTagInvalidLengthZero() {
		/* set arbitrary 3-byte tag with invalid length encoding */
		byte[] tagExpected = new byte[] { (byte) 0x1F, (byte) 0x80, (byte) 0x00 };
		new TlvTag(tagExpected);
	}
	
	/**
	 * Negative test case: Extract 2 byte tag with indicated tag number too
	 * small for the length of the tag
	 */
	@Test(expected = ISO7816Exception.class)
	public void testConstructor_2ByteTagTooSmallForLength() {
		/*
		 * set arbitrary 2-byte tag with tag number too small for the length of
		 * the tag
		 */
		byte[] tagExpected = new byte[] { (byte) 0x1F, (byte) 0x01 };
		new TlvTag(tagExpected);
	}
	
	/**
	 * Negative test case: Extract 4 byte tag (longer than the allowed 3 bytes)
	 */
	@Test(expected = ISO7816Exception.class)
	public void testConstructor_4ByteTag() {
		/*
		 * set arbitrary but "valid" 4-byte tag, too long for allowed 3 bytes
		 * length
		 */
		byte[] tagExpected = new byte[] { (byte) 0x1F, (byte) 0x81,
				(byte) 0x80, (byte) 0x00 };
		new TlvTag(tagExpected);
	}
	
	/**
	 * Positive test case: Extract indicated tag number from a 1 byte tag
	 */
	@Test
	public void testGetIndicatedTagNo_Tag_80() {
		/* set arbitrary but valid 1-byte tag encoding tag number 0 */
		byte[] tagExpected = new byte[] { (byte) 0x80 };
		TlvTag tagExtracted = new TlvTag(tagExpected);
		assertEquals("Equals expected indicated tag no",
				tagExtracted.getIndicatedTagNo(), 0);
	}
	
	/**
	 * Positive test case: Extract indicated tag number from a 2 byte tag
	 */
	@Test
	public void testGetIndicatedTagNo_Tag_7F4C() {
		/* set arbitrary but valid 2-byte tag encoding tag number 76 */
		byte[] tagExpected = new byte[] { (byte) 0x7F, (byte) 0x4C };
		TlvTag tagExtracted = new TlvTag(tagExpected);
		assertEquals("Equals expected indicated tag no",
				tagExtracted.getIndicatedTagNo(), 76);
	}
	
	/**
	 * Positive test case: Test equals for equals self and same constructor
	 * arguments with ASN1 Integer
	 */
	@Test
	public void testEquals_Asn1Integer() {
		byte[] tagInput = new byte[] {Asn1.INTEGER}; //ASN1 INTEGER
		TlvTag tag1 = new TlvTag(tagInput);
		TlvTag tag2 = new TlvTag(tagInput);
		
		assertEquals("Equals self", tag1, tag1);
		assertEquals("Equals same constructor arguments", tag1, tag2);
	}
	
	/**
	 * Positive test case: Test equals for equals self and same constructor
	 * arguments with ASN1 Octet String
	 */
	@Test
	public void testEquals_Asn1OctetString() {
		byte[] tagInput = new byte[] { Asn1.OCTET_STRING }; // ASN1 OCTET_STRING
		TlvTag tag1 = new TlvTag(tagInput);
		TlvTag tag2 = new TlvTag(tagInput);

		assertEquals("Equals with self", tag1, tag1);
		assertEquals("Equals with same constructor arguments", tag1, tag2);
	}
	
	/**
	 * Positive test case: Test valid DER encoding for being valid BER encoding
	 */
	@Test
	public void testIsValidBerEncoding_7F4C() {
		/* set arbitrary but valid 2-byte tag */
		byte[] tagExpected = new byte[] { (byte) 0x7F, (byte) 0x4C };

		TlvTag tag = new TlvTag(tagExpected);

		assertEquals("Is valid BER encoding", tag.isValidBerEncoding(), true);
	}
		
	/**
	 * Positive test case: Test valid DER encoding for being valid DER encoding
	 * 
	 */
	@Test
	public void testIsValidDerEncoding_7F4C() {
		/* set arbitrary but valid 2-byte tag */
		byte[] tagExpected = new byte[] { (byte) 0x7F, (byte) 0x4C };

		TlvTag tag = new TlvTag(tagExpected);

		assertEquals("Is valid DER encoding", tag.isValidDerEncoding(), true);
	}

}
