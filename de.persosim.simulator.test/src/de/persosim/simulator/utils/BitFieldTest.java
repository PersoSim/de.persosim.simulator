package de.persosim.simulator.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;

public class BitFieldTest extends PersoSimTestCase {

	byte [] field1Content;
	byte [] field2Content;
	BitField field1;
	BitField field2;
	
	@Before
	public void setUp(){
		field1Content = new byte []{(byte) 0xFF, 0b01010101};
		field2Content = new byte []{0x00, (byte) 0b10101010, (byte)0xF};
		field1 = new BitField(16, field1Content);
		field2 = new BitField(20, field2Content);	
	}
	
	@Test
	public void testBuildFromBigEndian(){
		BitField field = BitField.buildFromBigEndian(10, new byte [] {0b01001011, (byte) 0b10110100});
		assertFalse(field.getBit(0));
		assertFalse(field.getBit(1));
		assertTrue(field.getBit(2));
		assertFalse(field.getBit(3));
		assertTrue(field.getBit(4));
		assertTrue(field.getBit(5));
		assertFalse(field.getBit(6));
		assertTrue(field.getBit(7));
		assertTrue(field.getBit(8));
		assertTrue(field.getBit(9));
	}
	
	@Test
	public void testConstructorByteArray(){
		BitField field = new BitField(10, new byte [] {0b01001011, 0b00001111});
		assertTrue(field.getBit(0));
		assertTrue(field.getBit(1));
		assertFalse(field.getBit(2));
		assertTrue(field.getBit(3));
		assertFalse(field.getBit(4));
		assertFalse(field.getBit(5));
		assertTrue(field.getBit(6));
		assertFalse(field.getBit(7));
		assertTrue(field.getBit(8));
		assertTrue(field.getBit(9));
	}
	
	@Test
	public void testConstructorSetBits(){
		BitField field = new BitField(10, 2, 5);
		assertFalse(field.getBit(0));
		assertFalse(field.getBit(1));
		assertTrue(field.getBit(2));
		assertFalse(field.getBit(3));
		assertFalse(field.getBit(4));
		assertTrue(field.getBit(5));
		assertFalse(field.getBit(6));
		assertFalse(field.getBit(7));
		assertFalse(field.getBit(8));
		assertFalse(field.getBit(9));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorSetBitsNegativeBit(){
		new BitField(10, 2, -5);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorSetBitsToBig(){
		new BitField(10, 2, 200);
	}
	
	@Test
	public void testConstructor(){
		BitField field = new BitField(10);
		
		for (int i = 0; i < 10; i++){
			assertFalse(field.getBit(i));
		}
	}
	
	@Test
	public void testEquals(){
		BitField equal = new BitField(16, field1Content);
		//run mut
		assertEquals(field1, equal);
	}
	
	@Test
	public void testEqualsNegative(){
		//run mut
		assertTrue(!field1.equals(field2));
	}
	
	@Test
	public void testGetAsZeroPaddedByteArray(){
		assertArrayEquals(field2Content, field2.getAsZeroPaddedByteArray());
	}
	
	@Test
	public void testGetAsZeroPaddedBigEndianByteArray() {
		BitField bf = BitField.buildFromBigEndian(16, field1Content);
		assertArrayEquals(field1Content, bf.getAsZeroPaddedBigEndianByteArray());
	}
	
	@Test
	public void testConcatenate(){
		//create test data
		byte [] concatenated = Utils.concatByteArrays(field1Content, field2Content);
		BitField concatenatedField = new BitField(36, concatenated);
		
		//call mut
		BitField result = field1.concatenate(field2);
		
		//check result
		assertEquals(36, result.getNumberOfBits());
		assertEquals(concatenatedField, result);
	}
	
	@Test
	public void testOr(){
		//create test data
		BitField expected = new BitField(20, new byte []{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xF0});
		
		//call mut
		BitField result = field1.or(field2);
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testAnd(){
		//create test data
		BitField expected = new BitField(20, new byte []{0,0,0});
		
		//call mut
		BitField result = field1.and(field2);
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testGetNumberOfBits(){
		assertEquals(16, field1.getNumberOfBits());
	}
	
	@Test
	public void testFlip(){
		//create test data
		BitField expected = new BitField(16, new byte []{(byte) 0xFF, (byte) 0b01010100});

		//call mut
		BitField result = field1.flipBit(8);
		
		assertEquals(expected, result);
	}
	
}
