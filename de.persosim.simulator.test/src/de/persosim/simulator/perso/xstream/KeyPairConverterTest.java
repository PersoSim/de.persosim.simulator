package de.persosim.simulator.perso.xstream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.converters.ConversionException;

import de.persosim.simulator.perso.PersonalizationFactory;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class KeyPairConverterTest extends PersoSimTestCase {
	KeyPair keyPair;
	
	@Before
	public void setUp() {
		PublicKey pk = null;
		PrivateKey sk = null;
		String byteValuePk = "308202050201003081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013"
				+ "481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCD"
				+ "C18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F04"
				+ "6997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A70201010482010F3082010B0201010420A07EB62E891DAA84643E0AFCC1AF006891B"
				+ "669B8F51E379477DBEAB8C987A610A081E33081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E537730"
				+ "4404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B6044"
				+ "1048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB"
				+ "57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101";
		
		String byteValueSk = "308201333081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F"
				+ "6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8"
				+ "C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F04699702"
				+ "2100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A70201010342000419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E7"
				+ "6567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8";
		
		String algorithmValue = "ECDH";
		
		PKCS8EncodedKeySpec  ks_sk = new PKCS8EncodedKeySpec (HexString.toByteArray(byteValuePk));
		X509EncodedKeySpec   ks_pk = new X509EncodedKeySpec (HexString.toByteArray(byteValueSk));
		
		try {
			pk = KeyFactory.getInstance(algorithmValue, bcProvider).generatePublic(ks_pk);
			sk = KeyFactory.getInstance(algorithmValue, bcProvider).generatePrivate(ks_sk);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyPair = new KeyPair(pk, sk);
	}
	
	/**
	 * Marshals a KeyPair object and unmarshals it again.
	 * 
	 * All attributes must be equal.
	 */
	@Test
	public void testKeyPairConverter_MarshalUnmarshal_KeyPair() {
		
		StringWriter writer = new StringWriter();
		PersonalizationFactory.marshal(keyPair, writer);
		
		KeyPair keyPairUnmarshal = (KeyPair) PersonalizationFactory.unmarshal(new StringReader(writer.toString()));
		
		assertArrayEquals("ByteArray", keyPair.getPrivate().getEncoded(), keyPairUnmarshal.getPrivate().getEncoded());
		assertEquals("EncodedBytes", HexString.encode(keyPair.getPrivate().getEncoded()), HexString.encode(keyPairUnmarshal.getPrivate().getEncoded()));
		assertEquals("Algorithm", keyPair.getPrivate().getAlgorithm(), keyPairUnmarshal.getPrivate().getAlgorithm());
		
		assertArrayEquals("ByteArray", keyPair.getPublic().getEncoded(), keyPairUnmarshal.getPublic().getEncoded());
		assertEquals("EncodedBytes", HexString.encode(keyPair.getPublic().getEncoded()), HexString.encode(keyPairUnmarshal.getPublic().getEncoded()));
		assertEquals("Algorithm", keyPair.getPublic().getAlgorithm(), keyPairUnmarshal.getPublic().getAlgorithm());
	}
	
	/**
	 * Marshals a KeyPair object, unmarshals it and marshals it again.
	 * 
	 * All unmarshalled object content (from both objects) must be identical.
	 */
	@Test
	public void testKeyPairConverter_MarshalUnmarshalMarshal_KeyPair() {
		
		StringWriter writer = new StringWriter();
		PersonalizationFactory.marshal(keyPair, writer);
		
		KeyPair keyPairUnmarshal = (KeyPair) PersonalizationFactory.unmarshal(new StringReader(writer.toString()));
		
		StringWriter writer2 = new StringWriter();
		PersonalizationFactory.marshal(keyPairUnmarshal, writer2);
		
		assertEquals("KeyPairXml", writer.toString(), writer2.toString());
	}
	
	/**
	 * Try to to unmarshal KeyPair, but the algorithm inside the keys is null.
	 * 
	 * @throws ConversionException 
	 */
	@Test(expected = ConversionException.class)
	public void testKeyPairConverter_UnmarshalKeyPair_AlgorithmIsNull() throws ConversionException {
		
		StringWriter writer = new StringWriter();
		PersonalizationFactory.marshal(keyPair, writer);
				
		String xmlKeyPair = writer.toString();
		xmlKeyPair = xmlKeyPair.replace("<algorithm>ECDH</algorithm>", "");
		
		assertNull(PersonalizationFactory.unmarshal(new StringReader(xmlKeyPair)));
	}
	
	/**
	 * Try to to unmarshal KeyPair, but the value inside the keys is null.
	 * 
	 * @throws ConversionException 
	 */
	@Test(expected = ConversionException.class)
	public void testKeyPairConverter_UnmarshalKeyPair_PrivKeyValueIsNull() throws ConversionException {
		
		StringWriter writer = new StringWriter();
		PersonalizationFactory.marshal(keyPair, writer);
		
		String xmlKeyPair = writer.toString();
		xmlKeyPair = xmlKeyPair.replace("<value>308202050201003081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC"
				+ "3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042"
				+ "026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A44"
				+ "53BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B"
				+ "561A6F7901E0E82974856A70201010482010F3082010B0201010420A07EB62E891DAA84643E0AFCC1AF006891B669B8F51E379477DBEAB8C987A610A081"
				+ "E33081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975F"
				+ "C2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B6"
				+ "0441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D5"
				+ "4C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101</value>", "");
		
		assertNull(PersonalizationFactory.unmarshal(new StringReader(xmlKeyPair)));
	}
}
