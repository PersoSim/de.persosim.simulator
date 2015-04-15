package de.persosim.simulator.perso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.RenderingHints.Key;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.KeyObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.ProtocolStateMachine;
import de.persosim.simulator.protocols.ca.CaProtocol;
import de.persosim.simulator.protocols.file.FileProtocol;
import de.persosim.simulator.protocols.pace.PaceProtocol;
import de.persosim.simulator.protocols.ta.TaProtocol;
import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.test.PersoSimTestCase;

public class XmlPersonalizationTest extends PersoSimTestCase {

	XmlPersonalization testPerso;

	@Before
	public void setUp() throws Exception {
		//build/fill test perso

		testPerso = new XmlPersonalization();
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
	@Ignore
	public void test_MarshallUnmarshall_File() throws Exception {
		// instantiate marshaller
		Marshaller m = PersoSimJaxbContextProvider.getContext().createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Write to File
		File xmlFile = new File(getXmlFilename());
		xmlFile.getParentFile().mkdirs();
		m.marshal(testPerso, xmlFile);

		// get variables from our xml file, created before
		Unmarshaller um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
		XmlPersonalization unmarshalledPerso = (XmlPersonalization) um
				.unmarshal(new FileReader(xmlFile));
		assertNotNull(unmarshalledPerso);
	}
	
	/**
	 * Positive test test marshalling/unmarshalling the testPerso to/from a temporary file in the filesystem.
	 * @throws Exception
	 */
	@Test
	public void test_MarshallUnmarshall_FileWithXStream() throws Exception {
		// instantiate marshaller
//		StaxDriver drv = new StaxDriver();
//		XStream xstream = new XStream(drv);
		XStream xstream = new XStream(new DomDriver("UTF8")) {
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				return new MapperWrapper(next) {
					@SuppressWarnings("rawtypes")
					public boolean shouldSerializeMember(Class definedIn,
							String fieldName) {

						if (definedIn.getName().startsWith("de.persosim.simulator.protocols.") || 
								definedIn.getName().equals("de.persosim.simulator.statemachine.AbstractStateMachine") ||
								definedIn.getName().equals("de.persosim.simulator.perso.AbstractProfile")) {
							return false;
						}
//						System.out.println(definedIn.getName() + "\n");
						return super
								.shouldSerializeMember(definedIn, fieldName);
					}
				};
			}
		};
		
		xstream.registerConverter(new EncodedByteArrayConverter());
//		xstream.alias("BCECPrivateKey", BCECPrivateKey.class);
//		xstream.omitField(KeyObject.class, "BCECPrivateKey");
		

//		xstream.setMode(XStream.SINGLE_NODE_XPATH_RELATIVE_REFERENCES);
		xstream.setMode(XStream.ID_REFERENCES);
		
		xstream.registerConverter(new KeyValueAdapter());
		
//		xstream.omitField(A.class, "protocols");
		
		
		// Write to File
		String xml = xstream.toXML(testPerso);
		
		File xmlFile = new File(getXmlFilename());
		xmlFile.getParentFile().mkdirs();
		
		StringWriter writer = new StringWriter();

		TransformerFactory ft = TransformerFactory.newInstance();
		ft.setAttribute("indent-number", new Integer(2));
		
		Transformer transformer = ft.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		transformer.transform(new StreamSource(new StringReader(xml)),
				new StreamResult(writer));

		OutputStreamWriter char_output = new OutputStreamWriter(
				new FileOutputStream(xmlFile), "UTF-8");
		char_output.append(writer.getBuffer());
		char_output.flush();
		char_output.close();

		// get variables from our xml file, created before
		XmlPersonalization unmarshalledPerso = (XmlPersonalization) xstream.fromXML(xmlFile);
//		assertNotNull(unmarshalledPerso);
	}

	/**
	 * Positive test: Ensure that the datastructure restored from the XML
	 * representation is identical to the input. As an equals comparison is
	 * impossible on a perso this comparison is performed on a second
	 * marshalled version.
	 * 
	 * @throws Exception
	 */
	@Ignore
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
		XmlPersonalization unmarshalledPerso = (XmlPersonalization) um
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
	@Ignore
	public void test_MarshallUnmarshall_StringBuffer_checkObjectTypes() throws Exception {
		// instantiate marshaller
		Marshaller m = PersoSimJaxbContextProvider.getContext().createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Marshall/unmarshall through StringBuffer
		StringWriter strWriter = new StringWriter();
		m.marshal(testPerso, strWriter);
		StringReader sr = new StringReader(strWriter.toString());
		Unmarshaller um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
		XmlPersonalization unmarshalledPerso = (XmlPersonalization) um
				.unmarshal(sr);
		
		//check all CardObjects, their children and all Identifiers of the card objet tree
		assertObjectTypes(unmarshalledPerso.getObjectTree());

	}

	protected String getXmlFilename() {
		String retVal = "./tmp/" + testPerso.getClass().getSimpleName() + ".xml"; 
		return retVal;
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
