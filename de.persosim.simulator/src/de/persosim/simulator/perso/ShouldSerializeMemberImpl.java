package de.persosim.simulator.perso;

import org.globaltester.lib.xstream.ShouldSerializeMemberInstruction;

public class ShouldSerializeMemberImpl implements ShouldSerializeMemberInstruction {
	
	@Override
	@SuppressWarnings("rawtypes")
	public byte shouldSerializeMember(Class definedIn, String fieldName) {

		//suppress all fields defined in AbstractProfile
		if (definedIn.getName().equals("de.persosim.simulator.perso.AbstractProfile")) {
			return DO_NOT_SERIALIZE;
		}

		//suppress CryptoProviderCache
		if (definedIn.getName().equals("de.persosim.simulator.protocols.ca.CaOid") && fieldName.equals("cryptoSupportCache")) {
			return DO_NOT_SERIALIZE;
		}

		return NO_DECISION;
	}
	
}
