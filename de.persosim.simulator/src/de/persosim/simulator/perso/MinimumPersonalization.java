package de.persosim.simulator.perso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.jaxb.PersoSimJaxbContextProvider;
import de.persosim.simulator.protocols.file.FileProtocol;
import de.persosim.simulator.secstatus.NullSecurityCondition;
import de.persosim.simulator.secstatus.SecCondition;

/**
 * This class represents a minimum of personalization e.g. for testing purposes.
 * The personalization contains an MF, an EF.CardAccess and the file management protocol.
 * Please note that the content of EF.CardAccess can be chosen arbitrarily and hence may neither be valid nor any TLV structure at all.
 * The content of EF.CardAccess primarily is intended for identification of the personalization.
 * 
 * @author slutters
 *
 */
public class MinimumPersonalization extends XmlPersonalization {
	
	protected byte[] efCardAccessValue;
	
	public MinimumPersonalization(byte[] efCardAccessValue) {
		this.efCardAccessValue = efCardAccessValue;
		
		buildProtocolList();
		buildObjectTree();
	}
	
	/**
	 * Build the default protocolList to only contain file management.
	 */
	private void buildProtocolList() {
		protocols = new ArrayList<>();

		/* load FM protocol */
		FileProtocol fileManagementProtocol = new FileProtocol();
		fileManagementProtocol.init();
		protocols.add(fileManagementProtocol);
	}
	
	/**
	 * Build the object tree.
	 * <p/>
	 * This contains a valid MF with an EF.CardSecurity containing arbitrary and possibly invalid data.
	 */
	public void buildObjectTree() {
		mf = new MasterFile(new FileIdentifier(0x3F00),
				new DedicatedFileIdentifier(new byte[] { (byte) 0xA0, 0x0,
						0x0, 0x2, 0x47, 0x10, 0x03 }));
		
		// add file to object tree
		ElementaryFile efCardAccess = new ElementaryFile(new FileIdentifier(
				0x011C), new ShortFileIdentifier(0x1C),
				efCardAccessValue,
				Arrays.asList((SecCondition) new NullSecurityCondition()),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		mf.addChild(efCardAccess);
	}
	
	public void writeToFile(String fileName) {
		try {
			// instantiate marshaller
			Marshaller m = PersoSimJaxbContextProvider.getContext().createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
					
			// Write to File
			File xmlFile = new File(fileName);
			m.marshal(this, xmlFile);
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
