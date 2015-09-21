package de.persosim.simulator.protocols;

public interface GenericOid {
	
	public String getIdString();
	
	public byte[] toByteArray();
	
	public boolean startsWithPrefix(byte[] oidPrefix);
	
}
