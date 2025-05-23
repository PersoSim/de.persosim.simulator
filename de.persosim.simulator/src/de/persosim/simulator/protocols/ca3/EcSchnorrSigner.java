package de.persosim.simulator.protocols.ca3;

import static org.globaltester.logging.BasicLogger.log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Random;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.Tr03111Utils;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class provides functionality for creating and verifying ECSchnorr
 * signatures as described in TR-03111.
 * 
 * @author slutters
 *
 */
public class EcSchnorrSigner {
	
	protected DomainParameterSetEcdh domParams;
	protected BigInteger privateKey;
	protected ECPoint publicKey;
	protected MessageDigest messageDigest;
	
	/**
	 * This constructor constructs an object of {@link EcSchnorrSigner}
	 * @param domParams the domain parameters to be used
	 * @param privateKey the private key component
	 * @param messageDigestName the message digest to be used
	 */
	public EcSchnorrSigner(DomainParameterSetEcdh domParams, BigInteger privateKey, MessageDigest messageDigest) {
		this.domParams = domParams;
		this.privateKey = privateKey;
		this.messageDigest = messageDigest;
		
		publicKey = CryptoUtil.scalarPointMultiplication(domParams.getCurve(), domParams.getOrder(), domParams.getGenerator(), this.privateKey);
	}
	
	/**
	 * This method generates a signature pair {r, s} for the provided parameters
	 * @param message the message to be signed
	 * @param k the random variable
	 * @return the signature pair {r, s}
	 */
	public BigInteger[] generateUncheckedSignature(byte[] message, BigInteger k) {
    	ECPoint pointGenerator = domParams.getGenerator();
    	
    	log(getClass(), "random v is: " +  HexString.encode(k.toByteArray()));
    	
    	// Compute Q = k*G
    	ECPoint pointQ = CryptoUtil.scalarPointMultiplication(domParams.getCurve(), domParams.getOrder(), pointGenerator, k);
    	
    	BigInteger r = performHash(messageDigest, domParams, pointQ, message);
    	
    	log(getClass(), "hash is: " + r + " (" + HexString.encode(r) + ")");
    	
    	BigInteger n = domParams.getOrder();
    	BigInteger s = (k.subtract(privateKey.multiply(r))).mod(n); // s = k-r*dA mod n
    	
    	log(getClass(), "r is: " + r);
    	log(getClass(), "s is: " + s);
    	
    	return new BigInteger[] {r, s};
    }
	
	/**
	 * This method checks whether the provided signature is plausible from a cryptographic perspective.
	 * It does not verify the signature in any way.
	 * @param domParams the domain parameters to be used
	 * @param signature the signature to be checked
	 * @return true if the signature is plausible, false otherwise
	 */
	public static boolean isSignatureOk(DomainParameterSetEcdh domParams, BigInteger[] signature) {
		BigInteger r, s;
		
		r = signature[0];
		s = signature[1];
		
		if((r.mod(domParams.getOrder())).compareTo(BigInteger.ZERO) == 0) {
    		return false;
    	}
		
		if(s.compareTo(BigInteger.ZERO) == 0) {
    		return false;
    	}
		
		return true;
	}
	
	/**
	 * This method generates a signature for the provided message
	 * @param message the message to be signed
	 * @return the signature pair {r, s}
	 */
	public BigInteger[] generateSignature(byte[] message) {
		BigInteger kRandom;
		BigInteger[] signature;
		boolean signatureFailed;
		
		do {
			// Generate a random v from [1, n-1]
			kRandom = createRandomInRange(BigInteger.ONE, domParams.getOrder().subtract(BigInteger.ONE), new SecureRandom());
			signature = generateUncheckedSignature(message, kRandom);
			signatureFailed = !isSignatureOk(domParams, signature);
		} while(signatureFailed);
    	
		return signature;
	}
	
	/**
	 * This method generates a random BigInteger within a specified range
	 * @param start the lowest value that is possibly returned
	 * @param end the highest value that is possibly returned
	 * @param rand the source of randomness
	 * @return a random BigInteger within a specified range
	 */
	public static BigInteger createRandomInRange(BigInteger start, BigInteger end, Random rand) {
		if(start.compareTo(end) > 0) {
			throw new IllegalArgumentException("lowest value must be lower than highest value");
		}
		BigInteger r;
		BigInteger diff = (end.subtract(start)).add(BigInteger.ONE);
		
		do {
		    r = new BigInteger(diff.bitLength(), rand);
		    r = r.add(start);
		} while (r.compareTo(end) > 0);
		
		return r;
	}
	
	/**
	 * This method generates a private key that can be used to sign messages
	 * @param domainParameters the domain parameters to be used
	 * @return the private key
	 */
	public static BigInteger generatePrivateKey(DomainParameterSetEcdh domainParameters) {
		// Generate a private key from [1, n-1]
    	BigInteger privateKey = createRandomInRange(BigInteger.ONE, domainParameters.getOrder().subtract(BigInteger.ONE), new SecureRandom());
    	
    	return privateKey;
	}
	
