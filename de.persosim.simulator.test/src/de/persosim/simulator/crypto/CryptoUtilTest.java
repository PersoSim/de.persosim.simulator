package de.persosim.simulator.crypto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.spec.ECPoint;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * @author slutters
 *
 */
public class CryptoUtilTest extends PersoSimTestCase {

	/**
	 * Positive test case: multiply EC point with scalar.
	 */
	@Test
	public void testScalarPointMultiplication() {
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// values originate from successful PACE test run
		BigInteger basicX = new BigInteger(1, HexString.toByteArray("3EB50DD69CA2E6B0BE8D4C3089DD55F1657273CFC5728012CA346BAE0AF9A7D8"));
		BigInteger basicY = new BigInteger(1, HexString.toByteArray("829F38EB7E87D468BD9A63CEE4CB15DA25D6EAFE1008FD889D3D6B0F5FB04C02"));
		BigInteger multX = new BigInteger(1, HexString.toByteArray("2100DFDFFE149B14E2D9C0BCD71F50B1A96BC6778531FAE793C3AB1BCCF3FD68"));
		BigInteger multY = new BigInteger(1, HexString.toByteArray("4DBEF9BE48DEB0183AA6AB8BD2B51D7870E050993BEBE823A6AA976AC3088611"));
		BigInteger mult = new BigInteger(1, HexString.toByteArray("A54985F313B9936B9707177A7386639294D3D08D8DE318097323A0D69C8421F8"));
		
		ECPoint basicPoint = new ECPoint(basicX, basicY);
		ECPoint multPointExpected = new ECPoint(multX, multY);
		ECPoint multPointReceived = CryptoUtil.scalarPointMultiplication(domParamsEcdh.getCurve(), basicPoint, mult);
		
		BigInteger exX = multPointExpected.getAffineX();
		BigInteger reX = multPointReceived.getAffineX();
		
		BigInteger exY = multPointExpected.getAffineY();
		BigInteger reY = multPointReceived.getAffineY();
		
		System.out.println("         p: " + domParamsEcdh.getPrime());
		System.out.println("expected x: " + exX);
		System.out.println("received x: " + reX);
		System.out.println("expected y: " + exY);
		System.out.println("received y: " + reY);
		System.out.println("diff X: " + exX.subtract(reX));
		System.out.println("diff Y: " + exY.subtract(reY));
		
		System.out.println("expected point on curve: " + domParamsEcdh.isOnCurve(multPointExpected));
		System.out.println("received point on curve: " + domParamsEcdh.isOnCurve(multPointReceived));
		
		assertEquals("mult x", multPointExpected.getAffineX(), multPointReceived.getAffineX());
		assertEquals("mult y", multPointExpected.getAffineY(), multPointReceived.getAffineY());
	}
	
	/**
	 * Positive test case: multiply EC point with scalar.
	 */
	@Test
	public void testScalarPointMultiplicationBc() {
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// values originate from successful PACE test run
		BigInteger basicX = new BigInteger(1, HexString.toByteArray("3EB50DD69CA2E6B0BE8D4C3089DD55F1657273CFC5728012CA346BAE0AF9A7D8"));
		BigInteger basicY = new BigInteger(1, HexString.toByteArray("829F38EB7E87D468BD9A63CEE4CB15DA25D6EAFE1008FD889D3D6B0F5FB04C02"));
		BigInteger multX = new BigInteger(1, HexString.toByteArray("2100DFDFFE149B14E2D9C0BCD71F50B1A96BC6778531FAE793C3AB1BCCF3FD68"));
		BigInteger multY = new BigInteger(1, HexString.toByteArray("4DBEF9BE48DEB0183AA6AB8BD2B51D7870E050993BEBE823A6AA976AC3088611"));
		BigInteger mult = new BigInteger(1, HexString.toByteArray("A54985F313B9936B9707177A7386639294D3D08D8DE318097323A0D69C8421F8"));
		
		ECPoint basicPoint = new ECPoint(basicX, basicY);
		ECPoint multPointExpected = new ECPoint(multX, multY);
		ECPoint multPointReceived = CryptoUtil.scalarPointMultiplicationBc(domParamsEcdh.getCurve(), basicPoint, mult);
		
		BigInteger exX = multPointExpected.getAffineX();
		BigInteger reX = multPointReceived.getAffineX();
		
		BigInteger exY = multPointExpected.getAffineY();
		BigInteger reY = multPointReceived.getAffineY();
		
		System.out.println("         p: " + domParamsEcdh.getPrime());
		System.out.println("expected x: " + exX);
		System.out.println("received x: " + reX);
		System.out.println("expected y: " + exY);
		System.out.println("received y: " + reY);
		System.out.println("diff X: " + exX.subtract(reX));
		System.out.println("diff Y: " + exY.subtract(reY));
		
		System.out.println("expected point on curve: " + domParamsEcdh.isOnCurve(multPointExpected));
		System.out.println("received point on curve: " + domParamsEcdh.isOnCurve(multPointReceived));
		
		assertEquals("mult x", multPointExpected.getAffineX(), multPointReceived.getAffineX());
		assertEquals("mult y", multPointExpected.getAffineY(), multPointReceived.getAffineY());
	}
	
