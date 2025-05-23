package de.persosim.simulator.protocols.ca3;

import static org.globaltester.logging.BasicLogger.log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.spec.ECPoint;
import java.util.Random;

import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class can be used to calculate pseudonymous signatures based on the
 * ECSchnorr algorithm as described in TR-03110 v2.2 Part 2 3.1.6.2</br>
 * 
 * @author mboonk
 *
 */
public class PsSigner{
	
	private ECPoint groupManagerPublicKey;
	private DomainParameterSetEcdh groupManagerDomainParameters;
	private MessageDigest messageDigest;
	private Random random;
	
	/**
	 * This creates a signer object ready to be used. It will be initialized
	 * using the given group manager parameters and helper objects.
	 * 
	 * @param groupManagerDomainParameters
	 *            the domain parameters used for calculations
	 * @param groupManagerPublicKey
	 *            the public of the group manager
	 * @param messageDigest
	 *            the message digest to use during witness creation
	 * @param random
	 *            the source of randomness to use
	 */
	public PsSigner(DomainParameterSetEcdh groupManagerDomainParameters, ECPoint groupManagerPublicKey, MessageDigest messageDigest, Random random) {
		this.messageDigest = messageDigest;
		this.groupManagerDomainParameters = groupManagerDomainParameters;
		this.groupManagerPublicKey = groupManagerPublicKey;
		this.random = random;
	}

