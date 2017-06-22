package de.persosim.simulator.crypto.certificates;

import static org.globaltester.logging.BasicLogger.log;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;

import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.logging.tags.LogLevel;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.protocols.Tr03110Utils;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;

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
		
		if(isComplete()) {
			publicKeyBody.addAll((Tr03110Utils.encodePublicKey(cvOid, (PublicKey) key, includeConditionalObjects).getTlvObjects()));
		} else{
			PrimitiveTlvDataObject objectIdentifier    = new PrimitiveTlvDataObject(TAG_06, cvOid.toByteArray());
			publicKeyBody.addTlvDataObject(objectIdentifier);
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
					log(CvEcPublicKey.class, "updating key must provide domain parameters", LogLevel.DEBUG);
					return false;
				}
				
				DomainParameterSetEcdh domParamsEcdh = new DomainParameterSetEcdh(ecParams);
				ECPoint publicPoint = DomainParameterSetEcdh.reconstructPoint(publicPointEncoding);
				key = domParamsEcdh.reconstructPublicKey(publicPoint, Crypto.getCryptoProvider());

				if(key == null) {
					log(CvEcPublicKey.class, "key update failed", LogLevel.DEBUG);
					return false;
				} else{
					publicPointEncoding = null;
					log(CvEcPublicKey.class, "key update successfull", LogLevel.DEBUG);
					return true;
				}
			} else{
				throw new IllegalArgumentException("updating key must be of type ECPublicKey");
			}
		} else{
			log(CvEcPublicKey.class, "key update unnecessary", LogLevel.DEBUG);
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
