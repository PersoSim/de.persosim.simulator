package de.persosim.simulator.perso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.MrzAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.Tr03110;

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
	 * This method returns the MRZ used for personalization.
	 * @return the MRZ used for personalization
	 */
	protected String getMrz() {
		Collection<CardObject> cardObjects = mf.findChildren(new AuthObjectIdentifier(Tr03110.ID_MRZ));
		
		MrzAuthObject mrzAuthObject = (MrzAuthObject) cardObjects.iterator().next();
		
		return mrzAuthObject.getMrz();
	}
	
	/**
	 * This method returns the requested password as set during personalization.
	 * Valid password identifiers as set in {@link Tr03110} e.g. are ID_MRZ, ID_CA, ID_PIN, ID_PUK.
	 * @param passwordIdentifier the password identifier
	 * @return the requested password as set during personalization
	 */
	protected byte[] getPassword(int passwordIdentifier) {
		Collection<CardObject> cardObjects = mf.findChildren(new AuthObjectIdentifier(passwordIdentifier));
		PasswordAuthObject pwdAuthObject = (PasswordAuthObject) cardObjects.iterator().next();
		return pwdAuthObject.getPassword();
	}
	
}