package de.persosim.simulator.protocols;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;

import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;

public interface Tr03110UtilsProvider {

	PublicKey parsePublicKey(ConstructedTlvDataObject publicKeyData,
			PublicKey trustPointPublicKey) throws GeneralSecurityException;

	DomainParameterSet getDomainParameterSetFromKey(Key key);

}
