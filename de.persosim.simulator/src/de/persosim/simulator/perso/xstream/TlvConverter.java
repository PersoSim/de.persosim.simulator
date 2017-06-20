package de.persosim.simulator.perso.xstream;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectFactory;
import de.persosim.simulator.utils.HexString;

/**
 * This class is a converter which is responsible for converting all tlv data in hexstrings and back.
 * 
 * @author mboonk
 *
 */

public class TlvConverter implements Converter {

	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return TlvDataObject.class.isAssignableFrom(type);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		writer.setValue(HexString.encode(((TlvDataObject)value).toByteArray()));
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {	
		byte [] data = HexString.toByteArray(reader.getValue());
		
		try {
			return TlvDataObjectFactory.createTLVDataObject(data);
		} catch (RuntimeException e) {
			throw new XStreamException ("Object could not be unmarshalled as TlvDataObject.", e);
		}
	}
}
