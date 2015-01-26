package de.persosim.simulator.protocols.pace;

import static de.persosim.simulator.utils.PersoSimLogger.TRACE;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class performs the ECDH specific parts of generic mapping.
 * 
 * @author slutters
 *
 */
public class GenericMappingEcdh extends GenericMapping {
	
	@Override
	public DomainParameterSet performGenericMappingOfDomainParameters(DomainParameterSet domainParameterSetUnMapped, BigInteger nonceS, byte[] secretOfKeyAgreement) {
		if(!(domainParameterSetUnMapped instanceof DomainParameterSetEcdh)) {throw new IllegalArgumentException("domain parameters must be ECDH");};
		
		DomainParameterSetEcdh domainParameterSetEcdhUnMapped = (DomainParameterSetEcdh) domainParameterSetUnMapped;
		
		ECPoint h = DomainParameterSetEcdh.reconstructPoint(secretOfKeyAgreement);
		
		EllipticCurve curve = domainParameterSetEcdhUnMapped.getCurve();
		ECPoint gUnMapped = domainParameterSetEcdhUnMapped.getGenerator();
		
		log(this.getClass(), "gUnMapped x: " + HexString.encode(Utils.toUnsignedByteArray(gUnMapped.getAffineX())));
		log(this.getClass(), "gUnMapped y: " + HexString.encode(Utils.toUnsignedByteArray(gUnMapped.getAffineY())));
		log(this.getClass(), "nonce S: " + HexString.encode(Utils.toUnsignedByteArray(nonceS)));
		
		ECPoint gspm = CryptoUtil.scalarPointMultiplication(curve, domainParameterSetEcdhUnMapped.getOrder(), gUnMapped, nonceS);
		
		log(this.getClass(), "gspm x: " + HexString.encode(Utils.toUnsignedByteArray(gspm.getAffineX())));
		log(this.getClass(), "gspm y: " + HexString.encode(Utils.toUnsignedByteArray(gspm.getAffineY())));
		
		ECPoint  gMapped = CryptoUtil.addPoint(curve, gspm, h);
		
		DomainParameterSetEcdh domainParametersMapped = domainParameterSetEcdhUnMapped.getUpdatedDomainParameterSet(gMapped);
		
		return domainParametersMapped;
	}
	
	@Override
	public String getMappingName() {
		return super.getMappingName() + " with ECDH key agreement";
	}
	
	@Override
	public byte[] performKeyAgreement(DomainParameterSet domainParameters, PrivateKey privKeyPicc, PublicKey pubKeyPcd) {
		/*
		 * This method performs a manual ECDH key agreement that returns a
		 * complete EC point as its result. The key agreement provided via the
		 * Java crypto API only returns the x-coordinate of the expected EC
		 * point and reconstructing the y-coordinate is complicated by the
		 * ambiguity of the coordinate.
		 */
		
		DomainParameterSetEcdh domainParameterSetEcdh = (DomainParameterSetEcdh) domainParameters;
		ECPrivateKey ecPrivateKeyPicc = (ECPrivateKey) privKeyPicc;
		ECPublicKey ecPublicKeyPcd = (ECPublicKey) pubKeyPcd;
		
		ECPoint secretPoint = CryptoUtil.scalarPointMultiplication(domainParameterSetEcdh.getCurve(), domainParameterSetEcdh.getOrder(), ecPublicKeyPcd.getW(), ecPrivateKeyPicc.getS());
		
		log(GenericMappingEcdh.class, "result H of ECDH key agreement is", TRACE);
		log(GenericMappingEcdh.class, "H.x: " + HexString.encode(secretPoint.getAffineX()), TRACE);
		log(GenericMappingEcdh.class, "H.y: " + HexString.encode(secretPoint.getAffineY()), TRACE);
		
		byte[] encodedPoint = CryptoUtil.encode(secretPoint, domainParameterSetEcdh.getPublicPointReferenceLengthL());
		log(GenericMappingEcdh.class, "H uncompressed encoding: " + HexString.encode(encodedPoint), TRACE);
		
		return encodedPoint;
	}
	
	@Override
	public String getMeaningOfMappingData() {
		return "PCD's ECDH public key point";
	}
	
}
