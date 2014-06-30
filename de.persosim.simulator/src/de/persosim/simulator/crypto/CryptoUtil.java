package de.persosim.simulator.crypto;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.ERROR;
import static de.persosim.simulator.utils.PersoSimLogger.log;
import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.math.ec.ECFieldElement;

import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class provides static methods offering support for basic operations in the field of cryptography.
 * 
 * XXX functional overlap with {@link Crypto} - merge?
 * 
 * @author slutters
 *
 */
public class CryptoUtil {
	public static final String CIPHER_DELIMITER = "/";
	
	public static final byte[] BITMASK            = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80};
	public static final byte[] BITMASK_COMPLEMENT = new byte[]{(byte) 0xFE, (byte) 0xFD, (byte) 0xFB, (byte) 0xF7, (byte) 0xEF, (byte) 0xDF, (byte) 0xBF, (byte) 0x7F};
	
	/**
	 * This method extracts the basic cipher name from the full cipher
	 * String, e.g. it will turn "AES/CBC/NoPadding" into simply "AES".
	 * 
	 * @param cAlgNMP
	 *            the full cipher name String
	 * @return the basic cipher name
	 */
	public static String getCipherNameAsString(String cAlgNMP) {
		return cAlgNMP.substring(0, cAlgNMP.indexOf(CIPHER_DELIMITER));
	}
	
	/**
	 * This method extracts the cipher mode from the full cipher
	 * String, e.g. it will turn "AES/CBC/NoPadding" into simply "CBC".
	 * 
	 * @param cAlgNMP
	 *            the full cipher name String
	 * @return the cipher mode
	 */
	public static String getCipherAlgorithmModeAsString(String cAlgNMP) {
		int index;
		
		index = cAlgNMP.indexOf(CIPHER_DELIMITER);
		
		return cAlgNMP.substring(index + 1, cAlgNMP.indexOf(CIPHER_DELIMITER, index + 1));
	}
	
	/**
	 * This method extracts the cipher padding from the full cipher
	 * String, e.g. it will turn "AES/CBC/NoPadding" into simply "NoPadding".
	 * 
	 * @param cAlgNMP
	 *            the full cipher name String
	 * @return the cipher padding
	 */
	public static String getCipherAlgorithmPaddingAsString(String cAlgNMP) {
		int index;
		
		index = cAlgNMP.indexOf(CIPHER_DELIMITER);
		
		return cAlgNMP.substring(index + 1, cAlgNMP.indexOf(CIPHER_DELIMITER, index + 1));
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * TODO remove BC dependency, maybe move to a dedicated helper class
	 * 
	 * This method performs EC scalar point multiplication
	 * @param curve the elliptic curve to be used
	 * @param ecPoint the point to be multiplied
	 * @param multiplicator the scalar multiplier
	 * @return the multiplied EC point
	 */
	public static ECPoint scalarPointMultiplication(EllipticCurve curve, ECPoint ecPoint, BigInteger multiplicator) {
		org.bouncycastle.math.ec.ECCurve curveBc;
		org.bouncycastle.math.ec.ECPoint pointBc, pointBcMult;
		
		curveBc = EC5Util.convertCurve(curve);
		pointBc = EC5Util.convertPoint(curveBc, ecPoint, false);
		
		pointBcMult = pointBc.multiply(multiplicator);
		
		ECFieldElement ecfX = pointBcMult.normalize().getXCoord();
		ECFieldElement ecfY = pointBcMult.normalize().getYCoord();
		
		BigInteger x = ecfX.toBigInteger();
		BigInteger y = ecfY.toBigInteger();
		
		ECPoint ecPointMult = new ECPoint(x, y);
		
		return ecPointMult;
	}
	
	/**
	 * TODO remove BC dependency, maybe move to a dedicated helper class
	 * 
	 * This method decodes a byte array and restores the represented {@link ECPoint}
	 * @param curve the curve of the point
	 * @param ecPointEncoding the encoding of the point
	 * @return an {@link ECPoint} representing the encoded point
	 */
	public static ECPoint decode(EllipticCurve curve, byte[] ecPointEncoding) {
		org.bouncycastle.math.ec.ECCurve curveBc;
		org.bouncycastle.math.ec.ECPoint decodedPointBc;
		
		curveBc = EC5Util.convertCurve(curve);
		
		try {
			decodedPointBc = curveBc.decodePoint(ecPointEncoding);
		} catch (Exception e) {
			log(CryptoUtil.class, "erroneous point encoding of " + ecPointEncoding.length + " bytes length is: " + HexString.encode(ecPointEncoding), DEBUG);
			logException(CryptoUtil.class, e, ERROR);
			throw e;
		}
		
		ECFieldElement ecfX = decodedPointBc.normalize().getXCoord();
		ECFieldElement ecfY = decodedPointBc.normalize().getYCoord();
		
		BigInteger x = ecfX.toBigInteger();
		BigInteger y = ecfY.toBigInteger();
		
		ECPoint ecPointDecoded = new ECPoint(x, y);
		
		return ecPointDecoded;
	}
	
	/**
	 * This method encodes an {@link ECPoint} (using uncompressed encoding
	 * according to X9.62)
	 * <p/> 
	 * According to ANSI X9.62 EC public point encoding in uncompressed mode is
	 * supposed to look as follows: 04|x-coordinate|y-coordinate If an encoded
	 * coordinate does not match the reference length l, it needs to be padded
	 * with leading 00 bytes. The complete point encoding hence needs to be of
	 * total length (2 * l) + 1.
	 * 
	 * @param ecPoint
	 *            point to be encoded
	 * @param referenceLength
	 *            expected length l of each coordinate in bytes 
	 * @return byte[] containing the uncompressed point encoding
	 */
	public static byte[] encode(ECPoint ecPoint, int referenceLength) {

		// extract coordinates
		byte[] xBytes = Utils.toUnsignedByteArray(ecPoint.getAffineX());
		byte[] yBytes = Utils.toUnsignedByteArray(ecPoint.getAffineY());
		
		//check coordinate lengths
		if ((xBytes.length > referenceLength) || (yBytes.length > referenceLength)) {
			throw new IllegalArgumentException("Coordinates of point are larger than reference length");
		}

		// add padding to x coordinate it needed
		if (xBytes.length < referenceLength) {
			byte[] padding = new byte[referenceLength - xBytes.length];
			Arrays.fill(padding, (byte) 0x00);
			xBytes = Utils.concatByteArrays(padding, xBytes);
		}

		// add padding to x coordinate it needed
		if (yBytes.length < referenceLength) {
			byte[] padding = new byte[referenceLength - yBytes.length];
			Arrays.fill(padding, (byte) 0x00);
			yBytes = Utils.concatByteArrays(padding, yBytes);
		}

		return Utils.concatByteArrays(new byte[] { (byte) 0x04 }, xBytes,
				yBytes);
	}
	
	/**
	 * TODO remove BC dependency, maybe move to a dedicated helper class
	 * 
	 * This method performs EC point addition
	 * @param curve the elliptic curve to be used
	 * @param ecPoint1 the first point for addition
	 * @param ecPoint2 the second point for addition
	 * @return the addition result
	 */
	public static ECPoint pointAddition(EllipticCurve curve, ECPoint ecPoint1, ECPoint ecPoint2) {
		org.bouncycastle.math.ec.ECCurve curveBc;
		org.bouncycastle.math.ec.ECPoint pointBc1, pointBc2, pointBcAdd;
		
		curveBc = EC5Util.convertCurve(curve);
		
		pointBc1 = EC5Util.convertPoint(curveBc, ecPoint1, false);
		pointBc2 = EC5Util.convertPoint(curveBc, ecPoint2, false);
		
		pointBcAdd = pointBc1.add(pointBc2);
		
		ECFieldElement ecfX = pointBcAdd.normalize().getXCoord();
		ECFieldElement ecfY = pointBcAdd.normalize().getYCoord();
		
		BigInteger x = ecfX.toBigInteger();
		BigInteger y = ecfY.toBigInteger();
		
		ECPoint ecPointAdd = new ECPoint(x, y);
		
		return ecPointAdd;
	}
	
	public static KeyPair generateKeyPair(DomainParameterSet domParamSet, SecureRandom secRandom) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		KeyPairGenerator keyPairGenerator;
		
		keyPairGenerator = KeyPairGenerator.getInstance(domParamSet.getKeyAgreementAlgorithm(), Crypto.getCryptoProvider());
		keyPairGenerator.initialize(domParamSet.getKeySpec(), secRandom);
		
		return keyPairGenerator.generateKeyPair();
	}
	
	/**
	 * This method returns a copy of the provided key pair which is updated to the new provided domain parameters.
	 * 
	 * @param keyPair the key pair template to be used for creating the key pairs updated to the new domain parameters
	 * @return a copy of the provided key pair which is updated to the new provided domain parameters
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 */
	public static KeyPair updateKeyPairToNewDomainParameters(KeyPair keyPair, DomainParameterSet domainParametersMapped) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// create key specs for ephemeral public key pair according to mapped domain parameters
		KeySpec[] keySpecsPiccMapped = domainParametersMapped.updateKeySpec(keyPair);
		KeySpec mappedPrivateKeySpec = keySpecsPiccMapped[0];
		KeySpec mappedPublicKeySpec = keySpecsPiccMapped[1];
		
		// Actually create keys from key specs
		KeyFactory keyFactory = KeyFactory.getInstance(domainParametersMapped.getKeyAgreementAlgorithm());
		PrivateKey mappedPrivateKey = keyFactory.generatePrivate(mappedPrivateKeySpec);
		PublicKey mappedPublicKey = keyFactory.generatePublic(mappedPublicKeySpec);
		return new KeyPair(mappedPublicKey, mappedPrivateKey);
	}
	
	/**
	 * This method pads the byte representation of a {@link BigInteger} with a
	 * leading single 0x00 byte if it is negative.
	 * 
	 * @param value
	 * @return
	 */
	private static byte[] getPadded(BigInteger value) {
		if (value.signum() < 0) {
			return Utils.concatByteArrays(new byte[1], value.toByteArray());
		}
		return value.toByteArray();
	}
	
	/**
	 * When an ECDSA signature is given, this method restores the ASN.1 structure
	 * as used by the {@link Signature#verify(byte[])} method
	 * before the actual verification is possible.
	 * 
	 * @param signatureData
	 * @return ASN.1 formatted TLV object
	 */
	public static ConstructedTlvDataObject restoreAsn1SignatureStructure(byte [] signatureData){
		int length = signatureData.length / 2;
		
		BigInteger r = new BigInteger(Arrays.copyOfRange(signatureData, 0, length));
		BigInteger s = new BigInteger(Arrays.copyOfRange(signatureData, length, signatureData.length));
		
		TlvDataObjectContainer integers = new TlvDataObjectContainer();

		PrimitiveTlvDataObject integerRObject = new PrimitiveTlvDataObject(new TlvTag(Asn1.INTEGER), getPadded(r));
		PrimitiveTlvDataObject integerSObject = new PrimitiveTlvDataObject(new TlvTag(Asn1.INTEGER), getPadded(s));
		
		integers.addTlvDataObject(integerRObject);
		integers.addTlvDataObject(integerSObject);
		
		ConstructedTlvDataObject signatureObject = new ConstructedTlvDataObject(new TlvTag(Asn1.SEQUENCE), integers);
		return signatureObject;
	}

	//XXX these two methods rely on BouncyCastle (but work for now), maybe compare to PublicKeyAdapter and see to find a more portable solution
	/**
	 * Restore a public key object from its ASN.1 representation.
	 * @param publicKeyData the ASN.1 encoded key
	 * @return the {@link PublicKey} object
	 * @throws GeneralSecurityException
	 */
	public static PublicKey parsePublicKeyEc(ConstructedTlvDataObject publicKeyData) throws GeneralSecurityException {
		TlvDataObject publicPointData = publicKeyData.getTagField(TlvConstants.TAG_86);
		ECPoint publicPoint = DomainParameterSetEcdh
				.reconstructPoint(publicPointData.getValueField());

		TlvDataObject modulusData = publicKeyData.getTagField(TlvConstants.TAG_81);
		TlvDataObject firstCoefficientData = publicKeyData.getTagField(TlvConstants.TAG_82);
		TlvDataObject secondCoefficientData = publicKeyData.getTagField(TlvConstants.TAG_83);
		TlvDataObject basePointData = publicKeyData.getTagField(TlvConstants.TAG_84);
		TlvDataObject orderOfBasePointData = publicKeyData.getTagField(TlvConstants.TAG_85);
		TlvDataObject cofactorData = publicKeyData.getTagField(TlvConstants.TAG_87);

		ECField field = new ECFieldFp(new BigInteger(1,
				modulusData.getValueField()));
		EllipticCurve curve = new EllipticCurve(field, new BigInteger(1,
				firstCoefficientData.getValueField()), new BigInteger(1,
				secondCoefficientData.getValueField()));
		ECPoint basePoint = DomainParameterSetEcdh
				.reconstructPoint(basePointData.getValueField());
		ECParameterSpec paramSpec = new ECParameterSpec(curve, basePoint,
				new BigInteger(1, orderOfBasePointData.getValueField()),
				Utils.getIntFromUnsignedByteArray(cofactorData.getValueField()));
		ECPublicKeySpec keySpec = new ECPublicKeySpec(publicPoint, paramSpec);
		return KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePublic(keySpec);
	}
	
}
