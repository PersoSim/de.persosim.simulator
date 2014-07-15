package de.persosim.simulator.apdumatching;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;

public class TlvSpecificationContainerTest extends PersoSimTestCase implements ApduSpecificationConstants {
	
	TlvTag tagPrimitive01   = new TlvTag((byte) 0x01);
	TlvTag tagPrimitive02   = new TlvTag((byte) 0x02);
	TlvTag tagPrimitive03   = new TlvTag((byte) 0x03);
	TlvTag tagConstructed21 = new TlvTag((byte) 0x21);
	TlvTag tagConstructed22 = new TlvTag((byte) 0x22);
	
	/**
	 * Positive test case: test matching of empty TLV data object container against empty TLV specification.
	 */
	@Test
	public void testMatches_EmptySpec() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with primitive/empty TLV data object against specification expecting object to be present.
	 */
	@Test
	public void testMatches_PresentRequired() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new TlvSpecification(TlvConstants.TAG_06, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_06));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of empty TLV data object container against specification expecting object to be present.
	 */
	@Test
	public void testMatches_AbsentRequired() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new TlvSpecification(TlvConstants.TAG_06, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with single primitive/empty TLV data object against specification expecting object to be optional.
	 */
	@Test
	public void testMatches_PresentOptional() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new TlvSpecification(tagPrimitive01, REQ_OPTIONAL));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of empty TLV data object container against specification expecting object to be optional.
	 */
	@Test
	public void testMatches_AbsentOptional() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new TlvSpecification(tagPrimitive01, REQ_OPTIONAL));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with single primitive/empty TLV data object against specification requiring object to be absent.
	 */
	@Test
	public void testMatches_PresentForbidden() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new TlvSpecification(TlvConstants.TAG_06, REQ_MISMATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_06));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of primitive/empty TLV data object container against specification requiring object to be absent.
	 */
	@Test
	public void testMatches_AbsentForbidden() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new TlvSpecification(TlvConstants.TAG_06, REQ_MISMATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with multiple primitive/empty TLV data objects against specification requiring all objects to appear in fixed order.
	 */
	@Test
	public void testMatches_ComplyingStrictOrder() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new TlvSpecification(tagPrimitive01, REQ_MATCH));
		containerSpec.add(new TlvSpecification(tagPrimitive02, REQ_MATCH));
		containerSpec.add(new TlvSpecification(tagPrimitive03, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive02));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive03));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of TLV data object container with multiple primitive/empty TLV data objects against specification requiring all objects to appear in different order.
	 */
	@Test
	public void testMatches_FailingStrictOrder() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new TlvSpecification(tagPrimitive01, REQ_MATCH));
		containerSpec.add(new TlvSpecification(tagPrimitive02, REQ_MATCH));
		containerSpec.add(new TlvSpecification(tagPrimitive03, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive02));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive03));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with multiple primitive/empty TLV data objects against specification requiring all objects to appear in arbitrary order.
	 */
	@Test
	public void testMatches_ComplyingArbitraryOrder() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, ARBITRARY_ORDER);
		containerSpec.add(new TlvSpecification(tagPrimitive01, REQ_MATCH));
		containerSpec.add(new TlvSpecification(tagPrimitive02, REQ_MATCH));
		containerSpec.add(new TlvSpecification(tagPrimitive03, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive02));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive03));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of TLV data object container with multiple primitive/empty TLV data objects against specification requiring only some objects in arbitrary order.
	 */
	@Test
	public void testMatches_MultiplePrimitiveMixedOrderWithUnallowed() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, ARBITRARY_ORDER);
		containerSpec.add(new TlvSpecification(tagPrimitive01, REQ_MATCH));
		containerSpec.add(new TlvSpecification(tagPrimitive02, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive02));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive03));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of TLV data object container with empty constructed TLV data object against specification requiring missing nested object.
	 * 
	 * expected: 21(01)
	 * received: 21()
	 */
	@Test
	public void testMatches_AbsentRequiredNested() {
		//FIXME AMY review testcases again, based on datastructure comments
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		TlvSpecification constructedTlvSpecification1 = new TlvSpecification(tagConstructed21, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		containerSpec.add(constructedTlvSpecification1);
		constructedTlvSpecification1.add(new TlvSpecification(tagPrimitive01, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new ConstructedTlvDataObject(tagConstructed21));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of TLV data object container with single simply nested constructed TLV data object against specification not expecting nested object.
	 * 
	 * expected: 21()
	 * received: 21(01)
	 */
	@Test
	public void testMatches_PresentRequiredConstructedUnexpectedNested() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new TlvSpecification(tagConstructed21, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		ConstructedTlvDataObject constructedTlvDataObject = new ConstructedTlvDataObject(tagConstructed21);
		containerTlv.addTlvDataObject(constructedTlvDataObject);
		constructedTlvDataObject.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of TLV data object container with single simply nested constructed TLV data object against specification expecting nested object to be absent.
	 * 
	 * expected: 21(!01)
	 * received: 21(01)
	 */
	@Test
	public void testMatches_PresentRequiredConstructedPresentForbiddenNested() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		TlvSpecification constructedTlvSpecification1 = new TlvSpecification(tagConstructed21, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		containerSpec.add(constructedTlvSpecification1);
		constructedTlvSpecification1.add(new TlvSpecification(tagPrimitive01, REQ_MISMATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		ConstructedTlvDataObject constructedTlvDataObject = new ConstructedTlvDataObject(tagConstructed21);
		containerTlv.addTlvDataObject(constructedTlvDataObject);
		constructedTlvDataObject.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with multiple nested constructed TLV data object against specification requiring all (nested) objects.
	 * 
	 * expected: 21(22(01))
	 * received: 21(22(01))
	 */
	@Test
	public void testMatches_PresentRequiredConstructedMultipleNested() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		TlvSpecification constructedTlvSpecification1 = new TlvSpecification(tagConstructed21, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		containerSpec.add(constructedTlvSpecification1);
		TlvSpecification constructedTlvSpecification2 = new TlvSpecification(tagConstructed22, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		constructedTlvSpecification1.add(constructedTlvSpecification2);
		constructedTlvSpecification2.add(new TlvSpecification(tagPrimitive01, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		ConstructedTlvDataObject constructedTlvDataObject1 = new ConstructedTlvDataObject(tagConstructed21);
		containerTlv.addTlvDataObject(constructedTlvDataObject1);
		ConstructedTlvDataObject constructedTlvDataObject2 = new ConstructedTlvDataObject(tagConstructed22);
		constructedTlvDataObject1.addTlvDataObject(constructedTlvDataObject2);
		constructedTlvDataObject2.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
}
