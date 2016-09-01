package de.persosim.simulator.crypto.certificates;

import static org.globaltester.logging.BasicLogger.DEBUG;
import static org.globaltester.logging.BasicLogger.log;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import org.globaltester.cryptoprovider.Crypto;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.Utils;

/**
 * This class represents an EC public key to be used in the context of CV certificates.
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
		
		if (cvOid.getKeyType().equals("EC")) {
			ECParameterSpec paramSpec = null;
			
			if (publicKeyEncoding.containsTlvDataObject(TlvConstants.TAG_86)) {
				if (publicKeyEncoding.containsTlvDataObject(TlvConstants.TAG_81)
						&& publicKeyEncoding.containsTlvDataObject(TlvConstants.TAG_82)
						&& publicKeyEncoding.containsTlvDataObject(TlvConstants.TAG_83)
						&& publicKeyEncoding.containsTlvDataObject(TlvConstants.TAG_84)
						&& publicKeyEncoding.containsTlvDataObject(TlvConstants.TAG_85)
						&& publicKeyEncoding.containsTlvDataObject(TlvConstants.TAG_87)) {
					paramSpec = CryptoUtil.parseParameterSpecEc(publicKeyEncoding);
					
					if (publicKeyEncoding.containsTlvDataObject(TlvConstants.TAG_86)) {
						key = CryptoUtil.parsePublicKeyEc(publicKeyEncoding, paramSpec);
					} else{
						throw new IllegalArgumentException("no public key component found");
					}
				} else {
					publicPointEncoding = publicKeyEncoding.getTlvDataObject(TAG_86).getValueField();
				}
			} else{
				throw new IllegalArgumentException("no public key component found");
			}
		} else{
			throw new IllegalArgumentException("no EC key indicated by OID");
		}
	}
	
	private static CvOid parseOid(ConstructedTlvDataObject publicKeyData) {
		return new TaOid(publicKeyData.getTlvDataObject(TlvConstants.TAG_06).getValueField());
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
	
	@Override
	public KeyPairGenerator getKeyPairGenerator(SecureRandom secRandom) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		KeyPairGenerator keyPairGenerator;
		
		keyPairGenerator = KeyPairGenerator.getInstance(getAlgorithm(), Crypto.getCryptoProvider());
		keyPairGenerator.initialize(getParams(), secRandom);
		
		return keyPairGenerator;
	}

	@Override
	public boolean updateKey(PublicKey publicKey) {
		if(key == null) {
			if(publicKey instanceof ECPublicKey) {
				ECPublicKey ecPublicKey = (ECPublicKey) publicKey;
				
				ECParameterSpec ecParams = ecPublicKey.getParams();
				
				if(ecParams == null) {
					log(CvEcPublicKey.class, "updating key must provide domain parameters", DEBUG);
					return false;
				}
				
				DomainParameterSetEcdh domParamsEcdh = new DomainParameterSetEcdh(ecParams);
				ECPoint publicPoint = DomainParameterSetEcdh.reconstructPoint(publicPointEncoding);
				key = domParamsEcdh.reconstructPublicKey(publicPoint, Crypto.getCryptoProvider());

				if(key == null) {
					log(CvEcPublicKey.class, "key update failed", DEBUG);
					return false;
				} else{
					publicPointEncoding = null;
					log(CvEcPublicKey.class, "key update successfull", DEBUG);
					return true;
				}
			} else{
				throw new IllegalArgumentException("updating key must be of type ECPublicKey");
			}
		} else{
			log(CvEcPublicKey.class, "key update unnecessary", DEBUG);
			return false; // key already complete and fully usable
		}
	}

	@Override
	public boolean matchesCoreMaterial(CvPublicKey publicKey) {
		if (publicKey instanceof CvEcPublicKey){
			CvEcPublicKey cvKey = (CvEcPublicKey) publicKey;
			return cvKey.getW().equals(this.getW());
		}
		return false;
	}

}
