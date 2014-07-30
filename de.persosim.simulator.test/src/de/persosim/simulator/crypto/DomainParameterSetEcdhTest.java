package de.persosim.simulator.crypto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import de.persosim.simulator.jaxb.PersoSimJaxbContextProvider;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class DomainParameterSetEcdhTest extends PersoSimTestCase {

	/**
	 * Positive test case: get byte array encoding of public key.
	 */
	@Test
	public void testEncodePublicKey() throws Exception {	
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		byte[] xArray = HexString.toByteArray("4DD4D9CCB21EA76850E96699DF3EED2FA65CE0CBB3BF7604E1C458CF71B47F59");
		byte[] yArray = HexString.toByteArray("5AF8C1A214A81761DAA6D134DE0E5EA52D54C3BE3F05944F4460F81158D89DEA");
		
		byte[] publicKeyEncodingPlainExpected = Utils.concatByteArrays(new byte[]{(byte) 0x04}, xArray, yArray);
		
		// point coordinates originate from successful PACE test run, i.e. have been verified to be on the curve
		BigInteger publicPointX = new BigInteger(1, xArray);
		BigInteger publicPointY = new BigInteger(1, yArray);
		
		ECPoint point = new ECPoint(publicPointX, publicPointY);
		
		KeySpec publicKeySpec = new ECPublicKeySpec(point, domParamsEcdh.getKeySpec());
		
		KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
		ECPublicKey ecdhPublicKeyExpected = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		
		byte[] publicKeyEncodingPlain = domParamsEcdh.encodePublicKey(ecdhPublicKeyExpected);
		
		assertArrayEquals("reconstructed encoding", publicKeyEncodingPlainExpected, publicKeyEncodingPlain);
	}	

	/**
	 * Positive test case: get byte array compressed (TR-03110) encoding of public key.
	 */
	@Test
	public void testComp() throws Exception {	
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		byte[] xArray = HexString.toByteArray("4DD4D9CCB21EA76850E96699DF3EED2FA65CE0CBB3BF7604E1C458CF71B47F59");
		byte[] yArray = HexString.toByteArray("5AF8C1A214A81761DAA6D134DE0E5EA52D54C3BE3F05944F4460F81158D89DEA");
				
		// point coordinates originate from successful PACE test run, i.e. have been verified to be on the curve
		BigInteger publicPointX = new BigInteger(1, xArray);
		BigInteger publicPointY = new BigInteger(1, yArray);
		
		ECPoint point = new ECPoint(publicPointX, publicPointY);
		
		KeySpec publicKeySpec = new ECPublicKeySpec(point, domParamsEcdh.getKeySpec());
		
		KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
		ECPublicKey ecdhPublicKeyExpected = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		
		byte[] publicKeyEncodingPlain = domParamsEcdh.comp(ecdhPublicKeyExpected);
		
		assertArrayEquals("reconstructed encoding", xArray, publicKeyEncodingPlain);
	}
	
	/**
	 * Positive test case: get byte array compressed (TR-03110) encoding of public ECDH key, key is padded with 0-bytes.
	 */
	@Test
	public void testCompShortEcdhKey() throws Exception {	
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		byte[] xArray = HexString.toByteArray("0015A12C49DC3F2985AE44E5EF75AA0A1862527CD9D5B03D17CD1E2FC0290DB7");
		byte[] yArray = HexString.toByteArray("0AF39509F439220E7EEA61D15668BB5D63DD256BD7F4E9E1F9753866C4A6BD59");
				
		// point coordinates originate from successful PACE test run, i.e. have been verified to be on the curve
		BigInteger publicPointX = new BigInteger(1, xArray);
		BigInteger publicPointY = new BigInteger(1, yArray);
		
		ECPoint point = new ECPoint(publicPointX, publicPointY);
		
		KeySpec publicKeySpec = new ECPublicKeySpec(point, domParamsEcdh.getKeySpec());
		
		KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
		ECPublicKey ecdhPublicKeyExpected = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		
		byte[] publicKeyEncodingPlain = domParamsEcdh.comp(ecdhPublicKeyExpected);
		
		assertArrayEquals("reconstructed encoding", xArray, publicKeyEncodingPlain);
	}
	
	/**
	 * Positive test case: reconstruct public key from byte array encoding (uncompressed encoding according to ANSI X9.62).
	 */
	@Test
	public void testReconstructPublicKey() throws Exception {		
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		byte[] xArray = HexString.toByteArray("4DD4D9CCB21EA76850E96699DF3EED2FA65CE0CBB3BF7604E1C458CF71B47F59");
		byte[] yArray = HexString.toByteArray("5AF8C1A214A81761DAA6D134DE0E5EA52D54C3BE3F05944F4460F81158D89DEA");
		
		byte[] publicKeyEncodingPlain = Utils.concatByteArrays(new byte[]{(byte) 0x04}, xArray, yArray);
		
		// point coordinates originate from successful PACE test run, i.e. have been verified to be on the curve
		BigInteger publicPointX = new BigInteger(1, xArray);
		BigInteger publicPointY = new BigInteger(1, yArray);
		
		ECPoint point = new ECPoint(publicPointX, publicPointY);
		
		KeySpec publicKeySpecExpected = new ECPublicKeySpec(point, domParamsEcdh.getKeySpec());
		
		KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
		ECPublicKey ecdhPublicKeyExpected = (ECPublicKey) keyFactory.generatePublic(publicKeySpecExpected);
		
		ECPublicKey ecdhPublicKeyReconstructed = domParamsEcdh.reconstructPublicKey(publicKeyEncodingPlain);
		
		assertEquals("reconstructed w", ecdhPublicKeyExpected.getW(), ecdhPublicKeyReconstructed.getW());
		assertEquals("reconstructed curve", ecdhPublicKeyExpected.getParams().getCurve(), ecdhPublicKeyReconstructed.getParams().getCurve());
		assertEquals("reconstructed G", ecdhPublicKeyExpected.getParams().getGenerator(), ecdhPublicKeyReconstructed.getParams().getGenerator());
		assertEquals("reconstructed order", ecdhPublicKeyExpected.getParams().getOrder(), ecdhPublicKeyReconstructed.getParams().getOrder());
		assertEquals("reconstructed cofactor", ecdhPublicKeyExpected.getParams().getCofactor(), ecdhPublicKeyReconstructed.getParams().getCofactor());
		assertArrayEquals("reconstructed encoding", ecdhPublicKeyExpected.getEncoded(), ecdhPublicKeyReconstructed.getEncoded());
	}
	
	/**
	 * Positive test case: reconstruct private key from byte array encoding (plain value S).
	 */
	@Test
	public void testReconstructPrivateKey() throws Exception {		
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		byte[] ecdhPrivateKeyDataPicc = HexString.toByteArray("79 84 67 4C F3 B3 A5 24 BF 92 9C E8 A6 7F CF 22 17 3D A0 BA D5 95 EE D6 DE B7 2D 22 C5 42 FA 9D");
		BigInteger sExpected = new BigInteger(1, ecdhPrivateKeyDataPicc);
		
		KeySpec privateKeySpecExpected = new ECPrivateKeySpec(sExpected, domParamsEcdh.getKeySpec());
		
		KeyFactory keyFactory = KeyFactory.getInstance(domParamsEcdh.getKeyAgreementAlgorithm());
		ECPrivateKey ecdhPrivateKeyExpected = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpecExpected);
		
		ECPrivateKey ecdhPrivateKeyReceived = domParamsEcdh.reconstructPrivateKey(ecdhPrivateKeyDataPicc);
		
		assertEquals("reconstructed S", ecdhPrivateKeyExpected.getS(), ecdhPrivateKeyReceived.getS());
		assertEquals("reconstructed curve", ecdhPrivateKeyExpected.getParams().getCurve(), ecdhPrivateKeyReceived.getParams().getCurve());
		assertEquals("reconstructed G", ecdhPrivateKeyExpected.getParams().getGenerator(), ecdhPrivateKeyReceived.getParams().getGenerator());
		assertEquals("reconstructed order", ecdhPrivateKeyExpected.getParams().getOrder(), ecdhPrivateKeyReceived.getParams().getOrder());
		assertEquals("reconstructed cofactor", ecdhPrivateKeyExpected.getParams().getCofactor(), ecdhPrivateKeyReceived.getParams().getCofactor());
		assertArrayEquals("reconstructed encoding", ecdhPrivateKeyExpected.getEncoded(), ecdhPrivateKeyReceived.getEncoded());
	}
	
	/**
	 * Positive test case: reconstruct point from X9.62 uncompressed byte array encoding.
	 */
	@Test
	public void testReconstructPointUncompressed() throws Exception {		
		byte[] xArray = HexString.toByteArray("4DD4D9CCB21EA76850E96699DF3EED2FA65CE0CBB3BF7604E1C458CF71B47F59");
		byte[] yArray = HexString.toByteArray("5AF8C1A214A81761DAA6D134DE0E5EA52D54C3BE3F05944F4460F81158D89DEA");
		
		byte[] keyEncodingPlain = Utils.concatByteArrays(new byte[]{(byte) 0x04}, xArray, yArray);
		
		// point coordinates originate from successful PACE test run, i.e. have been verified to be on the curve
		BigInteger publicPointX = new BigInteger(1, xArray);
		BigInteger publicPointY = new BigInteger(1, yArray);
		
		ECPoint pointExpected = new ECPoint(publicPointX, publicPointY);
		
		ECPoint pointReconstructed = DomainParameterSetEcdh.reconstructPoint(keyEncodingPlain);
		
		assertEquals("point x", pointExpected.getAffineX(), pointReconstructed.getAffineX());
		assertEquals("point y", pointExpected.getAffineY(), pointReconstructed.getAffineY());
	}
	
	/**
	 * Positive test case: check that valid EC point is identified as being on the curve.
	 */
	@Test
	public void testIsOnCurve() {		
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// point coordinates originate from successful PACE test run, i.e. have been verified to be on the curve
		BigInteger publicPointX = new BigInteger(1, HexString.toByteArray("4DD4D9CCB21EA76850E96699DF3EED2FA65CE0CBB3BF7604E1C458CF71B47F59"));
		BigInteger publicPointY = new BigInteger(1, HexString.toByteArray("5AF8C1A214A81761DAA6D134DE0E5EA52D54C3BE3F05944F4460F81158D89DEA"));
		
		ECPoint ecPoint = new ECPoint(publicPointX, publicPointY);
		
		assertTrue(domParamsEcdh.isOnCurve(ecPoint));
	}
	
	/**
	 * Positive test case: check that EC point with tampered Y coordinate is identified as not being on the curve.
	 */
	@Test
	public void testIsOnCurve_WrongY() {		
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// original point coordinates originate from successful PACE test run, i.e. have been verified to be on the curve
		// Y coordinate has been decreased by 1
		BigInteger publicPointX = new BigInteger(1, HexString.toByteArray("4DD4D9CCB21EA76850E96699DF3EED2FA65CE0CBB3BF7604E1C458CF71B47F59"));
		BigInteger publicPointY = new BigInteger(1, HexString.toByteArray("5AF8C1A214A81761DAA6D134DE0E5EA52D54C3BE3F05944F4460F81158D89DE9"));
		
		ECPoint ecPoint = new ECPoint(publicPointX, publicPointY);
		
		assertFalse(domParamsEcdh.isOnCurve(ecPoint));
	}
	
	/**
	 * Positive test case: test update key spec of key pair to reflect mapped
	 * domain parameters. Check afterwards that created key specs match expected
	 * ones.
	 */
	@Test
	public void testUpdateKeySpec() {		
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// point coordinates originate from successful PACE test run, i.e. have been verified to be on the curve
		BigInteger publicPointX = new BigInteger(1, HexString.toByteArray("4DD4D9CCB21EA76850E96699DF3EED2FA65CE0CBB3BF7604E1C458CF71B47F59"));
		BigInteger publicPointY = new BigInteger(1, HexString.toByteArray("5AF8C1A214A81761DAA6D134DE0E5EA52D54C3BE3F05944F4460F81158D89DEA"));
		
		ECPoint publicPoint = new ECPoint(publicPointX, publicPointY);
		
		BigInteger privateDexpected = new BigInteger(1, HexString.toByteArray("A54985F313B9936B9707177A7386639294D3D08D8DE318097323A0D69C8421F8"));
		
		ECPublicKeySpec keySpecPublic = new ECPublicKeySpec(publicPoint, domParamsEcdh.getKeySpec());
		ECPrivateKeySpec keySpecPrivate = new ECPrivateKeySpec(privateDexpected, domParamsEcdh.getKeySpec());
		
		ECPrivateKey ecdhPrivateKey;
		ECPublicKey ecdhPublicKey;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
			ecdhPublicKey = (ECPublicKey) keyFactory.generatePublic(keySpecPublic);
			ecdhPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(keySpecPrivate);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("invalid key agreement algorithm");
		} catch (InvalidKeySpecException e) {
			throw new IllegalArgumentException("invalid key spec");
		}
		
		KeyPair keyPair = new KeyPair(ecdhPublicKey, ecdhPrivateKey);
		
		// point coordinates originate from successful PACE test run, i.e. have been verified to be on the curve
		BigInteger mappedGX = new BigInteger(1, HexString.toByteArray("3EB50DD69CA2E6B0BE8D4C3089DD55F1657273CFC5728012CA346BAE0AF9A7D8"));
		BigInteger mappedGY = new BigInteger(1, HexString.toByteArray("829F38EB7E87D468BD9A63CEE4CB15DA25D6EAFE1008FD889D3D6B0F5FB04C02"));
		
		ECPoint mappedG = new ECPoint(mappedGX, mappedGY);
		
		DomainParameterSetEcdh domParamsEcdhMapped = new DomainParameterSetEcdh(domParamsEcdh.getCurve(), mappedG, domParamsEcdh.getOrder(), domParamsEcdh.getCofactor());
		
		KeySpec[] updatedKeySpecs = domParamsEcdhMapped.updateKeySpec(keyPair);
		
		ECPrivateKeySpec keySpecPrivateMapped = (ECPrivateKeySpec) updatedKeySpecs[0];
		ECPublicKeySpec keySpecPublicMapped = (ECPublicKeySpec) updatedKeySpecs[1];
		
		// point coordinates originate from successful PACE test run, i.e. have been verified to be on the curve
		BigInteger publicPointMappedX = new BigInteger(1, HexString.toByteArray("2100DFDFFE149B14E2D9C0BCD71F50B1A96BC6778531FAE793C3AB1BCCF3FD68"));
		BigInteger publicPointMappedY = new BigInteger(1, HexString.toByteArray("4DBEF9BE48DEB0183AA6AB8BD2B51D7870E050993BEBE823A6AA976AC3088611"));
				
		ECPoint publicPointMappedExpected = new ECPoint(publicPointMappedX, publicPointMappedY);
		
		assertEquals("private key component", privateDexpected, keySpecPrivateMapped.getS());
		assertEquals("public key point", publicPointMappedExpected, keySpecPublicMapped.getW());
		
		assertEquals("private key curve", domParamsEcdh.getCurve(), keySpecPrivateMapped.getParams().getCurve());
		assertEquals("private key generator", domParamsEcdhMapped.getGenerator(), keySpecPrivateMapped.getParams().getGenerator());
		assertEquals("private key order", domParamsEcdh.getOrder(), keySpecPrivateMapped.getParams().getOrder());
		assertEquals("private key cofactor", domParamsEcdh.getCofactor(), keySpecPrivateMapped.getParams().getCofactor());
		
		assertEquals("public key curve", domParamsEcdh.getCurve(), keySpecPublicMapped.getParams().getCurve());
		assertEquals("public key generator", domParamsEcdhMapped.getGenerator(), keySpecPublicMapped.getParams().getGenerator());
		assertEquals("public key order", domParamsEcdh.getOrder(), keySpecPublicMapped.getParams().getOrder());
		assertEquals("public key cofactor", domParamsEcdh.getCofactor(), keySpecPublicMapped.getParams().getCofactor());
	}
	
	/**
	 * Positive test case: test standardized domain parameters for base points/generators being on the curve.
	 */
	@Test
	public void testCheckCurveMatchingGenerator() {
		DomainParameterSet domParams;
		DomainParameterSetEcdh domParamsEcdh;
		
		for(int i = 0; i < StandardizedDomainParameters.NO_OF_STANDARDIZED_DOMAIN_PARAMETERS; i++) {
			domParams = StandardizedDomainParameters.getDomainParameterSetById(i);
			
			if(domParams instanceof DomainParameterSetEcdh) {
				domParamsEcdh = (DomainParameterSetEcdh) domParams;
				
				assertTrue(domParamsEcdh.isOnCurve(domParamsEcdh.getGenerator()));
				System.out.println("domain parameter set " + i + " passed check");
			}
		}
	}
	
	/**
	 * Positive test: Ensure that the datastructure restored from the XML
	 * representation is identical to the input.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_JaxbMarshallUnmarshall() throws Exception {
		DomainParameterSetEcdh input = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// instantiate marshaller
		Marshaller m = PersoSimJaxbContextProvider.getContext().createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Write to String
		StringWriter strWriter = new StringWriter();
		m.marshal(input, strWriter);
		String marshalledPerso = strWriter.toString();
		System.out.println(marshalledPerso);
		
		//unmarshall from string
		StringReader sr = new StringReader(marshalledPerso);
		Unmarshaller um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
		Object unmarshalledObject = um.unmarshal(sr);
		
		//assert that the recreated object matches the input
		assertEquals(input, unmarshalledObject);

	}
	
}
