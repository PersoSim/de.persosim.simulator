package de.persosim.simulator.perso;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
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
	protected List<Protocol> protocols = new ArrayList<>();
	
	@XmlAnyElement(lax=true)
	protected MasterFile mf = new MasterFile();
	
	@XmlElementWrapper(name = "unmarshallerCallbacks")
	@XmlAnyElement(lax=true)
	protected List<PersoUnmarshallerCalback> unmarshallerCallbacks = new ArrayList<>();
	
	public List<Protocol> getProtocols() {
		return protocols;
	}

	public MasterFile getMf() {
		return mf;
	}

	@Override
	public MasterFile getObjectTree() {
		return mf;
	}

	@Override
	public List<Protocol> getProtocolList() {
		return protocols;
	}
	
	/**
	 * JAXB callback
	 * <p/>
	 * Used to fix the parent relation
	 * @param u
	 * @param parent
	 */
	protected void afterUnmarshal(Unmarshaller u, Object parent) {
		if (unmarshallerCallbacks != null) {
			for (PersoUnmarshallerCalback curPostProcessor : unmarshallerCallbacks) {
				curPostProcessor.afterUnmarshall(this);	
			}
		}
	}
}