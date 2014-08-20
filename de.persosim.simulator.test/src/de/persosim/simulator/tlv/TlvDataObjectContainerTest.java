package de.persosim.simulator.tlv;

import static org.junit.Assert.*;

import org.junit.Test;

import de.persosim.simulator.utils.HexString;

public class TlvDataObjectContainerTest implements TlvConstants {

	/**
	 * Positive test: get the first occurence of a given tag from a
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
	 * Positive test: get the second occurence of a given tag from a
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
	
	//FIXME add negative test, when this occurence cannot be found
	//FIXME check usage of TagIndentifiers from within a TlvPath
}
