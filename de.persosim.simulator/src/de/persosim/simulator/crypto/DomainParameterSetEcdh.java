package de.persosim.simulator.crypto;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.ERROR;
import static de.persosim.simulator.utils.PersoSimLogger.TRACE;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class specifies the domain parameter sets to be used for ECDH key
 * agreements or within ECDH keys.
 * 
 * @author slutters
 * 
 */
public class DomainParameterSetEcdh implements DomainParameterSet, TlvConstants {
	
	public static final byte[] id_ecPublicKey = HexString.toByteArray("2A8648CE3D0201");
	public static final byte[] id_primeField = HexString.toByteArray("2A8648CE3D0101");
	
	protected ECParameterSpec ecParameterSpec;
	
	public DomainParameterSetEcdh() {}
	
	/**
	 * Constructor for constructing a {@link DomainParameterSetEcdh} object
	 * @param curve the elliptic curve
	 * @param g the group generator
	 * @param n the group order
	 * @param h the co-factor
	 */
	public DomainParameterSetEcdh(EllipticCurve curve, ECPoint g, BigInteger n, int h) {
		ecParameterSpec = new ECParameterSpec(curve, g,n, h);
	}
	
	@Override
	public BigInteger getPrime() {
		ECField ecField = ecParameterSpec.getCurve().getField();
		if (ecField instanceof ECFieldFp) {
			return ((ECFieldFp) ecField).getP();
		} else{
			throw new IllegalArgumentException("ECDH domain parameter set's curve does not provide a prime finite field");
		}
	}
	
	/**
	 * This method checks whether the provided EC point is on the curve specified by the parameters within this object.
	 * @param ecPoint the EC point to be checked
	 * @return whether the provided EC Point is on the curve
	 */
	public boolean isOnCurve(ECPoint ecPoint) {
		BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
		BigInteger three = two.add(BigInteger.ONE);
		
		BigInteger p = getPrime();
		
		EllipticCurve ecCurve = ecParameterSpec.getCurve();
		BigInteger a = ecCurve.getA();
		BigInteger b = ecCurve.getB();
		
		BigInteger x = ecPoint.getAffineX();
		BigInteger y = ecPoint.getAffineY();
		
		BigInteger leftSide  = y.modPow(two, p);
		BigInteger rightSide = (((x.modPow(three, p)).add(a.multiply(x))).add(b)).mod(p);
		
		boolean result = leftSide.compareTo(rightSide) == 0;
		
		if(!result) {
			log(getClass(), "point not on curve - x: " + HexString.encode(x), DEBUG);
			log(getClass(), "point not on curve - y: " + HexString.encode(y), DEBUG);
			log(getClass(), "point not on curve - p: " + HexString.encode(p), DEBUG);
			log(getClass(), "point not on curve - " + HexString.encode(leftSide) + " != " + HexString.encode(rightSide), DEBUG);
			log(getClass(), "point not on curve - x: " + x, DEBUG);
			log(getClass(), "point not on curve - y: " + y, DEBUG);
			log(getClass(), "point not on curve - p: " + p, DEBUG);
			log(getClass(), "point not on curve - " + leftSide + " != " + rightSide, DEBUG);
		}
		
		return result;
	}
	
	/**
	 * Computes reference length l used for Point-to-Octet-String Conversion according to ANSI X9.62 chapter 4.3.6.
	 * @return reference length l
	 */
	public int getPublicPointReferenceLengthL() {
		return getPublicPointReferenceLengthL(getPrime());
	}
	
	/**
	 * Computes reference length l used for Point-to-Octet-String Conversion according to ANSI X9.62 chapter 4.3.6.
	 * @param q the prime
	 * @return reference length l
	 */
	public static int getPublicPointReferenceLengthL(BigInteger q) {
		int log = q.bitLength();
		int result = ((Double) Math.ceil(log/8.0)).intValue();
		return result;
	}
	
	/**
	 * This method creates a {@link ECPrivateKeySpec} object for a private key based on the provided secret key parameter and the basic domain parameters of this object.
	 * @param privateX the secret key parameter
	 * @return a {@link ECPrivateKeySpec} object for a private key
	 */
	public ECPrivateKeySpec getPrivateKeySpec(BigInteger privateD) {
		return new ECPrivateKeySpec(privateD, ecParameterSpec);
	}
	
