package de.persosim.simulator.perso.xstream;

import static org.globaltester.logging.BasicLogger.log;

import java.util.ArrayList;
import java.util.List;

import org.globaltester.logging.tags.LogLevel;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.platform.AbstractCommandProcessor;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.protocols.Protocol;

/**
 * This class is a converter which is responsible for converting
 * CommandProcessor objects.
 * 
 * @author cstroh
 *
 */

public class CommandProcessorConverter implements Converter {

	private MasterFile masterFile;
	private List<Protocol> protocols;

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return AbstractCommandProcessor.class.isAssignableFrom(clazz);
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
		CommandProcessor commandProcessor = (CommandProcessor) object;

		MasterFile mfToMarshal = commandProcessor.getMasterFile();
		List<Protocol> protocolsToMarshal = commandProcessor.getProtocolList();

		writer.startNode("masterFile");
		context.convertAnother(mfToMarshal);
		writer.endNode();

		writer.startNode("protocols");
		context.convertAnother(protocolsToMarshal);
		writer.endNode();
	}

	@SuppressWarnings("unchecked")
	public void getValuesFromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String nodeName = reader.getNodeName();
			switch (nodeName) {
			case "masterFile":
				masterFile = (MasterFile) context.convertAnother(reader, MasterFile.class);
				break;
			case "protocols":
				protocols = (List<Protocol>) context.convertAnother(reader, ArrayList.class);
				break;
			}

			if (reader.hasMoreChildren()) {
				getValuesFromXML(reader, context);
			}
			reader.moveUp();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		if (reader.getNodeName().toLowerCase().endsWith("commandprocessor")) {
			getValuesFromXML(reader, context);
		}

		if (masterFile == null || protocols == null) {
			String message = "can not create CommandProcessor object, unmarshal failed!";
			log(getClass(), message, LogLevel.ERROR);
			throw new XStreamException(message);
		}
		try {
			return new CommandProcessor(protocols, masterFile);
		} catch (AccessDeniedException e) {
			String message = "can not create CommandProcessor object, unmarshal failed!";
			log(getClass(), message, LogLevel.ERROR);
			throw new XStreamException(message);
		}
	}

}
