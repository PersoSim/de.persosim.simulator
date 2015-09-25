package de.persosim.simulator.protocols;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.certificates.CvEcPublicKey;
import de.persosim.simulator.crypto.certificates.CvPublicKey;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.Utils;

public class Tr03110UtilsDefaultProvider implements Tr03110UtilsProvider {

	@Override
	public PublicKey parsePublicKey(ConstructedTlvDataObject publicKeyData,
			PublicKey trustPointPublicKey) throws GeneralSecurityException {
		TaOid oid = new TaOid(publicKeyData.getTlvDataObject(TlvConstants.TAG_06)
				.getValueField());

		if (oid.getIdString().contains("ECDSA")) {
			ECParameterSpec paramSpec = null;
			ECPublicKey trustPointEcPublicKey = (ECPublicKey) trustPointPublicKey;
			if (publicKeyData.containsTlvDataObject(TlvConstants.TAG_81)
					&& publicKeyData.containsTlvDataObject(TlvConstants.TAG_82)
					&& publicKeyData.containsTlvDataObject(TlvConstants.TAG_83)
					&& publicKeyData.containsTlvDataObject(TlvConstants.TAG_84)
					&& publicKeyData.containsTlvDataObject(TlvConstants.TAG_85)
					&& publicKeyData.containsTlvDataObject(TlvConstants.TAG_87)) {
				paramSpec = CryptoUtil.parseParameterSpecEc(publicKeyData);
			} else {
				if (trustPointEcPublicKey == null){
					throw new InvalidKeySpecException("The public key data does not contain domain parameters and not additional public key was given");
				}
				if (trustPointEcPublicKey.getParams() == null){
					throw new InvalidKeySpecException("The given additional public key does not contain domain parameters");
				}
				if (trustPointEcPublicKey.getParams().getCurve().getField()
						.getFieldSize() / 8 != ((publicKeyData
						.getTlvDataObject(TlvConstants.TAG_86).getLengthValue() - 1) / 2)) {
					throw new InvalidKeySpecException(
							"The trust points field bit length does not match");
				}
				paramSpec = trustPointEcPublicKey.getParams();
			}

			return CryptoUtil.parsePublicKeyEc(publicKeyData, paramSpec);
		}

		return null;
	}
	
	@Override
	public CvPublicKey parseCvPublicKey(ConstructedTlvDataObject publicKeyData) {
		try{
			return new CvEcPublicKey(publicKeyData);
		} catch(IllegalArgumentException | GeneralSecurityException e) {
			return null;
		}
	}
	
	@Override
	public DomainParameterSet getDomainParameterSetFromKey(Key key) {
		if((key instanceof ECPublicKey) || (key instanceof ECPrivateKey)) {
			ECParameterSpec ecParameterSpec;
			
			if(key instanceof ECPublicKey) {
				ecParameterSpec = ((ECPublicKey) key).getParams();
			} else{
				ecParameterSpec = ((ECPrivateKey) key).getParams();
			}
			
			return new DomainParameterSetEcdh(ecParameterSpec.getCurve(), ecParameterSpec.getGenerator(), ecParameterSpec.getOrder(), ecParameterSpec.getCofactor());
		}
		return null;
	}

	@Override
	public ConstructedTlvDataObject encodeKey(PublicKey publicKey, Oid oid) {
		ConstructedTlvDataObject publicKeyTlv = new ConstructedTlvDataObject(TlvConstants.TAG_7F49);
		PrimitiveTlvDataObject oidTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_06, oid.toByteArray());
		publicKeyTlv.addTlvDataObject(oidTlv);
		
		if(publicKey instanceof ECPublicKey) {
			ECPublicKey ecPublicKey = (ECPublicKey) publicKey;
			ECParameterSpec params = ecPublicKey.getParams();
			
			EllipticCurve curve = params.getCurve();
			ECFieldFp field = (ECFieldFp) curve.getField();
			
			BigInteger primeModulus = field.getP();
			BigInteger firstCoefficient = curve.getA();
			BigInteger secondCoefficient = curve.getB();
			ECPoint basePoint = params.getGenerator();
			BigInteger orderOfTheBasePoint = params.getOrder();
			ECPoint publicPoint = ecPublicKey.getW();
			int coFactor = params.getCofactor();
			
			int publicPointReferenceLength = DomainParameterSetEcdh.getPublicPointReferenceLengthL(primeModulus);
			
			PrimitiveTlvDataObject tlv81 = new PrimitiveTlvDataObject(TlvConstants.TAG_81, Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(primeModulus)));
			PrimitiveTlvDataObject tlv82 = new PrimitiveTlvDataObject(TlvConstants.TAG_82, Utils.toUnsignedByteArray(firstCoefficient));
			PrimitiveTlvDataObject tlv83 = new PrimitiveTlvDataObject(TlvConstants.TAG_83, Utils.toUnsignedByteArray(secondCoefficient));
			PrimitiveTlvDataObject tlv84 = new PrimitiveTlvDataObject(TlvConstants.TAG_84, CryptoUtil.encode(basePoint, publicPointReferenceLength, CryptoUtil.ENCODING_UNCOMPRESSED));
			PrimitiveTlvDataObject tlv85 = new PrimitiveTlvDataObject(TlvConstants.TAG_85, Utils.toUnsignedByteArray(orderOfTheBasePoint));
			PrimitiveTlvDataObject tlv86 = new PrimitiveTlvDataObject(TlvConstants.TAG_86, CryptoUtil.encode(publicPoint, publicPointReferenceLength, CryptoUtil.ENCODING_UNCOMPRESSED));
			PrimitiveTlvDataObject tlv87 = new PrimitiveTlvDataObject(TlvConstants.TAG_87, Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(coFactor)));
			
			publicKeyTlv.addTlvDataObject(tlv81);
			publicKeyTlv.addTlvDataObject(tlv82);
			publicKeyTlv.addTlvDataObject(tlv83);
			publicKeyTlv.addTlvDataObject(tlv84);
			publicKeyTlv.addTlvDataObject(tlv85);
			publicKeyTlv.addTlvDataObject(tlv86);
			publicKeyTlv.addTlvDataObject(tlv87);
			
			return publicKeyTlv;
		}
		
		return null;
	}
	
}
