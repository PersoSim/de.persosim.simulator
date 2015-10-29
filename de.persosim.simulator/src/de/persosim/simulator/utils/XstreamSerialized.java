package de.persosim.simulator.utils;

import com.thoughtworks.xstream.XStream;

/**
 * This {@link Serialized} implementation stores serialized data as a
 * {@link XStream} string.
 * 
 * @author mboonk
 *
 * @param <T>
 */
public class XstreamSerialized<T> implements Serialized<T> {

	private String serialization;

	/**
	 * @return the serialized object as {@link XStream} string
	 */
	public String getSerialization() {
		return serialization;
	}

	/**
	 * @param serialization the string created by {@link XStream}
	 */
	public XstreamSerialized(String serialization) {
		this.serialization = serialization;
	}
}
