package de.persosim.simulator.crypto.certificates;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.Utils;

/**
 * This class represents an EC public key.
 * 
 * @author slutters
 *
 */
public class CvEcPublicKey extends CvPublicKey implements ECPublicKey {
	
	private static final long serialVersionUID = 1L;
	
	protected byte[] publicPointEncoding;
	
	public CvEcPublicKey(CvOid cvOid, ECPublicKey ecPublicKey) {
		super(cvOid, ecPublicKey);
	}
	
	public CvEcPublicKey(ConstructedTlvDataObject publicKeyEncoding) throws GeneralSecurityException {
		super(parseOid(publicKeyEncoding), null);
		
		parsePublicKey(publicKeyEncoding);
	}
	
	public static CvOid parseOid(ConstructedTlvDataObject publicKeyData) {
		// TODO replace fixed TaOid with OID factory, checking for CvOid compliance
		return new TaOid(publicKeyData.getTlvDataObject(TlvConstants.TAG_06).getValueField());
	}
	
	private void parsePublicKey(ConstructedTlvDataObject publicKeyData) throws GeneralSecurityException {
		if (cvOid.getIdString().contains("EC")) {
			ECParameterSpec paramSpec = null;
			if (publicKeyData.containsTlvDataObject(TlvConstants.TAG_81)
					&& publicKeyData.containsTlvDataObject(TlvConstants.TAG_82)
					&& publicKeyData.containsTlvDataObject(TlvConstants.TAG_83)
					&& publicKeyData.containsTlvDataObject(TlvConstants.TAG_84)
					&& publicKeyData.containsTlvDataObject(TlvConstants.TAG_85)
					&& publicKeyData.containsTlvDataObject(TlvConstants.TAG_87)) {
				paramSpec = CryptoUtil.parseParameterSpecEc(publicKeyData);
				
				if (publicKeyData.containsTlvDataObject(TlvConstants.TAG_86)) {
					key = CryptoUtil.parsePublicKeyEc(publicKeyData, paramSpec);
				} else{
					throw new IllegalArgumentException("no public key component found");
				}
			} else {
				if (publicKeyData.containsTlvDataObject(TlvConstants.TAG_86)) {
					publicPointEncoding = publicKeyData.getTlvDataObject(TAG_86).getValueField();
				} else{
					throw new IllegalArgumentException("no public key component found");
				}
			}
		} else{
			throw new IllegalArgumentException("no EC key indicated by OID");
		}
	}

	@Override
	public ConstructedTlvDataObject toTlvDataObject(boolean includeConditionalObjects) {
		ConstructedTlvDataObject publicKeyBody = new ConstructedTlvDataObject(TAG_7F49);
		
		PrimitiveTlvDataObject objectIdentifier    = new PrimitiveTlvDataObject(TAG_06, this.cvOid.toByteArray());
		publicKeyBody.addTlvDataObject(objectIdentifier);
		
		if(isComplete()) {
			ECParameterSpec ecParams = ((ECPublicKey) key).getParams();
			EllipticCurve curve = ecParams.getCurve();
			
			int referenceLength = DomainParameterSetEcdh.getPublicPointReferenceLengthL(((ECFieldFp) curve.getField()).getP());
			
			PrimitiveTlvDataObject publicPoint         = new PrimitiveTlvDataObject(TAG_86, CryptoUtil.encode(((ECPublicKey) key).getW(), referenceLength, CryptoUtil.ENCODING_UNCOMPRESSED));
			
			if(includeConditionalObjects) {
				PrimitiveTlvDataObject primeModulus        = new PrimitiveTlvDataObject(TAG_81, Utils.toUnsignedByteArray(((ECFieldFp) curve.getField()).getP()));
				PrimitiveTlvDataObject firstCoefficient    = new PrimitiveTlvDataObject(TAG_82, Utils.toUnsignedByteArray(curve.getA()));
				PrimitiveTlvDataObject secondCoefficient   = new PrimitiveTlvDataObject(TAG_83, Utils.toUnsignedByteArray(curve.getB()));
				PrimitiveTlvDataObject basePoint           = new PrimitiveTlvDataObject(TAG_84, CryptoUtil.encode(ecParams.getGenerator(), referenceLength, CryptoUtil.ENCODING_UNCOMPRESSED));
				PrimitiveTlvDataObject orderOfTheBasePoint = new PrimitiveTlvDataObject(TAG_85, Utils.toUnsignedByteArray(ecParams.getOrder()));
				
				publicKeyBody.addTlvDataObject(primeModulus);
				publicKeyBody.addTlvDataObject(firstCoefficient);
				publicKeyBody.addTlvDataObject(secondCoefficient);
				publicKeyBody.addTlvDataObject(basePoint);
				publicKeyBody.addTlvDataObject(orderOfTheBasePoint);
			}
			
			publicKeyBody.addTlvDataObject(publicPoint);
			
			if(includeConditionalObjects) {
				PrimitiveTlvDataObject coFactor            = new PrimitiveTlvDataObject(TAG_87, Utils.toUnsignedByteArray(new BigInteger((new Integer(ecParams.getCofactor())).toString())));
				
				publicKeyBody.addTlvDataObject(coFactor);
			}
		} else{
			PrimitiveTlvDataObject publicPoint         = new PrimitiveTlvDataObject(TAG_86, publicPointEncoding);
			publicKeyBody.addTlvDataObject(publicPoint);
		}
		
		return publicKeyBody;
	}

	@Override
	public ECParameterSpec getParams() {
		if(key != null) {
			return ((ECPublicKey) key).getParams();
		} else{
			return null;
		}
	}

	@Override
	public ECPoint getW() {
		if(key != null) {
			return ((ECPublicKey) key).getW();
		} else{
			return null;
		}
	}

	@Override
	public boolean isComplete() {
		return key != null;
	}
	
	public KeyPairGenerator getKeyPairGenerator(SecureRandom secRandom) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		KeyPairGenerator keyPairGenerator;
		
		keyPairGenerator = KeyPairGenerator.getInstance(getAlgorithm(), Crypto.getCryptoProvider());
		keyPairGenerator.initialize(getParams(), secRandom);
		
		return keyPairGenerator;
	}

}