	/**
	 * This method creates a {@link ECPublicKeySpec} object for a public key based on the provided public key parameter and the basic domain parameters of this object.
	 * @param privateX the public key parameter
	 * @return a {@link ECPublicKeySpec} object for a public key
	 */
	public ECPublicKeySpec getPublicKeySpec(ECPoint publicW) {
		return new ECPublicKeySpec(publicW, ecParameterSpec);
	}
	
	@Override
	public KeySpec[] updateKeySpec(KeyPair keyPair) {
		ECPublicKey ecPublicKey;
		ECPrivateKey ecPrivateKey;
		try {
			ecPublicKey = (ECPublicKey) keyPair.getPublic();
			ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("key pair must be EC");
		}
		
		EllipticCurve ec = ecParameterSpec.getCurve();
		ECPoint gUnmapped = ecPrivateKey.getParams().getGenerator();
		ECPoint gMapped = ecParameterSpec.getGenerator();
		
		BigInteger ecFirstCoefficientA = ec.getA();
		BigInteger ecSecondCoefficientB = ec.getB();
		BigInteger ecFp = getPrime();
		
		BigInteger gUnmappedX = gUnmapped.getAffineX();
		BigInteger gUnmappedY = gUnmapped.getAffineY();
		
		BigInteger gMappedX = gMapped.getAffineX();
		BigInteger gMappedY = gMapped.getAffineY();
		
		if(gMappedX.equals(gUnmappedX) && gMappedY.equals(gUnmappedY)) {
			throw new IllegalArgumentException("ECDH unmapped and mapped generator g of group G are identical!");
		}
		
		BigInteger order = getOrder();
		int coFactor = ecParameterSpec.getCofactor();
		
		BigInteger piccPrivateKeyD = ecPrivateKey.getS();
		ECPoint publicPointW = ecPublicKey.getW();
		BigInteger publicPointWx = publicPointW.getAffineX();
		BigInteger publicPointWy = publicPointW.getAffineY();
		
		ECPoint publicPointWmapped = CryptoUtil.scalarPointMultiplication(ec, getOrder(), gMapped, piccPrivateKeyD);
		
		BigInteger publicPointWmappedX = publicPointWmapped.getAffineX();
		BigInteger publicPointWmappedY = publicPointWmapped.getAffineY();
		
		log(getClass(), "ECDH ephemeral private key d is                    : " + HexString.encode(piccPrivateKeyD), DEBUG);
		log(getClass(), "ECDH ephemeral public point w.x under unmapped g is: " + HexString.encode(publicPointWx), DEBUG);
		log(getClass(), "ECDH ephemeral public point w.y under unmapped g is: " + HexString.encode(publicPointWy), DEBUG);
		log(getClass(), "ECDH ephemeral public point w.x under mapped g is  : " + HexString.encode(publicPointWmappedX), DEBUG);
		log(getClass(), "ECDH ephemeral public point w.y under mapped g is  : " + HexString.encode(publicPointWmappedY), DEBUG);
		log(getClass(), "ECDH curve's first coefficient A is                : " + HexString.encode(ecFirstCoefficientA), DEBUG);
		log(getClass(), "ECDH curve's second coefficient B is               : " + HexString.encode(ecSecondCoefficientB), DEBUG);
		log(getClass(), "ECDH original generator g.x of group G is          : " + HexString.encode(gUnmappedX), DEBUG);
		log(getClass(), "ECDH original generator g.y of group G is          : " + HexString.encode(gUnmappedY), DEBUG);
		log(getClass(), "ECDH mapped generator g.x of group G is            : " + HexString.encode(gMappedX), DEBUG);
		log(getClass(), "ECDH mapped generator g.y of group G is            : " + HexString.encode(gMappedY), DEBUG);
		log(getClass(), "ECDH prime modulus p of group G is                 : " + HexString.encode(ecFp), DEBUG);
		log(getClass(), "ECDH order of group G is                           : " + HexString.encode(order), DEBUG);
		log(getClass(), "ECDH cofactor is                                   : " + coFactor, DEBUG);
		
		KeySpec mappedPrivateKeySpec = getPrivateKeySpec(piccPrivateKeyD);
		KeySpec mappedPublicKeySpec = getPublicKeySpec(publicPointWmapped);
		
		return new KeySpec[]{mappedPrivateKeySpec, mappedPublicKeySpec};
	}
	
