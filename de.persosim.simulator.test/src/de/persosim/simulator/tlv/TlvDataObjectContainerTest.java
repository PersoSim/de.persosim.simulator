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
	public void testGetTlvDataObjectFirstOccurence() {
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
	public void testGetTlvDataObjectSecondOccurence() {
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
	public void testGetTlvDataObjectMissingThirdOccurence() {
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
	public void testGetTlvDataObjectTlvPathsecondOccurence() {
		PrimitiveTlvDataObject child1 = new PrimitiveTlvDataObject(HexString.toByteArray("020101"));
		PrimitiveTlvDataObject child2 = new PrimitiveTlvDataObject(HexString.toByteArray("020102"));
		
		TlvDataObjectContainer container = new TlvDataObjectContainer(child1, child2);

		assertEquals(child2, container.getTlvDataObject(new TlvPath(new TlvTagIdentifier(TAG_INTEGER, 1))));
	}
	
	@Test
	public void testBla() {
		try {
			TlvDataObject objUTF8String = TlvDataObjectFactory.createTLVDataObject("1206313233343536");
			assertNotNull(objUTF8String);
			// byte[] bla = HexString.toByteArray("2906313233343536");
			// PrimitiveTlvDataObject tlvObject = new TL(bla);
			TlvDataObject objNumericString = TlvDataObjectFactory.createTLVDataObject("1806313233343536");
			assertNotNull(objNumericString);
			//TlvDataObject objIA5String = TlvDataObjectFactory.createTLVDataObject("2206313233343536");
			TlvDataObject objPRintableString = TlvDataObjectFactory.createTLVDataObject("1906313233343536");
			assertNotNull(objPRintableString);
			TlvDataObject objOctetString = TlvDataObjectFactory.createTLVDataObject("0406313233343536");
			assertNotNull(objOctetString);
		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
