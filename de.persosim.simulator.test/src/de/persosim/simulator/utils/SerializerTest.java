package de.persosim.simulator.utils;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class SerializerTest {
	
	@Test
	public void testDeepCopy(){
		byte [] expected1 = new byte [] {1,2,3,4,5};
		byte [] expected2 = new byte [] {9,8,7,6,5};
		
		byte [] content1 = new byte [] {1,2,3,4,5};
		byte [] content2 = new byte [] {9,8,7,6,5};
		
		MutableArrayWrapper mutableArrayWrapper = new MutableArrayWrapper();
		MutableArrayWrapper mutableArrayWrapper2 = new MutableArrayWrapper();
		
		mutableArrayWrapper.content = content1;
		mutableArrayWrapper.next = mutableArrayWrapper2;
		mutableArrayWrapper2.content = content2;
		
		//call mut
		MutableArrayWrapper copy = Serializer.deepCopy(mutableArrayWrapper);

		assertArrayEquals(expected1, copy.content);
		assertArrayEquals(expected2, copy.next.content);

		content1[0] = 100;
		content2[0] = 100;

		assertArrayEquals(expected1, copy.content);
		assertArrayEquals(expected2, copy.next.content);
	}
	
	@Test
	public void testSerialization(){
		byte [] expected1 = new byte [] {1,2,3,4,5};
		byte [] expected2 = new byte [] {9,8,7,6,5};
	
		byte [] content1 = new byte [] {1,2,3,4,5};
		byte [] content2 = new byte [] {9,8,7,6,5};
	
		MutableArrayWrapper mutableArrayWrapper = new MutableArrayWrapper();
		MutableArrayWrapper mutableArrayWrapper2 = new MutableArrayWrapper();
	
		mutableArrayWrapper.content = content1;
		mutableArrayWrapper.next = mutableArrayWrapper2;
		mutableArrayWrapper2.content = content2;
		
		
		//call mut
		Serialized<MutableArrayWrapper> serialized = Serializer.serialize(mutableArrayWrapper);
		MutableArrayWrapper copy = Serializer.deserialize(serialized);

		assertArrayEquals(expected1, copy.content);
		assertArrayEquals(expected2, copy.next.content);

		content1[0] = 100;
		content2[0] = 100;

		assertArrayEquals(expected1, copy.content);
		assertArrayEquals(expected2, copy.next.content);
	} 
}
