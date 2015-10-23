package de.persosim.simulator.protocols;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;

import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.certificates.CvEcPublicKey;
import de.persosim.simulator.crypto.certificates.CvPublicKey;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;

public class Tr03110UtilsDefaultProvider implements Tr03110UtilsProvider {
	
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
	
}