	@Test
	public void testScalarPointMultiplication2() {
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// values originate from successful PACE test run
		BigInteger basicX = new BigInteger(1, HexString.toByteArray("8BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262"));
		BigInteger basicY = new BigInteger(1, HexString.toByteArray("547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997"));
		BigInteger multX = new BigInteger(1, HexString.toByteArray("874F6277A3C0621DFE106B47DA1242C0F7155905B3A1847D59A64494CF5CE0B9"));
		BigInteger multY = new BigInteger(1, HexString.toByteArray("3E4EBE21E2E131D5A740037635F823DFB859AC58305CEEC9992CEE437F60A730"));
		BigInteger mult = new BigInteger(1, HexString.toByteArray("FA587945E9FE2AEB417DF0ADF951B7CBD9D5E476F8F6EF1B701C59C56B180204"));
		
		ECPoint basicPoint = new ECPoint(basicX, basicY);
		ECPoint multPointExpected = new ECPoint(multX, multY);
		ECPoint multPointReceived = CryptoUtil.scalarPointMultiplication(domParamsEcdh.getCurve(), basicPoint, mult);
		
		BigInteger exX = multPointExpected.getAffineX();
		BigInteger reX = multPointReceived.getAffineX();
		
		BigInteger exY = multPointExpected.getAffineY();
		BigInteger reY = multPointReceived.getAffineY();
		
		System.out.println("         p: " + domParamsEcdh.getPrime());
		System.out.println("expected x: " + exX);
		System.out.println("received x: " + reX);
		System.out.println("expected y: " + exY);
		System.out.println("received y: " + reY);
		System.out.println("diff X    : " + exX.subtract(reX));
		System.out.println("diff Y    : " + exY.subtract(reY));
		
		ECPoint test1 = CryptoUtil.doublePoint(domParamsEcdh.getCurve(), multPointReceived);
		BigInteger t1x = test1.getAffineX();
		BigInteger t1y = test1.getAffineY();
		
		System.out.println("test1 x   : " + t1x);
		System.out.println("test1 y   : " + t1y);
		
		ECPoint test2 = CryptoUtil.addPoint(domParamsEcdh.getCurve(), test1, basicPoint);
		BigInteger t2x = test2.getAffineX();
		BigInteger t2y = test2.getAffineY();
		
		System.out.println("test2 x   : " + t2x);
		System.out.println("test2 y   : " + t2y);
		
		System.out.println("expected point on curve: " + domParamsEcdh.isOnCurve(multPointExpected));
		System.out.println("received point on curve: " + domParamsEcdh.isOnCurve(multPointReceived));
		System.out.println("test1    point on curve: " + domParamsEcdh.isOnCurve(test1));
		
		assertEquals("mult x", multPointExpected.getAffineX(), multPointReceived.getAffineX());
		assertEquals("mult y", multPointExpected.getAffineY(), multPointReceived.getAffineY());
	}
	
