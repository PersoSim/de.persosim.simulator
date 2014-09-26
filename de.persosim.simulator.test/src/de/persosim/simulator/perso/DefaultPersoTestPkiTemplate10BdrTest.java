package de.persosim.simulator.perso;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.jaxb.PersoSimJaxbContextProvider;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.ca.CaProtocol;
import de.persosim.simulator.protocols.file.FileProtocol;
import de.persosim.simulator.protocols.pace.PaceProtocol;
import de.persosim.simulator.protocols.ta.TaProtocol;
import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.test.PersoSimTestCase;

public class DefaultPersoTestPkiTemplate10BdrTest extends PersoSimTestCase {
	
	public static final String XML_FILENAME = "./tmp/persoTestPkiTemplate10Bdr-jaxb.xml";

	XmlPersonalization testPerso;

	@Before
	public void setUp() throws Exception {
		//build/fill test perso

		testPerso = new DefaultPersoTestPkiTemplate10Bdr();
		List<Protocol> protocolList = testPerso.getProtocolList();
		protocolList.add(new FileProtocol());
		protocolList.add(new PaceProtocol());
		protocolList.add(new TaProtocol());
		protocolList.add(new CaProtocol());

		
		MasterFile mf = testPerso.getMf();
		mf.addChild(new ElementaryFile(new FileIdentifier(0x1C),
				null, new byte[] {}, Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet(), Collections
						.<SecCondition> emptySet()));
	}
	
	/**
	 * Positive test test marshalling/unmarshalling the testPerso to/from a temporary file in the filesystem.
	 * @throws Exception
	 */
	@Test
	public void test_MarshallUnmarshall_File() throws Exception {
		// instantiate marshaller
		Marshaller m = PersoSimJaxbContextProvider.getContext().createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Write to File
		File xmlFile = new File(XML_FILENAME);
		xmlFile.getParentFile().mkdirs();
		m.marshal(testPerso, xmlFile);

		// get variables from our xml file, created before
		Unmarshaller um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
		XmlPersonalization unmarshalledPerso = (XmlPersonalization) um
				.unmarshal(new FileReader(xmlFile));
		assertNotNull(unmarshalledPerso);
	}

}
