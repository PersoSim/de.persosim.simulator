package de.persosim.simulator.protocols.ta;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;

import org.junit.Test;

import de.persosim.simulator.PersoSimTest;

public class AuthenticatedAuxiliaryDataTest extends PersoSimTest {
	@Test
	public void testGetDiscretionaryDataImmutability() {
		byte[] data = new byte[] { 1, 2, 3, 4, 5 };
		byte[] expected = Arrays.copyOf(data, data.length);

		AuthenticatedAuxiliaryData authData = new AuthenticatedAuxiliaryData(TaOid.id_AT, data);

		data[0] = 2;

		byte[] result = authData.getDiscretionaryData();

		assertArrayEquals(expected, result);

		result[0] = 3;

		assertArrayEquals(expected, authData.getDiscretionaryData());
	}
}
