package de.persosim.simulator.apdumatching;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;

public class TlvSpecificationTest extends PersoSimTestCase implements ApduSpecificationConstants {
	
	TlvTag tagPrimitive01   = new TlvTag((byte) 0x01);
	TlvTag tagPrimitive02   = new TlvTag((byte) 0x02);
	
	/**
	 * Negative test case: test matching of single primitive TLV data object against specification expecting different object.
	 */
	@Test
	public void testMatches_DifferentObject() {
		TlvSpecification tlvSpec = new TlvSpecification(tagPrimitive01, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		
		PrimitiveTlvDataObject primitiveTlvDataObject = new PrimitiveTlvDataObject(tagPrimitive02);
		
		assertFalse(tlvSpec.matches(primitiveTlvDataObject));
	}
	
	/**
	 * Positive test case: test matching of single primitive TLV data object against specification expecting object to be required.
	 */
	@Test
	public void testMatches_PresentRequired() {
		TlvSpecification tlvSpec = new TlvSpecification(tagPrimitive01, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		
		PrimitiveTlvDataObject primitiveTlvDataObject = new PrimitiveTlvDataObject(tagPrimitive01);
		
		assertTrue(tlvSpec.matches(primitiveTlvDataObject));
	}
	
	/**
	 * Positive test case: test matching of single primitive TLV data object against specification expecting object to be optional.
	 */
	@Test
	public void testMatches_PresentOptional() {
		TlvSpecification tlvSpec = new TlvSpecification(tagPrimitive01, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_OPTIONAL);
		
		PrimitiveTlvDataObject primitiveTlvDataObject = new PrimitiveTlvDataObject(tagPrimitive01);
		
		assertTrue(tlvSpec.matches(primitiveTlvDataObject));
	}
	
	/**
	 * Negative test case: test matching of single primitive TLV data object against specification expecting object to be forbidden.
	 */
	@Test
	public void testMatches_PresentForbidden() {
		TlvSpecification tlvSpec = new TlvSpecification(tagPrimitive01, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MISMATCH);
		
		PrimitiveTlvDataObject primitiveTlvDataObject = new PrimitiveTlvDataObject(tagPrimitive01);
		
		assertFalse(tlvSpec.matches(primitiveTlvDataObject));
	}
	
}
