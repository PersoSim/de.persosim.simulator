package de.persosim.simulator.tlv;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import de.persosim.simulator.utils.HexString;

public class Asn1DataStructuresTest {
	
	/**
	 * Positive test: test generation of TLV structure for data group containing ASN.1 data type Date.
	 */
	@Test
	public void testGetDateDgTlv() {
		ConstructedTlvDataObject received = Asn1DateWrapper.getInstance().encode(new TlvTag((byte) 0x63), "20201031");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("630A12083230323031303331"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of TLV structure for data group containing ASN.1 data type ICAOString.
	 */
	@Test
	public void testGetIcaoStringDgTlv() {
		ConstructedTlvDataObject received = Asn1IcaoStringWrapper.getInstance().encode(new TlvTag((byte) 0x61), "ID");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("610413024944"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of TLV structure for data group containing ASN.1 data type IssuingState.
	 */
	@Test
	public void testGetIssuingStateDgTlv() {
		ConstructedTlvDataObject received = Asn1IcaoCountryWrapper.getInstance().encode(new TlvTag((byte) 0x62), "D");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("6203130144"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of TLV structure for data group containing ASN.1 data type UTF8String.
	 */
	@Test
	public void testGetUtf8StringDgTlv() {
		ConstructedTlvDataObject received = Asn1Utf8StringWrapper.getInstance().encode(new TlvTag((byte) 0x64), "ERIKA");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("64070C054552494B41"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}

}
