package de.persosim.simulator.protocols;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidKeySpecException;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;

public class Tr03110UtilsDefaultProvider implements Tr03110UtilsProvider {

	@Override
	public PublicKey parsePublicKey(ConstructedTlvDataObject publicKeyData,
			PublicKey trustPointPublicKey) throws GeneralSecurityException {
		TaOid oid = new TaOid(publicKeyData.getTlvDataObject(Tr03110Utils.TAG_06)
				.getValueField());

		if (oid.getIdString().contains("ECDSA")) {
			ECParameterSpec paramSpec = null;
			ECPublicKey trustPointEcPublicKey = (ECPublicKey) trustPointPublicKey;
			if (publicKeyData.containsTlvDataObject(Tr03110Utils.TAG_81)
					&& publicKeyData.containsTlvDataObject(Tr03110Utils.TAG_82)
					&& publicKeyData.containsTlvDataObject(Tr03110Utils.TAG_83)
					&& publicKeyData.containsTlvDataObject(Tr03110Utils.TAG_84)
					&& publicKeyData.containsTlvDataObject(Tr03110Utils.TAG_85)
					&& publicKeyData.containsTlvDataObject(Tr03110Utils.TAG_87)) {
				paramSpec = CryptoUtil.parseParameterSpecEc(publicKeyData);
			} else {
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
}
