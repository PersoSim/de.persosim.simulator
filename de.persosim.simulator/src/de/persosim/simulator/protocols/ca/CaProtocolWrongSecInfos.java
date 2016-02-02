package de.persosim.simulator.protocols.ca;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyPairObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.Utils;

public class CaProtocolWrongSecInfos extends DefaultCaProtocol {
	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		
		OidIdentifier caOidIdentifier = new OidIdentifier(OID_id_CA);
		
		Collection<CardObject> caKeyCardObjects = mf.findChildren(
				new KeyIdentifier(), caOidIdentifier);
		
		ArrayList<TlvDataObject> secInfos = new ArrayList<>();
		ArrayList<TlvDataObject> privilegedSecInfos = new ArrayList<>();
		ArrayList<TlvDataObject> unprivilegedPublicKeyInfos = new ArrayList<>();
		ArrayList<TlvDataObject> privilegedPublicKeyInfos = new ArrayList<>();
		
		
		for (CardObject curObject : caKeyCardObjects) {
			if (! (curObject instanceof KeyPairObject)) {
				continue;
			}
			KeyPairObject curKey = (KeyPairObject) curObject;
			Collection<CardObjectIdentifier> identifiers = curKey.getAllIdentifiers();
			
			//extract keyId
			int keyId = -1;
			for (CardObjectIdentifier curIdentifier : identifiers) {
				if (curIdentifier instanceof KeyIdentifier) {
					keyId = ((KeyIdentifier) curIdentifier).getKeyReference();
					break;
				}
			}
			if (keyId == -1) continue; // skip keys that dont't provide a keyId
			
			//cached values
			byte[] genericCaOidBytes = null;
			
			//construct and add ChipAuthenticationInfo object(s)
			for (CardObjectIdentifier curIdentifier : identifiers) {
				if (curIdentifier instanceof OidIdentifier) {
					Oid curOid = ((OidIdentifier) curIdentifier).getOid();
					if (curOid.startsWithPrefix(id_CA)) {
						byte[] oidBytes = curOid.toByteArray();
						genericCaOidBytes = Arrays.copyOfRange(oidBytes, 0, 9);
						
						ConstructedTlvDataObject caInfo = constructChipAuthenticationInfoObject(oidBytes, keyId);
						
						if (curKey.isPrivilegedOnly()) {
							privilegedSecInfos.add(caInfo);
						} else {
							secInfos.add(caInfo);
						}
					}
				}
			}
			
			//extract required data from curKey
			ConstructedTlvDataObject encKey = new ConstructedTlvDataObject(curKey.getKeyPair().getPublic().getEncoded());
			ConstructedTlvDataObject algIdentifier = (ConstructedTlvDataObject) encKey.getTlvDataObject(TAG_SEQUENCE);
			
			//using standardized domain parameters if possible
			algIdentifier = StandardizedDomainParameters.simplifyAlgorithmIdentifier(algIdentifier);
			
			/*
			 * add ChipAuthenticationDomainParameterInfo object(s)
			 * 
			 * ChipAuthenticationDomainParameterInfo ::= SEQUENCE {
             *   protocol        OBJECT IDENTIFIER(id-CA-DH | id-CA-ECDH),
             *   domainParameter AlgorithmIdentifier,
             *   keyId           INTEGER OPTIONAL
             * }
			 */
			ConstructedTlvDataObject caDomainInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
			caDomainInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, genericCaOidBytes));
			caDomainInfo.addTlvDataObject(algIdentifier);
			//always set keyId even if truly optional/not mandatory
			//another version of CA may be present so keys are no longer unique and the keyId field becomes mandatory
			caDomainInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, Utils.toShortestUnsignedByteArray(keyId)));
			if (curKey.isPrivilegedOnly()) {
				privilegedSecInfos.add(caDomainInfo);
			} else {
				secInfos.add(caDomainInfo);
			}
			
		}
		
		// add publicKeys if publicity allows
		if ((publicity == SecInfoPublicity.AUTHENTICATED) || (publicity == SecInfoPublicity.PRIVILEGED)) {
			secInfos.addAll(unprivilegedPublicKeyInfos);
		}
		
		//add PrivilegedTerminalInfo if privileged keys are available
		if (privilegedSecInfos.size() + privilegedPublicKeyInfos.size() > 0) {
			ConstructedTlvDataObject privilegedTerminalInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
			privilegedTerminalInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, Tr03110.id_PT));
			ConstructedTlvDataObject privilegedTerminaInfoSet = new ConstructedTlvDataObject(TAG_SET);
			privilegedTerminalInfo.addTlvDataObject(privilegedTerminaInfoSet);
			
			// add all privileged infos
			privilegedTerminaInfoSet.addAll(privilegedSecInfos);
		
			// add privileged public keys if publicity allows
			if ((publicity == SecInfoPublicity.PRIVILEGED)) {
				privilegedTerminaInfoSet.addAll(privilegedPublicKeyInfos);
			}
			
			secInfos.add(privilegedTerminalInfo);
		}
		
		return secInfos;
	}
}
