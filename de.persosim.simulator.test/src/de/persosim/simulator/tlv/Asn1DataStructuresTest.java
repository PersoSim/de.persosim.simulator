package de.persosim.simulator.tlv;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import de.persosim.simulator.utils.HexString;

public class Asn1DataStructuresTest {
	
	/**
	 * Positive test: test generation of ASN.1 data structure "Date".
	 */
	@Test
	public void testAsn1DateEncode() {
		PrimitiveTlvDataObject received = Asn1Date.getInstance().encode("20201031");
		PrimitiveTlvDataObject expected = new PrimitiveTlvDataObject(HexString.toByteArray("12083230323031303331"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Negative test: test generation of ASN.1 data structure "Date" with date to be encoded containing literals.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAsn1DateEncode_NonNumber() {
		Asn1Date.getInstance().encode("2020103F");
	}
	
	/**
	 * Negative test: test generation of ASN.1 data structure "Date" with date to be encoded too short.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAsn1DateEncode_TooShort() {
		Asn1Date.getInstance().encode("2020103");
	}
	
	/**
	 * Negative test: test generation of ASN.1 data structure "Date" with date to be encoded too long.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAsn1DateEncode_TooLong() {
		Asn1Date.getInstance().encode("202010310");
	}
	
	/**
	 * Positive test: test generation of wrapped ASN.1 data structure "Date".
	 */
	@Test
	public void testAsn1DateWrapperEncode() {
		ConstructedTlvDataObject received = Asn1DateWrapper.getInstance().encode(new TlvTag((byte) 0x63), "20201031");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("630A12083230323031303331"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of ASN.1 data structure "ICAOCountry" with string to be encoded of length 1.
	 */
	@Test
	public void testAsn1IcaoCountryEncode1() {
		PrimitiveTlvDataObject received = Asn1IcaoCountry.getInstance().encode("D");
		PrimitiveTlvDataObject expected = new PrimitiveTlvDataObject(HexString.toByteArray("130144"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of ASN.1 data structure "ICAOCountry" with string to be encoded of length 3.
	 */
	@Test
	public void testAsn1IcaoCountryEncode3() {
		PrimitiveTlvDataObject received = Asn1IcaoCountry.getInstance().encode("DED");
		PrimitiveTlvDataObject expected = new PrimitiveTlvDataObject(HexString.toByteArray("1303444544"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Negative test: test generation of ASN.1 data structure "ICAOCountry" with string to be encoded containing a number.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAsn1IcaoCountryEncode_ContainingNumber() {
		Asn1IcaoCountry.getInstance().encode("3");
	}
	
	/**
	 * Negative test: test generation of ASN.1 data structure "ICAOCountry" with string to be encoded of illegal length 2.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAsn1IcaoCountryEncode_IllegalLength2() {
		Asn1IcaoCountry.getInstance().encode("DE");
	}
	
	/**
	 * Negative test: test generation of ASN.1 data structure "ICAOCountry" with string to be encoded of illegal length 4.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAsn1IcaoCountryEncode_IllegalLength4() {
		Asn1IcaoCountry.getInstance().encode("DEDE");
	}
	
	/**
	 * Positive test: test generation of wrapped ASN.1 data structure "ICAOCountry".
	 */
	@Test
	public void testAsn1IcaoCountryWrapperEncode() {
		ConstructedTlvDataObject received = Asn1IcaoCountryWrapper.getInstance().encode(new TlvTag((byte) 0x62), "D");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("6203130144"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of ASN.1 data structure "ICAOString".
	 */
	@Test
	public void testAsn1IcaoStringEncode() {
		PrimitiveTlvDataObject received = Asn1IcaoString.getInstance().encode("ID");
		PrimitiveTlvDataObject expected = new PrimitiveTlvDataObject(HexString.toByteArray("13024944"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of wrapped ASN.1 data structure "ICAOString".
	 */
	@Test
	public void testAsn1IcaoStringWrapperEncode() {
		ConstructedTlvDataObject received = Asn1IcaoStringWrapper.getInstance().encode(new TlvTag((byte) 0x61), "ID");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("610413024944"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of ASN.1 data structure "UTF8String".
	 */
	@Test
	public void testAsn1Utf8StringEncode() {
		PrimitiveTlvDataObject received = Asn1Utf8String.getInstance().encode("ERIKA");
		PrimitiveTlvDataObject expected = new PrimitiveTlvDataObject(HexString.toByteArray("0C054552494B41"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of wrapped ASN.1 data structure "UTF8String".
	 */
	@Test
	public void testAsn1Utf8StringWrapperEncode() {
		ConstructedTlvDataObject received = Asn1Utf8StringWrapper.getInstance().encode(new TlvTag((byte) 0x64), "ERIKA");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("64070C054552494B41"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of wrapped ASN.1 data structure "DocumentType".
	 */
	@Test
	public void testAsn1Asn1DocumentTypeEncode() {
		ConstructedTlvDataObject received = Asn1DocumentType.getInstance().encode(new TlvTag((byte) 0x61), "ID");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("610413024944"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of ASN.1 data structure "PrintableString".
	 */
	@Test
	public void testAsn1PrintableStringEncode() {
		PrimitiveTlvDataObject received = Asn1PrintableString.getInstance().encode("ERIKA");
		PrimitiveTlvDataObject expected = new PrimitiveTlvDataObject(HexString.toByteArray("13054552494B41"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}

}
