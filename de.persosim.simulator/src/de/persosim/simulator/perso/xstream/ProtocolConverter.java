package de.persosim.simulator.perso.xstream;

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

public class ProtocolConverter implements Converter {

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		//FIXME JGE Why this static list?
		if (type.equals(PaceProtocol.class))
			return true;
		else if (type.equals(FileProtocol.class))
			return true;
		else if (type.equals(TaProtocol.class))
			return true;
		else if (type.equals(RiProtocol.class))
			return true;
		else if (type.equals(CaProtocol.class))
			return true;
		else if (type.equals(AuxProtocol.class))
			return true;
		else if (type.equals(PinProtocol.class))
			return true;
		else if (type.equals(NpaProtocol.class))
			return true;
		else if (type.equals(PaceBypassProtocol.class))
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
			UnmarshallingContext context) {

		String protocolName = reader.getNodeName().substring(reader.getNodeName().lastIndexOf(".")+1);
		
		
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
			return null;
		}
	}

}