	@Test
	public void testScalarPointMultiplication3() {
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// values originate from successful PACE test run
		BigInteger basicX = new BigInteger(1, HexString.toByteArray("8BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262"));
		BigInteger basicY = new BigInteger(1, HexString.toByteArray("547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997"));
		BigInteger multX = new BigInteger(1, HexString.toByteArray("874F6277A3C0621DFE106B47DA1242C0F7155905B3A1847D59A64494CF5CE0B9"));
		BigInteger multY = new BigInteger(1, HexString.toByteArray("3E4EBE21E2E131D5A740037635F823DFB859AC58305CEEC9992CEE437F60A730"));
		BigInteger mult = new BigInteger(1, HexString.toByteArray("FA587945E9FE2AEB417DF0ADF951B7CBD9D5E476F8F6EF1B701C59C56B180204"));
		
		ECPoint basicPoint = new ECPoint(basicX, basicY);
		ECPoint multPointExpected = new ECPoint(multX, multY);
		ECPoint multPointReceived = CryptoUtil.scalarPointMultiplication3(domParamsEcdh.getCurve(), basicPoint, mult);
		
		BigInteger exX = multPointExpected.getAffineX();
		BigInteger reX = multPointReceived.getAffineX();
		
		BigInteger exY = multPointExpected.getAffineY();
		BigInteger reY = multPointReceived.getAffineY();
		
		System.out.println("         p: " + domParamsEcdh.getPrime());
		System.out.println("expected x: " + exX);
		System.out.println("received x: " + reX);
		System.out.println("expected y: " + exY);
		System.out.println("received y: " + reY);
//		System.out.println("diff X    : " + exX.subtract(reX));
//		System.out.println("diff Y    : " + exY.subtract(reY));
		
		ECPoint test1 = CryptoUtil.doublePoint(domParamsEcdh.getCurve(), multPointReceived);
		BigInteger t1x = test1.getAffineX();
		BigInteger t1y = test1.getAffineY();
		
		System.out.println("test1 x   : " + t1x);
		System.out.println("test1 y   : " + t1y);
		
		ECPoint test2 = CryptoUtil.addPoint(domParamsEcdh.getCurve(), test1, basicPoint);
		BigInteger t2x = test2.getAffineX();
		BigInteger t2y = test2.getAffineY();
		
		System.out.println("test2 x   : " + t2x);
		System.out.println("test2 y   : " + t2y);
		
		System.out.println("expected point on curve: " + domParamsEcdh.isOnCurve(multPointExpected));
		System.out.println("received point on curve: " + domParamsEcdh.isOnCurve(multPointReceived));
		System.out.println("test1    point on curve: " + domParamsEcdh.isOnCurve(test1));
		
		assertEquals("mult x", multPointExpected.getAffineX(), multPointReceived.getAffineX());
		assertEquals("mult y", multPointExpected.getAffineY(), multPointReceived.getAffineY());
	}
	
	@Test
	public void testScalarPointMultiplication2Bc() {
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// values originate from successful PACE test run
		BigInteger basicX = new BigInteger(1, HexString.toByteArray("8BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262"));
		BigInteger basicY = new BigInteger(1, HexString.toByteArray("547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997"));
		BigInteger multX = new BigInteger(1, HexString.toByteArray("874F6277A3C0621DFE106B47DA1242C0F7155905B3A1847D59A64494CF5CE0B9"));
		BigInteger multY = new BigInteger(1, HexString.toByteArray("3E4EBE21E2E131D5A740037635F823DFB859AC58305CEEC9992CEE437F60A730"));
		BigInteger mult = new BigInteger(1, HexString.toByteArray("FA587945E9FE2AEB417DF0ADF951B7CBD9D5E476F8F6EF1B701C59C56B180204"));
		
		ECPoint basicPoint = new ECPoint(basicX, basicY);
		ECPoint multPointExpected = new ECPoint(multX, multY);
		ECPoint multPointReceived = CryptoUtil.scalarPointMultiplicationBc(domParamsEcdh.getCurve(), basicPoint, mult);
		
		BigInteger exX = multPointExpected.getAffineX();
		BigInteger reX = multPointReceived.getAffineX();
		
		BigInteger exY = multPointExpected.getAffineY();
		BigInteger reY = multPointReceived.getAffineY();
		
		System.out.println("         p: " + domParamsEcdh.getPrime());
		System.out.println("expected x: " + exX);
		System.out.println("received x: " + reX);
		System.out.println("expected y: " + exY);
		System.out.println("received y: " + reY);
		System.out.println("diff X: " + exX.subtract(reX));
		System.out.println("diff Y: " + exY.subtract(reY));
		
		System.out.println("expected point on curve: " + domParamsEcdh.isOnCurve(multPointExpected));
		System.out.println("received point on curve: " + domParamsEcdh.isOnCurve(multPointReceived));
		
		assertEquals("mult x", multPointExpected.getAffineX(), multPointReceived.getAffineX());
		assertEquals("mult y", multPointExpected.getAffineY(), multPointReceived.getAffineY());
	}
	
