package de.persosim.simulator.securemessaging;

import static org.globaltester.logging.BasicLogger.DEBUG;
import static org.globaltester.logging.BasicLogger.ERROR;
import static org.globaltester.logging.BasicLogger.TRACE;
import static org.globaltester.logging.BasicLogger.log;
import static org.globaltester.logging.BasicLogger.logException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import org.globaltester.simulator.LogTags;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.crypto.CryptoSupport;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Layer;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusEventUpdatePropagation;
import de.persosim.simulator.secstatus.SecStatusMechanismUpdatePropagation;
import de.persosim.simulator.secstatus.SecurityEvent;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This layer implements secure messaging according to ISO7816-4. Ascending
 * APDUs are checked and unwrapped and descending APDUs are wrapped (if the
 * matching command APDU was secured).
 * 
 * Each behavior is controlled by state stored within this layer as well as the
 * UpdatePropagations provided along the APDU.
 * 
 * @author amay
 * @author slutters
 * 
 */
public class SecureMessaging extends Layer implements TlvConstants{
	
	public static final String SECUREMESSAGING = "SecureMessaging";
	
	/*--------------------------------------------------------------------------------*/
	protected SmDataProvider dataProvider = null;
	
	/*--------------------------------------------------------------------------------*/

	@Override
	public String getLayerName() {
		return SECUREMESSAGING;
	}
	
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public void powerOn() {
		super.powerOn();
		discardSecureMessagingSession();
	}
	
	@Override
	public void processAscending() {
		if(processingData.getCommandApdu() instanceof IsoSecureMessagingCommandApdu) {
			if (((IsoSecureMessagingCommandApdu) processingData.getCommandApdu()).getSecureMessaging() != SM_OFF_OR_NO_INDICATION) {
				if (dataProvider != null) {
					if (processIncomingSmApdu()){
						//propagate changes in SM status
						SmDataProviderGenerator smDataProviderGenerator = dataProvider.getSmDataProviderGenerator();
						processingData.addUpdatePropagation(this, "init SM", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, smDataProviderGenerator));
						
						log(HexString.encode(processingData.getCommandApdu().toByteArray()), LogTags.APDU_TAG_DEC_IN);
						log(this, "successfully processed ascending secured APDU", TRACE);
					} else {
						discardSecureMessagingSession();
					}
					return;
				} else {
					log(HexString.encode(processingData.getCommandApdu().toByteArray()), LogTags.APDU_TAG_DEC_IN);
					log(this, "No SmDataProvider available", ERROR);
					
					//create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
					processingData.updateResponseAPDU(this, "SecureMessaging not properly initialized", resp);
					return;
				}
			} else {
				log(this, "don't process ascending unsecured APDU", TRACE);
			}
		} else{
			log(this, "don't process non interindustry APDU", TRACE);
		}
		
		log(HexString.encode(processingData.getCommandApdu().toByteArray()), LogTags.APDU_TAG_DEC_IN);
		
