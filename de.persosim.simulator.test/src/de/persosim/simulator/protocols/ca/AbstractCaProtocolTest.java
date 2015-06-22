package de.persosim.simulator.protocols.ca;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.Collection;

import javax.crypto.spec.SecretKeySpec;

import mockit.Deencapsulation;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.MasterFileIdentifier;
import de.persosim.simulator.cardobjects.NullCardObject;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Tr03110Utils;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * @author slutters
 *
 */
public class AbstractCaProtocolTest extends PersoSimTestCase {
	protected static byte[] COMMAND_DATA_IMPL_KEY_SELECTION = HexString.toByteArray("80 0A 04 00 7F 00 07 02 02 03 02 02");
	protected static byte[] COMMAND_DATA_EXPL_KEY_SELECTION = HexString.toByteArray("80 0A 04 00 7F 00 07 02 02 03 02 02 84 01 02");
	protected static TlvDataObjectContainer TLV_COMMAND_DATA_IMPL_KEY_SELECTION = new TlvDataObjectContainer(COMMAND_DATA_IMPL_KEY_SELECTION);
	protected static TlvDataObjectContainer TLV_COMMAND_DATA_EXPL_KEY_SELECTION = new TlvDataObjectContainer(COMMAND_DATA_EXPL_KEY_SELECTION);
	
	private DefaultCaProtocol caProtocol;
	@Mocked
	CardStateAccessor mockedCardStateAccessor;
	@Mocked
	SecStatus mockedSecurityStatus;
	@Mocked
	SecureRandom secRandom;
	@Mocked
	MasterFile mockedMf;
	PasswordAuthObject passwordAuthObject;
	ConstructedTlvDataObject cvcaIsTlv;
	TlvDataObject cvcaIsCarTlv;
	byte[] ecdhPublicKeyDataPicc, ecdhPrivateKeyDataPicc, ecdhPublicKeyDataPcd;
	byte[] ecdhSharedSecretK, ecdhRPiccNonce, ecdhTPiccToken, ecdhKeySpecMac, ecdhKeySpecEnc;
	DomainParameterSetEcdh domainParametersEcdh;
	ECPublicKey ecdhPublicKeyPicc, ecdhPublicKeyPcd;
	ECPrivateKey ecdhPrivateKeyPicc;
	KeyPair ecdhKeyPairPicc;
	Collection<CardObject> ecdhKeys, emptyKeySet;
	KeyObject ecdhKeyObject;
	@Mocked
	TerminalAuthenticationMechanism taMechanism;
	Collection<TerminalAuthenticationMechanism> taMechanismCollection;
	byte[] ecdhPublicKeyPcdCompressed;
	
	/**
	 * Create the test environment.
	 * 
	 * @throws ReflectiveOperationException
	 */
	@Before
	public void setUp() throws ReflectiveOperationException {
		// create and init the object under test
		caProtocol = new DefaultCaProtocol();
		caProtocol.setCardStateAccessor(mockedCardStateAccessor);
		caProtocol.init();
		
		// --> ECDH <--
		domainParametersEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		
		ecdhPublicKeyDataPicc = HexString.toByteArray("04 A4 4E BE 54 51 DF 7A AD B0 1E 45 9B 8C 92 8A 87 74 6A 57 92 7C 8C 28 A6 77 5C 97 A7 E1 FE 8D 9A 46 FF 4A 1C C7 E4 D1 38 9A EA 19 75 8E 4F 75 C2 8C 59 8F D7 34 AE BE B1 35 33 7C F9 5B E1 2E 94");
		ecdhPrivateKeyDataPicc = HexString.toByteArray("79 84 67 4C F3 B3 A5 24 BF 92 9C E8 A6 7F CF 22 17 3D A0 BA D5 95 EE D6 DE B7 2D 22 C5 42 FA 9D");
		ecdhPublicKeyDataPcd = HexString.toByteArray("04 A9 80 49 3A 83 03 16 4B 04 6C BB 00 21 2D 52 EC 84 30 1C E8 93 3D 02 88 75 E7 63 73 0B 66 80 1A 1B 65 DB F0 15 D0 8F A4 DC 70 F7 9D 8A 92 CE 5A 4D 6A 2F 0C 30 71 3B D5 B8 44 E2 D0 5C C1 54 7D");
		ecdhPublicKeyPcdCompressed = HexString.toByteArray("A980493A8303164B046CBB00212D52EC84301CE8933D028875E763730B66801A");
		
		ecdhSharedSecretK = HexString.toByteArray("82 23 73 DD 9F 0E F5 82 1D E2 C3 96 99 6C 79 39 F6 7F 09 97 E4 0D 62 77 BE 2D 37 38 5E 6A 4B 73 CC BA 54 8D B5 92 FF CB");
		ecdhRPiccNonce = HexString.toByteArray("CC BA 54 8D B5 92 FF CB");
		ecdhTPiccToken = HexString.toByteArray("84 65 BE 39 7E 18 BF BB");
		ecdhKeySpecMac = HexString.toByteArray("14 F5 EC 36 8F 19 9D 03 EA 69 10 CC 7E FF AA 64");
		ecdhKeySpecEnc = HexString.toByteArray("E0 1F 0C 20 87 DD DD F7 8C F0 69 40 3B 97 B1 A7");
		
		ecdhPublicKeyPicc = domainParametersEcdh.reconstructPublicKey(ecdhPublicKeyDataPicc);
		ecdhPrivateKeyPicc = domainParametersEcdh.reconstructPrivateKey(ecdhPrivateKeyDataPicc);
		ecdhKeyPairPicc = new KeyPair(ecdhPublicKeyPicc, ecdhPrivateKeyPicc);
		
		ecdhPublicKeyPcd = domainParametersEcdh.reconstructPublicKey(ecdhPublicKeyDataPcd);
		
		ecdhKeys = new ArrayList<CardObject>();
		KeyIdentifier KeyIdentifier = new KeyIdentifier(2);
		OidIdentifier oidIdentifier = new OidIdentifier(Ca.OID_id_CA_ECDH_AES_CBC_CMAC_128);
		ecdhKeyObject = new KeyObject(ecdhKeyPairPicc, KeyIdentifier);
		ecdhKeyObject.addOidIdentifier(oidIdentifier);
		ecdhKeys.add(ecdhKeyObject);
		
		emptyKeySet = new ArrayList<CardObject>();
		
		taMechanismCollection = new ArrayList<TerminalAuthenticationMechanism>();
		taMechanismCollection.add(taMechanism);
	}
	
