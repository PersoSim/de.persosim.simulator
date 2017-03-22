package de.persosim.simulator.protocols;

import static de.persosim.simulator.tlv.TlvConstants.TAG_06;
import static de.persosim.simulator.tlv.TlvConstants.TAG_81;
import static de.persosim.simulator.tlv.TlvConstants.TAG_82;
import static de.persosim.simulator.tlv.TlvConstants.TAG_83;
import static de.persosim.simulator.tlv.TlvConstants.TAG_84;
import static de.persosim.simulator.tlv.TlvConstants.TAG_85;
import static de.persosim.simulator.tlv.TlvConstants.TAG_86;
import static de.persosim.simulator.tlv.TlvConstants.TAG_87;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.EllipticCurve;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.certificates.CvEcPublicKey;
import de.persosim.simulator.crypto.certificates.CvPublicKey;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.Utils;

public class Tr03110UtilsDefaultProvider implements Tr03110UtilsProvider {

	@Override
	public CvPublicKey parseCvPublicKey(ConstructedTlvDataObject publicKeyData) {
		try {
			return new CvEcPublicKey(publicKeyData);
		} catch (IllegalArgumentException | GeneralSecurityException e) {
			return null;
		}
	}

	@Override
	public DomainParameterSet getDomainParameterSetFromKey(Key key) {
		if ((key instanceof ECPublicKey) || (key instanceof ECPrivateKey)) {
			ECParameterSpec ecParameterSpec;

			if (key instanceof ECPublicKey) {
				ecParameterSpec = ((ECPublicKey) key).getParams();
			} else {
				ecParameterSpec = ((ECPrivateKey) key).getParams();
			}

			return new DomainParameterSetEcdh(ecParameterSpec.getCurve(), ecParameterSpec.getGenerator(),
					ecParameterSpec.getOrder(), ecParameterSpec.getCofactor());
		}
		return null;
	}

	@Override
	public TlvDataObjectContainer encodePublicKey(Oid oid, PublicKey pk, boolean includeConditionalObjects) {

		if (!(pk instanceof ECPublicKey)) {
			return null;
		}

		ECPublicKey ecKey = (ECPublicKey) pk;

		TlvDataObjectContainer publicKeyBody = new TlvDataObjectContainer();

		PrimitiveTlvDataObject objectIdentifier = new PrimitiveTlvDataObject(TAG_06, oid.toByteArray());
		publicKeyBody.addTlvDataObject(objectIdentifier);
		
		ECParameterSpec ecParams = ecKey.getParams();
		EllipticCurve curve = ecParams.getCurve();

		int referenceLength = DomainParameterSetEcdh.getPublicPointReferenceLengthL(((ECFieldFp) curve.getField()).getP());
		PrimitiveTlvDataObject publicPoint = new PrimitiveTlvDataObject(TAG_86, CryptoUtil.encode(ecKey.getW(), referenceLength, CryptoUtil.ENCODING_UNCOMPRESSED));
		
		if (includeConditionalObjects) {
			PrimitiveTlvDataObject primeModulus = new PrimitiveTlvDataObject(TAG_81, Utils.toUnsignedByteArray(((ECFieldFp) curve.getField()).getP()));
			PrimitiveTlvDataObject firstCoefficient = new PrimitiveTlvDataObject(TAG_82, Utils.toUnsignedByteArray(curve.getA()));
			PrimitiveTlvDataObject secondCoefficient = new PrimitiveTlvDataObject(TAG_83, Utils.toUnsignedByteArray(curve.getB()));
			PrimitiveTlvDataObject basePoint = new PrimitiveTlvDataObject(TAG_84, CryptoUtil.encode(ecParams.getGenerator(), referenceLength, CryptoUtil.ENCODING_UNCOMPRESSED));
			PrimitiveTlvDataObject orderOfTheBasePoint = new PrimitiveTlvDataObject(TAG_85, Utils.toUnsignedByteArray(ecParams.getOrder()));

			publicKeyBody.addTlvDataObject(primeModulus);
			publicKeyBody.addTlvDataObject(firstCoefficient);
			publicKeyBody.addTlvDataObject(secondCoefficient);
			publicKeyBody.addTlvDataObject(basePoint);
			publicKeyBody.addTlvDataObject(orderOfTheBasePoint);
		}

		publicKeyBody.addTlvDataObject(publicPoint);

		if (includeConditionalObjects) {
			PrimitiveTlvDataObject coFactor = new PrimitiveTlvDataObject(TAG_87,
					Utils.toUnsignedByteArray(new BigInteger((new Integer(ecParams.getCofactor())).toString())));

			publicKeyBody.addTlvDataObject(coFactor);
		}

		return publicKeyBody;
	}

}
