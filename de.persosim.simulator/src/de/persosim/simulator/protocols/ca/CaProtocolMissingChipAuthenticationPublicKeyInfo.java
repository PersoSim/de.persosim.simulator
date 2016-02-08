package de.persosim.simulator.protocols.ca;

import java.util.ArrayList;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;

public class CaProtocolMissingChipAuthenticationPublicKeyInfo extends DefaultCaProtocol {
	@Override
	protected void addChipAuthenticationPublicKeyInfo(boolean isPrivilegedOnly, ArrayList<TlvDataObject> privilegedPublicKeyInfos, ArrayList<TlvDataObject> unprivilegedPublicKeyInfos, ConstructedTlvDataObject caPublicKeyInfo) {
		//do not add ChipAuthenticationPublicKeyInfo
	}
}
