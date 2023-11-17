package de.persosim.simulator.utils;

import java.util.HashSet;

import org.globaltester.cryptoprovider.Crypto;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * This class contains methods to serialize and deserialize objects. The format
 * for serialized data is subject to change and can not be depended upon.
 * 
 * @author mboonk
 *
 */
public class Serializer {

	private static XStream xstream;
	private static HashSet<ClassLoader> loaders;

	static {
		CompositeClassLoader loader = new CompositeClassLoader();
		
		loaders = new HashSet<>();
		loaders.add(Thread.currentThread().getContextClassLoader());
		loaders.add(Crypto.getCryptoProvider().getClass().getClassLoader());
		
		for (ClassLoader current:loaders){
			loader.add(current);
		}
				
		xstream = new XStream(new DomDriver("UTF-8"));
		xstream.addPermission(AnyTypePermission.ANY); // allow all; no limitations for deserialization
		xstream.setClassLoader(loader);
	}
	
	private Serializer() {
		// do nothing
	}
	
	/**
	 * Creates a deep copy of the given object.
	 * 
	 * @param objectToCopy
	 * @return the object copy
	 */
	public static <T> T deepCopy(T objectToCopy) {
		return deserialize(serialize(objectToCopy));
	}
	
	private static void updateLoaders(Object object){
		if(object != null) {
			ClassLoader newLoader = object.getClass().getClassLoader();
			if (!loaders.contains(newLoader)){
				loaders.add(newLoader);
				((CompositeClassLoader)xstream.getClassLoader()).add(newLoader);
			}
		}
	}

	/**
	 * Serializes an object to a format that can be correctly deserialized by
	 * the deserialization methods in this class.
	 * 
	 * @param toSerialize
	 *            an object to serialize
	 * @return the serialized representation
	 */
	public static <T> Serialized<T> serialize(T toSerialize) {
		updateLoaders(toSerialize);
		return new XstreamSerialized<>(xstream.toXML(toSerialize));
	}

	/**
	 * Deserialize a representation created by serialization using this class.
	 * 
	 * @param serialized the serialized object
	 * @return the deserialized object
	 * @throws IllegalArgumentException
	 *             when the serialized is not an {@link XstreamSerialized}
	 *             object
	 */
	public static <T> T deserialize(Serialized<T> serialized) {
		if (serialized instanceof XstreamSerialized<?>) {
			@SuppressWarnings("unchecked")
			T result = (T) xstream.fromXML(((XstreamSerialized<?>) serialized).getSerialization());
			return result;
		}
		throw new IllegalArgumentException("The serialization was not created using this class");
	}
}