	@Override
	public String getKeyAgreementAlgorithm() {
		return "ECDH";
	}
	
	/**
	 * This method reconstructs an {@link ECPoint} object from its basic encoding according to ANSI X9.62.
	 * Currently only uncompressed encoding indicated by leading 0x04 byte is supported.
	 * @param rawKeyPlain the point encoded as byte array
	 * @return the {@link ECPoint} representation of the provided byte array encoding
	 */
	public static ECPoint reconstructPoint(byte[] rawKeyPlain) {
		if(rawKeyPlain == null) {throw new NullPointerException("raw key material must not be null");};
		
		log(DomainParameterSetEcdh.class, "raw public key EC point byte array is: " + HexString.encode(rawKeyPlain), TRACE);
		
		if(rawKeyPlain.length % 2 != 1) {throw new IllegalArgumentException("encoded public key EC point must be of uneven byte length");};
		
		byte firstKeyByte = rawKeyPlain[0];
		
		switch (firstKeyByte){
		case (byte) 0x01:
			throw new IllegalArgumentException("encoding 0x01 of public key point is currently not supported"); //IMPL ECPoint encoding
		case (byte) 0x02:
			throw new IllegalArgumentException("encoding 0x02 of public key point is currently not supported"); //IMPL ECPoint encoding
		case (byte) 0x03:
			throw new IllegalArgumentException("encoding 0x03 of public key point is currently not supported"); //IMPL ECPoint encoding
		case (byte) 0x04:
			log(DomainParameterSetEcdh.class, "leading byte 0x04 of public key point indicates uncompressed encoding", TRACE);
			break;
		default:
			throw new IllegalArgumentException("encoding indication of public key point is unknown (must be 0x01..04)");
		}
		
		// the following lines only work for uncompressed point encoding!
		
		int lengthOfCoordinates = (rawKeyPlain.length - 1)/2;
		byte[] pointXplain = Arrays.copyOfRange(rawKeyPlain, 1, 1 + lengthOfCoordinates);
		byte[] pointYplain = Arrays.copyOfRange(rawKeyPlain, 1 + lengthOfCoordinates, rawKeyPlain.length);
		
		log(DomainParameterSetEcdh.class, "byte array x coordinate of public key EC point is: " + HexString.encode(pointXplain), TRACE);
		log(DomainParameterSetEcdh.class, "byte array y coordinate of public key EC point is: " + HexString.encode(pointYplain), TRACE);
		
		BigInteger publicPointX = new BigInteger(1, pointXplain);
		BigInteger publicPointY = new BigInteger(1, pointYplain);
		
		log(DomainParameterSetEcdh.class, "x coordinate of public key EC point is: " + publicPointX, TRACE);
		log(DomainParameterSetEcdh.class, "y coordinate of public key EC point is: " + publicPointY, TRACE);
		
		return new ECPoint(publicPointX, publicPointY);
	}
	
	
	@Override
	public ECPublicKey reconstructPublicKey(byte[] rawKeyPlain) {
		int l = getPublicPointReferenceLengthL();
		log(getClass(), "reference length l is: " + l + " bytes", TRACE);
		
		int expectedRawKeyLength = (2*l) + 1;
		if(rawKeyPlain.length != expectedRawKeyLength) {
			throw new IllegalArgumentException("public key data length mismatches expected length of " + expectedRawKeyLength + " bytes according to domain parameters");
		}
		
		ECPoint point = reconstructPoint(rawKeyPlain);
		
		boolean pointOnCurve = isOnCurve(point);
		
		if(pointOnCurve) {
			log(this.getClass(), "EC point is on curve", TRACE);
		} else{
			log(this.getClass(), "EC point is NOT on curve", ERROR);
			throw new IllegalArgumentException("public key data does not represent a point on the used curve");
		}
		
		KeySpec reconstructedPublicKeySpec = new ECPublicKeySpec(point, ecParameterSpec);
		
		PublicKey mappedPublicKey;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(getKeyAgreementAlgorithm(), Crypto.getCryptoProvider());
			mappedPublicKey = keyFactory.generatePublic(reconstructedPublicKeySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("invalid key agreement algorithm");
		} catch (InvalidKeySpecException e) {
			throw new IllegalArgumentException("invalid public ECDH key");
		}
		
		return (ECPublicKey) mappedPublicKey;
	}
	
