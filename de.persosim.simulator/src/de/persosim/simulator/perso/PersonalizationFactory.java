package de.persosim.simulator.perso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.persosim.simulator.perso.xstream.EncodedByteArrayConverter;
import de.persosim.simulator.perso.xstream.KeyConverter;
import de.persosim.simulator.perso.xstream.ProtocolConverter;

/**
 * This class provides methods that serializes Personalization objects
 * 
 * @author jge
 *
 */

public class PersonalizationFactory {
	
	/**
	 * This method serializes the personalization object
	 * @param pers object which contains the whole personalization
	 * @param writer object which will be filled with the serialized personalization
	 * @return a StringWriter with the serialized personalization object
	 */
	public static void marshal(Object pers, StringWriter writer) {
		
		XStream xstream = getXStream();
		StringWriter xmlWriter = new StringWriter();
		xstream.toXML (pers, xmlWriter);
		
		TransformerFactory ft = TransformerFactory.newInstance();
		//ft.setAttribute("indent-number", new Integer(2)); 
		Transformer transformer;
		try {
			transformer = ft.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform (new StreamSource(new StringReader(xmlWriter.toString())),
					new StreamResult (writer));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	public static void marshal (Object pers, String path) {

		File xmlFile = new File (path);
		xmlFile.getParentFile().mkdirs();
		
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter (path);
			marshal (pers, fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
		
	public static void marshal (Object pers, FileWriter file) {
		StringWriter writer = new StringWriter();
		marshal (pers, writer);
		//TODO find a alternative to suppress the class attribute, created by xStream, if element is a type of Key
		//xml = xml.replaceAll("class=\"org.*[Kk]ey\"", "");
		try {
			file.write (writer.toString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * This method deserializes the personalization object
	 * @param reader object which the personalization
	 * @return a object with a deserialized personalization
	 */
	public static Object unmarshal (Reader reader) {
		
		XStream xstream = getXStream();
		return xstream.fromXML (reader);
	}
	
	public static Object unmarshal (String path) throws FileNotFoundException {
		
		File xmlFile = new File(path);
		if (!xmlFile.exists()) {
			throw new FileNotFoundException (path + " does not exist");
		}
		return unmarshal (new FileReader(path));
	}
	
	/**
	 * This method creates a xStream object with all necessary configuration
	 * @return a xStream object
	 */
	private static XStream getXStream() {
		
		XStream xstream = new XStream (new DomDriver("UTF-8"))
		{
			@Override
			protected MapperWrapper wrapMapper (MapperWrapper next) 
			{
				return new MapperWrapper(next) {
					@SuppressWarnings("rawtypes")
					public boolean shouldSerializeMember(Class definedIn,
							String fieldName) {

						if (definedIn.getName().equals ("de.persosim.simulator.perso.AbstractProfile")) {
							return false;
						}
						return super
								.shouldSerializeMember (definedIn, fieldName);
					}
				};
			}
		};

		xstream.setMode (XStream.XPATH_RELATIVE_REFERENCES);
		xstream.setMode (XStream.ID_REFERENCES);
		xstream.registerConverter (new EncodedByteArrayConverter());
		xstream.registerConverter (new ProtocolConverter());
		xstream.registerConverter (new KeyConverter());		
		return xstream;
	}
}
