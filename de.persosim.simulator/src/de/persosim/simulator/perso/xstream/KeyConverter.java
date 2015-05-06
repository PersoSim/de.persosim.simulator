package de.persosim.simulator.perso.xstream;

import static de.persosim.simulator.utils.PersoSimLogger.ERROR;
import static de.persosim.simulator.utils.PersoSimLogger.log;


import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.utils.HexString;

/**
 * This class is a converter which is responsible for marshal and unmarshal all king of key objects.
 * 
 * @author jge
 *
 */
public class KeyConverter implements Converter {
	String keyType = "";
	String algorithmValue = "";
	String byteValue = "";
	
	
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		String name = type.getName();
		if (name.toLowerCase().endsWith("publickey") || name.toLowerCase().endsWith("privatekey"))
			return true;
		else
			return false;
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Key key = (Key) value;
		
		writer.startNode("algorithm");
		writer.setValue(key.getAlgorithm());
		writer.endNode();
		writer.startNode("value");
		writer.setValue(HexString.encode(key.getEncoded()));
		writer.endNode();
	}
	
	public void getValuesFromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
		while (reader.hasMoreChildren()) {
			keyType = reader.getNodeName().toLowerCase();
			reader.moveDown();
			String nodeName = reader.getNodeName();
			switch(nodeName) {
			case "algorithm":
				algorithmValue  = reader.getValue().replace("\n", "").replace(" ", "");
				break;
			case "value":
				byteValue = reader.getValue().replace("\n", "").replace(" ", "");
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
			UnmarshallingContext context) throws NullPointerException {

		PrivateKey sk = null;
		PublicKey pk = null;
		
		if (reader.getNodeName().toLowerCase().endsWith("key")) {
			getValuesFromXML (reader, context);
		}
		
		if (byteValue == null || algorithmValue == null) {
			log(ECParameterSpecConverter.class, "can not create "+ keyType +" object, unmarshal failed", ERROR);
			throw new NullPointerException ("can not create "+ keyType +" object, unmarshal failed!");
		}
		
		PKCS8EncodedKeySpec  ks_priv = new PKCS8EncodedKeySpec (HexString.toByteArray(byteValue));
		X509EncodedKeySpec  ks_pub = new X509EncodedKeySpec (HexString.toByteArray(byteValue));
		
		try {
			if (keyType.equals("publickey"))
				pk = KeyFactory.getInstance(algorithmValue, Crypto.getCryptoProvider()).generatePublic(ks_pub);
			else if (keyType.equals("privatekey"))
				sk = KeyFactory.getInstance(algorithmValue, Crypto.getCryptoProvider()).generatePrivate(ks_priv);
		} catch (InvalidKeySpecException| NoSuchAlgorithmException e) {
			log(KeyConverter.class, "Invalid KeySpec or Algorithm during unmarshal", ERROR);
			e.printStackTrace();
		}
		
		switch(keyType) {
		case "publickey":
			return pk;
		case "privatekey":
			return sk;
		default: return null;
		}
	}
}
