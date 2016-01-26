package de.persosim.simulator.protocols.ta;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.persosim.simulator.utils.BitField;

public class CertificateRoleTest { //extends PersoSimTestCase {
	
	/**
	 * Positive test: get CertificateRole from Byte
	 */
	@Test
	public void testGetFromMostSignificantBitsByte(){
		CertificateRole terminal = CertificateRole.getFromMostSignificantBits((byte) 0x00);
		CertificateRole dv1 = CertificateRole.getFromMostSignificantBits((byte) 0x80);
		CertificateRole dv2 = CertificateRole.getFromMostSignificantBits((byte) 0x40);
		CertificateRole cvca = CertificateRole.getFromMostSignificantBits((byte) 0xC0);
		
		assertEquals("Role \"TERMINAL\" is not correctly created.", CertificateRole.TERMINAL, terminal);
		assertEquals("Role \"DV_TYPE_1\" is not correctly created", CertificateRole.DV_TYPE_1, dv1);
		assertEquals("Role \"DV_TYPE_2\" is not correctly created", CertificateRole.DV_TYPE_2, dv2);
		assertEquals("Role \"CVCA\" is not correctly created", CertificateRole.CVCA, cvca);
	}
	
	/**
	 * Positive test: get CertificateRole from most significant bits of a BitField
	 */
	@Test
	public void testGetFromMostSignificantBitsBitField(){
		CertificateRole terminal = CertificateRole.getFromMostSignificantBits(new BitField(32, 0, 1, 2));
		CertificateRole dv1 = CertificateRole.getFromMostSignificantBits(new BitField(32, 31, 0, 1, 2));
		CertificateRole dv2 = CertificateRole.getFromMostSignificantBits(new BitField(32, 30, 0, 1, 2));
		CertificateRole cvca = CertificateRole.getFromMostSignificantBits(new BitField(32, 30, 31, 0, 1, 2));
		
		assertEquals("Role \"TERMINAL\" is not correctly created.", CertificateRole.TERMINAL, terminal);
		assertEquals("Role \"DV_TYPE_1\" is not correctly created", CertificateRole.DV_TYPE_1, dv1);
		assertEquals("Role \"DV_TYPE_2\" is not correctly created", CertificateRole.DV_TYPE_2, dv2);
		assertEquals("Role \"CVCA\" is not correctly created", CertificateRole.CVCA, cvca);
	}
	
	/**
	 * Positive test: get CertificateRole from a BitField
	 */
	@Test
	public void testGetField(){
		CertificateRole terminal = CertificateRole.getFromField(new BitField(2));
		CertificateRole dv1 = CertificateRole.getFromField(new BitField(2, 1));
		CertificateRole dv2 = CertificateRole.getFromField(new BitField(2, 0));
		CertificateRole cvca = CertificateRole.getFromField(new BitField(2, 0, 1));
		
		assertEquals("Role \"TERMINAL\" is not correctly created.", CertificateRole.TERMINAL, terminal);
		assertEquals("Role \"DV_TYPE_1\" is not correctly created", CertificateRole.DV_TYPE_1, dv1);
		assertEquals("Role \"DV_TYPE_2\" is not correctly created", CertificateRole.DV_TYPE_2, dv2);
		assertEquals("Role \"CVCA\" is not correctly created", CertificateRole.CVCA, cvca);
	}
}
