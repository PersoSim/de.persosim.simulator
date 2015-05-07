package de.persosim.simulator.perso.xstream;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.persosim.simulator.protocols.NpaProtocol;
import de.persosim.simulator.protocols.auxVerification.AuxProtocol;
import de.persosim.simulator.protocols.ca.CaProtocol;
import de.persosim.simulator.protocols.file.FileProtocol;
import de.persosim.simulator.protocols.pace.PaceBypassProtocol;
import de.persosim.simulator.protocols.pace.PaceProtocol;
import de.persosim.simulator.protocols.pin.PinProtocol;
import de.persosim.simulator.protocols.ri.RiProtocol;
import de.persosim.simulator.protocols.ta.TaProtocol;

/**
 * This class is a converter which is responsible for serializing/deserializing all kind of protocol objects.
 * 
 * @author jgoeke
 *
 */
public class ProtocolConverter implements Converter {

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		
		String name = type.getName();
		if (name.toLowerCase().endsWith("protocol"))
			return true;
		else
			return false;
	}
	
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		// nothing to do
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) throws XStreamException {
		
		String protocolName = reader.getNodeName().substring(reader.getNodeName().lastIndexOf(".")+1);
		
		//XXX replace this static list by some dynamic construction method
		switch(protocolName){
		case "PaceProtocol":
			return new PaceProtocol();
		case "FileProtocol":
			return new FileProtocol();
		case "RiProtocol":
			return new RiProtocol();
		case "CaProtocol":
			return new CaProtocol();
		case "AuxProtocol":
			return new AuxProtocol();
		case "PinProtocol":
			return new PinProtocol();
		case "NpaProtocol":
			return new NpaProtocol();
		case "PaceBypassProtocol":
			return new PaceBypassProtocol();
		case "TaProtocol":
			return new TaProtocol();
		default:
			throw new XStreamException (protocolName + " is unknown, unmarshaling failed!");
		}
	}
}
