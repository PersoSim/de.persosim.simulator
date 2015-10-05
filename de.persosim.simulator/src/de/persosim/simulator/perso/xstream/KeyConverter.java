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
import de.persosim.simulator.crypto.certificates.CvKey;
import de.persosim.simulator.utils.HexString;

/**
 * This class is a converter which is responsible for for serializing/deserializing all kind of key objects.
 * 
 * @author jgoeke
 *
 */
public class KeyConverter implements Converter {
	String keyType = "";
	String algorithmValue = "";
	String byteValue = "";
	
	
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		if (CvKey.class.isAssignableFrom(type)){
			return false;
		}
		
		if (Key.class.isAssignableFrom(type)){
			return true;
		}
		
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
			UnmarshallingContext context) {

		PrivateKey sk = null;
		PublicKey pk = null;
		
		getValuesFromXML (reader, context);
		
		if (byteValue == null || algorithmValue == null || algorithmValue.equals("") || byteValue.equals("")) {
			log(getClass(), "can not create "+ keyType +" object, unmarshal failed", ERROR);
			throw new NullPointerException ("can not create "+ keyType +" object, unmarshal failed!");
		}
		
		PKCS8EncodedKeySpec  ks_priv = new PKCS8EncodedKeySpec (HexString.toByteArray(byteValue));
		X509EncodedKeySpec  ks_pub = new X509EncodedKeySpec (HexString.toByteArray(byteValue));
		
		//XXX split into private and public key converters
		try {
			pk = KeyFactory.getInstance(algorithmValue, Crypto.getCryptoProvider()).generatePublic(ks_pub);
		} catch (InvalidKeySpecException| NoSuchAlgorithmException e1) {
			log(getClass(), "this is not a valid public key", ERROR);
			
			try {
				sk = KeyFactory.getInstance(algorithmValue, Crypto.getCryptoProvider()).generatePrivate(ks_priv);
			} catch (InvalidKeySpecException| NoSuchAlgorithmException e2) {
				log(getClass(), "this is also not a valid private key", ERROR);
				log(getClass(), "Invalid KeySpec or Algorithm during unmarshal", ERROR);
				e2.printStackTrace();
			}
		}
		
		if (pk != null) {
			return pk;
		} else {
			return sk;
		}
	}
}