	/**
	 * This method generates a public key matching the provided private key
	 * @param domainParameters the domain parameters to be used
	 * @param privateKey the private key for which the public key is to be created
	 * @return the public key matching the provided private key
	 */
	public static ECPoint generatePublicKey(DomainParameterSetEcdh domainParameters, BigInteger privateKey) {
		if((privateKey.compareTo(BigInteger.ZERO) <= 0) ||(privateKey.compareTo(domainParameters.getOrder()) >= 0)) {
			throw new IllegalArgumentException("private key too small or too large");
		}
		
		EllipticCurve curve = domainParameters.getCurve();
		ECPoint generator = domainParameters.getGenerator();
		ECPoint publicKey = CryptoUtil.scalarPointMultiplication(curve, domainParameters.getOrder(), generator, privateKey);
		
		return publicKey;
	}
	
	/**
	 * This method creates a hash over the provided parameters
	 * @param messageDigestName the message digest to be used
	 * @param domParams the domain parameters to be used
	 * @param pointQ the mapped random point V
	 * @param message the message to be hashed
	 * @return the hash value
	 */
	public static BigInteger performHash(MessageDigest messageDigest, DomainParameterSetEcdh domParams, ECPoint pointQ, byte[] message) {
    	byte[] xQ = Tr03111Utils.fe2os(pointQ.getAffineX(), domParams.getPrime());
    	byte[] hashInput = Utils.concatByteArrays(message, xQ);
    	
    	
    	byte[] hashH;
    	try {
    		
    		log(EcSchnorrSigner.class, "message is   : " + HexString.encode(message));
    		log(EcSchnorrSigner.class, "xQ is        : " + HexString.encode(xQ));
    		log(EcSchnorrSigner.class, "hash input is: " + HexString.encode(hashInput));
    		messageDigest.update(hashInput);
    		hashH = messageDigest.digest();
    		log(EcSchnorrSigner.class, "hash is: " + HexString.encode(hashH));
    	} catch (Exception e) {
    		e.printStackTrace();
    		hashH = new byte[0];
    	}
    	
    	BigInteger r = new BigInteger(1, hashH);
    	log(EcSchnorrSigner.class, "hash as BigInteger is: " + r);
    	
    	return r;
    }
	
	/**
	 * This method verifies an ECSchnorr signature according to the provided parameters
	 * @param ecSchnorrSignature the signature to be verified
	 * @param publicKey the public key to be used for verification
	 * @param message the message
	 * @param messageDigestName the message digest to be used
	 * @return true if signature has been successfully verified, false otherwise
	 */
	public static boolean verifySignature(BigInteger[] ecSchnorrSignature, ECPoint publicKey, DomainParameterSetEcdh domainParameters, byte[] message, MessageDigest messageDigest) {
    	ECPoint generator = domainParameters.getGenerator();
    	BigInteger prime = domainParameters.getPrime();
    	EllipticCurve curve = domainParameters.getCurve();
    	BigInteger order = domainParameters.getOrder();
    	
    	BigInteger r = ecSchnorrSignature[0];
    	BigInteger s = ecSchnorrSignature[1];
    	
    	
    	
    	// perform general checks on public key
    	
    	// Check that public key != infinity
    	if (publicKey.equals(ECPoint.POINT_INFINITY)){
    		return false;
    	}
    	
    	// Check that x and y coordinates of the public key are in F_q, i.e., x, y in [0, q-1]
    	if (publicKey.getAffineX().compareTo(BigInteger.ZERO) == -1 ||
    			publicKey.getAffineX().compareTo(prime.subtract(BigInteger.ONE)) == 1 ||
    			publicKey.getAffineY().compareTo(BigInteger.ZERO) == -1 ||
    			publicKey.getAffineY().compareTo(prime.subtract(BigInteger.ONE)) == 1) {
    		return false;
    	}
    	
    	// check that public key lies on the curve
    	boolean isOnCurve = domainParameters.isOnCurve(publicKey);
    	log(EcSchnorrSigner.class, "point is on curve: " + isOnCurve);
    	if(!isOnCurve) {
    		return false;
    	}
    	
    	
    	
    	// perform checks on signature
    	
    	BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
    	if(((r.compareTo(BigInteger.ZERO)) < 0) || ((s.compareTo(two.pow(domainParameters.getPublicPointReferenceLengthL()*8))) >= 0)) {
    		return false;
    	}
    	
    	if(((s.compareTo(BigInteger.ZERO)) <= 0) || ((s.compareTo(domainParameters.getOrder())) >= 0)) {
    		return false;
    	}
    	
    	ECPoint sG = CryptoUtil.scalarPointMultiplication(curve, order, generator, s);
    	ECPoint rPa = CryptoUtil.scalarPointMultiplication(curve, order, publicKey, r);
    	ECPoint q = CryptoUtil.addPoint(domainParameters.getCurve(), sG, rPa);
    	
    	// check that Q != infinity.
    	if (q.equals(ECPoint.POINT_INFINITY)) { 
    		return false;
    	}
    	
    	BigInteger v = performHash(messageDigest, domainParameters, q, message);
    	boolean verified = v.compareTo(r) == 0;
    	log(EcSchnorrSigner.class, "signature verified: " + verified);
    	
    	return verified;
    }

	public DomainParameterSetEcdh getDomParams() {
		return domParams;
	}
	
}