		// if this line is reached the key material needs to be discarded
		if (dataProvider != null) {
			log(this, "discard key material", DEBUG);
			discardSecureMessagingSession();
		}
	}
	
	private void discardSecureMessagingSession() {
		if (dataProvider != null) {
			log(this, "discard key material", DEBUG);
			dataProvider = null;
		} else {
			log(this, "no data provider present, nothing to discard", TRACE);
		}
		if (processingData != null) {
			processingData.addUpdatePropagation(this, "Inform the SecStatus about the ended secure messaging session",
					new SecStatusEventUpdatePropagation(
							SecurityEvent.SECURE_MESSAGING_SESSION_ENDED));
		}
	}

	/**
	 * Layer specific processing of descending APDUs.
	 * @param commandApdu.apdu The APDU received by the PICC
	 * @param processingData data collected during processing of the APDU
	 */
	@Override
	public void processDescending() {
		log(HexString.encode(getProcessingData().getResponseApdu().toByteArray()), LogTags.APDU_TAG_DEC_OUT);
		
		if (isSmWrappingApplicable()){
			processOutgoingSmApdu();
		}
		
		log(this, "successfully processed descending APDU", TRACE);
		
		handleUpdatePropagations();
	}
	
	public boolean isSmWrappingApplicable(){
		CommandApdu cApdu = processingData.getCommandApdu();

		if (!(cApdu instanceof IsoSecureMessagingCommandApdu)) {
			log(this, "descending APDU is does not support iso secure messaging",
					TRACE);
			return false;
		}

		if (((IsoSecureMessagingCommandApdu) cApdu).wasSecureMessaging()
				&& ((IsoSecureMessagingCommandApdu) cApdu).getSecureMessaging() != SM_OFF_OR_NO_INDICATION) {
			log(this,
					"descending APDU was sm secured but not unwrapped properly",
					TRACE);
			return false;
		}
		
		if (dataProvider == null){
			log(this,
					"no secure messaging session is established (no secure messaging data provider is set)",
					TRACE);
			return false;
		}
		
		return true;
	}
	
	public void handleUpdatePropagations() {
		LinkedList<UpdatePropagation> dataProviderList = processingData.getUpdatePropagations(SmDataProvider.class);
		for (UpdatePropagation curDataProvider : dataProviderList) {
			if (curDataProvider != null && curDataProvider instanceof SmDataProvider) {
				setDataProvider((SmDataProvider) curDataProvider);
			}
		}
		
	}

	/**
	 * This method performs the SM operations for outgoing APDUs if they are needed
	 */
	public void processOutgoingSmApdu() {
		log(this, "START encryption of outgoing SM APDU");
		dataProvider.nextOutgoing();
		
		TlvDataObjectContainer container = new TlvDataObjectContainer();
		
		TlvValue dataObject = this.processingData.getResponseApdu().getData();
		if((dataObject != null) && (dataObject.getLength() > 0)) {
			log(this, "APDU to be sent contains data", TRACE);
			
			byte[] data = dataObject.toByteArray();
			
			log(this, "data to be padded is: " + HexString.encode(data), TRACE);
			
			byte[] paddedData = this.padData(data);
			
			log(this, "padded data is: " + HexString.encode(paddedData), DEBUG);
			log(this, "block size is: " + dataProvider.getCipher().getBlockSize(), DEBUG);
			
			byte[] encryptedData = CryptoSupport.encrypt(dataProvider.getCipher(), paddedData, dataProvider.getKeyEnc(), dataProvider.getCipherIv());
			log(this, "encrypted data is: " + HexString.encode(encryptedData), DEBUG);
			
			// check for odd instruction byte
			if(((byte) (processingData.getCommandApdu().getIns() & (byte) 0x01)) == (byte) 0x01) {
				container.addTlvDataObject(new PrimitiveTlvDataObject(TAG_85, encryptedData));
			} else {
				byte[] postpaddedData = new byte[paddedData.length + 1];
				System.arraycopy(encryptedData, 0, postpaddedData, 1, encryptedData.length);
				postpaddedData[0] = (byte) 0x01;
				
				container.addTlvDataObject(new PrimitiveTlvDataObject(TAG_87, postpaddedData));
			}
		} else{
			log(this, "APDU to be sent contains NO data", DEBUG);
		}
		
		//add status word
		byte[] sw = Utils.toUnsignedByteArray(this.processingData.getResponseApdu().getStatusWord());
		PrimitiveTlvDataObject primitive99 = new PrimitiveTlvDataObject(TAG_99, sw);
		container.addTlvDataObject(primitive99);
		
		//add MAC
		byte[] macedData = this.padAndMac(container);
		PrimitiveTlvDataObject primitive8E = new PrimitiveTlvDataObject(TAG_8E, macedData);
		container.addTlvDataObject(primitive8E);
		
		//create and propagate response APDU
		ResponseApdu resp = new ResponseApdu(container, this.processingData.getResponseApdu().getStatusWord());
		this.processingData.updateResponseAPDU(this, "Encrypted outgoing SM APDU", resp);
	}
	
	protected byte [] padData(byte[] data) {
		return CryptoUtil.padData(data, dataProvider.getCipher().getBlockSize());
	}
	
	/**
	 * This method performs the SM operations for incoming APDUs
	 * @return if the secure messaging can be continued
	 */
	public boolean processIncomingSmApdu() {
		log(this, "start processing SM APDU", TRACE);
		dataProvider.nextIncoming();
		CommandApdu smApdu = processingData.getCommandApdu();
		
		log(this, "Incoming SM APDU is: " + smApdu.toString(), DEBUG);
		log(this, "Incoming SM APDU is ISO case: " + smApdu.getIsoCase(), DEBUG);
		
		try {
			//create new CommandAPDU
			CommandApdu plainCommand = extractPlainTextAPDU();
			log(this, "plain text APDU is " + plainCommand, DEBUG);
			
			if (verifyMac()) {
				log(this, "verification of mac: correct", DEBUG);
				
				//propagate new CommandAPDU
				processingData.updateCommandApdu(this, "SM APDU extracted", plainCommand);
				
				log(this, "completed processing SM APDU");
				return true;
			} else {
				log(this, "verification of mac: failed", ERROR);
				
				//create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6988_INCORRECT_SM_DATA_OBJECTS);
				processingData.updateResponseAPDU(this, "MAC verification failed", resp);
			}
		} catch (RuntimeException e) {
			log(this, "failure while processing incoming APDU", ERROR);
			logException(this, e, ERROR);
			
			//create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6988_INCORRECT_SM_DATA_OBJECTS);
			processingData.updateResponseAPDU(this, "decoding sm APDU failed", resp);
		}
		log(this, "completed processing SM APDU with secure messaging failure");
		return false;
	}
	
	/**
	 * This method returns a plain APDU.
	 * @return a byte array representation of an SM secured APDU
	 */
	public CommandApdu extractPlainTextAPDU() {
		TlvDataObject cryptogram, tlvObject8E, tlvObject97;
		byte[] encryptedData, paddedData, data, le, plainApduCommandData, dbgIv;
		int isoCaseOfPlainAPDU;
		ByteArrayOutputStream apduStream;
		
		log(this, "started extracting SM APDU", TRACE);
		
		if(processingData.getCommandApdu().getIsoCase() != ISO_CASE_4) {
			throw new IllegalArgumentException("SM APDU is expected to be ISO case 4");
		}
		
		if (!(processingData.getCommandApdu() instanceof IsoSecureMessagingCommandApdu)){
			throw new IllegalArgumentException("SM APDU is expected to be an IsoSecureMessagingCommandApdu");
		}
		
		TlvDataObjectContainer constructedCommandDataField = processingData.getCommandApdu().getCommandDataObjectContainer();
		tlvObject8E = constructedCommandDataField .getTlvDataObject(TAG_8E);
		log(this, "TLV object 8E is: " + tlvObject8E, TRACE);
		
		if(tlvObject8E == null) {
			//create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6987_EXPECTED_SM_DATA_OBJECTS_MISSING);
			processingData.updateResponseAPDU(this, "SM APDU is expected to contain tag 8E (mac)", resp);

			throw new IllegalArgumentException("SM APDU is expected to contain tag 8E (mac)");
		}
		
		if (processingData.getCommandApdu().getIns() %2 == 0) {
			cryptogram = constructedCommandDataField.getTlvDataObject(TAG_87);
		} else {
			cryptogram = constructedCommandDataField.getTlvDataObject(TAG_85);
		}
		tlvObject97 = constructedCommandDataField.getTlvDataObject(TAG_97);
		
		if(cryptogram == null) {
			if(tlvObject97 == null) {
				isoCaseOfPlainAPDU = 1;
			} else{
				isoCaseOfPlainAPDU = 2;
			}
		} else{
			if(tlvObject97 == null) {
				isoCaseOfPlainAPDU = 3;
			} else{
				isoCaseOfPlainAPDU = 4;
			}
		}
		
		apduStream = new ByteArrayOutputStream();
		// append extendedLengthIndicator if needed
		if (processingData.getCommandApdu().isExtendedLength()) {
			apduStream.write(0x00);
		}
		
		// append data if present 
		if(isoCaseOfPlainAPDU > 2) {
			log(this, "Cryptogram is: " + cryptogram);
			encryptedData = this.getEncryptedDataFromFormattedEncryptedData(cryptogram);
			log(this, "encrypted data is: " + HexString.encode(encryptedData));
			log(this, "used cipher iv is     : " + HexString.encode(dataProvider.getCipherIv().getIV()));
			
			dbgIv = CryptoSupport.decryptWithIvZero(dataProvider.getCipher(), dataProvider.getCipherIv().getIV(), dataProvider.getKeyEnc());
			log(this, "decrypted cipher iv is: " + HexString.encode(dbgIv));
			
			paddedData = CryptoSupport.decrypt(dataProvider.getCipher(), encryptedData, dataProvider.getKeyEnc(), dataProvider.getCipherIv());
			log(this, "padded data is: " + HexString.encode(paddedData));
			
			data = this.unpadPlainTextData(paddedData);
			log(this, "plain text data is: " + HexString.encode(data));
			
			try {
				if (processingData.getCommandApdu().isExtendedLength()) {
					apduStream.write(Utils.toUnsignedByteArray((short) data.length));
				} else {			
					apduStream.write(data.length);
				}
			
				apduStream.write(data);
			} catch (IOException e) {
				logException(this, e);
			}
			
		}
		
		// append le if present
		if((isoCaseOfPlainAPDU == 2) || (isoCaseOfPlainAPDU == 4)) {
			log(this, "TLV object 97 is: " + tlvObject97, TRACE);
			le = tlvObject97.getValueField();
			
			//ensure correct length of le field
			if (processingData.getCommandApdu().isExtendedLength()) {
				if (le.length == 1) {
					le = new byte[]{0, le[0]};
				}
			}
			
			try {
				
				apduStream.write(le);
			} catch (IOException e) {
				logException(this, e);
			}
		}
		
		plainApduCommandData = apduStream.toByteArray();
		CommandApdu result = ((IsoSecureMessagingCommandApdu)this.processingData.getCommandApdu()).rewrapApdu(Iso7816.SM_OFF_OR_NO_INDICATION, plainApduCommandData);
		log(this, "completed extracting SM APDU", TRACE);
		return result;
	}
	
	/**
	 * This method performs the mac verification for an SM secured APDU.
	 * @return the result of mac verification: true iff verified, false otherwise
	 */
	public boolean verifyMac() {
		TlvDataObject cryptogram, tlvObject8E, tlvObject97;
		byte[] extractedMac, tlv97Plain, tlv87Plain;
		byte[] header, paddingHeader, paddingMacInput, macResult;
		int paddingLengthHeader, blockSize, lengthOfMacInputData, paddingLengthMacInput;
		ByteArrayOutputStream macInputStream;
		int isoCaseOfPlainAPDU;
		
		log(this, "started verifying SM APDU", TRACE);
		
		header = this.processingData.getCommandApdu().getHeader();
		
		if(processingData.getCommandApdu().getIsoCase() != ISO_CASE_4) {
			throw new IllegalArgumentException("SM APDU is expected to be ISO case 4");
		}
		
		TlvDataObjectContainer constructedCommandDataField = processingData.getCommandApdu().getCommandDataObjectContainer();
		tlvObject8E = constructedCommandDataField.getTlvDataObject(TAG_8E);
		log(this, "TLV object 8E is: " + tlvObject8E, TRACE);
		
		if(tlvObject8E == null) {
			throw new IllegalArgumentException("SM APDU is expected to contain tag 8E (mac)");
		}
		
		if (processingData.getCommandApdu().getIns() %2 == 0) {
			cryptogram = constructedCommandDataField.getTlvDataObject(TAG_87);
		} else {
			cryptogram = constructedCommandDataField.getTlvDataObject(TAG_85);
		}
		tlvObject97 = constructedCommandDataField.getTlvDataObject(TAG_97);
		
		if(cryptogram == null) {
			if(tlvObject97 == null) {
				isoCaseOfPlainAPDU = 1;
			} else{
				isoCaseOfPlainAPDU = 2;
			}
		} else{
			if(tlvObject97 == null) {
				isoCaseOfPlainAPDU = 3;
			} else{
				isoCaseOfPlainAPDU = 4;
			}
		}
		
		if((isoCaseOfPlainAPDU == 2) || (isoCaseOfPlainAPDU == 4)) {
			log(this, "TLV object 97 is: " + tlvObject97, TRACE);
		}
		
		if(isoCaseOfPlainAPDU > 2) {
			log(this, "Cryptogram is: " + cryptogram, TRACE);
		}
		
		/* verify mac */
		
		blockSize = dataProvider.getCipher().getBlockSize();
		macInputStream = new ByteArrayOutputStream();
		
		/* header must be padded to match block size */
		paddingLengthHeader = blockSize - header.length;
		paddingHeader = new byte[paddingLengthHeader];
		Arrays.fill(paddingHeader, (byte) 0x00);
		paddingHeader[0] = (byte) 0x80;
		
		try {
			macInputStream.write(header);
			macInputStream.write(paddingHeader);
		} catch (IOException e) {
			logException(this, e);
		}
		
		lengthOfMacInputData = header.length + paddingHeader.length;
		
		if(isoCaseOfPlainAPDU > 2) {
			tlv87Plain = cryptogram.toByteArray();
			lengthOfMacInputData += tlv87Plain.length;
			
			try {
				macInputStream.write(tlv87Plain);
			} catch (IOException e) {
				logException(this, e);
			}
		}
		
		if((isoCaseOfPlainAPDU == 2) || (isoCaseOfPlainAPDU == 4)) {
			tlv97Plain = tlvObject97.toByteArray();
			lengthOfMacInputData += tlvObject97.getLength();
			
			try {
				macInputStream.write(tlv97Plain);
			} catch (IOException e) {
				logException(this, e);
			}
		}
		
		if(isoCaseOfPlainAPDU > 1) {
			/* mac input must be padded to match block size */
			log(this, "length of mac input data is " + lengthOfMacInputData + " bytes", TRACE);
			paddingLengthMacInput = blockSize - ((lengthOfMacInputData + 1) % blockSize) + 1;
			log(this, "mac input data needs " + paddingLengthMacInput + " bytes padding to match multiple of blockSize " + blockSize, TRACE);
			paddingMacInput = new byte[paddingLengthMacInput];
			Arrays.fill(paddingMacInput, (byte) 0x00);
			paddingMacInput[0] = (byte) 0x80;
			log(this, "padding of mac input data is " + HexString.encode(paddingMacInput), TRACE);
			
			try {
				macInputStream.write(paddingMacInput);
			} catch (IOException e) {
				logException(this, e);
			}
		}
		

		log(this, "padded mac input is " + HexString.encode(macInputStream.toByteArray()), TRACE);
		
		macResult = CryptoSupport.mac(dataProvider.getMac(), dataProvider.getMacAuxiliaryData(),
				dataProvider.getCipher(), macInputStream.toByteArray(), dataProvider.getKeyMac(), dataProvider.getMacLength());
		
		log(this, "expected mac is : " + HexString.encode(macResult), DEBUG);
		extractedMac = tlvObject8E.getValueField();
		log(this, "extracted mac is: " + HexString.encode(extractedMac), DEBUG);
		
		if(Arrays.equals(macResult, extractedMac)) {
			log(this, "mac match", DEBUG);
			return true;
		} else {
			log(this, "mac mismatch", ERROR);
			return false;
		}
		
		
	}
	
	/**
	 * This method extracts the encrypted data from the formatted encrypted data
	 * @param tlvDataObject the formatted encrypted data
	 * @return the encrypted data
	 */
	public byte[] getEncryptedDataFromFormattedEncryptedData(TlvDataObject tlvDataObject) {
		byte[] encryptedData, tlvDataObjectValuePlain;
		
		tlvDataObjectValuePlain = tlvDataObject.getValueField();
		if (tlvDataObject.getTlvTag().equals(TAG_87)) {
			encryptedData = Arrays.copyOfRange(tlvDataObjectValuePlain, 1, tlvDataObjectValuePlain.length);
		} else {
			encryptedData = Arrays.copyOf(tlvDataObjectValuePlain, tlvDataObjectValuePlain.length);
		}
		return encryptedData;
	}
	
	/**
	 * This method delegates padding of data for mac computation.
	 * @param unpaddedData the data to be padded
	 * @return the padded data
	 */
	public byte[] padDataForMac(byte[] unpaddedData) {
		return CryptoUtil.padData(unpaddedData, dataProvider.getCipher().getBlockSize());
	}
	
	/**
	 * This method delegates padding and mac computation of input data.
	 * @param input the data to be padded and maced
	 * @return the padded and maced data
	 */
	public byte[] padAndMac(TlvDataObjectContainer input) {
		byte[] dataToBePadded, dataToBeMaced, macedData;
		
		dataToBePadded = input.toByteArray();
		dataToBeMaced = padDataForMac(dataToBePadded);
		log(this, "data to be maced is: " + HexString.encode(dataToBeMaced));
		
		macedData = CryptoSupport.mac(dataProvider.getMac(), dataProvider.getMacAuxiliaryData(),
				dataProvider.getCipher(), dataToBeMaced, dataProvider.getKeyMac(), dataProvider.getMacLength());
		
		return macedData;
	}
	
	/**
	 * This method delegates the unpadding of padded data
	 * @param paddedData the data to remove the padding from
	 * @return the unpadded data
	 */
	public byte[] unpadPlainTextData(byte[] paddedData) {
		return unpadData(paddedData, dataProvider.getCipher().getBlockSize());
	}
	
	/**
	 * This method removes the padding from padded data.
	 * @param paddedData paddedData the data to remove the padding from
	 * @param blockSize the block size
	 * @return the unpadded data
	 */
	public static byte[] unpadData(byte[] paddedData, int blockSize) {
		if(paddedData == null) {throw new NullPointerException("padded data must not be null");}
		if(blockSize < 1) {throw new IllegalArgumentException("block size must be > 0");}
		if(paddedData.length < 1) {throw new IllegalArgumentException("padded data is too short");}
		
		byte[] unpaddedData;
		byte currentByte;
		int offsetStart, offsetEnd;
		
		offsetStart = 0;
		offsetEnd = paddedData.length - 1;
		
		for (int i = 0; i < blockSize; i++) {
			currentByte = paddedData[offsetEnd];
			
			if(currentByte == (byte) 0x00) {
				offsetEnd--;
			} else{
				if(currentByte == (byte) 0x80) {
					unpaddedData = new byte[offsetEnd - offsetStart];
					System.arraycopy(paddedData, offsetStart, unpaddedData, 0, unpaddedData.length);
					return unpaddedData;
				} else{
					throw new IllegalArgumentException("invalid padding");
				}
			}
		}
		
		throw new IllegalArgumentException("invalid padding");
	}

	private void setDataProvider(SmDataProvider newProvider) {
		log(this, "still active SM data provider is:\n" + dataProvider, TRACE);
		newProvider.init(dataProvider);
		dataProvider = newProvider;
		log(this, "updated SM data provider", TRACE);
		log(this, "new active SM data provider is:\n" + dataProvider, TRACE);
	}

	@Override
	public void initializeForUse() {
		// nothing to do here
	}
	
}
