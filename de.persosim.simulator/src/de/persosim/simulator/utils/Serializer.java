package de.persosim.simulator.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This class contains methods to serialize and deserialize objects. The format
 * for serialized data is subject to change and can not be depended upon.
 * 
 * @author mboonk
 *
 */
public class Serializer {

	private static XStream xstream = new XStream(new DomDriver("UTF-8"));

	/**
	 * Creates a deep copy of the given object.
	 * 
	 * @param objectToCopy
	 * @return the object copy
	 */
	public static <T> T deepCopy(T objectToCopy) {
		return (T) deserialize(serialize(objectToCopy));
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
		return new XstreamSerialized<T>(xstream.toXML(toSerialize));
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
