package de.persosim.simulator.crypto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
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
		
		assertEquals("mult x", multPointExpected.getAffineX(), multPointReceived.getAffineX());
		assertEquals("mult y", multPointExpected.getAffineY(), multPointReceived.getAffineY());
	}
	
	/**
	 * Positive test case: add two EC points.
	 */
	@Test
	public void testPointAddition() {
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
		
		ECPoint addPointReceived = CryptoUtil.pointAddition(domParamsEcdh.getCurve(), basic1Point, basic2Point);
		
		assertEquals("add x", addPointExpected.getAffineX(), addPointReceived.getAffineX());
		assertEquals("add y", addPointExpected.getAffineY(), addPointReceived.getAffineY());
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
	
}
