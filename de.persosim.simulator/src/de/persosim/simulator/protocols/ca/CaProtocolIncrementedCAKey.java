package de.persosim.simulator.protocols.ca;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;

public class CaProtocolIncrementedCAKey extends DefaultCaProtocol {
	@Override
	protected TlvDataObject computeSubjectPublicKey(ConstructedTlvDataObject encKey) {
		byte[] subjectPublicKey = encKey.getTlvDataObject(TAG_BIT_STRING).toByteArray();
		subjectPublicKey[subjectPublicKey.length-1]++;
		return new PrimitiveTlvDataObject(subjectPublicKey);
	}
}
