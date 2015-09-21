package de.persosim.simulator.protocols;

public interface OidIf {
	
	public String getIdString();
	
	public byte[] toByteArray();
	
	public boolean startsWithPrefix(byte[] oidPrefix);
	
}
