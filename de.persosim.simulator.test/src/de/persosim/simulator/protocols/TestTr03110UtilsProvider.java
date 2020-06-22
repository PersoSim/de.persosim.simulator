package de.persosim.simulator.protocols;

import java.security.Key;
import java.security.PublicKey;

import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.certificates.CvPublicKey;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;

final class TestTr03110UtilsProvider implements Tr03110UtilsProvider {
	private final Object parseCvPublicKeyResult;

	TestTr03110UtilsProvider(Object parseCvPublicKeyResult) {
		this.parseCvPublicKeyResult = parseCvPublicKeyResult;
	}

	@Override
	public CvPublicKey parseCvPublicKey(ConstructedTlvDataObject publicKeyData) {
		if (parseCvPublicKeyResult instanceof RuntimeException) throw (RuntimeException) parseCvPublicKeyResult; 
		return (CvPublicKey) parseCvPublicKeyResult;
	}

	@Override
	public DomainParameterSet getDomainParameterSetFromKey(Key key) {
		return null;
	}

	@Override
	public TlvDataObjectContainer encodePublicKey(Oid oid, PublicKey pk, boolean includeConditionalObjects) {
		return null;
	}
}