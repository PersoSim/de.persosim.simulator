package de.persosim.simulator.protocols.pace;

import de.persosim.simulator.protocols.CAPA;
import de.persosim.simulator.protocols.GenericOid;

public class PaceProtocol extends DefaultPaceProtocol {

	public PaceProtocol() {
		protocolName = getProtocolName();
	}

	@Override
	public String getProtocolName() {
		return "PACE";
	}

	@Override
	protected PaceOid getOid(byte[] oidRaw) {
		GenericOid oid = new GenericOid(oidRaw);
		if (oid.startsWithPrefix(CAPA.id_CAPA))
			return null;
		return new PaceOid(oidRaw, prefixOid);
	}

}
