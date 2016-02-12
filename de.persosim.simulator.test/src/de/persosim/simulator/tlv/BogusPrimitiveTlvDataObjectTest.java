package de.persosim.simulator.tlv;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.utils.Utils;

public class BogusPrimitiveTlvDataObjectTest {
	byte[] tlvTag;
	byte[] tlvLength;
	byte[] tlvData;

	@Before
	public void setUp() {
		tlvData = new byte[] { 0, 1, 2, 3, 4, 5 };
		tlvLength = new byte[] { (byte) 0x06 };
	}

	/**
	 * Positive test case: Check if a creating a
	 * {@link BogusPrimitiveTlvDataObject} with a {@link TlvTag} with empty
	 * bytes is working.
	 */
	@Test
	public void testConstructorEmptyTlvTag() {
		tlvTag = new byte[] {};
		byte[] tlvExpected = Utils.concatByteArrays(tlvTag, tlvLength, tlvData);
		BogusPrimitiveTlvDataObject tlvObject = new BogusPrimitiveTlvDataObject(new TlvTag(new byte[] {}, false),
				tlvData);
		assertArrayEquals("Equals expected byte array representation", tlvObject.toByteArray(), tlvExpected);
	}

	/**
	 * Positive test case: Check if a creating a
	 * {@link BogusPrimitiveTlvDataObject} with a {@link TlvTag} without
	 * primitive encoding is working.
	 */
	@Test
	public void testConstructorIncorrectTlvTag() {
		tlvTag = new byte[] { 1, 2, 3, 4 };
		byte[] tlvExpected = Utils.concatByteArrays(tlvTag, tlvLength, tlvData);
		BogusPrimitiveTlvDataObject tlvObject = new BogusPrimitiveTlvDataObject(new TlvTag(tlvTag, false), tlvData);
		assertArrayEquals("Equals expected byte array representation", tlvObject.toByteArray(), tlvExpected);
	}

	/**
	 * Positive test case: Check if a creating a
	 * {@link BogusPrimitiveTlvDataObject} with omitTlvValue set to true really
	 * omits the TLV value when creating a byte array out of the object.
	 */
	@Test
	public void testConstructorOmitTlvValue() {
		byte[] tlvExpected = Utils.concatByteArrays(TlvConstants.TAG_53.toByteArray(), tlvLength);
		BogusPrimitiveTlvDataObject tlvObject = new BogusPrimitiveTlvDataObject(TlvConstants.TAG_53, tlvData, true);
		assertArrayEquals("Equals expected byte array representation", tlvObject.toByteArray(), tlvExpected);
	}

}
