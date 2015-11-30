package de.persosim.simulator.protocols;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 * 
 */
public class OidTest {

	/**
	 * Inner class to test methods implemented by Oid without references to
	 * other implementing classes.
	 * 
	 * @author amay
	 * 
	 */
	private class TestOid extends Oid {

		public TestOid(byte[] byteArrayRepresentation) {
			super(byteArrayRepresentation);
		}

		@Override
		public String getIdString() {
			return "TestOid";
		}

	}

	/**
	 * Positive test: check whether OID object equals itself.
	 */
	@Test
	public void equals_Self() {
		Oid oid = new TestOid(new byte[] { 1 });

		assertTrue(oid.equals(oid));
	}

	/**
	 * Positive test: check whether OID object equals identical copy.
	 */
	@Test
	public void equals_Same() {
		Oid oid1 = new TestOid(new byte[] { 1 });
		Oid oid2 = new TestOid(new byte[] { 1 });

		assertTrue(oid1.equals(oid2));
	}

	/**
	 * Negative test: check whether OID object does not equal different OID of
	 * same type.
	 */
	@Test
	public void equals_Null() {
		Oid oid = new TestOid(new byte[] { 1 });

		assertFalse(oid.equals(null));
	}

	/**
	 * Negative test: check whether OID object does not equal different OID of
	 * same type.
	 */
	@Test
	public void equals_DifferentContent() {
		Oid oid1 = new TestOid(new byte[] { 1 });
		Oid oid2 = new TestOid(new byte[] { 2 });

		assertFalse(oid1.equals(oid2));
	}

	/**
	 * Positive test: check whether OID object that contain the same byte[]
	 * contents but differ in type are nevertheless equal.
	 */
	@Test
	public void equals_equalContentAndDifferentType() {
		Oid testOid = new TestOid(new byte[] { 1 });
		Oid anonymousTypeOid = new Oid(new byte[] { 1 }) {
			@Override
			public String getIdString() {
				return "SecondTestOidType";
			}
		};

		assertTrue("testOid.equals(annonymousType)", testOid.equals(anonymousTypeOid));
		assertTrue("annonymousType.equals(testOid)", anonymousTypeOid.equals(testOid));
	}

	/**
	 * Positive test: check whether OID object starts with the provided prefix.
	 */
	@Test
	public void startsWithPrefix() {
		Oid oid = new TestOid(HexString.toByteArray("00112233445566778899"));
		byte[] prefix = HexString.toByteArray("00112233");

		assertTrue(oid.startsWithPrefix(prefix));
	}

	/**
	 * Positive test: check whether OID object starts with the provided prefix,
	 * prefix is empty.
	 */
	@Test
	public void startsWithPrefix_PrefixIsEmpty() {
		Oid oid = new TestOid(HexString.toByteArray("00112233445566778899"));
		byte[] prefix = new byte[0];

		assertTrue(oid.startsWithPrefix(prefix));
	}

	/**
	 * Positive test: check whether whole OID is regarded as prefix.
	 */
	@Test
	public void startsWithPrefix_PrefixOfSameLengthAsOid() {
		Oid oid = new TestOid(HexString.toByteArray("00112233445566778899"));
		byte[] prefix = oid.toByteArray();

		assertTrue(oid.startsWithPrefix(prefix));
	}

	/**
	 * Negative test: check whether OID object starts with the provided prefix,
	 * prefix different.
	 */
	@Test
	public void startsWithPrefix_PrefixDifferent() {
		Oid oid = new TestOid(HexString.toByteArray("00112233445566778899"));
		byte[] prefix = HexString.toByteArray("11");

		assertFalse(oid.startsWithPrefix(prefix));
	}

	/**
	 * Negative test: check whether OID object starts with the provided prefix,
	 * provided prefix longer that actual oid content.
	 */
	@Test
	public void startsWithPrefix_PrefixLonger() {
		Oid oid = new TestOid(HexString.toByteArray("00112233445566778899"));
		byte[] prefix = HexString.toByteArray("00112233445566778899AA");

		assertFalse(oid.startsWithPrefix(prefix));
	}

	/**
	 * Negative test: check whether OID object starts with the provided prefix,
	 * prefix is null.
	 */
	@Test(expected = NullPointerException.class)
	public void startsWithPrefix_PrefixNull() {
		Oid oid = new TestOid(HexString.toByteArray("00112233445566778899"));
		byte[] prefix = null;

		oid.startsWithPrefix(prefix);
	}
}