	/**
	 * Positive test case: add two EC points.
	 */
	@Test
	public void testAddPoint() {
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// values originate from successful PACE test run
		BigInteger basic1X = new BigInteger(1, HexString.toByteArray("3EB50DD69CA2E6B0BE8D4C3089DD55F1657273CFC5728012CA346BAE0AF9A7D8"));
		BigInteger basic1Y = new BigInteger(1, HexString.toByteArray("829F38EB7E87D468BD9A63CEE4CB15DA25D6EAFE1008FD889D3D6B0F5FB04C02"));
		BigInteger basic2X = new BigInteger(1, HexString.toByteArray("2100DFDFFE149B14E2D9C0BCD71F50B1A96BC6778531FAE793C3AB1BCCF3FD68"));
		BigInteger basic2Y = new BigInteger(1, HexString.toByteArray("4DBEF9BE48DEB0183AA6AB8BD2B51D7870E050993BEBE823A6AA976AC3088611"));
		BigInteger addX = new BigInteger(1, HexString.toByteArray("5EED472701BF5F15C19A7BA97323DC6BD8DF35C331D78B9EAA57A8864BA00D9E"));
		BigInteger addY = new BigInteger(1, HexString.toByteArray("2DD711DA7FF82CA05D0C67D01AFD94210512A908EBF85ADE326F487E05D4C390"));
		
		ECPoint basic1Point = new ECPoint(basic1X, basic1Y);
		ECPoint basic2Point = new ECPoint(basic2X, basic2Y);
		ECPoint addPointExpected = new ECPoint(addX, addY);
		
		ECPoint addPointReceived = CryptoUtil.addPoint(domParamsEcdh.getCurve(), basic1Point, basic2Point);
		
		assertEquals("add x", addPointExpected.getAffineX(), addPointReceived.getAffineX());
		assertEquals("add y", addPointExpected.getAffineY(), addPointReceived.getAffineY());
	}
	
	/**
	 * Positive test case: add two EC points.
	 */
	@Test
	public void testAddPointBc() {
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// values originate from successful PACE test run
		BigInteger basic1X = new BigInteger(1, HexString.toByteArray("3EB50DD69CA2E6B0BE8D4C3089DD55F1657273CFC5728012CA346BAE0AF9A7D8"));
		BigInteger basic1Y = new BigInteger(1, HexString.toByteArray("829F38EB7E87D468BD9A63CEE4CB15DA25D6EAFE1008FD889D3D6B0F5FB04C02"));
		BigInteger basic2X = new BigInteger(1, HexString.toByteArray("2100DFDFFE149B14E2D9C0BCD71F50B1A96BC6778531FAE793C3AB1BCCF3FD68"));
		BigInteger basic2Y = new BigInteger(1, HexString.toByteArray("4DBEF9BE48DEB0183AA6AB8BD2B51D7870E050993BEBE823A6AA976AC3088611"));
		BigInteger addX = new BigInteger(1, HexString.toByteArray("5EED472701BF5F15C19A7BA97323DC6BD8DF35C331D78B9EAA57A8864BA00D9E"));
		BigInteger addY = new BigInteger(1, HexString.toByteArray("2DD711DA7FF82CA05D0C67D01AFD94210512A908EBF85ADE326F487E05D4C390"));
		
		ECPoint basic1Point = new ECPoint(basic1X, basic1Y);
		ECPoint basic2Point = new ECPoint(basic2X, basic2Y);
		ECPoint addPointExpected = new ECPoint(addX, addY);
		
		ECPoint addPointReceived = CryptoUtil.addPointBc(domParamsEcdh.getCurve(), basic1Point, basic2Point);
		
		assertEquals("add x", addPointExpected.getAffineX(), addPointReceived.getAffineX());
		assertEquals("add y", addPointExpected.getAffineY(), addPointReceived.getAffineY());
	}
	
	/**
	 * Positive test case: add two EC points.
	 */
	@Test
	public void testDoublePoint() {
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// values originate from successful PACE test run
		BigInteger basicX = new BigInteger(1, HexString.toByteArray("3EB50DD69CA2E6B0BE8D4C3089DD55F1657273CFC5728012CA346BAE0AF9A7D8"));
		BigInteger basicY = new BigInteger(1, HexString.toByteArray("829F38EB7E87D468BD9A63CEE4CB15DA25D6EAFE1008FD889D3D6B0F5FB04C02"));
		BigInteger doubledX = new BigInteger(1, HexString.toByteArray("1E2F557EC531A3E47859A238F8CCDB3F646FC6533A02F05359B2200C41BC7F79"));
		BigInteger doubledY = new BigInteger(1, HexString.toByteArray("754D80BA26DD2B844F2C02D26CD693A9F28BBB0B318493C3EB84A20086BFD2B0"));
		
		ECPoint basicPoint = new ECPoint(basicX, basicY);
		ECPoint doubledPointExpected = new ECPoint(doubledX, doubledY);
		
		ECPoint addPointReceived = CryptoUtil.doublePoint(domParamsEcdh.getCurve(), basicPoint);
		
		System.out.println("doubled x: " + HexString.encode(addPointReceived.getAffineX()));
		System.out.println("doubled y: " + HexString.encode(addPointReceived.getAffineY()));
		
		assertEquals("double x", doubledPointExpected.getAffineX(), addPointReceived.getAffineX());
		assertEquals("double y", doubledPointExpected.getAffineY(), addPointReceived.getAffineY());
	}
	
