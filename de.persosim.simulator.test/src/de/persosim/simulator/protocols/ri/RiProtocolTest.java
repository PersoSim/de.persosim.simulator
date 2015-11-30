package de.persosim.simulator.protocols.ri;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.util.Collection;
import java.util.HashSet;

import javax.crypto.KeyAgreement;

import mockit.Deencapsulation;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.KeyPairObject;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.EffectiveAuthorizationMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class RiProtocolTest extends PersoSimTestCase {

	@Mocked
	KeyAgreement keyAgreement;
	@Mocked
	MessageDigest messageDigest;
	@Mocked
	PublicKey publicKey;
	@Mocked
	PrivateKey privateKey;
	@Mocked
	CardStateAccessor cardStateAccessor;
	@Mocked
	KeyPair keypair;
	@Mocked
	KeyPairObject keyObject;
	@Mocked
	TerminalAuthenticationMechanism taMechanism;
	@Mocked
	EffectiveAuthorizationMechanism authMechanism;
	@Mocked
	RiOid oid;
	
	RiProtocol protocol;
	
	@Before
	public void setUp(){
		protocol = new RiProtocol();
		protocol.setCardStateAccessor(cardStateAccessor);
		
		keyObject = new KeyPairObject();
	}
	
	/**
	 * Positive test using a mocked key agreement with fixed output and a mocked
	 * hash algorithm, that adds 1 to each byte in the given array.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateSectorIdentifier() throws Exception{
		//prepare the mock
		final byte [] keyAgreementResult = new byte [] {1,2,3,4};
		
		new Expectations() {{

			keyAgreement.init((Key) any);
			keyAgreement.doPhase((Key) any, true);
			keyAgreement.generateSecret();
			result = keyAgreementResult;
			messageDigest.digest((byte[]) any);
			result = new Delegate<Object>() {
				@SuppressWarnings("unused")
				byte [] digest(byte [] data){
					byte [] result = new byte [data.length];
					for (int i = 0; i < data.length; i++){
						result[i] = (byte) ((data[i] + 1) % 256);
					}
					return result;
				}
			};
		}
		};
		
		byte [] expectedResult = new byte [] {2,3,4,5};
		byte [] result = Deencapsulation.invoke(protocol, "calculateSectorIdentifier", privateKey, publicKey, keyAgreement, messageDigest);
		assertArrayEquals(expectedResult, result);
	}
	
	/**
	 * Positive test using a mocked hash algorithm (identity). 
	 */
	@Test
	public void testCheckSectorPublicKeyHash(){
		PrimitiveTlvDataObject data1 = new PrimitiveTlvDataObject(TlvConstants.TAG_06, new byte [] {1,2,3,4});
		PrimitiveTlvDataObject data2 = new PrimitiveTlvDataObject(TlvConstants.TAG_80, new byte [] {5,6,7,8});
		
		ConstructedTlvDataObject sectorPublicKey = new ConstructedTlvDataObject(RiOid.RI_FIRST_SECTOR_KEY_TAG);
		sectorPublicKey.addTlvDataObject(data1);
		sectorPublicKey.addTlvDataObject(data2);

		ConstructedTlvDataObject expectedResult = new ConstructedTlvDataObject(TlvConstants.TAG_7F49);
		expectedResult.addTlvDataObject(data1);
		expectedResult.addTlvDataObject(data2);
		
		new Expectations() {
			{
				messageDigest.digest((byte[]) any);
				result = new Delegate<Object>() {
					@SuppressWarnings("unused")
					byte [] digest(byte [] data){
						return data;
					}
				};
			}
		};
		assertTrue((boolean)Deencapsulation.invoke(protocol, "checkSectorPublicKeyHash", sectorPublicKey, messageDigest, expectedResult.toByteArray()));
		
	}
	
	@SuppressWarnings("unchecked") // jMockit
	@Test
	public void testGeneralAuthenticate() throws Exception{
		// prepare the mock
		final HashSet<SecMechanism> mechanisms = new HashSet<>();
		
		mechanisms.add(taMechanism);
		mechanisms.add(authMechanism);
		
		new Expectations() {
			{
			MessageDigest.getInstance((String) any, (Provider) any);
			result = messageDigest;
			}
		};

		final byte [] keyAgreementResult = new byte [] {1,2,3,4};
		new NonStrictExpectations() {
			{
				taMechanism.getTerminalType();
				result = TerminalType.AT;
				cardStateAccessor.getCurrentMechanisms((SecContext)any, (Collection<Class<? extends SecMechanism>>) any);
				result = mechanisms;
				keyObject.getKeyPair();
				result=keypair;
				keypair.getPrivate();
				result = privateKey;
				messageDigest.digest((byte[]) any);
				result = new Delegate<Object>() {
					@SuppressWarnings("unused")
					byte [] digest(byte [] data){
						return data;
					}
				};
				keyAgreement.init((Key) any);
				keyAgreement.doPhase((Key) any, true);
				keyAgreement.generateSecret();
				result = keyAgreementResult;
				taMechanism.getFirstSectorPublicKeyHash();
				result = HexString.toByteArray("7F4982011D060A04007F000702020502038120A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E537782207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9832026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B68441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F0469978520A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A78641045D3B49C8EE250295F7C0EF6A1AE810C4B9E1F5F80D316DC9ADAD16080C1784CF881EE0A375BCA1B53C98F3AC39FD0CA90CE31D2D8276D3CFB32B316BA0221023870101");
				oid.getKeyAgreement();
				result = keyAgreement;
				oid.getHash();
				result = messageDigest;
			}
		};
		Deencapsulation.setField(protocol, keyObject);
		Deencapsulation.setField(keyObject, keypair);
		
		
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 86 00 00 00 01 25 7C 82 01 21 A0 82 01 1D 06 0A 04 00 7F 00 07 02 02 05 02 03 81 20 A9 FB 57 DB A1 EE A9 BC 3E 66 0A 90 9D 83 8D 72 6E 3B F6 23 D5 26 20 28 20 13 48 1D 1F 6E 53 77 82 20 7D 5A 09 75 FC 2C 30 57 EE F6 75 30 41 7A FF E7 FB 80 55 C1 26 DC 5C 6C E9 4A 4B 44 F3 30 B5 D9 83 20 26 DC 5C 6C E9 4A 4B 44 F3 30 B5 D9 BB D7 7C BF 95 84 16 29 5C F7 E1 CE 6B CC DC 18 FF 8C 07 B6 84 41 04 8B D2 AE B9 CB 7E 57 CB 2C 4B 48 2F FC 81 B7 AF B9 DE 27 E1 E3 BD 23 C2 3A 44 53 BD 9A CE 32 62 54 7E F8 35 C3 DA C4 FD 97 F8 46 1A 14 61 1D C9 C2 77 45 13 2D ED 8E 54 5C 1D 54 C7 2F 04 69 97 85 20 A9 FB 57 DB A1 EE A9 BC 3E 66 0A 90 9D 83 8D 71 8C 39 7A A3 B5 61 A6 F7 90 1E 0E 82 97 48 56 A7 86 41 04 5D 3B 49 C8 EE 25 02 95 F7 C0 EF 6A 1A E8 10 C4 B9 E1 F5 F8 0D 31 6D C9 AD AD 16 08 0C 17 84 CF 88 1E E0 A3 75 BC A1 B5 3C 98 F3 AC 39 FD 0C A9 0C E3 1D 2D 82 76 D3 CF B3 2B 31 6B A0 22 10 23 87 01 01 00 00");		
				
		processingData.updateCommandApdu(this, "general authenticate APDU", CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		protocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord());
		assertArrayEquals(HexString.toByteArray("7C06810401020304"), processingData.getResponseApdu().getData().toByteArray());
	}
	
	/**
	 * Negative test: process General Authenticate command when preceding TA was performed for non-AT terminal type.
	 */
	@Test
	public void testGeneralAuthenticate_NonAtTerminalType() throws Exception{
		// prepare the mock
		final HashSet<SecMechanism> mechanisms = new HashSet<>();
		mechanisms.add(taMechanism);
		
		final HashSet<Class<? extends SecMechanism>> requestedMechanisms = new HashSet<>();
		requestedMechanisms.add(TerminalAuthenticationMechanism.class);
		
		new NonStrictExpectations() {
			{
				taMechanism.getTerminalType();
				result = TerminalType.IS;
				cardStateAccessor.getCurrentMechanisms((SecContext)any, requestedMechanisms);
				result = mechanisms;
			}
		};
		
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 86 00 00 00 01 25 7C 82 01 21 A0 82 01 1D 06 0A 04 00 7F 00 07 02 02 05 02 03 81 20 A9 FB 57 DB A1 EE A9 BC 3E 66 0A 90 9D 83 8D 72 6E 3B F6 23 D5 26 20 28 20 13 48 1D 1F 6E 53 77 82 20 7D 5A 09 75 FC 2C 30 57 EE F6 75 30 41 7A FF E7 FB 80 55 C1 26 DC 5C 6C E9 4A 4B 44 F3 30 B5 D9 83 20 26 DC 5C 6C E9 4A 4B 44 F3 30 B5 D9 BB D7 7C BF 95 84 16 29 5C F7 E1 CE 6B CC DC 18 FF 8C 07 B6 84 41 04 8B D2 AE B9 CB 7E 57 CB 2C 4B 48 2F FC 81 B7 AF B9 DE 27 E1 E3 BD 23 C2 3A 44 53 BD 9A CE 32 62 54 7E F8 35 C3 DA C4 FD 97 F8 46 1A 14 61 1D C9 C2 77 45 13 2D ED 8E 54 5C 1D 54 C7 2F 04 69 97 85 20 A9 FB 57 DB A1 EE A9 BC 3E 66 0A 90 9D 83 8D 71 8C 39 7A A3 B5 61 A6 F7 90 1E 0E 82 97 48 56 A7 86 41 04 5D 3B 49 C8 EE 25 02 95 F7 C0 EF 6A 1A E8 10 C4 B9 E1 F5 F8 0D 31 6D C9 AD AD 16 08 0C 17 84 CF 88 1E E0 A3 75 BC A1 B5 3C 98 F3 AC 39 FD 0C A9 0C E3 1D 2D 82 76 D3 CF B3 2B 31 6B A0 22 10 23 87 01 01 00 00");		
				
		processingData.updateCommandApdu(this, "general authenticate APDU", CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		protocol.process(processingData);

		// check results
		assertArrayEquals("Statusword is not 6985", Utils.toUnsignedByteArray(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED), processingData.getResponseApdu().toByteArray());
	}
	
	/**
	 * Negative test: process Manage Security Environment for command APDU missing tag 80 (cryptographic mechanism reference data).
	 */
	@Test
	public void testManageSecurityEnvironment_MissingCryptoMechRefData() throws Exception{
		// prepare the mock
		final HashSet<SecMechanism> mechanisms = new HashSet<>();
		mechanisms.add(taMechanism);
		
		final HashSet<Class<? extends SecMechanism>> requestedMechanisms = new HashSet<>();
		requestedMechanisms.add(TerminalAuthenticationMechanism.class);
		
		new NonStrictExpectations() {
			{
				taMechanism.getTerminalType();
				result = TerminalType.AT;
				cardStateAccessor.getCurrentMechanisms((SecContext)any, requestedMechanisms);
				result = mechanisms;
			}
		};
		
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("002241A403840129");		
				
		processingData.updateCommandApdu(this, "general authenticate APDU", CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		protocol.process(processingData);

		// check results
		assertArrayEquals("Statusword is not 6A88", Utils.toUnsignedByteArray(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND), processingData.getResponseApdu().toByteArray());
	}
	
}
