package de.persosim.simulator.securemessaging;

import static org.globaltester.logging.BasicLogger.log;
import static org.globaltester.logging.BasicLogger.logException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;
import org.globaltester.simulator.event.DecodedCommandApduEvent;
import org.globaltester.simulator.event.DecodedResponseApduEvent;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.crypto.CryptoSupport;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.log.PersoSimLogTags;
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
public class SecureMessaging extends Layer implements TlvConstants
{

	public static final String SECUREMESSAGING = "SecureMessaging";

	/*--------------------------------------------------------------------------------*/
	protected SmDataProvider dataProvider = null;

	/*--------------------------------------------------------------------------------*/

	@Override
	public String getLayerName()
	{
		return SECUREMESSAGING;
	}

	/*--------------------------------------------------------------------------------*/

	@Override
	public void powerOn()
	{
		super.powerOn();
		discardSecureMessagingSession();
	}

	@Override
	public boolean processAscending()
	{
		if (processingData.getCommandApdu() instanceof IsoSecureMessagingCommandApdu) {
			if (((IsoSecureMessagingCommandApdu) processingData.getCommandApdu()).getSecureMessaging() != SM_OFF_OR_NO_INDICATION) {
				if (dataProvider != null) {
					if (processIncomingSmApdu()) {
						// propagate changes in SM status
						SmDataProviderGenerator smDataProviderGenerator = dataProvider.getSmDataProviderGenerator();
						processingData.addUpdatePropagation(this, "init SM", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, smDataProviderGenerator));

						processingData.notifySimulatorEventListeners(new DecodedCommandApduEvent(processingData.getCommandApdu().toByteArray()));
						log("successfully processed ascending secured APDU", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
						return true;
					}
					else {
						discardSecureMessagingSession();
						return false;
					}
				}
				else {
					log("No SmDataProvider available", LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
					processingData.notifySimulatorEventListeners(new DecodedCommandApduEvent(processingData.getCommandApdu().toByteArray()));


					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
					processingData.updateResponseAPDU(this, "SecureMessaging not properly initialized", resp);
					return false;
				}
			}
			else {
				log("don't process ascending unsecured APDU", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			}
		}
		else {
			log("don't process non interindustry APDU", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		}

		processingData.notifySimulatorEventListeners(new DecodedCommandApduEvent(processingData.getCommandApdu().toByteArray()));


		// if this line is reached the key material needs to be discarded
		if (dataProvider != null) {
			log("discard key material", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			discardSecureMessagingSession();
		}
		return true;
	}

	private void discardSecureMessagingSession()
	{
		if (dataProvider != null) {
			log("discard key material", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			dataProvider = null;
		}
		else {
			log("no data provider present, nothing to discard", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		}
		if (processingData != null) {
			processingData.addUpdatePropagation(this, "Inform the SecStatus about the ended secure messaging session",
					new SecStatusEventUpdatePropagation(SecurityEvent.SECURE_MESSAGING_SESSION_ENDED));
		}
	}

	/**
	 * Layer specific processing of descending APDUs.
	 *
	 * @param commandApdu.apdu
	 *            The APDU received by the PICC
	 * @param processingData
	 *            data collected during processing of the APDU
	 */
	@Override
	public void processDescending()
	{
		processingData.notifySimulatorEventListeners(new DecodedResponseApduEvent(processingData.getResponseApdu().toByteArray()));

		if (isSmWrappingApplicable()) {
			processOutgoingSmApdu();
		}

		log("successfully processed descending APDU", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		handleUpdatePropagations();
	}

	public boolean isSmWrappingApplicable()
	{
		CommandApdu cApdu = processingData.getCommandApdu();

		if (!(cApdu instanceof IsoSecureMessagingCommandApdu)) {
			log("descending APDU is does not support iso secure messaging", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			return false;
		}

		if (((IsoSecureMessagingCommandApdu) cApdu).wasSecureMessaging() && ((IsoSecureMessagingCommandApdu) cApdu).getSecureMessaging() != SM_OFF_OR_NO_INDICATION) {
			log("descending APDU was sm secured but not unwrapped properly", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			return false;
		}

		if (dataProvider == null) {
			log("no secure messaging session is established (no secure messaging data provider is set)", LogLevel.TRACE,
					new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			return false;
		}

		return true;
	}

	public void handleUpdatePropagations()
	{
		LinkedList<UpdatePropagation> dataProviderList = processingData.getUpdatePropagations(SmDataProvider.class);
		for (UpdatePropagation curDataProvider : dataProviderList) {
			if (curDataProvider instanceof SmDataProvider prov) {
				setDataProvider(prov);
			}
		}
	}

	/**
	 * This method performs the SM operations for outgoing APDUs if they are needed
	 */
	public void processOutgoingSmApdu()
	{
		log("START encryption of outgoing SM APDU", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		dataProvider.nextOutgoing();

		TlvDataObjectContainer container = new TlvDataObjectContainer();

		TlvValue dataObject = this.processingData.getResponseApdu().getData();
		if ((dataObject != null) && (dataObject.getLength() > 0)) {
			log("APDU to be sent contains data", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			byte[] data = dataObject.toByteArray();

			log("data to be padded is: " + HexString.encode(data), LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			byte[] paddedData = this.padData(data);

			log("padded data is: " + HexString.encode(paddedData), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			log("block size is: " + dataProvider.getCipher().getBlockSize(), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			byte[] encryptedData = CryptoSupport.encrypt(dataProvider.getCipher(), paddedData, dataProvider.getKeyEnc(), dataProvider.getCipherIv());
			log("encrypted data is: " + HexString.encode(encryptedData), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			// check for odd instruction byte
			if (((byte) (processingData.getCommandApdu().getIns() & (byte) 0x01)) == (byte) 0x01) {
				container.addTlvDataObject(new PrimitiveTlvDataObject(TAG_85, encryptedData));
			}
			else {
				byte[] postpaddedData = new byte[paddedData.length + 1];
				System.arraycopy(encryptedData, 0, postpaddedData, 1, encryptedData.length);
				postpaddedData[0] = (byte) 0x01;

				container.addTlvDataObject(new PrimitiveTlvDataObject(TAG_87, postpaddedData));
			}
		}
		else {
			log("APDU to be sent contains NO data", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		}

		// add status word
		byte[] sw = Utils.toUnsignedByteArray(this.processingData.getResponseApdu().getStatusWord());
		PrimitiveTlvDataObject primitive99 = new PrimitiveTlvDataObject(TAG_99, sw);
		container.addTlvDataObject(primitive99);

		// add MAC
		byte[] macedData = this.padAndMac(container);
		PrimitiveTlvDataObject primitive8E = new PrimitiveTlvDataObject(TAG_8E, macedData);
		container.addTlvDataObject(primitive8E);

		// create and propagate response APDU
		ResponseApdu resp = new ResponseApdu(container, this.processingData.getResponseApdu().getStatusWord());
		this.processingData.updateResponseAPDU(this, "Encrypted outgoing SM APDU", resp);
	}

	protected byte[] padData(byte[] data)
	{
		return CryptoUtil.padData(data, dataProvider.getCipher().getBlockSize());
	}

	/**
	 * This method performs the SM operations for incoming APDUs
	 *
	 * @return if the secure messaging can be continued
	 */
	public boolean processIncomingSmApdu()
	{
		log("start processing SM APDU", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		dataProvider.nextIncoming();
		CommandApdu smApdu = processingData.getCommandApdu();

		log("Incoming SM APDU is: " + smApdu.toString(), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		log("Incoming SM APDU is ISO case: " + smApdu.getIsoCase(), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		try {
			// create new CommandAPDU
			CommandApdu plainCommand = extractPlainTextAPDU();
			log("plain text APDU is " + plainCommand, LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			if (verifyMac()) {
				log("verification of mac: correct", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

				// propagate new CommandAPDU
				processingData.updateCommandApdu(this, "SM APDU extracted", plainCommand);

				log("completed processing SM APDU", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
				return true;
			}
			else {
				log("verification of mac: failed", LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6988_INCORRECT_SM_DATA_OBJECTS);
				processingData.updateResponseAPDU(this, "MAC verification failed", resp);
			}
		}
		catch (RuntimeException e) {
			log("failure while processing incoming APDU", LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6988_INCORRECT_SM_DATA_OBJECTS);
			processingData.updateResponseAPDU(this, "decoding sm APDU failed", resp);
		}
		log("completed processing SM APDU with secure messaging failure", LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		return false;
	}

	/**
	 * This method returns a plain APDU.
	 *
	 * @return a byte array representation of an SM secured APDU
	 */
	public CommandApdu extractPlainTextAPDU()
	{
		log("started extracting SM APDU", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		if (processingData.getCommandApdu().getIsoCase() != ISO_CASE_4) {
			throw new IllegalArgumentException("SM APDU is expected to be ISO case 4");
		}

		if (!(processingData.getCommandApdu() instanceof IsoSecureMessagingCommandApdu)) {
			throw new IllegalArgumentException("SM APDU is expected to be an IsoSecureMessagingCommandApdu");
		}

		TlvDataObjectContainer constructedCommandDataField = processingData.getCommandApdu().getCommandDataObjectContainer();
		TlvDataObject tlvObject8E = constructedCommandDataField.getTlvDataObject(TAG_8E);
		log("TLV object 8E is: " + tlvObject8E, LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		if (tlvObject8E == null) {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6987_EXPECTED_SM_DATA_OBJECTS_MISSING);
			processingData.updateResponseAPDU(this, "SM APDU is expected to contain tag 8E (mac)", resp);

			throw new IllegalArgumentException("SM APDU is expected to contain tag 8E (mac)");
		}

		TlvDataObject cryptogram;
		if (processingData.getCommandApdu().getIns() % 2 == 0) {
			cryptogram = constructedCommandDataField.getTlvDataObject(TAG_87);
		}
		else {
			cryptogram = constructedCommandDataField.getTlvDataObject(TAG_85);
		}
		TlvDataObject tlvObject97 = constructedCommandDataField.getTlvDataObject(TAG_97);

		int isoCaseOfPlainAPDU;
		if (cryptogram == null) {
			if (tlvObject97 == null) {
				isoCaseOfPlainAPDU = 1;
			}
			else {
				isoCaseOfPlainAPDU = 2;
			}
		}
		else {
			if (tlvObject97 == null) {
				isoCaseOfPlainAPDU = 3;
			}
			else {
				isoCaseOfPlainAPDU = 4;
			}
		}

		ByteArrayOutputStream apduStream = new ByteArrayOutputStream();
		// append extendedLengthIndicator if needed
		if (processingData.getCommandApdu().isExtendedLength()) {
			apduStream.write(0x00);
		}

		// append data if present
		if (isoCaseOfPlainAPDU > 2) {
			log("Cryptogram is: " + cryptogram, LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			byte[] encryptedData = this.getEncryptedDataFromFormattedEncryptedData(cryptogram);
			log("encrypted data is: " + HexString.encode(encryptedData), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			log("used cipher iv is     : " + HexString.encode(dataProvider.getCipherIv().getIV()), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			byte[] dbgIv = CryptoSupport.decryptWithIvZero(dataProvider.getCipher(), dataProvider.getCipherIv().getIV(), dataProvider.getKeyEnc());
			log("decrypted cipher iv is: " + HexString.encode(dbgIv), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			byte[] paddedData = CryptoSupport.decrypt(dataProvider.getCipher(), encryptedData, dataProvider.getKeyEnc(), dataProvider.getCipherIv());
			log("padded data is: " + HexString.encode(paddedData), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			byte[] data = this.unpadPlainTextData(paddedData);
			log("plain text data is: " + HexString.encode(data), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			try {
				if (processingData.getCommandApdu().isExtendedLength()) {
					apduStream.write(Utils.toUnsignedByteArray((short) data.length));
				}
				else {
					apduStream.write(data.length);
				}

				apduStream.write(data);
			}
			catch (IOException e) {
				logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			}
		}

		// append le if present
		if ((isoCaseOfPlainAPDU == 2) || (isoCaseOfPlainAPDU == 4)) {
			log("TLV object 97 is: " + tlvObject97, LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			try {
				if (tlvObject97 == null)
					throw new IOException("Extracting of Plain Text APDU failed. TLV object 97 is null.");
				byte[] le = tlvObject97.getValueField();

				// ensure correct length of le field
				if (processingData.getCommandApdu().isExtendedLength() && (le.length == 1)) {
					le = new byte[] { 0, le[0] };
				}

				apduStream.write(le);
			}
			catch (IOException e) {
				logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			}
		}

		byte[] plainApduCommandData = apduStream.toByteArray();
		CommandApdu result = ((IsoSecureMessagingCommandApdu) this.processingData.getCommandApdu()).rewrapApdu(Iso7816.SM_OFF_OR_NO_INDICATION, plainApduCommandData);
		log(this, "completed extracting SM APDU", LogLevel.TRACE);
		return result;
	}

	/**
	 * This method performs the mac verification for an SM secured APDU.
	 *
	 * @return the result of mac verification: true if verified, false otherwise
	 */
	public boolean verifyMac()
	{
		TlvDataObject cryptogram;
		TlvDataObject tlvObject8E;
		TlvDataObject tlvObject97;
		byte[] extractedMac;
		byte[] tlv97Plain;
		byte[] tlv87Plain;
		byte[] header;
		byte[] paddingHeader;
		byte[] macResult;
		int paddingLengthHeader;
		int blockSize;
		ByteArrayOutputStream macInputStream;
		int isoCaseOfPlainAPDU;

		log("started verifying SM APDU", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		header = this.processingData.getCommandApdu().getHeader();

		if (processingData.getCommandApdu().getIsoCase() != ISO_CASE_4) {
			throw new IllegalArgumentException("SM APDU is expected to be ISO case 4");
		}

		TlvDataObjectContainer constructedCommandDataField = processingData.getCommandApdu().getCommandDataObjectContainer();
		tlvObject8E = constructedCommandDataField.getTlvDataObject(TAG_8E);
		log("TLV object 8E is: " + tlvObject8E, LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		if (tlvObject8E == null) {
			throw new IllegalArgumentException("SM APDU is expected to contain tag 8E (mac)");
		}

		if (processingData.getCommandApdu().getIns() % 2 == 0) {
			cryptogram = constructedCommandDataField.getTlvDataObject(TAG_87);
		}
		else {
			cryptogram = constructedCommandDataField.getTlvDataObject(TAG_85);
		}
		tlvObject97 = constructedCommandDataField.getTlvDataObject(TAG_97);

		if (cryptogram == null) {
			if (tlvObject97 == null) {
				isoCaseOfPlainAPDU = 1;
			}
			else {
				isoCaseOfPlainAPDU = 2;
			}
		}
		else {
			if (tlvObject97 == null) {
				isoCaseOfPlainAPDU = 3;
			}
			else {
				isoCaseOfPlainAPDU = 4;
			}
		}

		if ((isoCaseOfPlainAPDU == 2) || (isoCaseOfPlainAPDU == 4)) {
			log("TLV object 97 is: " + tlvObject97, LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		}

		if (isoCaseOfPlainAPDU > 2) {
			log("Cryptogram is: " + cryptogram, LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
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
		}
		catch (IOException e) {
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		}

		if (isoCaseOfPlainAPDU > 2 && cryptogram != null) {
			tlv87Plain = cryptogram.toByteArray();

			try {
				macInputStream.write(tlv87Plain);
			}
			catch (IOException e) {
				logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			}
		}

		if ((isoCaseOfPlainAPDU == 2 || isoCaseOfPlainAPDU == 4) && tlvObject97 != null) {
			tlv97Plain = tlvObject97.toByteArray();

			try {
				macInputStream.write(tlv97Plain);
			}
			catch (IOException e) {
				logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			}
		}

		byte[] macInput = macInputStream.toByteArray();

		if (isoCaseOfPlainAPDU > 1) {
			macInput = padDataForMac(macInput);
			log("padding of mac input data is " + HexString.encode(macInput), LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		}


		log("padded mac input is " + HexString.encode(macInput), LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		macResult = CryptoSupport.mac(dataProvider.getMac(), dataProvider.getMacAuxiliaryData(), dataProvider.getCipher(), macInput, dataProvider.getKeyMac(), dataProvider.getMacLength());

		log("expected mac is : " + HexString.encode(macResult), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		extractedMac = tlvObject8E.getValueField();
		log("extracted mac is: " + HexString.encode(extractedMac), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		if (Arrays.equals(macResult, extractedMac)) {
			log("mac match", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			return true;
		}
		else {
			log("mac mismatch", LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			return false;
		}


	}

	/**
	 * This method extracts the encrypted data from the formatted encrypted data
	 *
	 * @param tlvDataObject
	 *            the formatted encrypted data
	 * @return the encrypted data
	 */
	public byte[] getEncryptedDataFromFormattedEncryptedData(TlvDataObject tlvDataObject)
	{
		byte[] encryptedData;
		byte[] tlvDataObjectValuePlain;

		tlvDataObjectValuePlain = tlvDataObject.getValueField();
		if (tlvDataObject.getTlvTag().equals(TAG_87)) {
			encryptedData = Arrays.copyOfRange(tlvDataObjectValuePlain, 1, tlvDataObjectValuePlain.length);
		}
		else {
			encryptedData = Arrays.copyOf(tlvDataObjectValuePlain, tlvDataObjectValuePlain.length);
		}
		return encryptedData;
	}

	/**
	 * This method delegates padding of data for mac computation.
	 *
	 * @param unpaddedData
	 *            the data to be padded
	 * @return the padded data
	 */
	public byte[] padDataForMac(byte[] unpaddedData)
	{
		return CryptoUtil.padData(unpaddedData, dataProvider.getCipher().getBlockSize());
	}

	/**
	 * This method delegates padding and mac computation of input data.
	 *
	 * @param input
	 *            the data to be padded and maced
	 * @return the padded and maced data
	 */
	public byte[] padAndMac(TlvDataObjectContainer input)
	{
		byte[] dataToBePadded;
		byte[] dataToBeMaced;
		byte[] macedData;

		dataToBePadded = input.toByteArray();
		dataToBeMaced = padDataForMac(dataToBePadded);
		log("data to be maced is: " + HexString.encode(dataToBeMaced), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		macedData = CryptoSupport.mac(dataProvider.getMac(), dataProvider.getMacAuxiliaryData(), dataProvider.getCipher(), dataToBeMaced, dataProvider.getKeyMac(), dataProvider.getMacLength());

		return macedData;
	}

	/**
	 * This method delegates the unpadding of padded data
	 *
	 * @param paddedData
	 *            the data to remove the padding from
	 * @return the unpadded data
	 */
	public byte[] unpadPlainTextData(byte[] paddedData)
	{
		return unpadData(paddedData, dataProvider.getCipher().getBlockSize());
	}

	/**
	 * This method removes the padding from padded data.
	 *
	 * @param paddedData
	 *            paddedData the data to remove the padding from
	 * @param blockSize
	 *            the block size
	 * @return the unpadded data
	 */
	public static byte[] unpadData(byte[] paddedData, int blockSize)
	{
		if (paddedData == null) {
			throw new NullPointerException("padded data must not be null");
		}
		if (blockSize < 1) {
			throw new IllegalArgumentException("block size must be > 0");
		}
		if (paddedData.length < 1) {
			throw new IllegalArgumentException("padded data is too short");
		}

		byte[] unpaddedData;
		byte currentByte;
		int offsetStart;
		int offsetEnd;

		offsetStart = 0;
		offsetEnd = paddedData.length - 1;

		for (int i = 0; i < blockSize; i++) {
			currentByte = paddedData[offsetEnd];

			if (currentByte == (byte) 0x00) {
				offsetEnd--;
			}
			else {
				if (currentByte == (byte) 0x80) {
					unpaddedData = new byte[offsetEnd - offsetStart];
					System.arraycopy(paddedData, offsetStart, unpaddedData, 0, unpaddedData.length);
					return unpaddedData;
				}
				else {
					throw new IllegalArgumentException("invalid padding");
				}
			}
		}

		throw new IllegalArgumentException("invalid padding");
	}

	private void setDataProvider(SmDataProvider newProvider)
	{
		log("still active SM data provider is:\n" + dataProvider, LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		newProvider.init(dataProvider);
		dataProvider = newProvider;
		log("updated SM data provider", LogLevel.TRACE);
		log("new active SM data provider is:\n" + dataProvider, LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
	}

	@Override
	public void initializeForUse()
	{
		// nothing to do here
	}

}
