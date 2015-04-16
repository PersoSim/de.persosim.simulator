package de.persosim.simulator.perso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
	protected List<Protocol> protocols = null;
	
	@XmlAnyElement(lax=true)
	protected MasterFile mf = null;
	
	//@XmlElementWrapper(name = "unmarshallerCallbacks")
	//@XmlAnyElement(lax=true)
	//protected List<PersoUnmarshallerCallback> unmarshallerCallbacks = new ArrayList<>();
	
	public List<Protocol> getProtocols() {
		if (protocols == null) reset();
		return protocols;
	}

	public MasterFile getMf() {
		if (mf == null) reset();
		return mf;
	}

	@Override
	public MasterFile getObjectTree() {
		return getMf();
	}

	@Override
	public List<Protocol> getProtocolList() {
		return getProtocols();
	}

	@Override
	public void reset() {
		buildProtocolList();
		buildObjectTree();
	}

	/**
	 * (Re)Build the protocol list (in {@link #protocols})
	 * <p/>
	 * This method is called from {@link #reset()} and should be implemented at
	 * least in all Subclasses that are used within tests that need to reset the
	 * personalization.
	 */
	protected void buildProtocolList() {
		// initialize empty protocol list but do not overwrite a deserialized perso
		if (protocols == null) {
			protocols = new ArrayList<>();	
		}
	}
	
	/**
	 * (Re)Build the Object tree (in {@link #mf})
	 * <p/>
	 * This method is called from {@link #reset()} and should be implemented at
	 * least in all Subclasses that are used within tests that need to reset the
	 * personalization.
	 */
	protected void buildObjectTree() {
		// initialize empty protocol list but do not overwrite a deserialized perso
		if (mf == null) {
			mf = new MasterFile();
		}
	}


	/**
	 * This method writes a personalization to a file identified by a provided file name.
	 * @param fileName the file name to use
	 * @throws JAXBException 
	 */
	public void writeToFile(String fileName) throws JAXBException {
		// instantiate marshaller
		Marshaller m = PersoSimJaxbContextProvider.getContext().createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Write to File
		File xmlFile = new File(fileName);
		m.marshal(this, xmlFile);
	}
	
}