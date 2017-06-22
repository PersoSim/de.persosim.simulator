package de.persosim.simulator.crypto;

import static org.globaltester.logging.BasicLogger.log;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
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

import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.logging.tags.LogLevel;
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
	 * 
	 * @param p prime p specifying the base field
	 * @param a coefficient A defining the curve
	 * @param b coefficient B defining the curve
	 * @param x the affine x-coordinate of the group generator
	 * @param y the affine y-coordinate of the group generator
	 * @param n the group order
	 * @param h the co-factor
	 */
	public DomainParameterSetEcdh(BigInteger p, BigInteger a, BigInteger b, BigInteger x, BigInteger y, BigInteger n, int h) {
		this(new EllipticCurve(new ECFieldFp(p), a, b), new ECPoint(x, y), n, h);
	}
	
	/**
	 * Constructor for constructing a {@link DomainParameterSetEcdh} object
	 * @param curve the elliptic curve
	 * @param g the group generator
	 * @param n the group order
	 * @param h the co-factor
	 */
	public DomainParameterSetEcdh(EllipticCurve curve, ECPoint g, BigInteger n, int h) {
		this(new ECParameterSpec(curve, g,n, h));
	}
	
	/**
	 * Constructor for constructing a {@link DomainParameterSetEcdh} object from {@link ECParameterSpec}
	 * @param ecParameterSpec the {@link ECParameterSpec} to import
	 */
	public DomainParameterSetEcdh(ECParameterSpec ecParameterSpec) {
		this.ecParameterSpec = ecParameterSpec;
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
			log(getClass(), "point not on curve - x: " + HexString.encode(x), LogLevel.DEBUG);
			log(getClass(), "point not on curve - y: " + HexString.encode(y), LogLevel.DEBUG);
			log(getClass(), "point not on curve - p: " + HexString.encode(p), LogLevel.DEBUG);
			log(getClass(), "point not on curve - " + HexString.encode(leftSide) + " != " + HexString.encode(rightSide), LogLevel.DEBUG);
			log(getClass(), "point not on curve - x: " + x, LogLevel.DEBUG);
			log(getClass(), "point not on curve - y: " + y, LogLevel.DEBUG);
			log(getClass(), "point not on curve - p: " + p, LogLevel.DEBUG);
			log(getClass(), "point not on curve - " + leftSide + " != " + rightSide, LogLevel.DEBUG);
		}
		
		return result;
	}
	
	/**
	 * Computes reference length l in bytes used for Point-to-Octet-String Conversion according to ANSI X9.62 chapter 4.3.6.
	 * @return reference length l
	 */
	public int getPublicPointReferenceLengthL() {
		return CryptoUtil.getPublicPointReferenceLengthL(getPrime());
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
		
		log(getClass(), "ECDH ephemeral private key d is                    : " + HexString.encode(piccPrivateKeyD), LogLevel.DEBUG);
		log(getClass(), "ECDH ephemeral public point w.x under unmapped g is: " + HexString.encode(publicPointWx), LogLevel.DEBUG);
		log(getClass(), "ECDH ephemeral public point w.y under unmapped g is: " + HexString.encode(publicPointWy), LogLevel.DEBUG);
		log(getClass(), "ECDH ephemeral public point w.x under mapped g is  : " + HexString.encode(publicPointWmappedX), LogLevel.DEBUG);
		log(getClass(), "ECDH ephemeral public point w.y under mapped g is  : " + HexString.encode(publicPointWmappedY), LogLevel.DEBUG);
		log(getClass(), "ECDH curve's first coefficient A is                : " + HexString.encode(ecFirstCoefficientA), LogLevel.DEBUG);
		log(getClass(), "ECDH curve's second coefficient B is               : " + HexString.encode(ecSecondCoefficientB), LogLevel.DEBUG);
		log(getClass(), "ECDH original generator g.x of group G is          : " + HexString.encode(gUnmappedX), LogLevel.DEBUG);
		log(getClass(), "ECDH original generator g.y of group G is          : " + HexString.encode(gUnmappedY), LogLevel.DEBUG);
		log(getClass(), "ECDH mapped generator g.x of group G is            : " + HexString.encode(gMappedX), LogLevel.DEBUG);
		log(getClass(), "ECDH mapped generator g.y of group G is            : " + HexString.encode(gMappedY), LogLevel.DEBUG);
		log(getClass(), "ECDH prime modulus p of group G is                 : " + HexString.encode(ecFp), LogLevel.DEBUG);
		log(getClass(), "ECDH order of group G is                           : " + HexString.encode(order), LogLevel.DEBUG);
		log(getClass(), "ECDH cofactor is                                   : " + coFactor, LogLevel.DEBUG);
		
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
		
		log(DomainParameterSetEcdh.class, "raw public key EC point byte array is: " + HexString.encode(rawKeyPlain), LogLevel.TRACE);
		
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
			log(DomainParameterSetEcdh.class, "leading byte 0x04 of public key point indicates uncompressed encoding", LogLevel.TRACE);
			break;
		default:
			throw new IllegalArgumentException("encoding indication of public key point is unknown (must be 0x01..04)");
		}
		
		// the following lines only work for uncompressed point encoding!
		
		int lengthOfCoordinates = (rawKeyPlain.length - 1)/2;
		byte[] pointXplain = Arrays.copyOfRange(rawKeyPlain, 1, 1 + lengthOfCoordinates);
		byte[] pointYplain = Arrays.copyOfRange(rawKeyPlain, 1 + lengthOfCoordinates, rawKeyPlain.length);
		
		log(DomainParameterSetEcdh.class, "byte array x coordinate of public key EC point is: " + HexString.encode(pointXplain), LogLevel.TRACE);
		log(DomainParameterSetEcdh.class, "byte array y coordinate of public key EC point is: " + HexString.encode(pointYplain), LogLevel.TRACE);
		
		BigInteger publicPointX = new BigInteger(1, pointXplain);
		BigInteger publicPointY = new BigInteger(1, pointYplain);
		
		log(DomainParameterSetEcdh.class, "x coordinate of public key EC point is: " + publicPointX, LogLevel.TRACE);
		log(DomainParameterSetEcdh.class, "y coordinate of public key EC point is: " + publicPointY, LogLevel.TRACE);
		
		return new ECPoint(publicPointX, publicPointY);
	}
	
	@Override
	public ECPublicKey reconstructPublicKey(byte[] rawKeyPlain) {
		return reconstructPublicKey(rawKeyPlain, Crypto.getCryptoProvider());
	}
	
	public ECPublicKey reconstructPublicKey(byte[] rawKeyPlain, String providerString) {
		Provider cryptoProvider = Security.getProvider(providerString);
		return reconstructPublicKey(rawKeyPlain, cryptoProvider);
	}
	
	/**
	 * This method reconstructs an {@link ECPublicKey} from its public point raw encoding
	 * @param rawKeyPlain the public point encoding
	 * @param cryptoProvider the crypto provider to use
	 * @return the reconstructed {@link ECPublicKey}
	 */
	public ECPublicKey reconstructPublicKey(byte[] rawKeyPlain, Provider cryptoProvider) {
		int l = getPublicPointReferenceLengthL();
		log(getClass(), "reference length l is: " + l + " bytes", LogLevel.TRACE);
		
		int expectedRawKeyLength = (2*l) + 1;
		if(rawKeyPlain.length != expectedRawKeyLength) {
			throw new IllegalArgumentException("public key data length mismatches expected length of " + expectedRawKeyLength + " bytes according to domain parameters");
		}
		
		ECPoint point = reconstructPoint(rawKeyPlain);
		
		return reconstructPublicKey(point, cryptoProvider);
	}
	
	/**
	 * This method reconstructs an {@link ECPublicKey} from its public point
	 * @param point the public point
	 * @param cryptoProvider the crypto provider to use
	 * @return the reconstructed {@link ECPublicKey}
	 */
	public ECPublicKey reconstructPublicKey(ECPoint point, Provider cryptoProvider) {
		boolean pointOnCurve = isOnCurve(point);
		
		if(pointOnCurve) {
			log(this.getClass(), "EC point is on curve", LogLevel.TRACE);
		} else{
			log(this.getClass(), "EC point is NOT on curve", LogLevel.ERROR);
			throw new IllegalArgumentException("public key data does not represent a point on the used curve");
		}
		
		KeySpec reconstructedPublicKeySpec = new ECPublicKeySpec(point, ecParameterSpec);
		
		PublicKey mappedPublicKey;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(getKeyAgreementAlgorithm(), cryptoProvider);
			mappedPublicKey = keyFactory.generatePublic(reconstructedPublicKeySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("invalid key agreement algorithm");
		} catch (InvalidKeySpecException e) {
			throw new IllegalArgumentException("invalid public ECDH key");
		}
		
		return (ECPublicKey) mappedPublicKey;
	}
	
	public ECPrivateKey reconstructPrivateKey(BigInteger privateS) {
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
	public ECPrivateKey reconstructPrivateKey(byte[] rawKeyPlain) {
		BigInteger privateS = new BigInteger(1, rawKeyPlain);
		
		if(privateS.compareTo(this.getPrime()) >= 0) {
			throw new IllegalArgumentException("private key is greater than or equal to prime");
		}
		
		return reconstructPrivateKey(privateS);
	}
	
	/**
	 * This method returns an encoding of the provided public key according to the also provided encoding.
	 * See {@link CryptoUtil#encode(ECPoint, int, byte)}} for the supported encodings
	 * @param publicKey the public key to encode
	 * @param encoding the encoding
	 * @return the public key encoding
	 */
	public byte[] encodePublicKey(PublicKey publicKey, byte encoding) {
		ECPublicKey ecPublicKey;
		
		if(publicKey instanceof ECPublicKey) {
			ecPublicKey = (ECPublicKey) publicKey;
		} else{
			throw new IllegalArgumentException("invalid public ECDH key");
		}
		
		return encodePoint(ecPublicKey.getW(), encoding);
	}
	
	@Override
	public byte[] encodePrivateKey(PrivateKey privateKey) {
		if(privateKey instanceof ECPrivateKey) {
			return Utils.toUnsignedByteArray(((ECPrivateKey) privateKey).getS());
		} else{
			throw new IllegalArgumentException("key pair must be ECDH");
		}
	}
	
	@Override
	public byte[] encodePublicKey(PublicKey publicKey) {
		// This method returns an EC point encoding according to ANSI X9.62 uncompressed mode
		return encodePublicKey(publicKey, CryptoUtil.ENCODING_UNCOMPRESSED);
	}
	
	/**
	 * This method returns an encoding of a provided {@link ECPoint} according to the domain parameters specified by this object.
	 * See {@link CryptoUtil#encode(ECPoint, int, byte)} for the supported encodings.
	 * @param ecPoint the point to be encoded
	 * @param encoding the encoding to be used
	 * @return an encoding of a provided {@link ECPoint} according to the domain parameters specified by this object
	 */
	public byte[] encodePoint(ECPoint ecPoint, byte encoding) {
		/*
		 * This check is necessary to ensure that shorter point encodings from
		 * another ec domain parameter set will also be rejected and not
		 * processed unnoticed.
		 */
		if(!isOnCurve(ecPoint)) {
			return null;
		}
		
		int publicPointReferenceLength = getPublicPointReferenceLengthL();
		
		return CryptoUtil.encode(ecPoint, publicPointReferenceLength, encoding);
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
		
		return CryptoUtil.compressEcPublicKey(ecPublicKey);
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
		
		PrimitiveTlvDataObject base = new PrimitiveTlvDataObject(TlvConstants.TAG_OCTET_STRING, CryptoUtil.encode(getGenerator(), getPublicPointReferenceLengthL(), CryptoUtil.ENCODING_UNCOMPRESSED));
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
		StringBuilder sb = new StringBuilder();
		
		EllipticCurve curve = getCurve();
		BigInteger a = curve.getA();
		BigInteger b = curve.getB();
		BigInteger p = getPrime();
		BigInteger order = getOrder();
		int coFactor = getCofactor();
		ECPoint generator = getGenerator();
		int referenceLength = CryptoUtil.getPublicPointReferenceLengthL(p);
		
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
	
	/**
	 * This method performs a manual ECDH key agreement that returns a complete
	 * EC point as its result. The key agreement provided by Bouncy Castle via
	 * the Java crypto API only returns the x-coordinate of the expected EC
	 * point and reconstructing the y-coordinate is complicated by the ambiguity
	 * of the coordinate.
	 * 
	 * @param domainParameterSetEcdh
	 *            the domain parameters to be used
	 * @param ecPublicKey
	 *            the public key to use
	 * @param ecPrivateKey
	 *            the private key to use
	 * @return
	 */
	public ECPoint performEcdhKeyAgreement(ECPublicKey ecPublicKey, ECPrivateKey ecPrivateKey) {
		ECPoint secretPoint = CryptoUtil.scalarPointMultiplication(getCurve(), getOrder(), ecPublicKey.getW(), ecPrivateKey.getS());
		
		log(CryptoUtil.class, "result H of ECDH key agreement is", LogLevel.TRACE);
		log(CryptoUtil.class, "H.x: " + HexString.encode(secretPoint.getAffineX()), LogLevel.TRACE);
		log(CryptoUtil.class, "H.y: " + HexString.encode(secretPoint.getAffineY()), LogLevel.TRACE);
		
		return secretPoint;
	}
	
	/**
	 * This method returns a projection of the provided point's selected coordinate.
	 * If the length of the selected encoded coordinate is less than the provided reference length, it is padded to this length.
	 * @param ecPoint the point to work on
	 * @param encodeX true: encode x-coordinate, false: encode y-coordinate
	 * @return the projection of the provided point's selected coordinate
	 */
	public byte[] getProjectedRepresentation(ECPoint ecPoint, boolean encodeX) {
		return CryptoUtil.getProjectedRepresentation(ecPoint, getPublicPointReferenceLengthL(), encodeX);
	}
	
	/**
	 * This method returns a projection of the provided point's x-coordinate.
	 * If the length of the encoded x-coordinate is less than the provided reference length, it is padded to this length.
	 * @param ecPoint the point to work on
	 * @return the projection of the provided point's x-coordinate
	 */
	public byte[] getProjectedRepresentation(ECPoint ecPoint) {
		return CryptoUtil.getProjectedRepresentation(ecPoint, getPublicPointReferenceLengthL(), true);
	}
	
	/**
	 * This method returns a public {@link ECPoint} matching the provided private {@link BigInteger} s
	 * @param privateS the private component
	 * @return the public component
	 */
	public ECPoint computePublicPoint(BigInteger privateS) {
		return CryptoUtil.scalarPointMultiplication(getCurve(), getGenerator(), privateS);
	}
	
	/**
	 * This method returns the {@link ECPublicKey} matching the provided {@link ECPrivateKey}.
	 * @param ecPrivateKey the private key to use
	 * @param cryptoProvider the crypto provider to use
	 * @return the public key matching the provided private key
	 */
	public ECPublicKey computePublicKey(ECPrivateKey ecPrivateKey, Provider cryptoProvider) {
		ECPoint publicPoint = computePublicPoint(ecPrivateKey.getS());
		return reconstructPublicKey(publicPoint, cryptoProvider);
	}
	
}
