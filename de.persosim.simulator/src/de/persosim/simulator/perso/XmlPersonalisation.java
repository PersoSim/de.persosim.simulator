package de.persosim.simulator.perso;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.protocols.Protocol;

@XmlRootElement(name="Personalization")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlPersonalisation implements Personalization {

	@XmlElementWrapper(name = "protocols")
	@XmlAnyElement(lax=true)
	private List<Protocol> protocols;
	
	@XmlAnyElement(lax=true)
	MasterFile mf;

	public void setProtocolList(List<Protocol> protocolList) {
		this.protocols = protocolList;
	}

	public List<Protocol> getProtocols() {
		return protocols;
	}

	public MasterFile getMf() {
		return mf;
	}

	public void setMf(MasterFile mf) {
		this.mf = mf;
	}

	@Override
	public MasterFile getObjectTree() {
		return mf;
	}

	@Override
	public List<Protocol> getProtocolList() {
		return protocols;
	}
}