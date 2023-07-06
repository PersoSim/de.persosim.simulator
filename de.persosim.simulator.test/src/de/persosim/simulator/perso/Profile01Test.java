package de.persosim.simulator.perso;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECFieldFp;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.utils.HexString;

public class Profile01Test extends ArtifactPersonalizationTest {

	Personalization perso;
	
	@Before
	public void setUp() throws Exception {
		perso = null;
	}

	@Override
	public Personalization getPerso() throws AccessDeniedException {
		
		if (perso == null) {
			perso = new Profile01();
		}
			
		return perso;
	}
	
	@Test
	public void generateRiKeys() throws Exception {
		
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH");
	    AlgorithmParameterSpec params = StandardizedDomainParameters.getDomainParameterSetById(13).getAlgorithmParameterSpec();
	    kpg.initialize(params);
	    KeyPair newKeyPair = kpg.generateKeyPair();
	    
	    
	    ECPublicKey pubKey = (ECPublicKey)newKeyPair.getPublic();
		int referenceLength = CryptoUtil.getPublicPointReferenceLengthL(((ECFieldFp) pubKey.getParams().getCurve().getField()).getP());
		byte[] pubKeyBytes = CryptoUtil.encode(pubKey.getW(), referenceLength, CryptoUtil.ENCODING_UNCOMPRESSED);
		
		ECPrivateKey privKey = (ECPrivateKey)newKeyPair.getPrivate();
		byte[] privKeyBytes = privKey.getS().toByteArray();
		
		System.out.println("Public key:  " + HexString.encode(pubKeyBytes));
		System.out.println("Private key: " + HexString.encode(privKeyBytes));
		

		System.out.println("\t\t\t\tHexString.toByteArray(\"" + HexString.encode(pubKeyBytes)+"\"),");
		System.out.println("\t\t\t\tHexString.toByteArray(\"" + HexString.encode(privKeyBytes)+"\")),");
	} 

}