	@Override
	public ECPrivateKey reconstructPrivateKey(byte[] rawKeyPlain) {
		byte[] prime = Utils.toUnsignedByteArray(this.getPrime());
		
		if(rawKeyPlain.length != prime.length) {
			throw new IllegalArgumentException("public key data length mismatches expected length of " + rawKeyPlain.length + " bytes according to domain parameters");
		}
		
		BigInteger privateS = new BigInteger(1, rawKeyPlain);
		
		KeySpec reconstructedPrivateKeySpec = new ECPrivateKeySpec(privateS, ecParameterSpec);
		
		PrivateKey privateKey;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(getKeyAgreementAlgorithm(), Crypto.getCryptoProvider());
			privateKey = keyFactory.generatePrivate(reconstructedPrivateKeySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("invalid key agreement algorithm");
		} catch (InvalidKeySpecException e) {
			throw new IllegalArgumentException("invalid public ECDH key");
		}
		
		return (ECPrivateKey) privateKey;
	}
	
	@Override
	public byte[] encodePublicKey(PublicKey publicKey) {
		// This method returns an EC point encoding according to ANSI X9.62 uncompressed mode
		
		ECPublicKey ecPublicKey;
		
		if(publicKey instanceof ECPublicKey) {
			ecPublicKey = (ECPublicKey) publicKey;
		} else{
			throw new IllegalArgumentException("invalid public ECDH key");
		}
		
		return CryptoUtil.encode(ecPublicKey.getW(), getPublicPointReferenceLengthL(((ECFieldFp) ecPublicKey.getParams().getCurve().getField()).getP()), CryptoUtil.ENCODING_UNCOMPRESSED);
	}
	
	@Override
	public TlvTag getAuthenticationTokenPublicKeyTag() {
		return TAG_86;
	}
	
	/**
	 * This method returns a copy of this object that has been mapped according to the provided generator.
	 * @param gMapped the new generator to be used
	 * @return a copy of this object that has been mapped according to the provided generator
	 */
	public DomainParameterSetEcdh getUpdatedDomainParameterSet(ECPoint gMapped) {
		return new DomainParameterSetEcdh(ecParameterSpec.getCurve(), gMapped, getOrder(), ecParameterSpec.getCofactor());
	}

	@Override
	public byte[] comp(PublicKey publicKey) {
		ECPublicKey ecPublicKey;
		
		if(publicKey instanceof ECPublicKey) {
			ecPublicKey = (ECPublicKey) publicKey;
		} else{
			throw new IllegalArgumentException("public key must be an EC public key");
		}
		
		ECPoint publicPoint = ecPublicKey.getW();
		
		BigInteger publicPointX = publicPoint.getAffineX();
		
		
		
		
		ECField field = ecPublicKey.getParams().getCurve().getField();
		if(field instanceof ECFieldFp){
			ECFieldFp fieldFp = (ECFieldFp) field;

			int expectedLength = (int) Math.ceil(Utils.logarithm(fieldFp.getP().doubleValue(), 256));
			byte [] result = Utils.toUnsignedByteArray(publicPointX);
			
			if (result.length < expectedLength){
				byte [] padding = new byte [expectedLength - result.length];
				result = Utils.concatByteArrays(padding, result);
			} 
			return result;
		}
		
		return null;
	}

	@Override
	public BigInteger getOrder() {
		return ecParameterSpec.getOrder();
	}

	/**
	 * This method returns the cofactor.
	 * @return the cofactor
	 */
	public int getCofactor() {
		return ecParameterSpec.getCofactor();
	}

