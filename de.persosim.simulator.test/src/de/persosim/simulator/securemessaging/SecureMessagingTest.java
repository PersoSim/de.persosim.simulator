package de.persosim.simulator.securemessaging;

import static mockit.Deencapsulation.setField;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValuePlain;
import de.persosim.simulator.utils.HexString;

public class SecureMessagingTest extends PersoSimTestCase {

	// test data from ICAO Doc 9303 Part 3 Vol 2, Worked Example Appendix 6 to section IV
	private static final String ICAO_SK_ENC = "979EC13B1CBFE9DCD01AB0FED307EAE5";
	private static final String ICAO_SK_MAC = "F1CB1F1FB5ADF208806B89DC579DC1F8";
	
//	private static final String ICAO_SSC_INITIAL = "887022120C06C226";
	private static final String ICAO_SSC_PLUS1 = "887022120C06C227";
//	private static final String ICAO_SSC_PLUS2 = "887022120C06C228";
	
	private static final String ICAO_PLAIN_APDU = "00A4020C02011E";
	private static final String ICAO_SM_APDU = "0CA4020C158709016375432908C044F68E08BF8B92D635FF24F800";
//	private static final String ICAO_PLAIN_RESPONSE = "9000";
//	private static final String ICAO_SM_RESPONSE = "990290008E08FA855A5D4C50A8ED9000";
	
	// testdata from correct GlobalTester run
	private static final String AES256_SK_ENC = "4DD037AB00B6B0D7FC80DA1D567AEF8098F8D8AC417E212660CCD6BDD7002067";
	private static final String AES256_IV_ENC = "73E69C3F671B42F6A9C07F4A5CD821DC";
	private static final String AES256_MAC = "aescmac";
	private static final String AES256_SK_MAC = "8BC9DD9D33C62926101D212B04C0C79A5BF7032F4DDCA237552D665DCB560B68";
	private static final String AES256_SK_MAC_AUX_DATA = "00000000000000000000000000000003";
	
	private static final String AES256_CASE2_SM_APDU = "0C8200000D9701FF8E083D0A111D900F25A500";
	private static final String AES256_CASE2_PLAIN_APDU = "00820000FF";
	
	@Mocked SmDataProvider dataProviderMock;

	@Test
	public void processAscending_plainApduUntouched() {
		SecureMessaging secureMessaging = new SecureMessaging(0);

		// provide plain APDU
		ProcessingData pData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x84, 0x00, 0x00, 0x08 };
		pData.updateCommandApdu(this, "test command APDU", CommandApduFactory.createCommandApdu(
				apduBytes));

		// call mut
		secureMessaging.processAscending(pData);

