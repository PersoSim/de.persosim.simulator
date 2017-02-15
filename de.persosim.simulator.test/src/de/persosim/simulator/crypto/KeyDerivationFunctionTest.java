package de.persosim.simulator.crypto;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import de.persosim.simulator.utils.HexString;

public class KeyDerivationFunctionTest {
	@Test
	public void testDeriveEnc() {
		KeyDerivationFunction kdf = new KeyDerivationFunction(16);
		byte [] result = kdf.deriveENC(HexString.toByteArray("239AB9CB282DAF66231DC5A4DF6BFBAE"));
		assertArrayEquals(HexString.toByteArray("AB94FCEDF2664EDFB9B291F85D7F77F2"), result);
	}
	@Test
	public void testDeriveMac() {
		KeyDerivationFunction kdf = new KeyDerivationFunction(16);
		byte [] result = kdf.deriveMAC(HexString.toByteArray("239AB9CB282DAF66231DC5A4DF6BFBAE"));
		assertArrayEquals(HexString.toByteArray("7862D9ECE03C1BCD4D77089DCF131442"), result);
	}
}
