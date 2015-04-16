package de.persosim.simulator.perso;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyObject;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ca.CaOid;
import de.persosim.simulator.protocols.ri.RiOid;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.utils.HexString;


public class KeyPairAdapter implements Converter {
	String lifeCycleState;
	String pubKeyAlgorithm;
	String pubKeyBytes;
	String privKeyAlgorithm;
	String privKeyBytes;
	boolean privilegedOnly = false;
	String keyIdentifier;
	ArrayList<String[]> oid_list;
	
	
	
	
	@Override
	public boolean canConvert(Class type) {
		// TODO Auto-generated method stub
		
//		if (type is instanceof KeyPair)
		return type.equals(KeyObject.class);
//		return type.equals(KeyPair.class);

		
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		
		 	KeyObject key = (KeyObject) value;
//		 	KeyPair keyPair = (KeyPair) value;
		 	
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
				 	writer.setValue(HexString.encode(oId.getOid().toByteArray()));
				 	writer.endNode();				
				} 
		 	}
		 	
		 	writer.endNode();
		 	writer.endNode();
		 	
		 	writer.startNode("privilegedOnly");
		 	writer.setValue(String.valueOf(key.isPrivilegedOnly()));
		 	writer.endNode();
		 	
//		 	writer.startNode("keyPair");
//		 	
//		 	writer.startNode("pubKey");
//		 	writer.startNode("algorithm");
//		 	writer.setValue(keyPair.getPublic().getAlgorithm());
//		 	writer.endNode();
//		 	writer.startNode("byte-array");
//		 	writer.setValue(HexString.encode(keyPair.getPublic().getEncoded()));
//		 	writer.endNode();
//		 	writer.endNode();
//		 	
//		 	writer.startNode("privKey");
//		 	writer.startNode("algorithm");
//		 	writer.setValue(keyPair.getPrivate().getAlgorithm());
//		 	writer.endNode();
//		 	writer.startNode("byte-array");
//		 	writer.setValue(HexString.encode(keyPair.getPrivate().getEncoded()));
//		 	writer.endNode();
//		 	writer.endNode();
//		 	
//		 	writer.endNode();	
		 	
		 	
        }

	
	public void getKeyValuePair(HierarchicalStreamReader reader, String oldNode){
		
		while(reader.hasMoreChildren()) {
			
		reader.moveDown();
		
		String nodeName = reader.getNodeName();
		String nodeValue = reader.getValue().replace("\n", "").replace(" ", "");
		if (nodeName.equals("lifeCycleState")) {
			lifeCycleState = nodeValue;
		} else if (nodeName.equals("algorithm") && oldNode.equals("pubKey")) {
			nodeName = oldNode;
			pubKeyAlgorithm = nodeValue;
		} else if (nodeName.equals("byte-array") && oldNode.equals("pubKey")) {
			nodeName = oldNode;
			pubKeyBytes = nodeValue;
		} else if (nodeName.equals("algorithm") && oldNode.equals("privKey")) {
			nodeName = oldNode;
			privKeyAlgorithm = nodeValue;
		} else if (nodeName.equals("byte-array") && oldNode.equals("privKey")) {
			nodeName = oldNode;
			privKeyBytes = nodeValue;
		} else if(nodeName.equals("privilegedOnly")) {
			if(nodeValue.equals("true")){
				privilegedOnly = true;
			}
		
		} else if(nodeName.equals("keyIdentifier")) {
			keyIdentifier = nodeValue;
		} else if(nodeName.endsWith("Oid")) {
			String[] strArray = new String[]{nodeName, nodeValue};
 			oid_list.add(strArray);
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
		KeyPair keyPair = null;
		privilegedOnly = false;
		lifeCycleState = "";
		pubKeyAlgorithm = "";
		pubKeyBytes = "";
		privKeyAlgorithm = "";
		privKeyBytes = "";
		keyIdentifier = "";
		oid_list = new ArrayList<String[]>(); 
		KeyObject keyObject = null;
		getKeyValuePair(reader, "");
		
		
		try {
			
			PKCS8EncodedKeySpec  ks_priv = new PKCS8EncodedKeySpec (HexString.toByteArray(privKeyBytes));
			
			PrivateKey sk = KeyFactory.getInstance(privKeyAlgorithm, Crypto.getCryptoProvider()).
					generatePrivate(ks_priv);

			X509EncodedKeySpec  ks_pub = new X509EncodedKeySpec (HexString.toByteArray(pubKeyBytes));
		
			PublicKey pk = KeyFactory.getInstance(pubKeyAlgorithm, Crypto.getCryptoProvider()).
					generatePublic(ks_pub);
	
			keyPair = new KeyPair(pk, sk);
			KeyIdentifier identifier = new KeyIdentifier(Integer.parseInt(keyIdentifier));
			
			
			keyObject = new KeyObject(keyPair, identifier, privilegedOnly);
			
			for (int i = 0; i < oid_list.size(); i++) {
				
				String[] strA = oid_list.get(i);
				String oid_type = strA[0];
				OidIdentifier oidIdentifier = null;
				byte[] bytes = HexString.toByteArray(strA[1]);
				
				switch (oid_type){
				case "CaOid":
					CaOid caOid = new CaOid(bytes);
					oidIdentifier = new OidIdentifier(caOid);
					break;
				case "RiOid":
					RiOid riOid = new RiOid(bytes);
					oidIdentifier = new OidIdentifier(riOid);	
					break;
				default:
					break;
				}
				if (oidIdentifier != null){
					keyObject.addOidIdentifier(oidIdentifier);
				}
				
			}
			
			keyObject.updateLifeCycleState(Iso7816LifeCycleState.valueOf(lifeCycleState));
			
			
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
