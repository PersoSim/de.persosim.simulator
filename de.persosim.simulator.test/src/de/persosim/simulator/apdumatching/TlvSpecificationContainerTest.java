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
	 * Positive test case: test matching of empty TLV data object container against empty specification.
	 */
	@Test
	public void testMatches_Empty() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with single primitive TLV data object against specification requiring the object.
	 */
	@Test
	public void testMatches_PresentRequiredPrimitive() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new PrimitiveTlvSpecification(TlvConstants.TAG_06, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_06));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of empty TLV data object container against specification requiring absent object.
	 */
	@Test
	public void testMatches_AbsentForbiddenPrimitive() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new PrimitiveTlvSpecification(TlvConstants.TAG_06, REQ_MISMATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with single primitive TLV data object against specification requiring absent object.
	 */
	@Test
	public void testMatches_PresentForbiddenPrimitive() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new PrimitiveTlvSpecification(TlvConstants.TAG_06, REQ_MISMATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_06));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of empty TLV data object container against specification requiring object.
	 */
	@Test
	public void testMatches_AbsentRequiredPrimitive() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new PrimitiveTlvSpecification(TlvConstants.TAG_06, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with multiple primitive TLV data objects against specification requiring all objects in fixed order.
	 */
	@Test
	public void testMatches_MultiplePrimitivesMatchingStrictOrder() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive01, REQ_MATCH));
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive02, REQ_MATCH));
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive03, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive02));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive03));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of TLV data object container with multiple primitive TLV data objects against specification requiring all objects in different fixed order.
	 */
	@Test
	public void testMatches_MultiplePrimitivesMixedStrictOrder() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive01, REQ_MATCH));
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive02, REQ_MATCH));
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive03, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive02));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive03));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with multiple primitive TLV data objects against specification requiring all objects in arbitrary order.
	 */
	@Test
	public void testMatches_MultiplePrimitivesMixedArbitraryOrder() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, ARBITRARY_ORDER);
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive01, REQ_MATCH));
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive02, REQ_MATCH));
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive03, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive02));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive03));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with single primitive TLV data object against specification with object being optional.
	 */
	@Test
	public void testMatches_PresentOptionalPrimitive() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive01, REQ_OPTIONAL));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of empty TLV data object container against specification with optional object.
	 */
	@Test
	public void testMatches_AbsentOptionalPrimitive() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive01, REQ_OPTIONAL));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of TLV data object container with multiple primitive TLV data objects against specification requiring only some objects in arbitrary order.
	 */
	@Test
	public void testMatches_MultiplePrimitiveMixedOrderWithUnallowed() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, ARBITRARY_ORDER);
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive01, REQ_MATCH));
		containerSpec.add(new PrimitiveTlvSpecification(tagPrimitive02, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive02));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		containerTlv.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive03));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with single constructed TLV data object against specification requiring the object.
	 */
	@Test
	public void testMatches_PresentRequiredConstructed() {
		//FIXME SLS I think this testcase is redundant, whats the essential difference to testMatches_PresentRequiredPrimitive()?
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new ConstructedTlvSpecification(tagConstructed21, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new ConstructedTlvDataObject(tagConstructed21));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with single simply nested constructed TLV data object against specification requiring all (nested) objects.
	 */
	@Test
	public void testMatches_PresentRequiredConstructedSimplyNested() {
		//FIXME SLS more complicated testcases are quite dificult to understand, mybe show the desired datastructres in comments
		//FIXME AMY review testcases again, based on datastructure comments
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		ConstructedTlvSpecification constructedTlvSpecification1 = new ConstructedTlvSpecification(tagConstructed21, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		containerSpec.add(constructedTlvSpecification1);
		constructedTlvSpecification1.add(new PrimitiveTlvSpecification(tagPrimitive01, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		ConstructedTlvDataObject constructedTlvDataObject = new ConstructedTlvDataObject(tagConstructed21);
		containerTlv.addTlvDataObject(constructedTlvDataObject);
		constructedTlvDataObject.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of TLV data object container with empty constructed TLV data object against specification requiring missing nested object.
	 */
	@Test
	public void testMatches_PresentRequiredConstructedMissingNested() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		ConstructedTlvSpecification constructedTlvSpecification1 = new ConstructedTlvSpecification(tagConstructed21, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		containerSpec.add(constructedTlvSpecification1);
		constructedTlvSpecification1.add(new PrimitiveTlvSpecification(tagPrimitive01, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		containerTlv.addTlvDataObject(new ConstructedTlvDataObject(tagConstructed21));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of TLV data object container with single simply nested constructed TLV data object against specification not expecting nested object.
	 */
	@Test
	public void testMatches_PresentRequiredConstructedUnexpectedNested() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		containerSpec.add(new ConstructedTlvSpecification(tagConstructed21, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		ConstructedTlvDataObject constructedTlvDataObject = new ConstructedTlvDataObject(tagConstructed21);
		containerTlv.addTlvDataObject(constructedTlvDataObject);
		constructedTlvDataObject.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Negative test case: test matching of TLV data object container with single simply nested constructed TLV data object against specification forbidding nested object.
	 */
	@Test
	public void testMatches_PresentRequiredConstructedPresentForbiddenNested() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		ConstructedTlvSpecification constructedTlvSpecification1 = new ConstructedTlvSpecification(tagConstructed21, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		containerSpec.add(constructedTlvSpecification1);
		constructedTlvSpecification1.add(new PrimitiveTlvSpecification(tagPrimitive01, REQ_MISMATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		ConstructedTlvDataObject constructedTlvDataObject = new ConstructedTlvDataObject(tagConstructed21);
		containerTlv.addTlvDataObject(constructedTlvDataObject);
		constructedTlvDataObject.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		
		assertFalse(containerSpec.matches(containerTlv).isMatch());
	}
	
	/**
	 * Positive test case: test matching of TLV data object container with multiple nested constructed TLV data object against specification requiring all (nested) objects.
	 */
	@Test
	public void testMatches_PresentRequiredConstructedMultipleNested() {
		TlvSpecificationContainer containerSpec = new TlvSpecificationContainer(DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER);
		ConstructedTlvSpecification constructedTlvSpecification1 = new ConstructedTlvSpecification(tagConstructed21, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		containerSpec.add(constructedTlvSpecification1);
		ConstructedTlvSpecification constructedTlvSpecification2 = new ConstructedTlvSpecification(tagConstructed22, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
		constructedTlvSpecification1.add(constructedTlvSpecification2);
		constructedTlvSpecification2.add(new PrimitiveTlvSpecification(tagPrimitive01, REQ_MATCH));
		
		TlvDataObjectContainer containerTlv = new TlvDataObjectContainer();
		ConstructedTlvDataObject constructedTlvDataObject1 = new ConstructedTlvDataObject(tagConstructed21);
		containerTlv.addTlvDataObject(constructedTlvDataObject1);
		ConstructedTlvDataObject constructedTlvDataObject2 = new ConstructedTlvDataObject(tagConstructed22);
		constructedTlvDataObject1.addTlvDataObject(constructedTlvDataObject2);
		constructedTlvDataObject2.addTlvDataObject(new PrimitiveTlvDataObject(tagPrimitive01));
		
		assertTrue(containerSpec.matches(containerTlv).isMatch());
	}
	
}
