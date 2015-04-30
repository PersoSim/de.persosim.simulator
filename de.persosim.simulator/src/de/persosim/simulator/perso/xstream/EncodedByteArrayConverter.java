package de.persosim.simulator.perso.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.persosim.simulator.utils.*;

/**
 * This class is a converter which is responsible for converting all byte-arrays in hexstrings and back.
 * 
 * @author jge
 *
 */

public class EncodedByteArrayConverter implements Converter {

	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return type.isArray() && type.getComponentType().equals(byte.class);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		writer.setValue(HexString.encode((byte[])value));
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		
		String data = reader.getValue();		
		return HexString.toByteArray(data);
	}
}