package de.persosim.simulator.protocols;

//FIXME Javadoc is missing
public interface GenericOid {
	
	public String getIdString();
	
	public byte[] toByteArray();
	
	public boolean startsWithPrefix(byte[] oidPrefix);
	
}
