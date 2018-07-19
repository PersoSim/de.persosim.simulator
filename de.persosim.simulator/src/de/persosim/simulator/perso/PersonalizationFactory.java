package de.persosim.simulator.perso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import org.globaltester.lib.xstream.XstreamFactory;
import org.globaltester.logging.BasicLogger;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.persosim.simulator.Activator;
import de.persosim.simulator.perso.xstream.CommandProcessorConverter;
import de.persosim.simulator.perso.xstream.ECParameterSpecConverter;
import de.persosim.simulator.perso.xstream.EncodedByteArrayConverter;
import de.persosim.simulator.perso.xstream.KeyConverter;
import de.persosim.simulator.perso.xstream.KeyPairConverter;
import de.persosim.simulator.perso.xstream.ProtocolConverter;
import de.persosim.simulator.perso.xstream.TlvConverter;

/**
 * This class provides methods that serializes/deserializes personalization objects
 * 
 * @author jgoeke
 *
 */
public class PersonalizationFactory {
	
	/**
	 * This method serializes the personalization object and writes it into a given writer
	 * @param pers object which contains the whole personalization
	 * @param writer object which will be filled with the serialized personalization
	 */
	public static void marshal(Object pers, StringWriter writer) throws NullPointerException {
		if (pers == null) {
			throw new NullPointerException ("Personalization object is null!");
		}
		XStream xstream = getXStream();
		xstream.autodetectAnnotations(true);
		
		StringWriter xmlWriter = new StringWriter();
		xstream.toXML (pers, xmlWriter);
		
		//IMPL find a alternative to suppress the class attribute, created by xStream, if element is a type of Key
		String xmlRepresentation = xmlWriter.toString();
		xmlRepresentation = xmlRepresentation.replaceAll("class=\"org.*[Kk]ey\"", "");
		writer.append(xmlRepresentation);
	}
	
	public static void marshal (Object pers, String path) {		
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter (path);
			marshal (pers, fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public static void marshal (Object pers, FileWriter file) throws NullPointerException {
		StringWriter writer = new StringWriter();
		marshal (pers, writer);
		if (file == null) {
			throw new NullPointerException ("FileWriter object is null!");
		} 
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
	public static Object unmarshal (Reader reader) throws NullPointerException {
		if (reader == null) {
			throw new NullPointerException ("Reader object is null!");
		}
		XStream xstream = getXStream();
		return xstream.fromXML (reader);
	}
	
	public static Object unmarshal(InputStream stream) {
		return unmarshal(new InputStreamReader(stream));
	}
	
	public static Object unmarshal (String path) throws FileNotFoundException {	
		File xmlFile = new File(path);
		if (xmlFile.exists()) {
			BasicLogger.log(PersonalizationFactory.class, "File at " + path + " found");
		} else{
			throw new FileNotFoundException ("File at " + path + " NOT found");
		}
		return unmarshal (new FileReader(path));
	}
	
	/**
	 * This method creates a xStream object with all necessary configuration
	 * @return a xStream object
	 */
	private static XStream getXStream() {
		
		DomDriver domDriver = new DomDriver("UTF-8");
		ShouldSerializeMemberImpl ssm = new ShouldSerializeMemberImpl();
		XStream xstream = XstreamFactory.get(domDriver, ssm, PersonalizationFactory.class.getClassLoader());
		
		xstream.ignoreUnknownElements();

		xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
		xstream.setMode(XStream.ID_REFERENCES);
		
		xstream.registerConverter(new EncodedByteArrayConverter());
		xstream.registerConverter(new ProtocolConverter());
		xstream.registerConverter(new KeyPairConverter());
		xstream.registerConverter(new ECParameterSpecConverter());
		xstream.registerConverter(new KeyConverter());
		xstream.registerConverter(new TlvConverter());
		xstream.registerConverter(new CommandProcessorConverter());
		
		// get converters as services
		if (Activator.getContext() != null) {
			ServiceTracker<Converter, Converter> serviceTracker = new ServiceTracker<Converter, Converter>(
					Activator.getContext(), Converter.class.getName(), null);
			serviceTracker.open();
			ServiceReference<Converter>[] allServiceReferences = serviceTracker.getServiceReferences();
			StringBuilder availableConverters = new StringBuilder();
			availableConverters.append("Available xstream converter services:");
			if (allServiceReferences != null) {
				for (ServiceReference<Converter> serviceReference : allServiceReferences) {
					Converter service = serviceTracker.getService(serviceReference);
					availableConverters.append("\n " + service.getClass() + " from bundle: "
							+ serviceReference.getBundle().getSymbolicName());
					((CompositeClassLoader) xstream.getClassLoader()).add(service.getClass().getClassLoader());
					xstream.registerConverter(service, 10);
				}
			} else {
				availableConverters.append(" none");
			}
			serviceTracker.close();

			BasicLogger.log(PersonalizationFactory.class, availableConverters.toString());
		} else {
			BasicLogger.log(PersonalizationFactory.class,
					"Could not get the bundle context, no Converter services added to XStream");
		}

		return xstream;
	}
}
