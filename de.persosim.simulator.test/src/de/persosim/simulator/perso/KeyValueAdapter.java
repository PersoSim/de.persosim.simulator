package de.persosim.simulator.perso;

import java.awt.List;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Key;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi;
import org.w3c.dom.NodeList;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyObject;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ca.CaOid;
import de.persosim.simulator.protocols.ri.RiOid;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;


public class KeyValueAdapter implements Converter {
	String lifeCycleState;
	String pubKeyAlgorithm;
	String pubKeyBytes;
	String privKeyAlgorithm;
	String privKeyBytes;
	String privilegedOnly;
	ArrayList<String> oid_list;
	
	
	
	
	@Override
	public boolean canConvert(Class type) {
		// TODO Auto-generated method stub

		return type.equals(KeyObject.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		
		 	KeyObject key = (KeyObject) value;
		 	
			writer.startNode("lifeCycleState");
		 	writer.setValue(key.getLifeCycleState().toString());
		 	writer.endNode();
		 	writer.flush();
		 	writer.close();
		 	
		 	writer.startNode("keyPair");
		 	
		 	writer.startNode("pubKey");
		 	writer.startNode("algorithm");
		 	writer.setValue(key.getKeyPair().getPublic().getAlgorithm());
		 	writer.endNode();
		 	writer.startNode("byte-array");
		 	writer.setValue(HexString.encode(key.getKeyPair().getPublic().getEncoded()));
		 	writer.endNode();
		 	writer.endNode();
		 	
		 	writer.startNode("privKey");
		 	writer.startNode("algorithm");
		 	writer.setValue(key.getKeyPair().getPrivate().getAlgorithm());
		 	writer.endNode();
		 	writer.startNode("byte-array");
		 	writer.setValue(HexString.encode(key.getKeyPair().getPrivate().getEncoded()));
		 	writer.endNode();
		 	writer.endNode();
		 	
		 	writer.startNode("keyIdentifier");
		 	KeyIdentifier kId = key.getPrimaryIdentifier();
		 	writer.setValue(String.valueOf((kId.getInteger())));
		 	writer.endNode();
		 	
		 	writer.startNode("usage");
		 	writer.startNode("oidIdentifier");
		 	
		 	Collection<CardObjectIdentifier> cardIdentifier = key.getAllIdentifiers();
		 	
		 	for (Iterator<CardObjectIdentifier> iterator = cardIdentifier.iterator(); iterator.hasNext();){
		 		CardObjectIdentifier cId = (CardObjectIdentifier) iterator.next();
		 		if (cId instanceof OidIdentifier) {
		 			OidIdentifier oId = (OidIdentifier) cId;
		 			
		 			writer.startNode(oId.getOid().getClass().getSimpleName());
				 	writer.setValue(oId.getOid().getIdString());
				 	writer.endNode();				
				} 
		 	}
		 	
		 	writer.endNode();
		 	writer.endNode();
		 	
		 	writer.startNode("privilegedOnly");
		 	writer.setValue(String.valueOf(key.isPrivilegedOnly()));
		 	writer.endNode();
		 	
		 	writer.endNode();
        }

	
	public void getKeyValuePair(HierarchicalStreamReader reader, String oldNode){
		while(reader.hasMoreChildren()) {
			
		reader.moveDown();
		
		String nodeName = reader.getNodeName();
		String nodeValue = reader.getValue().replace("\n", "").replace(" ", "");
		if (nodeName.equals("lifeCycleState")) {
			lifeCycleState = reader.getValue().replace("\n", "").replace(" ", "");
		} else if (nodeName.equals("algorithm") && oldNode.equals("pubKey")) {
			nodeName = oldNode;
			pubKeyAlgorithm = reader.getValue().replace("\n", "").replace(" ", "");
		} else if (nodeName.equals("byte-array") && oldNode.equals("pubKey")) {
			nodeName = oldNode;
			pubKeyBytes = reader.getValue().replace("\n", "").replace(" ", "");
		} else if (nodeName.equals("algorithm") && oldNode.equals("privKey")) {
			nodeName = oldNode;
			privKeyAlgorithm = reader.getValue().replace("\n", "").replace(" ", "");
		} else if (nodeName.equals("byte-array") && oldNode.equals("privKey")) {
			nodeName = oldNode;
			privKeyBytes = reader.getValue().replace("\n", "").replace(" ", "");
		} else if(nodeName.equals("privilegedOnly")) {
			privilegedOnly = reader.getValue();
		} else if(nodeName.endsWith("Oid")) {
			oid_list.add(reader.getValue());
		}
		
		System.out.println(nodeName);
		System.out.println(nodeValue);
		
		if (reader.hasMoreChildren()) {
			getKeyValuePair(reader, nodeName);
		}
		reader.moveUp();
		}
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		// TODO Auto-generated method stu
		oid_list = new ArrayList<String>(); 
		KeyObject keyObject = null;
		getKeyValuePair(reader, "");
		
		
		try {
			
			PKCS8EncodedKeySpec  ks_priv = new PKCS8EncodedKeySpec (HexString.toByteArray(privKeyBytes));
			
			PrivateKey sk = KeyFactory.getInstance(privKeyAlgorithm, Crypto.getCryptoProvider()).
					generatePrivate(ks_priv);

			X509EncodedKeySpec  ks_pub = new X509EncodedKeySpec (HexString.toByteArray(pubKeyBytes));
		
			PublicKey pk = KeyFactory.getInstance(pubKeyAlgorithm, Crypto.getCryptoProvider()).
					generatePublic(ks_pub);
	
			KeyPair keyPair = new KeyPair(pk, sk);
//			
			keyObject = new KeyObject(keyPair, new KeyIdentifier());
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return keyObject;
	}

}