	/**
	 * This method returns the elliptic curve that this parameter defines.
	 * @return the elliptic curve that this parameter defines
	 */
	public EllipticCurve getCurve() {
		return ecParameterSpec.getCurve();
	}

	/**
	 * This method returns the generator which is also known as the base point.
	 * @return the generator which is also known as the base point
	 */
	public ECPoint getGenerator() {
		return ecParameterSpec.getGenerator();
	}

	@Override
	public ECParameterSpec getKeySpec() {
		return ecParameterSpec;
	}

	@Override
	public ConstructedTlvDataObject getAlgorithmIdentifier() {
		
		PrimitiveTlvDataObject version = new PrimitiveTlvDataObject(TlvConstants.TAG_INTEGER, new byte[] {0x01});
		
		ConstructedTlvDataObject fieldId = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE);
		fieldId.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_OID, id_primeField));
		fieldId.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_INTEGER, getPrime().toByteArray()));
				
		ConstructedTlvDataObject curve = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE);
		curve.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_OCTET_STRING, getCurve().getA().toByteArray()));
		curve.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_OCTET_STRING, getCurve().getB().toByteArray()));
		
		PrimitiveTlvDataObject base = new PrimitiveTlvDataObject(TlvConstants.TAG_OCTET_STRING, CryptoUtil.encode(getGenerator(), getPublicPointReferenceLengthL(getPrime()), CryptoUtil.ENCODING_UNCOMPRESSED));
		PrimitiveTlvDataObject order = new PrimitiveTlvDataObject(TlvConstants.TAG_INTEGER, getOrder().toByteArray());
		PrimitiveTlvDataObject cofactor = new PrimitiveTlvDataObject(TlvConstants.TAG_INTEGER, BigInteger.valueOf(getCofactor()).toByteArray());
		
		ConstructedTlvDataObject params = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE);
		params.addTlvDataObject(version);
		params.addTlvDataObject(fieldId);
    	params.addTlvDataObject(curve);
    	params.addTlvDataObject(base);
    	params.addTlvDataObject(order);
    	params.addTlvDataObject(cofactor);
			    
	    ConstructedTlvDataObject retVal = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE);
		retVal.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_OID, id_ecPublicKey));
		retVal.addTlvDataObject(params);
		
		return retVal;
	}

	@Override
	public int hashCode() {
		//implement hashCode() and equals based on the byte[] representation of getAlgorithmIdentifier
		return getAlgorithmIdentifier().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DomainParameterSetEcdh other = (DomainParameterSetEcdh) obj;
		return getAlgorithmIdentifier().equals(other.getAlgorithmIdentifier());
	}
	
	@Override
	public ECParameterSpec getAlgorithmParameterSpec() {
		return ecParameterSpec;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		EllipticCurve curve = getCurve();
		BigInteger a = curve.getA();
		BigInteger b = curve.getB();
		BigInteger p = getPrime();
		BigInteger order = getOrder();
		int coFactor = getCofactor();
		ECPoint generator = getGenerator();
		int referenceLength = DomainParameterSetEcdh.getPublicPointReferenceLengthL(p);
		
		sb.append("************ elliptic curve domain parameters ************");
		sb.append("\nCurve parameter A : " + HexString.encode(a));
		sb.append("\nCurve parameter B : " + HexString.encode(b));
		sb.append("\nPrime field p     : " + HexString.encode(p));
		sb.append("\nOrder of generator: " + HexString.encode(order));
		sb.append("\nCo-factor h       : " + coFactor);
		sb.append("\nGenerator G       : " + HexString.encode(CryptoUtil.encode(generator, referenceLength, CryptoUtil.ENCODING_HYBRID)));
		sb.append("\nGenerator G.x     : " + "  " + HexString.encode(generator.getAffineX()));
		
		char[] array = new char[(2*referenceLength) + 2];
	    Arrays.fill(array, ' ');
	    String padding = new String(array);
		
	    sb.append("\nGenerator G.y     : " + padding + HexString.encode(generator.getAffineY()));
		sb.append("\n**********************************************************");
		
		return sb.toString();
	}
	
}
