package de.persosim.simulator.perso.xstream;

import static de.persosim.simulator.utils.PersoSimLogger.ERROR;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class is a converter which is responsible for for serializing/deserializing all kind of keypair objects.
 * 
 * @author jgoeke
 *
 */
public class KeyPairConverter implements Converter {
	PrivateKey sk = null;
	PublicKey pk = null;

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz.equals(KeyPair.class);
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		
		KeyPair key = (KeyPair) object;
		
		PrivateKey sk = key.getPrivate();
		PublicKey pk = key.getPublic();
		
		writer.startNode("privateKey");
		context.convertAnother(sk, new KeyConverter());
		writer.endNode();
		
		writer.startNode("publicKey");
		context.convertAnother(pk, new KeyConverter());
		writer.endNode();
	}
	
	public void getValuesFromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String nodeName = reader.getNodeName();
			switch(nodeName) {
			case "privateKey":
				sk = (PrivateKey) context.convertAnother(reader, PrivateKey.class, new KeyConverter());
				break;
			case "publicKey":
				pk = (PublicKey) context.convertAnother(reader, PublicKey.class, new KeyConverter());
				break;
			}
			
			if(reader.hasMoreChildren()) {
				getValuesFromXML(reader, context);
			}
			reader.moveUp();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		if (reader.getNodeName().toLowerCase().endsWith("keypair")) {
			getValuesFromXML (reader, context);
		}
		
		if (pk == null || sk == null) {
			String message = "can not create keypair object, unmarshal failed!";
			log(getClass(), message, ERROR);
			throw new XStreamException (message);
		}
		return new KeyPair(pk, sk);
	}

}
