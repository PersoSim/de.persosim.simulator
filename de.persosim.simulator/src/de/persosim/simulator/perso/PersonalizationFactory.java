package de.persosim.simulator.perso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

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

public class PersonalizationFactory {
	


	public static void marshal(Personalization pers, Writer writer) {
		XStream xstream = getXStream();
		xstream.toXML(pers, writer);
	}
	
	
	public static void marshal(Personalization pers, String path) {
		//FIXME JGE reduce this method to the one above
		
		XStream xstream = getXStream();
		
		String xml = xstream.toXML(pers);
		
		xml = xml.replaceAll("class=\"org.*[Kk]ey\"", ""); //FIXME JGE what does this line mean here?

		// Write to File
		File xmlFile = new File(path);
		xmlFile.getParentFile().mkdirs();
		
		StringWriter writer = new StringWriter();

		TransformerFactory ft = TransformerFactory.newInstance();
		ft.setAttribute("indent-number", new Integer(2));
		
		Transformer transformer;
		try {
			transformer = ft.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new StreamSource(new StringReader(xml)),
					new StreamResult(writer));

			OutputStreamWriter char_output = new OutputStreamWriter(
					new FileOutputStream(xmlFile), "UTF-8");
			char_output.append(writer.getBuffer());
			char_output.flush();
			char_output.close();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public static Personalization unmarshal(Reader reader) {
		// TODO Auto-generated method stub
		XStream xstream = getXStream();
		return (Personalization) xstream.fromXML(reader);
	}
	
//FIXME JGE this method should be named unmarshal
	public static Personalization unmarchal(String path) {
		//FIXME JGE reduce this method to the one above
		
		XStream xstream = getXStream();
		
		File xmlFile = new File(path);
		xmlFile.getParentFile().mkdirs();
		
		// get variables from our xml file, created before
		Personalization unmarshalledPerso = (Personalization) xstream.fromXML(xmlFile);
		
		return unmarshalledPerso;
	}
	
	
	private static XStream getXStream() {
		
		XStream xstream = new XStream(new DomDriver("UTF8"))
		{
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) 
			{
				return new MapperWrapper(next) {
					@SuppressWarnings("rawtypes")
					public boolean shouldSerializeMember(Class definedIn,
							String fieldName) {

						if (definedIn.getName().equals("de.persosim.simulator.perso.AbstractProfile")) {
							return false;
						}
						return super
								.shouldSerializeMember(definedIn, fieldName);
					}
				};
			}
		};

		xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
		xstream.setMode(XStream.ID_REFERENCES);

		
		xstream.registerConverter(new EncodedByteArrayConverter());
		xstream.registerConverter(new ProtocolConverter());
		xstream.registerConverter(new KeyConverter());
		
		return xstream;
	}

}