	/**
	 * Positive test case: extract key identifier from command data, implicit domain parameter set reference (Tag 84 is missing).
	 */
	@Test
	public void testExtractKeyIdentifierFromCommandData_ImplicitDomainParameterReference(){
		CaProtocol caProtocol = new CaProtocol();
		KeyIdentifier keyIdentifierReceived = caProtocol.extractKeyIdentifierFromCommandData(TLV_COMMAND_DATA_IMPL_KEY_SELECTION);
		KeyIdentifier keyIdentifierExpected = new KeyIdentifier();
		
		assertEquals(keyIdentifierExpected.getInteger(), keyIdentifierReceived.getInteger());
	}
	
	/**
	 * Positive test case: extract key identifier from command data, explicit domain parameter set reference (Tag 84 is present).
	 */
	@Test
	public void testExtractKeyIdentifierFromCommandData_ExplicitDomainParameterReference(){
		CaProtocol caProtocol = new CaProtocol();
		KeyIdentifier keyIdentifierReceived = caProtocol.extractKeyIdentifierFromCommandData(TLV_COMMAND_DATA_EXPL_KEY_SELECTION);
		KeyIdentifier keyIdentifierExpected = new KeyIdentifier(2);
		
		assertEquals(keyIdentifierExpected.getInteger(), keyIdentifierReceived.getInteger());
	}
	
	/**
	 * Positive test case: perform Set AT command with data from valid CA test run, explicit key reference (Tag 84 is present).
	 */
	@Test
	public void testSetAt_ExplicitKeyReference(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(MasterFileIdentifier.class),
						withInstanceOf(Scope.class));
				result = mockedMf;
				
				mockedMf.findChildren(
						withInstanceOf(KeyIdentifier.class),
						withInstanceOf(OidIdentifier.class));
				result = ecdhKeys;
				
