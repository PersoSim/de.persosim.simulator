package de.persosim.simulator.protocols.ta;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;

public class TerminalAuthenticationMechanismTest extends PersoSimTestCase {
	@Test
	public void testGetCompressedTerminalEphemeralPublicKeyImmutability() {
		byte [] data = new byte [] {1,2,3,4,5};
		byte [] expected = Arrays.copyOf(data, data.length);
		
		TerminalAuthenticationMechanism mechanism = new TerminalAuthenticationMechanism(data, TerminalType.AT, new ArrayList<AuthenticatedAuxiliaryData>(), new byte [] {}, new byte [] {}, "test", null);
		
		data[0] = 2;
		
		byte[] result = mechanism.getCompressedTerminalEphemeralPublicKey();
		
		assertArrayEquals(expected, result);
		
		result[0] = 3;
		
		assertArrayEquals(expected, mechanism.getCompressedTerminalEphemeralPublicKey());
	}
	
	@Test
	public void testGetFirstSectorPublicKeyHashImmutability(){
		byte[] data = new byte[] { 1, 2, 3, 4, 5 };
		byte[] expected = Arrays.copyOf(data, data.length);

		TerminalAuthenticationMechanism mechanism = new TerminalAuthenticationMechanism(new byte[] {}, TerminalType.AT,
				new ArrayList<AuthenticatedAuxiliaryData>(), data, new byte[] {}, "test", null);

		data[0] = 2;

		byte[] result = mechanism.getFirstSectorPublicKeyHash();

		assertArrayEquals(expected, result);

		result[0] = 3;

		assertArrayEquals(expected, mechanism.getFirstSectorPublicKeyHash());
	}
	
	@Test
	public void testGetSecondSectorPublicKeyHashImmutability(){
		byte [] data = new byte [] {1,2,3,4,5};
		byte [] expected = Arrays.copyOf(data, data.length);
		
		TerminalAuthenticationMechanism mechanism = new TerminalAuthenticationMechanism(new byte [] {}, TerminalType.AT, new ArrayList<AuthenticatedAuxiliaryData>(), new byte [] {}, data, "test", null);
		
		data[0] = 2;
		
		byte[] result = mechanism.getSecondSectorPublicKeyHash();
		
		assertArrayEquals(expected, result);
		
		result[0] = 3;
		
		assertArrayEquals(expected, mechanism.getSecondSectorPublicKeyHash());}
	
	
}