	/**
	 * Positive test case: add two EC points.
	 */
	@Test
	public void testDoublePointBc() {
		DomainParameterSetEcdh domParamsEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		// values originate from successful PACE test run
		BigInteger basicX = new BigInteger(1, HexString.toByteArray("3EB50DD69CA2E6B0BE8D4C3089DD55F1657273CFC5728012CA346BAE0AF9A7D8"));
		BigInteger basicY = new BigInteger(1, HexString.toByteArray("829F38EB7E87D468BD9A63CEE4CB15DA25D6EAFE1008FD889D3D6B0F5FB04C02"));
		BigInteger doubledX = new BigInteger(1, HexString.toByteArray("1E2F557EC531A3E47859A238F8CCDB3F646FC6533A02F05359B2200C41BC7F79"));
		BigInteger doubledY = new BigInteger(1, HexString.toByteArray("754D80BA26DD2B844F2C02D26CD693A9F28BBB0B318493C3EB84A20086BFD2B0"));
		
		ECPoint basicPoint = new ECPoint(basicX, basicY);
		ECPoint doubledPointExpected = new ECPoint(doubledX, doubledY);
		
		ECPoint addPointReceived = CryptoUtil.doublePointBc(domParamsEcdh.getCurve(), basicPoint);
		
		System.out.println("doubled x: " + HexString.encode(addPointReceived.getAffineX()));
		System.out.println("doubled y: " + HexString.encode(addPointReceived.getAffineY()));
		
		assertEquals("double x", doubledPointExpected.getAffineX(), addPointReceived.getAffineX());
		assertEquals("double y", doubledPointExpected.getAffineY(), addPointReceived.getAffineY());
	}
	
	/**
	 * Positive test case: encode point with same length coordinates.
	 */
	@Test
	public void testEncode() {
		
		String xStr = "CA2E6B0BE8D4C39A";
		String yStr = "DC6BD8DF35C331D7";
		int refLength = 8;
		byte[] exp = HexString.toByteArray("04" + xStr + yStr);
		
		BigInteger x = new BigInteger(1, HexString.toByteArray(xStr));
		BigInteger y = new BigInteger(1, HexString.toByteArray(yStr));

		ECPoint ecPoint = new ECPoint(x, y);
		
		assertArrayEquals(exp, CryptoUtil.encode(ecPoint, refLength));
	}
	
	/**
	 * Positive test case: encode point with shorter x coordinate.
	 */
	@Test
	public void testEncode_shortX() {
		
		String xStr = "2E6B0BE8D4C39A";
		String yStr = "DC6BD8DF35C331D7";
		int refLength = 8;
		byte[] exp = HexString.toByteArray("0400" + xStr + yStr);
		
		BigInteger x = new BigInteger(1, HexString.toByteArray(xStr));
		BigInteger y = new BigInteger(1, HexString.toByteArray(yStr));

		ECPoint ecPoint = new ECPoint(x, y);
		
		assertArrayEquals(exp, CryptoUtil.encode(ecPoint, refLength));
	}
	
	/**
	 * Positive test case: encode point with shorter y coordinate.
	 */
	@Test
	public void testEncode_shortY() {
		
		String xStr = "CA2E6B0BE8D4C39A";
		String yStr = "D8DF35C331D7";
		int refLength = 8;
		byte[] exp = HexString.toByteArray("04" + xStr + "0000" + yStr);
		
		BigInteger x = new BigInteger(1, HexString.toByteArray(xStr));
		BigInteger y = new BigInteger(1, HexString.toByteArray(yStr));

		ECPoint ecPoint = new ECPoint(x, y);
		
		assertArrayEquals(exp, CryptoUtil.encode(ecPoint, refLength));
	}
	
