package de.persosim.simulator.tlv;

import static org.junit.Assert.*;

import org.junit.Test;

import de.persosim.simulator.utils.HexString;

public class TlvDataObjectContainerTest implements TlvConstants {

	/**
	 * Positive test: get the first occurrence of a given tag from a
	 * {@link TlvDataObjectContainer} containing more than one child with the
	 * same tag.
	 */
	@Test
	public void testGetTlvDataObject_firstOccurence() {
		PrimitiveTlvDataObject child1 = new PrimitiveTlvDataObject(HexString.toByteArray("020101"));
		PrimitiveTlvDataObject child2 = new PrimitiveTlvDataObject(HexString.toByteArray("020102"));
		
		TlvDataObjectContainer container = new TlvDataObjectContainer(child1, child2);
		
		assertEquals(child1, container.getTlvDataObject(TAG_INTEGER));
	}
	
	/**
	 * Positive test: get the second occurrence of a given tag from a
	 * {@link TlvDataObjectContainer} containing more than one child with the
	 * same tag.
	 */
	@Test
	public void testGetTlvDataObject_secondOccurence() {
		PrimitiveTlvDataObject child1 = new PrimitiveTlvDataObject(HexString.toByteArray("020101"));
		PrimitiveTlvDataObject child2 = new PrimitiveTlvDataObject(HexString.toByteArray("020102"));
		
		TlvDataObjectContainer container = new TlvDataObjectContainer(child1, child2);

		assertEquals(child2, container.getTlvDataObject(new TlvTagIdentifier(TAG_INTEGER, 1)));
	}
	
	/**
	 * Negative test: get the third occurrence of a given tag from a
	 * {@link TlvDataObjectContainer} containing only two children with the
	 * expected tag.
	 */
	@Test
	public void testGetTlvDataObject_missingThirdOccurence() {
		PrimitiveTlvDataObject child1 = new PrimitiveTlvDataObject(HexString.toByteArray("020101"));
		PrimitiveTlvDataObject child2 = new PrimitiveTlvDataObject(HexString.toByteArray("020102"));
		
		TlvDataObjectContainer container = new TlvDataObjectContainer(child1, child2);

		assertNull(container.getTlvDataObject(new TlvTagIdentifier(TAG_INTEGER, 2)));
	}
	
	/**
	 * Positive test: get the second occurrence of a given tag from a
	 * {@link TlvDataObjectContainer} containing more than one child with the
	 * same tag.
	 */
	@Test
	public void testGetTlvDataObject_TlvPath_secondOccurence() {
		PrimitiveTlvDataObject child1 = new PrimitiveTlvDataObject(HexString.toByteArray("020101"));
		PrimitiveTlvDataObject child2 = new PrimitiveTlvDataObject(HexString.toByteArray("020102"));
		
		TlvDataObjectContainer container = new TlvDataObjectContainer(child1, child2);

		assertEquals(child2, container.getTlvDataObject(new TlvPath(new TlvTagIdentifier(TAG_INTEGER, 1))));
	}
}