	/**
	 * This computes an anonymous signature as described in TR-03110 v2.2 Part 2
	 * 3.1.6.2</br>
	 * 
	 * @param sectorSpecificIdentifierIcc1
	 *            the first sector identifier calculated previously or null if not to be included
	 * @param sectorSpecificIdentifierIcc2
	 *            the second sector identifier calculated previously or null if not to be included
	 * @param privateStaticKeyIcc1
	 *            the first private ICC key
	 * @param privateStaticKeyIcc2
	 *            the second private ICC key
	 * @param sectorPublicKey
	 *            the sector public key of the terminal
	 * @param idDsi
	 *            the {@link Oid} to be used in the signature
	 * @param message
	 *            the message to sign
	 * @return the signature result
	 */
	public PsSignature sign( ECPoint sectorSpecificIdentifierIcc1, ECPoint sectorSpecificIdentifierIcc2, BigInteger privateStaticKeyIcc1, BigInteger privateStaticKeyIcc2, ECPoint sectorPublicKey, Oid idDsi, byte [] message){
		log(getClass(), "PsSigner signing with", LogLevel.DEBUG);
		
		log(getClass(), "group manager domain parameters :\n" + groupManagerDomainParameters, LogLevel.DEBUG);
		log(getClass(), "group manager public key        : " + HexString.encode(groupManagerDomainParameters.encodePoint(groupManagerPublicKey, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		log(getClass(), "message digest                  : " + messageDigest.getAlgorithm(), LogLevel.DEBUG);
		
		if(sectorSpecificIdentifierIcc1 != null) {
			log(getClass(), "sector specific identifier icc 1: " + HexString.encode(groupManagerDomainParameters.encodePoint(sectorSpecificIdentifierIcc1, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		} else{
			log(getClass(), "no sector specific identifier icc 1 provided", LogLevel.DEBUG);
		}
		
		if(sectorSpecificIdentifierIcc2 != null) {
			log(getClass(), "sector specific identifier icc 2: " + HexString.encode(groupManagerDomainParameters.encodePoint(sectorSpecificIdentifierIcc2, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		} else{
			log(getClass(), "no sector specific identifier icc 2 provided", LogLevel.DEBUG);
		}
		
		log(getClass(), "private key icc 1               : " + HexString.encode(Utils.toUnsignedByteArray(privateStaticKeyIcc1)), LogLevel.DEBUG);
		log(getClass(), "private key icc 2               : " + HexString.encode(Utils.toUnsignedByteArray(privateStaticKeyIcc2)), LogLevel.DEBUG);
		log(getClass(), "sector public key               : " + HexString.encode(groupManagerDomainParameters.encodePoint(sectorPublicKey, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		log(getClass(), "OID                             : " + idDsi, LogLevel.DEBUG);
		log(getClass(), "Message                         : " + HexString.encode(message), LogLevel.DEBUG);
		
		ECPoint q1;
		ECPoint a1 = null;
		ECPoint a2 = null;
		BigInteger k1, k2, c, s1, s2;
		
		boolean isInfinity, isZero, continueLoop;
		do{
			k1 = EcSchnorrSigner.createRandomInRange(BigInteger.ZERO, groupManagerDomainParameters.getOrder().subtract(BigInteger.ONE), random);
			k2 = EcSchnorrSigner.createRandomInRange(BigInteger.ZERO, groupManagerDomainParameters.getOrder().subtract(BigInteger.ONE), random);
			
			log(getClass(), "random value k_1                : " + HexString.encode(k1), LogLevel.DEBUG);
			log(getClass(), "random value k_2                : " + HexString.encode(k2), LogLevel.DEBUG);
			
			q1 = calculateQForSigning(k1, k2);
			
			log(getClass(), "q_1 is                          : " + HexString.encode(groupManagerDomainParameters.encodePoint(q1, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
			
			if (sectorSpecificIdentifierIcc1 != null){
				a1 = calculateAforSigning(sectorPublicKey, k1);
				log(getClass(), "a_1 is                          : " + HexString.encode(groupManagerDomainParameters.encodePoint(a1, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
			} else{
				log(getClass(), "sector specific identifier ICC1 not available", LogLevel.DEBUG);
			}
			
			if (sectorSpecificIdentifierIcc2 != null){
				a2 = calculateAforSigning(sectorPublicKey, k2);
				log(getClass(), "a_2 is                          : " + HexString.encode(groupManagerDomainParameters.encodePoint(a2, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
			} else{
				log(getClass(), "sector specific identifier ICC2 not available", LogLevel.DEBUG);
			}
		
			c = computeWitness(q1, sectorSpecificIdentifierIcc1, a1, sectorSpecificIdentifierIcc2, a2, sectorPublicKey, idDsi, message);
			s1 = computeSignatureComponent(c, k1, privateStaticKeyIcc1);
			s2 = computeSignatureComponent(c, k2, privateStaticKeyIcc2);
			
			log(getClass(), "s1 is: " + HexString.encode(s1), LogLevel.DEBUG);
			log(getClass(), "s2 is: " + HexString.encode(s2), LogLevel.DEBUG);
			
			isInfinity = checkForInfinity(q1, a1, a2);
			isZero = checkForZero(c, s1, s2);
			continueLoop = isInfinity || isZero;
			
			if(continueLoop) {
				log(getClass(), "mismatching pair of generated k1 & k2, retrying", LogLevel.DEBUG);
			} else{
				log(getClass(), "found valid combination of k1 & k2", LogLevel.DEBUG);
			}
		} while (continueLoop);
		
		log(getClass(), "k1                              : " + HexString.encode(Utils.toUnsignedByteArray(k1)), LogLevel.DEBUG);
		log(getClass(), "k2                              : " + HexString.encode(Utils.toUnsignedByteArray(k2)), LogLevel.DEBUG);
		
		PsSignature psSignature = new PsSignature(c, s1, s2);
		
		log(getClass(), "Signature is                    : " + psSignature, LogLevel.DEBUG);
		
		return psSignature;
	}
	
	/**
	 * This tries a verification of the given signature as described in TR-03110
	 * v2.2 Part 2 3.1.6.2</br>
	 * 
	 * @param sectorPublicKey
	 *            the terminals sector public key
	 * @param publicKeyIcc
	 *            the public key of the ICC
	 * @param sectorSpecificIdentifierIcc1
	 *            the first sector identifier calculated previously or null if
	 *            not to be included
	 * @param sectorSpecificIdentifierIcc2
	 *            the second sector identifier calculated previously or null if
	 *            not to be included
	 * @param idDsi
	 *            the {@link Oid} that is contained in the given signature
	 * @param message
	 *            the message to verify
	 * @param signature
	 *            the signature to verify
	 * @return true, iff the signature can be verified using the given
	 *         parameters
	 */
	public boolean verify(ECPoint sectorPublicKey, ECPoint publicKeyIcc, ECPoint sectorSpecificIdentifierIcc1, ECPoint sectorSpecificIdentifierIcc2, Oid idDsi, byte[] message, PsSignature signature){
		ECPoint a1 = null, a2 = null, q1;
		
		log(getClass(), "PsSigner verifying with", LogLevel.DEBUG);
		log(getClass(), "group manager domain parameters :\n" + groupManagerDomainParameters.toString(), LogLevel.DEBUG);
		log(getClass(), "group manager public key        : " + HexString.encode(groupManagerDomainParameters.encodePoint(groupManagerPublicKey, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		log(getClass(), "message digest                  : " + messageDigest.getAlgorithm(), LogLevel.DEBUG);
		log(getClass(), "sector public key               : " + HexString.encode(groupManagerDomainParameters.encodePoint(sectorPublicKey, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		log(getClass(), "public key icc                  : " + HexString.encode(groupManagerDomainParameters.encodePoint(publicKeyIcc, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		
		if(sectorSpecificIdentifierIcc1 != null) {
			log(getClass(), "sector specific identifier icc 1: " + HexString.encode(groupManagerDomainParameters.encodePoint(sectorSpecificIdentifierIcc1, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		} else{
			log(getClass(), "no sector specific identifier icc 1 provided", LogLevel.DEBUG);
		}
		
		if(sectorSpecificIdentifierIcc2 != null) {
			log(getClass(), "sector specific identifier icc 2: " + HexString.encode(groupManagerDomainParameters.encodePoint(sectorSpecificIdentifierIcc2, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		} else{
			log(getClass(), "no sector specific identifier icc 2 provided", LogLevel.DEBUG);
		}
		
		log(getClass(), "OID                             : " + idDsi.toString(), LogLevel.DEBUG);
		log(getClass(), "Message                         : " + HexString.encode(message), LogLevel.DEBUG);
		log(getClass(), "Signature is                    : " + signature.toString(), LogLevel.DEBUG);
		
		q1 = calculateQForVerification(publicKeyIcc, signature);
		
		if (sectorSpecificIdentifierIcc1 != null){
			a1 = calculateAforVerification(sectorSpecificIdentifierIcc1, sectorPublicKey, signature.getC(), signature.getS1());
		}
		
		if (sectorSpecificIdentifierIcc2 != null){
			a2 = calculateAforVerification(sectorSpecificIdentifierIcc2, sectorPublicKey, signature.getC(), signature.getS2());
		}
		
		BigInteger v = computeWitness(q1, sectorSpecificIdentifierIcc1, a1, sectorSpecificIdentifierIcc2, a2, sectorPublicKey, idDsi, message);
		BigInteger c = signature.getC();
		
		log(getClass(), "Witness expected                : " + HexString.encode(Utils.toUnsignedByteArray(v)), LogLevel.DEBUG);
		log(getClass(), "Witness received                : " + HexString.encode(Utils.toUnsignedByteArray(c)), LogLevel.DEBUG);
		
		return v.equals(c);
	}

	/**
	 * This computes the Q value for verification as described in TR-03110 v2.2
	 * Part 2 3.1.6.2</br>
	 * 
	 * @param publicKeyIcc
	 *            the ICC public key to use as {@link ECPoint}
	 * @param signature
	 *            the {@link PsSignature} to use
	 * @return the Q value as {@link ECPoint}
	 */
	private ECPoint calculateQForVerification(ECPoint publicKeyIcc,
			PsSignature signature) {
		ECPoint tmp1, tmp2, tmp3;
		tmp1 = CryptoUtil.scalarPointMultiplication(groupManagerDomainParameters.getCurve(), publicKeyIcc, signature.getC());
		tmp2 = CryptoUtil.scalarPointMultiplication(groupManagerDomainParameters.getCurve(), groupManagerDomainParameters.getGenerator(), signature.getS1());
		tmp3 = CryptoUtil.scalarPointMultiplication(groupManagerDomainParameters.getCurve(), groupManagerPublicKey, signature.getS2());
		
		return CryptoUtil.addPoint(groupManagerDomainParameters.getCurve(), CryptoUtil.addPoint(groupManagerDomainParameters.getCurve(), tmp1, tmp2), tmp3);
	}

	/**
	 * This computes the Q value for signing as described in TR-03110 v2.2
	 * Part 2 3.1.6.2</br>
	 * 
	 * @param publicKeyIcc
	 *            the ICC public key to use as {@link ECPoint}
	 * @param signature
	 *            the {@link PsSignature} to use
	 * @return the Q value as {@link ECPoint}
	 */
	private ECPoint calculateQForSigning(BigInteger k1, BigInteger k2) {
		ECPoint tmp1 = CryptoUtil.scalarPointMultiplication(groupManagerDomainParameters.getCurve(), groupManagerDomainParameters.getGenerator(), k1);
		ECPoint tmp2 = CryptoUtil.scalarPointMultiplication(groupManagerDomainParameters.getCurve(), groupManagerPublicKey, k2);
		
		return CryptoUtil.addPoint(groupManagerDomainParameters.getCurve(), tmp1, tmp2);
	}
	
	/**
	 * This checks the given values for zeroes.
	 * @param values
	 * @return true, iff the list contains a {@link BigInteger#ZERO} value
	 */
	public boolean checkForZero(BigInteger ... values) {
		for(BigInteger value : values){
			if (value.equals(BigInteger.ZERO)){
				return true;
			}
		}
		return false;
	}

	/**
	 * This computes the signature component as described in TR-03110 v2.2 Part
	 * 2 3.1.6.2</br>
	 * 
	 * s = (k - c * SK_ICC) mod n </br> k is random number smaller than the
	 * order of the group</br> c is the previously computed witness</br> SK_ICC
	 * is the secret key to be used
	 * 
	 * @param witness
	 *            the witness to use
	 * @param random
	 *            the random component
	 * @param secretKey
	 *            the secret key to use
	 * @return
	 */
	protected BigInteger computeSignatureComponent(BigInteger witness, BigInteger random, BigInteger secretKey) {
		BigInteger multiplicationResult = witness.multiply(secretKey);
		BigInteger subtractionResult = random.subtract(multiplicationResult);
		BigInteger moduloResult = subtractionResult.mod(groupManagerDomainParameters.getOrder());
		return moduloResult;
	}

	/**
	 * This computes the signature component as described in TR-03110 v2.2 Part
	 * 2 3.1.6.2</br>
	 * 
	 * @param q1
	 * @param sectorSpecificIdentifierIcc1
	 *            the first sector identifier calculated previously or null if
	 *            not to be included
	 * @param a1
	 *            the A value fitting the first sector identifier, it is ignored
	 *            if not needed
	 * @param sectorSpecificIdentifierIcc2
	 *            the second sector identifier calculated previously or null if
	 *            not to be included
	 * @param a2
	 *            the A value fitting the second sector identifier, it is
	 *            ignored if not needed
	 * @param sectorPublicKey
	 *            the terminals sector public key
	 * @param idDsi
	 *            the {@link Oid} to be used in the signature
	 * @param message
	 *            the message to sign
	 * @return the hashed witness as a {@link BigInteger}
	 */
	protected BigInteger computeWitness(ECPoint q1, ECPoint sectorSpecificIdentifierIcc1, ECPoint a1, ECPoint sectorSpecificIdentifierIcc2, ECPoint a2, ECPoint sectorPublicKey, Oid idDsi, byte[] message) {
		log(getClass(), "computing witness", LogLevel.DEBUG);
		
		byte[] q1Projection = groupManagerDomainParameters.getProjectedRepresentation(q1);
		byte [] bytesToHash = q1Projection;
		
		log(getClass(), "projected representation of q_1: " + HexString.encode(q1Projection), LogLevel.DEBUG);
		log(getClass(), "initial bytes to hash: " + HexString.encode(bytesToHash), LogLevel.DEBUG);
		
		byte[] iccProjection, aProjection;
		
		if (sectorSpecificIdentifierIcc1 != null){
			log(getClass(), "sector specific identifier ICC1 available", LogLevel.DEBUG);
			iccProjection = groupManagerDomainParameters.getProjectedRepresentation(sectorSpecificIdentifierIcc1);
			aProjection = groupManagerDomainParameters.getProjectedRepresentation(a1);
			bytesToHash = Utils.concatByteArrays(bytesToHash, iccProjection, aProjection);
			
			log(getClass(), "projected representation of ICC1: " + HexString.encode(iccProjection), LogLevel.DEBUG);
			log(getClass(), "projected representation of a_1 : " + HexString.encode(aProjection), LogLevel.DEBUG);
			log(getClass(), "updated bytes to hash: " + HexString.encode(bytesToHash), LogLevel.DEBUG);
		} else{
			log(getClass(), "sector specific identifier ICC1 NOT available", LogLevel.DEBUG);
		}
		
		if (sectorSpecificIdentifierIcc2 != null){
			log(getClass(), "sector specific identifier ICC2 available", LogLevel.DEBUG);
			iccProjection = groupManagerDomainParameters.getProjectedRepresentation(sectorSpecificIdentifierIcc2);
			aProjection = groupManagerDomainParameters.getProjectedRepresentation(a2);
			bytesToHash = Utils.concatByteArrays(bytesToHash, iccProjection, aProjection);
			
			log(getClass(), "projected representation of ICC2: " + HexString.encode(iccProjection), LogLevel.DEBUG);
			log(getClass(), "projected representation of a_2 : " + HexString.encode(aProjection), LogLevel.DEBUG);
			log(getClass(), "updated bytes to hash: " + HexString.encode(bytesToHash), LogLevel.DEBUG);
		} else{
			log(getClass(), "sector specific identifier ICC2 NOT available", LogLevel.DEBUG);
		}
		
		PrimitiveTlvDataObject oidTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_06, idDsi.toByteArray());
		
		byte[] idDsiBytes = oidTlv.toByteArray();
		byte[] pkSectorProjection = groupManagerDomainParameters.getProjectedRepresentation(sectorPublicKey);
		
		log(getClass(), "ID_DSI is: " + HexString.encode(idDsiBytes), LogLevel.DEBUG);
		log(getClass(), "projected representation of sector public key is : " + HexString.encode(pkSectorProjection), LogLevel.DEBUG);
		
		bytesToHash = Utils.concatByteArrays(bytesToHash, pkSectorProjection, idDsiBytes, message);
		log(getClass(), "final bytes to hash: " + HexString.encode(bytesToHash), LogLevel.DEBUG);
		
		BigInteger witness = new BigInteger(1, messageDigest.digest(bytesToHash));
		
		log(getClass(), "witness is: " + HexString.encode(witness), LogLevel.DEBUG);
		
		return witness;
	}

	/**
	 * This checks the given values for infinity elliptic curve points.
	 * @param values
	 * @return true, iff the list contains a {@link ECPoint#POINT_INFINITY} value
	 */
	public boolean checkForInfinity(ECPoint ... points){
		for (ECPoint point : points){
			if (point != null && point.equals(ECPoint.POINT_INFINITY)){
				return true;
			}
		}
		return false;
	}

	/**
	 * This calculates the A values as described in TR-03110 v2.2 Part 2
	 * 3.1.6.2 for signing</br>
	 * 
	 * @param sectorPublicKey
	 *            the terminals sector public key
	 * @param random
	 *            the random value to use
	 * @return the A value as {@link ECPoint}
	 */
	private ECPoint calculateAforSigning(ECPoint sectorPublicKey, BigInteger random) {
		return CryptoUtil.scalarPointMultiplication(groupManagerDomainParameters.getCurve(), sectorPublicKey, random);
	}

	/**
	 * This calculates the A values as described in TR-03110 v2.2 Part 2 3.1.6.2
	 * for verification</br>
	 * 
	 * @param sectorIdentifierIcc
	 *            the sectorIdentifier to use
	 * @param sectorPublicKey
	 *            the terminals sector public key
	 * @param witness
	 *            the previously computed witness
	 * @param signatureComponent
	 *            the signature component fitting the sectorIdentifier
	 * @return the A value as {@link ECPoint}
	 */
	private ECPoint calculateAforVerification(ECPoint sectorIdentifierIcc, ECPoint sectorPublicKey, BigInteger witness, BigInteger signatureComponent) {
		ECPoint tmp1 = CryptoUtil.scalarPointMultiplication(groupManagerDomainParameters.getCurve(), sectorIdentifierIcc, witness);
		ECPoint tmp2 = CryptoUtil.scalarPointMultiplication(groupManagerDomainParameters.getCurve(), sectorPublicKey, signatureComponent);
		return CryptoUtil.addPoint(groupManagerDomainParameters.getCurve(), tmp1, tmp2);
	}
}
