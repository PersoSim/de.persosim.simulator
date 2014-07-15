package de.persosim.simulator.perso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
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

public class XmlPersonalisationTest extends PersoSimTestCase {

	public static final String XML_FILENAME = "./tmp/perso-jaxb.xml";

	XmlPersonalisation testPerso;

	@Before
	public void setUp() throws Exception {
		//build/fill test perso

		testPerso = new XmlPersonalisation();
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
		XmlPersonalisation unmarshalledPerso = (XmlPersonalisation) um
				.unmarshal(new FileReader(xmlFile));
		assertNotNull(unmarshalledPerso);
	}

	/**
	 * Positive test: Ensure that the datastructure restored from the XML
	 * representation is identical to the input. As an equals comparison is
	 * impossible on a perso this comparison is performed on a second
	 * marshalled version.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_MarshallUnmarshallMarshall_StringBuffer() throws Exception {
		// instantiate marshaller
		Marshaller m = PersoSimJaxbContextProvider.getContext().createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Write to String
		StringWriter strWriter = new StringWriter();
		m.marshal(testPerso, strWriter);
		String marshalledPerso = strWriter.toString();
		System.out.println(marshalledPerso);
		
		//unmarshall from string
		StringReader sr = new StringReader(marshalledPerso);
		Unmarshaller um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
		XmlPersonalisation unmarshalledPerso = (XmlPersonalisation) um
				.unmarshal(sr);
		
		//marshall again
		StringWriter sndStrWriter = new StringWriter();
		m.marshal(unmarshalledPerso, sndStrWriter);
		String sndMarshalledPerso = sndStrWriter.toString();
		
		//assert that both marschalled persos are the same
		assertEquals(marshalledPerso, sndMarshalledPerso);

	}

	/**
	 * Positive test: Ensure that the unmarschalled Objects are of correct type
	 * <p/>
	 * In order to allow For all (as far as this can be checked with generic methods)
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_MarshallUnmarshall_StringBuffer_checkObjectTypes() throws Exception {
		// instantiate marshaller
		Marshaller m = PersoSimJaxbContextProvider.getContext().createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Marshall/unmarshall through StringBuffer
		StringWriter strWriter = new StringWriter();
		m.marshal(testPerso, strWriter);
		StringReader sr = new StringReader(strWriter.toString());
		Unmarshaller um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
		XmlPersonalisation unmarshalledPerso = (XmlPersonalisation) um
				.unmarshal(sr);
		
		//check all CardObjects, their children and all Identifiers of the card objet tree
		assertObjectTypes(unmarshalledPerso.getObjectTree());

	}

	/**
	 * Checks whether a given CardObject contains only children of type
	 * {@link CardObject} and identifiers of type {@link CardObjectIdentifier}
	 * <p/>
	 * This check is executed recursively and throws an AssertionError when any check fails.
	 * 
	 * @param objectToCheck
	 */
	private void assertObjectTypes(CardObject objectToCheck) {
		// check identifiers
		for (Object curIdentifier : objectToCheck.getAllIdentifiers()) {
			if (curIdentifier == null) continue; 
			assertTrue("Wrong identifier type (identifier <" + curIdentifier + ">on object <"+ objectToCheck + ">)", curIdentifier instanceof CardObjectIdentifier);
		}
		
		// check children (recursive)
		for (Object curChild : objectToCheck.getChildren()) {
			assertTrue("Wrong child type (child <" + curChild
					+ ">on object <" + objectToCheck + ">)",
					curChild instanceof CardObject);
			assertObjectTypes((CardObject) curChild);
		}
		
	}

}