		// extract/check CommandApdu
		CommandApdu commandApdu = pData.getCommandApdu();
		assertNotNull("commandApdu is empty", commandApdu);
		assertArrayEquals("plain commandApdu was modified", apduBytes,
				commandApdu.toByteArray());

	}

	
	/**
	 * SM after BAC, example form ICAO doc 9303
	 * @throws GeneralSecurityException
	 */
	@Test
	public void processAscending_isoCase3() throws GeneralSecurityException {
		//prepare configuration
		SecureMessaging secureMessaging = new SecureMessaging(0);
		
		//provide the mocked SmDataProvider
		setField(secureMessaging, "dataProvider", dataProviderMock);
		new NonStrictExpectations() {{

			dataProviderMock.getCipherIv(); result = new IvParameterSpec(new byte[8]);
			dataProviderMock.getCipher(); result = Cipher.getInstance("DESede/CBC/NoPadding", Crypto.getCryptoProvider());
			dataProviderMock.getKeyEnc(); result = new SecretKeySpec(Hex.decode(ICAO_SK_ENC), "DESede");

			dataProviderMock.getMac(); result = Mac.getInstance("ISO9797ALG3", Crypto.getCryptoProvider());
			dataProviderMock.getKeyMac(); result = new SecretKeySpec(Hex.decode(ICAO_SK_MAC), "DESede");
			dataProviderMock.getMacAuxiliaryData(); result = Hex.decode(ICAO_SSC_PLUS1);
			dataProviderMock.getMacLength(); result = 8;

		}};
		
		// provide sample APDU
		ProcessingData pData = new ProcessingData();
		byte[] apduBytes = Hex.decode(ICAO_SM_APDU);
		pData.updateCommandApdu(this, "test command APDU", CommandApduFactory.createCommandApdu(
				apduBytes));

		// call mut
		secureMessaging.processAscending(pData);

		// extract/check CommandApdu
		CommandApdu commandApdu = pData.getCommandApdu();
		assertNotNull("commandApdu is empty", commandApdu);
		assertArrayEquals("unwrapped APDU incorrect", Hex.decode(ICAO_PLAIN_APDU),
				commandApdu.toByteArray());

	}
	
	/**
	 * SM after PACE, example from previous GlobalTester run
	 * @throws Exception
	 */
	@Test
	public void processAscending_isoCase2_AES265() throws Exception {
		//prepare configuration
		SecureMessaging secureMessaging = new SecureMessaging(0);
				
		//provide the mocked SmDataProvider
		setField(secureMessaging, "dataProvider", dataProviderMock);
		new NonStrictExpectations() {{

			dataProviderMock.getCipherIv(); result = new IvParameterSpec(Hex.decode(AES256_IV_ENC));
			dataProviderMock.getCipher(); result = Cipher.getInstance("AES/CBC/NoPadding", Crypto.getCryptoProvider());
			dataProviderMock.getKeyEnc(); result = new SecretKeySpec(Hex.decode(AES256_SK_ENC), "AES");

			dataProviderMock.getMac(); result = Mac.getInstance(AES256_MAC);
			dataProviderMock.getKeyMac(); result = new SecretKeySpec(Hex.decode(AES256_SK_MAC), "AES");
			dataProviderMock.getMacAuxiliaryData(); result = Hex.decode(AES256_SK_MAC_AUX_DATA);
			dataProviderMock.getMacLength(); result = 8;

		}};
				
		// provide sample APDU
		ProcessingData pData = new ProcessingData();
		byte[] apduBytes = Hex.decode(AES256_CASE2_SM_APDU);
		pData.updateCommandApdu(this, "test command APDU", CommandApduFactory.createCommandApdu(
				apduBytes));

		// call mut
		secureMessaging.processAscending(pData);

		// extract/check CommandApdu
		CommandApdu commandApdu = pData.getCommandApdu();
		assertNotNull("commandApdu is empty", commandApdu);
		assertArrayEquals("unwrapped APDU incorrect", Hex.decode(AES256_CASE2_PLAIN_APDU),
						commandApdu.toByteArray());
	}
	
	/**
	 * Check the correct update of SM data provider otherwise similar to
	 * {@link #processAscending_isoCase3()}
	 * 
	 * @throws GeneralSecurityException
	 */
	@Test
	public void processDescending_handleUpdatePropagation() throws GeneralSecurityException {
		//prepare dataProvider
		SmDataProviderContainerProxy dataProvider = new SmDataProviderContainerProxy();
		dataProvider.setKeyEnc(new SecretKeySpec(Hex.decode(ICAO_SK_ENC), "DESede"));
		dataProvider.setKeySpecMAC(new SecretKeySpec(Hex.decode(ICAO_SK_MAC), "DESede"));
		dataProvider.setEncIv(new IvParameterSpec(new byte[8]));
		dataProvider.setCipher(Cipher.getInstance("DESede/CBC/NoPadding", Crypto.getCryptoProvider()));
		dataProvider.setKeyEnc(new SecretKeySpec(Hex.decode(ICAO_SK_ENC), "DESede"));
		dataProvider.setMac(Mac.getInstance("ISO9797ALG3", Crypto.getCryptoProvider()));
		dataProvider.setMacAuxiliaryData(Hex.decode(ICAO_SSC_PLUS1));
		dataProvider.setMacLength(8);
		
		// mut, propagate SmDataProvider
		SecureMessaging secureMessaging = new SecureMessaging(0);
		ProcessingData pData = new ProcessingData();
		pData.addUpdatePropagation(this, "testing handling of SmDataProvider", dataProvider);
		secureMessaging.processDescending(pData);
		
		// check correct decoding of sample APDU
		ProcessingData pData2 = new ProcessingData();
		byte[] apduBytes = Hex.decode(ICAO_SM_APDU);
		pData2.updateCommandApdu(this, "test command APDU", CommandApduFactory.createCommandApdu(
				apduBytes));
		secureMessaging.processAscending(pData2);
		CommandApdu commandApdu = pData2.getCommandApdu();
		assertNotNull("commandApdu is empty", commandApdu);
		assertArrayEquals("unwrapped APDU incorrect", Hex.decode(ICAO_PLAIN_APDU),
				commandApdu.toByteArray());

	}
	
	/**
	 * Encoding of SM Response APDU must not contain DO87 if data field of plain response is absent
	 * @throws Exception
	 */
	@Test
	public void processOutgoingSmApdu_responseDataAbsent() throws Exception {
		//prepare configuration
		SecureMessaging secureMessaging = new SecureMessaging(0);
				
		//provide the mocked SmDataProvider
		setField(secureMessaging, "dataProvider", dataProviderMock);
		new NonStrictExpectations() {{

			dataProviderMock.getCipherIv(); result = new IvParameterSpec(Hex.decode(AES256_IV_ENC));
			dataProviderMock.getCipher(); result = Cipher.getInstance("AES/CBC/NoPadding", Crypto.getCryptoProvider());
			dataProviderMock.getKeyEnc(); result = new SecretKeySpec(Hex.decode(AES256_SK_ENC), "AES");

			dataProviderMock.getMac(); result = Mac.getInstance(AES256_MAC);
			dataProviderMock.getKeyMac(); result = new SecretKeySpec(Hex.decode(AES256_SK_MAC), "AES");
			dataProviderMock.getMacAuxiliaryData(); result = Hex.decode(AES256_SK_MAC_AUX_DATA);
			dataProviderMock.getMacLength(); result = 8;

		}};
				
		// provide sample APDU
		ProcessingData pData = new ProcessingData();
		pData.updateResponseAPDU(this, "test response w/o response data", new ResponseApdu(SW_9000_NO_ERROR));
		setField(secureMessaging, "processingData", pData);

		// call mut
		secureMessaging.processOutgoingSMAPDU();

		// extract/check ResponseApdu
		ResponseApdu respApdu = pData.getResponseApdu();
		assertNotNull("responseApdu is empty", respApdu);
		assertFalse("DO 87 present", ((TlvDataObjectContainer) respApdu.getData()).containsTagField(new TlvTag((byte)0x87)));
	}
	
	/**
	 * Encoding of SM Response APDU must not contain DO87 if data field of plain response is present but empty
	 * @throws Exception
	 */
	@Test
	public void processOutgoingSmApdu_responseDataLengthZero() throws Exception {
		//prepare configuration
		SecureMessaging secureMessaging = new SecureMessaging(0);
				
		//provide the mocked SmDataProvider
		setField(secureMessaging, "dataProvider", dataProviderMock);
		new NonStrictExpectations() {{

			dataProviderMock.getCipherIv(); result = new IvParameterSpec(Hex.decode(AES256_IV_ENC));
			dataProviderMock.getCipher(); result = Cipher.getInstance("AES/CBC/NoPadding", Crypto.getCryptoProvider());
			dataProviderMock.getKeyEnc(); result = new SecretKeySpec(Hex.decode(AES256_SK_ENC), "AES");

			dataProviderMock.getMac(); result = Mac.getInstance(AES256_MAC);
			dataProviderMock.getKeyMac(); result = new SecretKeySpec(Hex.decode(AES256_SK_MAC), "AES");
			dataProviderMock.getMacAuxiliaryData(); result = Hex.decode(AES256_SK_MAC_AUX_DATA);
			dataProviderMock.getMacLength(); result = 8;

		}};
				
		// provide sample APDU
		ProcessingData pData = new ProcessingData();
		pData.updateResponseAPDU(this, "test response w/o response data", new ResponseApdu(new TlvValuePlain(new byte[0]), SW_9000_NO_ERROR));
		setField(secureMessaging, "processingData", pData);

		// call mut
		secureMessaging.processOutgoingSMAPDU();

		// extract/check ResponseApdu
		ResponseApdu respApdu = pData.getResponseApdu();
		assertNotNull("responseApdu is empty", respApdu);
		assertFalse("DO 87 present", ((TlvDataObjectContainer) respApdu.getData()).containsTagField(new TlvTag((byte)0x87)));
	}

	/**
	 * Positive test: input far below blocksize
	 */
	@Test
	public void testPaddData() {
		byte[] input = HexString.toByteArray("0011223344");
		int blockSize = 8;
		byte[] exp = HexString.toByteArray("0011223344800000");

		assertArrayEquals(exp, SecureMessaging.padData(input, blockSize));
	}

	/**
	 * Positive test: input larger than blocksize, but not near any cornercase
	 */
	@Test
	public void testPaddData_largInput() {
		byte[] input = HexString.toByteArray("001122334455667700112233");
		int blockSize = 8;
		byte[] exp = HexString.toByteArray("00112233445566770011223380000000");

		assertArrayEquals(exp, SecureMessaging.padData(input, blockSize));
	}

	/**
	 * Positive test: input exactly one byte shorter than blocksize
	 */
	@Test
	public void testPaddData_inputOneShorterThanBlocksize() {
		byte[] input = HexString.toByteArray("00112233445566");
		int blockSize = 8;
		byte[] exp = HexString.toByteArray("0011223344556680");

		assertArrayEquals(exp, SecureMessaging.padData(input, blockSize));
	}

	/**
	 * Positive test: input length matches blocksize
	 */
	@Test
	public void testPaddData_inputMatchesBlocksize() {
		byte[] input = HexString.toByteArray("0011223344556677");
		int blockSize = 8;
		byte[] exp = HexString.toByteArray("00112233445566778000000000000000");

		assertArrayEquals(exp, SecureMessaging.padData(input, blockSize));
	}
	
	//TODO SMTest not yet tested functionality 
	// extendedLength in both directions
	// incoming case4 short data extended le etc.
	// check discard key on powerOn and on incoming plain APDU
	// processDescending_withoutResonseData
	// processDescending_withResonseData
	// update SM keys after wrapping outgoing APDU
	// plainApduResetKeys()	
	// processAscending_isoCase1()
	// processAscending_isoCase4()


}
