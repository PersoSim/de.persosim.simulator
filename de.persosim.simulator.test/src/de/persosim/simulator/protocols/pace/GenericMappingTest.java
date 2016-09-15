package de.persosim.simulator.protocols.pace;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;

import org.globaltester.cryptoprovider.Crypto;
import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;
import mockit.NonStrictExpectations;

public class GenericMappingTest extends PersoSimTestCase {
	
	// ---> ECDH <---
	ECPoint ecdhGeneratorUnmappedExpected, ecdhGeneratorMappedExpected, ecdhPublicKeyPointUnmappedExpected, ecdhPublicKeyPointMappedExpected;
	BigInteger ecdhPrivateKeySExpected;
	DomainParameterSetEcdh ecdhDomainParameterSetUnMappedExpected, ecdhDomainParameterSetMappedExpected;
	ECPrivateKey ecdhPrivateKeyUnmappedExpected, ecdhPrivateKeyMappedExpected;
	ECPublicKey ecdhPublicKeyUnmappedExpected, ecdhPublicKeyMappedExpected;
	KeyPair ecdhKeyPairUnmappedExpected, ecdhKeyPairMappedExpected;
	
	/**
	 * Create the test environment containing an elementary file and the mocked
	 * object store.
	 * @throws ReflectiveOperationException 
	 */
	@Before
	public void setUp() throws Exception {
		// this setup is currently used only within testPerformMapping, maybe move it there
		ecdhDomainParameterSetUnMappedExpected = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		ecdhGeneratorUnmappedExpected = ecdhDomainParameterSetUnMappedExpected.getGenerator();
		
		byte[] ecdhGeneratorXMappedPlain = HexString.toByteArray("3D4C109FAF4EFD9FA4BDA13907401379067BF7F03C3ACE6D980575F6F9179E97");
		byte[] ecdhGeneratorYMappedPlain = HexString.toByteArray("61E6418377A06DD48D4B624ABC54F7DAA54008E445E8B808713E406726106E2E");
		ecdhGeneratorMappedExpected = new ECPoint(new BigInteger(1, ecdhGeneratorXMappedPlain), new BigInteger(1, ecdhGeneratorYMappedPlain));
		
		ecdhDomainParameterSetMappedExpected = ecdhDomainParameterSetUnMappedExpected.getUpdatedDomainParameterSet(ecdhGeneratorMappedExpected);
		
		byte[] ecdhPrivateSPlain = HexString.toByteArray("74F6631E3C1A185918CDE69B80016B5F6C34A686BD4E1DC2470911C4A6280A92");
		ecdhPrivateKeySExpected = new BigInteger(1, ecdhPrivateSPlain);
		
		byte[] ecdhPublicXUnmappedPlain = HexString.toByteArray("16CD374C07A5A00354F5AACC22DC0C5A57AF154E29F3DFDD17A3F05E4374DEF0");
		byte[] ecdhPublicYUnmappedPlain = HexString.toByteArray("A6478E512FE183B553EF84A424B0949A913953AC498FF7C4E316F1C71D9BD62A");
		ecdhPublicKeyPointUnmappedExpected = new ECPoint(new BigInteger(1, ecdhPublicXUnmappedPlain), new BigInteger(1, ecdhPublicYUnmappedPlain));
		
		ECPrivateKeySpec ecdhPrivateKeySpecUnmappedExpected = new ECPrivateKeySpec(ecdhPrivateKeySExpected, ecdhDomainParameterSetUnMappedExpected.getKeySpec());
		ECPublicKeySpec ecdhPublicKeySpecUnmappedExpected = new ECPublicKeySpec(ecdhPublicKeyPointUnmappedExpected, ecdhDomainParameterSetUnMappedExpected.getKeySpec());
		
		KeyFactory keyFactory = KeyFactory.getInstance("ECDH", Crypto.getCryptoProvider());
		ecdhPrivateKeyUnmappedExpected = (ECPrivateKey) keyFactory.generatePrivate(ecdhPrivateKeySpecUnmappedExpected);
		ecdhPublicKeyUnmappedExpected = (ECPublicKey) keyFactory.generatePublic(ecdhPublicKeySpecUnmappedExpected);
		
		ecdhKeyPairUnmappedExpected = new KeyPair(ecdhPublicKeyUnmappedExpected, ecdhPrivateKeyUnmappedExpected);
		ecdhKeyPairMappedExpected = CryptoUtil.updateKeyPairToNewDomainParameters(ecdhKeyPairUnmappedExpected, ecdhDomainParameterSetMappedExpected);
		
		ecdhPrivateKeyMappedExpected = (ECPrivateKey) ecdhKeyPairMappedExpected.getPrivate();
		ecdhPublicKeyMappedExpected = (ECPublicKey) ecdhKeyPairMappedExpected.getPublic();
	}
	
	/**
	 * Positive test case: perform mapping of ECDH domain parameters based on values from valid PACE test run.
	 */
	@Test
	public void testPerformMapping() throws Exception {
		new NonStrictExpectations(CryptoUtil.class) {
			{
				CryptoUtil.generateKeyPair(
						withInstanceOf(DomainParameterSetEcdh.class),
						withInstanceOf(SecureRandom.class));
				
				// key pair that is used for mapping, e.g. is unmapped
				result = ecdhKeyPairUnmappedExpected;
			}
		};
		
		GenericMappingEcdh mapping = new GenericMappingEcdh();	
		
		byte[] nonceSPlainExpected = HexString.toByteArray("1DD01F3933B57DA8EF4F07B5FDC46DC412C9E695707E9A391D804F24E683A305");
		byte[] mappingDataExpected = HexString.toByteArray("04A983801181B4DF9262ED4D277711BF3AB3FE260E4A814439A80B424CD4A6090E6559F7AE3702AD5C16348B384E09B4B50E8FBD3DEEA081F2A5AB85E7748A8243");
		
		byte[] mappingResponseExpected = ecdhDomainParameterSetUnMappedExpected.encodePublicKey(ecdhPublicKeyUnmappedExpected);
		
		MappingResult mappingResultReceived = mapping.performMapping(ecdhDomainParameterSetUnMappedExpected, nonceSPlainExpected, mappingDataExpected);
		DomainParameterSetEcdh domainParameterSetReceived = (DomainParameterSetEcdh) mappingResultReceived.getMappedDomainParameters();
		KeyPair keyPairReceived = mappingResultReceived.getKeyPairPiccMapped();
		ECPublicKey publicKeyReceived = (ECPublicKey) keyPairReceived.getPublic();
		ECPrivateKey privateKeyReceived = (ECPrivateKey) keyPairReceived.getPrivate();
		byte[] mappingResponseReceived = mappingResultReceived.getMappingResponse();
		
		assertEquals("generator", ecdhGeneratorMappedExpected, domainParameterSetReceived.getGenerator());
		assertNotEquals("ECDH public key W", ecdhPublicKeyMappedExpected.getW(), publicKeyReceived.getW());
		assertNotEquals("ECDH private key S", ecdhPrivateKeyMappedExpected.getS(), privateKeyReceived.getS());
		assertArrayEquals("mapping response", mappingResponseExpected, mappingResponseReceived);
	}
	
}