	/**
	 * Positive test case: encode point with shorter x and y coordinates.
	 */
	@Test
	public void testEncode_shortXY() {
		
		String xStr = "2E6B0BE8D4C39A";
		String yStr = "6BD8DF35C331D7";
		int refLength = 8;
		byte[] exp = HexString.toByteArray("0400" + xStr + "00" + yStr);
		
		BigInteger x = new BigInteger(1, HexString.toByteArray(xStr));
		BigInteger y = new BigInteger(1, HexString.toByteArray(yStr));

		ECPoint ecPoint = new ECPoint(x, y);
		
		assertArrayEquals(exp, CryptoUtil.encode(ecPoint, refLength));
	}
	
	/**
	 * Negative test case: x coordinate larger than reference length.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEncode_largeX() {
		
		String xStr = "ADCA2E6B0BE8D4C39A";
		String yStr = "DC6BD8DF35C331D7";
		int refLength = 8;

		BigInteger x = new BigInteger(1, HexString.toByteArray(xStr));
		BigInteger y = new BigInteger(1, HexString.toByteArray(yStr));

		ECPoint ecPoint = new ECPoint(x, y);
		
		CryptoUtil.encode(ecPoint, refLength);
	}
	
	/**
	 * Negative test case: y coordinate larger than reference length.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEncode_largeY() {
		
		String xStr = "CA2E6B0BE8D4C39A";
		String yStr = "ADDC6BD8DF35C331D7";
		int refLength = 8;

		BigInteger x = new BigInteger(1, HexString.toByteArray(xStr));
		BigInteger y = new BigInteger(1, HexString.toByteArray(yStr));

		ECPoint ecPoint = new ECPoint(x, y);
		
		CryptoUtil.encode(ecPoint, refLength);
	} 
	
	/**
	 * Positive test case: encode a raw representation of a valid signature into
	 * its ASN.1 representation
	 */
	@Test
	public void testRestoreAsn1SignatureStructure() {
		byte[] signature = HexString
				.toByteArray("24B41D25993A96A9CEB75CFB6ACAB615DE0A6124CBE41779B8ECDD804B8A7DB70A09DBFFC745865253BCAEFBBC228AB70C253B1B7E9CD92AB25EA934A8257E1F");
		byte[] expectedResult = HexString
				.toByteArray("3044022024B41D25993A96A9CEB75CFB6ACAB615DE0A6124CBE41779B8ECDD804B8A7DB702200A09DBFFC745865253BCAEFBBC228AB70C253B1B7E9CD92AB25EA934A8257E1F");

		assertArrayEquals(expectedResult, CryptoUtil
				.restoreAsn1SignatureStructure(signature).toByteArray());
	}

	/**
	 * Positive test case: encode a raw representation of a signature consisting
	 * of negative numbers to its ASN.1 representation
	 */
	@Test
	public void testRestoreAsn1SignatureStructureRSAreNegative() {
		byte[] testData = new BigInteger("-20000000000000", 10).toByteArray();

		byte[] signature = Utils.concatByteArrays(testData, testData);
		byte[] expectedResult = Utils.concatByteArrays(
				new byte []{Asn1.SEQUENCE},
				Utils.toUnsignedByteArray((byte) ((testData.length + 3) * 2)),
				new byte []{Asn1.INTEGER},
				Utils.toUnsignedByteArray((byte) (testData.length + 1)),
				HexString.toByteArray("00"), testData,
				new byte []{Asn1.INTEGER},
				Utils.toUnsignedByteArray((byte) (testData.length + 1)),
				HexString.toByteArray("00"), testData);

		assertArrayEquals(expectedResult, CryptoUtil
				.restoreAsn1SignatureStructure(signature).toByteArray());
	}
	
	/**
	 * Positive test case: recreate key pair from minimum representation of
	 * public and private parts.
	 */
	@Test
	public void testKeyConversion() {
		
		byte[] pubKeyBytes = HexString.toByteArray("047D1EA24146C3ADAC11143E7267B4E3EC572534828DB54904877B8D6EFDC5C13123A9E955890447643735C4F0AB9093FAA0C96DEFA1CE9079DA0B3C43BE6A0255");
		byte[] privKeyBytes = HexString.toByteArray("1183F16814B3947D01DAED7F8D236769F5ABD8020FFF53C5E5FE86A8ABAB02D2");
		
		KeyPair keyPair = CryptoUtil.reconstructKeyPair(13, pubKeyBytes, privKeyBytes);
		
		assertTrue(keyPair != null);
	}
	
}
