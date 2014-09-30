package de.persosim.simulator.perso;

import java.util.Arrays;

import de.persosim.simulator.protocols.Protocol.SecInfoPublicity;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;

/**
 * This can be used as callback on an {@link XmlPersonalization} in order to
 * automatically complete SecInfo files. The SecInfo files created by this
 * Unmarshaller comply with DefectList ObjectIdentifier
 * "id­EAC2Privile­gedTerminalIn­foMissing" and "id­eIDSecurity­InfoMissing"
 * according to TR-03127 attachment D.
 * 
 * Documents created by this Unmarshaller differ in the following points from
 * documents created according to latest specifications:
 * 
 * EF.CardAccess and EF.ChipSecurity do not contain a structure
 * PrivilegedTerminalInfo. The key available to non-privileged terminals for
 * chip authentication is the one addressed during first ChipAuthenticationInfo.
 * 
 * @author slutters
 * 
 */
public class DefectListNpaUnmarshallerCallback extends DefaultNpaUnmarshallerCallback {
	
	@Override
	protected ConstructedTlvDataObject getSecInfos(Personalization perso, SecInfoPublicity secInfoPublicity) {
		ConstructedTlvDataObject originalSecInfos = super.getSecInfos(perso, secInfoPublicity);
		ConstructedTlvDataObject mangledSecInfos = new ConstructedTlvDataObject(originalSecInfos.getTlvTag());
		
		System.out.println("original secInfos: " + originalSecInfos);
		
		for(TlvDataObject tlvDataObject : originalSecInfos) {
			if(tlvDataObject instanceof ConstructedTlvDataObject) {
				ConstructedTlvDataObject constructedTlvDataObject = (ConstructedTlvDataObject) tlvDataObject;
				
				TlvDataObject tlvDataObjectOid = constructedTlvDataObject.getTlvDataObject(TAG_06);
				if((tlvDataObjectOid != null) && (Arrays.equals(tlvDataObjectOid.getValueField(), Tr03110.id_PT))) {
					TlvDataObject privilegedSecInfoWrapper = constructedTlvDataObject.getTlvDataObject(TAG_SET);
					
					if(privilegedSecInfoWrapper != null) {
						ConstructedTlvDataObject privilegedSecInfoWrapperConstructed = (ConstructedTlvDataObject) privilegedSecInfoWrapper;
						
						for(TlvDataObject privilegedSecInfos : privilegedSecInfoWrapperConstructed) {
							mangledSecInfos.addTlvDataObject(privilegedSecInfos);
						}
					}
				} else{
					mangledSecInfos.addTlvDataObject(tlvDataObject);
				}
				
				
			} else{
				mangledSecInfos.addTlvDataObject(tlvDataObject);
			}
		}
		
		System.out.println("mangled secInfos: " + mangledSecInfos);
		
		return mangledSecInfos;
	}
	
}