				mockedCardStateAccessor.getObject(
						withInstanceOf(KeyIdentifier.class),
						withInstanceOf(Scope.class));
				result = ecdhKeyObject;
			}
		};
		
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 41 A4 0F 80 0A 04 00 7F 00 07 02 02 03 02 02 84 01 02");
		processingData.updateCommandApdu(this, "setAT APDU", CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		caProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord());
	}
	
	/**
	 * Negative test case: perform Set AT command with data from valid CA test run, explicit key reference (Tag 84 is present) but key is not found.
	 */
	@Test
	public void testSetAt_ExplicitKeyReferenceKeyNotFound(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(MasterFileIdentifier.class),
						withInstanceOf(Scope.class));
				result = mockedMf;

				mockedMf.findChildren(
						withInstanceOf(OidIdentifier.class),
						withInstanceOf(KeyIdentifier.class));
				result = ecdhKeys;

				mockedCardStateAccessor.getObject(
						withInstanceOf(KeyIdentifier.class),
						withInstanceOf(Scope.class));
				result = new NullCardObject();
			}
		};
		
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 41 A4 0F 80 0A 04 00 7F 00 07 02 02 03 02 02 84 01 02");
		processingData.updateCommandApdu(this, "setAT APDU", CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		caProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 6A88", Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, processingData.getResponseApdu().getStatusWord());
	}
	
	/**
	 * Positive test case: perform Set AT command with data from valid CA test run, implicit key reference (Tag 84 is missing).
	 */
	@Test
	public void testSetAt_ImplicitKeyReference(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(withInstanceOf(MasterFileIdentifier.class), Scope.FROM_MF);
				result = mockedMf;

				mockedMf.findChildren(
						withInstanceOf(KeyIdentifier.class),
						withInstanceOf(OidIdentifier.class));
				result = ecdhKeys;

				mockedCardStateAccessor.getObject(
						withInstanceOf(KeyIdentifier.class),
						withInstanceOf(Scope.class));
				result = ecdhKeyObject;
			}
		};
		
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 41 A4 0C 80 0A 04 00 7F 00 07 02 02 03 02 02");
		processingData.updateCommandApdu(this, "setAT APDU", CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		caProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord());
	}
	
	/**
	 * Negative test case: perform Set AT command with data from valid CA test run, implicit key reference (Tag 84 is missing) fails (no key found).
	 */
	@Test
	public void testSetAt_ImplicitKeyReferenceFail(){
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(withInstanceOf(MasterFileIdentifier.class), Scope.FROM_MF);
				result = mockedMf;
			}
		};
		
		new Expectations() {
			{
				mockedMf.findChildren(
						withInstanceOf(KeyIdentifier.class),
						withInstanceOf(OidIdentifier.class));
				result = emptyKeySet;
			}
		};
		
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 41 A4 0C 80 0A 04 00 7F 00 07 02 02 03 02 02");
		processingData.updateCommandApdu(this, "setAT APDU", CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		caProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 6A88", Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, processingData.getResponseApdu().getStatusWord());
	}
	
	/**
	 * Positive test case: perform General Authenticate command for EC keys with data from valid CA test run.
	 */
	@SuppressWarnings("unchecked")  //jmockit
	@Test
	public void testGeneralAuthenticateEcdh(){
		new NonStrictExpectations(CryptoUtil.class) {
            {
            	secRandom.nextBytes(
            			withInstanceOf(byte[].class));
            	
            	// eliminate true randomness by providing fixed "random" values
                result = new Delegate<Object>() {
                    @SuppressWarnings("unused") // JMockit
					void nextBytes(byte[] bytes) {
                        System.arraycopy(ecdhRPiccNonce, 0, bytes, 0, ecdhRPiccNonce.length);
                        return;
                     }
                };
            }
        };
        
        new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getCurrentMechanisms(
						SecContext.APPLICATION,
						withInstanceOf(Collection.class));
				result = taMechanismCollection;
			}
		};
		
		new NonStrictExpectations() {
			{
				taMechanism.getCompressedTerminalEphemeralPublicKey();
				result = ecdhPublicKeyPcdCompressed;
			}
		};
		
		caProtocol.caOid = Ca.OID_id_CA_ECDH_AES_CBC_CMAC_128;
		Deencapsulation.setField(caProtocol, ecdhKeyPairPicc);
		caProtocol.caDomainParameters = Tr03110Utils.getDomainParameterSetFromKey(caProtocol.staticKeyPairPicc.getPublic());
		caProtocol.cryptoSupport = caProtocol.caOid.getCryptoSupport();
		
		ProcessingData processingData = new ProcessingData();
		byte[] apduFront = HexString.toByteArray("00 86 00 00 45 7C 43 80 41");
		byte[] apduBack = HexString.toByteArray("00");
		byte[] apduBytes = Utils.concatByteArrays(apduFront, ecdhPublicKeyDataPcd, apduBack);
		CommandApdu cApdu = CommandApduFactory.createCommandApdu(apduBytes);
		processingData.updateCommandApdu(this, "General Authenticate APDU", cApdu);
		Deencapsulation.setField(caProtocol, processingData);
		
		// call mut
		caProtocol.processCommandGeneralAuthenticate();
		
		SecretKeySpec secretKeySpecMAC = Deencapsulation.getField(caProtocol, "secretKeySpecMAC");
		SecretKeySpec secretKeySpecENC = Deencapsulation.getField(caProtocol, "secretKeySpecENC");
		byte[] secretKeySpecMacKeyMaterial = secretKeySpecMAC.getEncoded();
		byte[] secretKeySpecEncKeyMaterial = secretKeySpecENC.getEncoded();
		
		System.out.println("key spec mac: " + HexString.encode(secretKeySpecMacKeyMaterial));
		System.out.println("key spec enc: " + HexString.encode(secretKeySpecEncKeyMaterial));
		
		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord());
		assertArrayEquals("key spec ENC mismatch", ecdhKeySpecEnc, secretKeySpecEncKeyMaterial);
		assertArrayEquals("key spec MAC mismatch", ecdhKeySpecMac, secretKeySpecMacKeyMaterial);
	}
}
