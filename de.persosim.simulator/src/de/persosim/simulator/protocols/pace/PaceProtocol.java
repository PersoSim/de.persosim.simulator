package de.persosim.simulator.protocols.pace;

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
		return new PaceOid(oidRaw, prefixOid);
	}

}
