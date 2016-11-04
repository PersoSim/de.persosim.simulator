package de.persosim.simulator.perso;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.HexString;

public class AbstractProfileTest extends PersoSimTestCase {
	
	/**
	 * Positive test: test generation of TLV structure for data group containing ASN.1 data type CommunityID.
	 */
	@Test
	public void testGetCommunityIdDgTlv() {
		ConstructedTlvDataObject received = AbstractProfile.getCommunityIdDgTlv(new TlvTag((byte) 0x72), "02760503150000");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("7209040702760503150000"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of empty TLV structure for data group containing ASN.1 data type CommunityID.
	 */
	@Test
	public void testGetCommunityIdDgTlv_Empty() {
		ConstructedTlvDataObject received = AbstractProfile.getCommunityIdDgTlv(new TlvTag((byte) 0x72), "");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("72020400"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of TLV structure for data group containing ASN.1 data type GeneralPlace.
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testGetGeneralPlaceDgTlv() throws UnsupportedEncodingException {
		ConstructedTlvDataObject received = AbstractProfile.getGeneralPlaceDgTlv(new TlvTag((byte) 0x71), "HEIDESTRASSE 17", "K\u00D6LN", null, "D", "51147");
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("712C302AAA110C0F484549444553545241535345203137AB070C054BC3964C4EAD03130144AE0713053531313437"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of empty TLV structure for data group containing ASN.1 data type GeneralPlace.
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testGetGeneralPlaceDgTlv_Empty() throws UnsupportedEncodingException {
		ConstructedTlvDataObject received = AbstractProfile.getGeneralPlaceDgTlv(new TlvTag((byte) 0x71), null, null, null, null, null);
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("7125A2230C216B65696E65204861757074776F686E756E6720696E20446575747363686C616E64"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of TLV structure for data group containing ASN.1 data type GeneralPlace for a single location descriptor.
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testGetGeneralPlaceDgTlv_Simple() throws UnsupportedEncodingException {
		ConstructedTlvDataObject received = AbstractProfile.getGeneralPlaceDgTlv(new TlvTag((byte) 0x69), null, "BERLIN", null, null, null);
		ConstructedTlvDataObject expected = new ConstructedTlvDataObject(HexString.toByteArray("690AA1080C064245524C494E"));
		
		assertArrayEquals(expected.toByteArray(), received.toByteArray());
	}
	
	/**
	 * Positive test: test generation of MRZ line 1 of 3.
	 */
	@Test
	public void testGetMrzLine1Of3() {
		String received = AbstractProfile.getMrzLine1of3("ID", "D", "000000001");
		String expected = "IDD<<0000000011<<<<<<<<<<<<<<<";
		
		assertEquals(expected, received);
	}
	
	/**
	 * Positive test: test generation of MRZ line 2 of 3.
	 */
	@Test
	public void testGetMrzLine2Of3() {
		String received = AbstractProfile.getMrzLine2of3("IDD<<0000000011<<<<<<<<<<<<<<<", "19640812", "F", "20201031", "D");
		String expected = "6408125F2010315D<<<<<<<<<<<<<8";
		
		assertEquals(expected, received);
	}
	
}
