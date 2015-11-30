package de.persosim.simulator.protocols.ta;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

import de.persosim.simulator.protocols.RoleOid;
import de.persosim.simulator.test.PersoSimTestCase;

public class AuthenticatedAuxiliaryDataTest extends PersoSimTestCase {
	@Test
	public void testGetDiscretionaryDataImmutability() {
		byte[] data = new byte[] { 1, 2, 3, 4, 5 };
		byte[] expected = Arrays.copyOf(data, data.length);

		AuthenticatedAuxiliaryData authData = new AuthenticatedAuxiliaryData(RoleOid.id_AT, data);

		data[0] = 2;

		byte[] result = authData.getDiscretionaryData();

		assertArrayEquals(expected, result);

		result[0] = 3;

		assertArrayEquals(expected, authData.getDiscretionaryData());
	}
}
