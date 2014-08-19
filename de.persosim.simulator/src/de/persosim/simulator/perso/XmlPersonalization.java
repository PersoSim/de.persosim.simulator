package de.persosim.simulator.perso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.jaxb.PersoSimJaxbContextProvider;
import de.persosim.simulator.protocols.Protocol;

@XmlRootElement(name="Personalization")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlPersonalization implements Personalization {

	@XmlElementWrapper(name = "protocols")
	@XmlAnyElement(lax=true)
	protected List<Protocol> protocols = new ArrayList<>();
	
	@XmlAnyElement(lax=true)
	protected MasterFile mf = new MasterFile();
	
	@XmlElementWrapper(name = "unmarshallerCallbacks")
	@XmlAnyElement(lax=true)
	protected List<PersoUnmarshallerCallback> unmarshallerCallbacks = new ArrayList<>();
	
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
			for (PersoUnmarshallerCallback curPostProcessor : unmarshallerCallbacks) {
				curPostProcessor.afterUnmarshall(this);	
			}
		}
	}
	
	/**
	 * This method writes a personalization to a file identified by a provided file name.
	 * @param fileName the file name to use
	 */
	public void writeToFile(String fileName) {
		try {
			// instantiate marshaller
			Marshaller m = PersoSimJaxbContextProvider.getContext().createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
					
			// Write to File
			File xmlFile = new File(fileName);
			m.marshal(this, xmlFile);
		} catch (PropertyException e) {
			// FIXME SLS Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// FIXME SLS Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}