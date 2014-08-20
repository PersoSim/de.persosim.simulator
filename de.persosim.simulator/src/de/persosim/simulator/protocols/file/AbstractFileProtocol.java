package de.persosim.simulator.protocols.file;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.FileIdentifierIncorrectValueException;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.FileToShortException;
import de.persosim.simulator.exception.TagNotFoundException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.protocols.AbstractProtocolStateMachine;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.tlv.TlvValuePlain;
import de.persosim.simulator.utils.Utils;

/**
 * Back end code for the file management state machine. This class implements
 * ISO7816-compliant file management.
 * 
 * @author mboonk
 * 
 */
public abstract class AbstractFileProtocol extends AbstractProtocolStateMachine {

	static final byte P1_MASK_EF_IN_P1_P2 = (byte) 0b10000000;
	static final byte INS_MASK_ODDINS = (byte) 0x01;
	static final byte P1_MASK_SHORT_FILE_IDENTIFIER = (byte) 0b10000000;
	static final byte ODDINS_RESPONSE_TAG = 0x53;
	static final byte ODDINS_COMMAND_TAG = 0x54;
	private static final byte [] ODDINS_COMMAND_DDO_TAGS = new byte [] {0x73, 0x53};
	static final short P1P2_MASK_SFI = 0b0000000000011111;
	static final byte P1_MASK_SFI = 0b00011111;
		
	public AbstractFileProtocol() {
		super("FM");
	}

	protected void processCommandSelectFile() {
		CommandApdu cmdApdu = processingData.getCommandApdu();
		byte p1 = cmdApdu.getP1();
		byte p2 = cmdApdu.getP2();
		
		CardFile file = null;
		
		try {
			switch (p1) {
			case P1_SELECT_FILE_MF_DF_EF:
				if ((p2 & P2_SELECT_OCCURRENCE_MASK) == P2_SELECT_OCCURRENCE_FIRST) {
					if ((cmdApdu.getCommandData() == null)
							|| ((cmdApdu.getNc() == 2 && Arrays.equals(cmdApdu.getCommandData().toByteArray(),
									new byte[] { 0x3F, 0x00 })))) {
						// special file identifier for the master file (absent or 3f00)
						file = cardState.selectMasterFile();
					} else {
						file = cardState.selectFile(
								new FileIdentifier(
										Utils.getShortFromUnsignedByteArray(cmdApdu.getCommandData().toByteArray())),
								Scope.FROM_DF);
					}
				} else {
					// IMPL implement handling of other file occurrence values
					// (ISO7816 p. 38)
					ResponseApdu resp = new ResponseApdu(SW_6A81_FUNC_NOT_SUPPORTED);
					this.processingData.updateResponseAPDU(this,
							"file occurence selector not supported", resp);
				}
				break;
			case P1_SELECT_FILE_EF_UNDER_CURRENT_DF:
				file = cardState.selectFile(
						new FileIdentifier(Utils
								.getShortFromUnsignedByteArray(cmdApdu.getCommandData().toByteArray())),
						Scope.FROM_DF);
				break;
			case P1_SELECT_FILE_DF_BY_NAME:
				file = cardState.selectFile(new DedicatedFileIdentifier(
						cmdApdu.getCommandData().toByteArray()), Scope.FROM_MF);
				// IMPL support multiple calls selecting files successively (ISO7816-4
				// 7.1.1)
				break;

			}
			
			if (file != null){
				TlvDataObjectContainer fco = getFileControlInformation(file, p2);
				ResponseApdu resp = new ResponseApdu(fco, SW_9000_NO_ERROR);
				this.processingData.updateResponseAPDU(this,
						"file selected successfully", resp);
			}
			
		} catch (FileNotFoundException e) {
			ResponseApdu resp = new ResponseApdu(SW_6A82_FILE_NOT_FOUND);
			this.processingData.updateResponseAPDU(this,
					"file not selected (not found)", resp);
		} catch (NullPointerException e) {
			ResponseApdu resp = new ResponseApdu(SW_6700_WRONG_LENGTH);
			this.processingData.updateResponseAPDU(this,
					"file identifier required in command datafield", resp);
		}

		processingData.addUpdatePropagation(this,
				"FileManagement protocol is not supposed to stay on the stack",
				new ProtocolUpdate(true));
	}
	
	/**
	 * @param file {@link CardFile} to get the FCI from
	 * @param p2 the P2 byte of the {@link CommandApdu}
	 * @return the file control information depending on the selection done in the P2 byte
	 */
	private TlvDataObjectContainer getFileControlInformation(CardFile file,
			byte p2) {		
		switch (p2 & P2_SELECT_FCI_MASK){
		case P2_SELECT_FCI_TEMPLATE:
			TlvDataObjectContainer result = new TlvDataObjectContainer();
			result.addTlvDataObject(file.getFileControlParameterDataObject());
			result.addTlvDataObject(file.getFileManagementDataObject());
			return result;
		case P2_SELECT_FCP_TEMPLATE:
			return new TlvDataObjectContainer(file.getFileControlParameterDataObject());
		case P2_SELECT_FMD_TEMPLATE:
			return new TlvDataObjectContainer(file.getFileManagementDataObject());
		case P2_SELECT_NO_OR_PROPRIETARY:
			return new TlvDataObjectContainer();
		}
		
		return null;
	}

	protected void processCommandUpdateBinary() {

		boolean isOddInstruction = ((processingData.getCommandApdu().getIns() & INS_MASK_ODDINS) == INS_MASK_ODDINS);
		
		CardObject file = getFile(processingData.getCommandApdu(), cardState);

		
		int updateOffset = getOffset(processingData.getCommandApdu());
		byte[] updateData = null;

		try {
			if (isOddInstruction) {
				updateData = getDDO(processingData.getCommandApdu())
						.getValueField();
			} else {
				updateData = processingData.getCommandApdu()
						.getCommandData().toByteArray();
			}

			if (file instanceof ElementaryFile) {
				try {
					((ElementaryFile) file).update(updateOffset, updateData);
					try {
						cardState.selectFile();
						ResponseApdu resp = new ResponseApdu(
								Iso7816.SW_9000_NO_ERROR);
						this.processingData.updateResponseAPDU(this,
								"binary file updated successfully", resp);
					} catch (FileNotFoundException e) {
						ResponseApdu resp = new ResponseApdu(
								Iso7816.SW_6A82_FILE_NOT_FOUND);
						this.processingData.updateResponseAPDU(this,
								"binary file not found for selection", resp);
					}
				} catch (AccessDeniedException e) {
					ResponseApdu resp = new ResponseApdu(
							Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
					this.processingData.updateResponseAPDU(this,
							e.getMessage(), resp);
				}

			} else {
				ResponseApdu resp = new ResponseApdu(
						Iso7816.SW_6986_COMMAND_NOT_ALLOWED_NO_EF);
				this.processingData.updateResponseAPDU(this,
						"no elementary file", resp);
			}
		} catch (TagNotFoundException e) {
			ResponseApdu resp = new ResponseApdu(
					Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			this.processingData.updateResponseAPDU(this, e.getMessage(),
					resp);
		}

		processingData.addUpdatePropagation(this,
				"FileManagement protocol is not supposed to stay on the stack",
				new ProtocolUpdate(true));
	}

	/**
	 * @param apdu file management {@link CommandApdu}
	 * @return the file offset encoded in the given apdu
	 */
	private int getOffset(CommandApdu apdu){
		boolean isOddInstruction = ((apdu.getIns() & INS_MASK_ODDINS) == INS_MASK_ODDINS);
		if (isOddInstruction){
			return getOffset(apdu.getCommandDataObjectContainer());
		} else {
			return getOffset(apdu.getP1(), apdu.getP2());
		}
	}
	
	/**
	 * @param apdu
	 * @return the first discretionary data object found in the give apdu 
	 * @throws TagNotFoundException
	 */
	private TlvDataObject getDDO(CommandApdu apdu) throws TagNotFoundException{
		for (byte tag : ODDINS_COMMAND_DDO_TAGS){
			TlvDataObject result = apdu.getCommandDataObjectContainer().getTlvDataObject(new TlvTag(tag)); 
			if (result != null){
				return result;
			}
		}
		throw new TagNotFoundException("DDO was not found.");
	}
	
	/**
	 * This method is used if the file offset is encoded in the P1P2 bytes.
	 * @param p1 P1 byte
	 * @param p2 P2 byte
	 * @return the value to be used as file offset
	 */
	private int getOffset(byte p1, byte p2) {
		boolean isShortFileIdentifier = (p1 & P1_MASK_EF_IN_P1_P2) == P1_MASK_EF_IN_P1_P2;
		if (isShortFileIdentifier) {
			return p2;
		} else {
			return Utils.concatenate(p1, p2);
		}
	}

	/**
	 * This method is used if the file offset is encoded in a tlv object.
	 * @param tlv {@link TlvDataObjectContainer} that contains the offset encoding
	 * @return the value to be used as file offset
	 */
	private int getOffset(TlvDataObjectContainer tlv) {
		TlvDataObject offset = tlv.getTlvDataObject(new TlvTag(ODDINS_COMMAND_TAG));
		return Utils.getIntFromUnsignedByteArray(offset.getValueField());
	}

	/**
	 * Access method for files using an odd instruction byte
	 * @param apdu
	 * @param cardState
	 * @return the {@link CardObject} fitting the given apdu or null if no match is found 
	 */
	private static CardObject getFileOddInstruction(CommandApdu apdu, CardStateAccessor cardState){
		if ((apdu.getP1P2() | P1P2_MASK_SFI) == 0b11111 && apdu.getP1P2() != 0 && apdu.getP1P2() != 0b11111){
			//short file identifier in the last 5 bits of P1P2
			return cardState.getObject(
					new ShortFileIdentifier(apdu.getP1P2()), Scope.FROM_DF);
		} else if (apdu.getP1P2() == 0x0000){
			//select the current file
			return cardState.getCurrentFile();
		} else {
			//P1P2 encodes a card file identifier
			return cardState.getObject(new FileIdentifier(apdu.getP1P2()), Scope.FROM_DF);
		}
	}
	
	/**
	 * Access method for files using an even instruction byte
	 * @param apdu
	 * @param cardState
	 * @return the {@link CardObject} fitting the given apdu or null if no match is found 
	 */
	private static CardObject getFileEvenInstruction(CommandApdu apdu, CardStateAccessor cardState){
		if ((apdu.getP1() & P1_MASK_SHORT_FILE_IDENTIFIER) == P1_MASK_SHORT_FILE_IDENTIFIER){
			int shortFileIdentifier = apdu.getP1() & P1_MASK_SFI;			
			if (MINIMUM_SHORT_FILE_IDENTIFIER > shortFileIdentifier
					|| MAXIMUM_SHORT_FILE_IDENTIFIER < shortFileIdentifier) {
				throw new FileIdentifierIncorrectValueException();
			}
			return cardState.getObject(
					new ShortFileIdentifier(shortFileIdentifier), Scope.FROM_DF);
		}
		return cardState.getCurrentFile();
	}
	
	/**
	 * Access method for files
	 * @param apdu
	 * @param cardState
	 * @return the {@link CardObject} fitting the given apdu or null if no match is found 
	 */
	private static CardObject getFile(CommandApdu apdu, CardStateAccessor cardState) {
		boolean isOddInstruction = ((apdu.getIns() & INS_MASK_ODDINS) == INS_MASK_ODDINS);
		
		if (isOddInstruction){
			return getFileOddInstruction(apdu, cardState);
		} else {
			return getFileEvenInstruction(apdu, cardState);
		}
	}
	
	/**
	 * Utility method to read a part of a file.
	 * 
	 * @param offset
	 *            the offset in the file contents
	 * @param ne
	 *            the NE fields value
	 * @param rawFileContents
	 *            the file contents
	 * @return the file contents starting with the offset and containing up to
	 *         NE value bytes of the file
	 * @throws FileToShortException
	 */
	private static byte [] getFileContents(int offset, int ne, byte [] rawFileContents) throws FileToShortException{
		int bytesToBeRead = Math.min(ne, rawFileContents.length - offset);
		
		if (bytesToBeRead < 0) {
			throw new FileToShortException();
		}

		return Arrays.copyOfRange(rawFileContents, offset, offset + bytesToBeRead);
	}
	
	protected void processCommandReadBinary() {
		byte ins = processingData.getCommandApdu().getIns();

		int ne = processingData.getCommandApdu().getNe();
		boolean isOddInstruction = ((ins & INS_MASK_ODDINS) == INS_MASK_ODDINS);
		boolean zeroEncoded = processingData.getCommandApdu()
				.isNeZeroEncoded();

		int offset = getOffset(processingData.getCommandApdu());
		CardObject file = null;

		file = getFile(processingData.getCommandApdu(), cardState);
		

		if (file instanceof ElementaryFile) {
			ElementaryFile binaryFile = (ElementaryFile) file;

			try {
				byte [] data = getFileContents(offset, ne, binaryFile.getContent());
				boolean shortRead = !zeroEncoded && data.length < ne;
				TlvValue toSend = null;

				if (isOddInstruction) {
					toSend = new TlvDataObjectContainer(
							new PrimitiveTlvDataObject(new TlvTag(
									ODDINS_RESPONSE_TAG), data));
				} else {
					toSend = new TlvValuePlain(data);
				}
				
				try {
					cardState.selectFile();
					ResponseApdu resp = new ResponseApdu(toSend,
							shortRead
									? Iso7816.SW_6282_END_OF_FILE_REACHED_BEFORE_READING_NE_BYTES
									: Iso7816.SW_9000_NO_ERROR);
					this.processingData.updateResponseAPDU(this,
							"binary file read successfully", resp);
				} catch (FileNotFoundException e) {
					ResponseApdu resp = new ResponseApdu(toSend,
							Iso7816.SW_6A82_FILE_NOT_FOUND);
					this.processingData.updateResponseAPDU(this,
							"binary file not found for selection", resp);
				}
			} catch (FileToShortException e) {
				ResponseApdu resp = new ResponseApdu(
						Iso7816.SW_6282_END_OF_FILE_REACHED_BEFORE_READING_NE_BYTES);
				this.processingData.updateResponseAPDU(this,
						"file too short", resp);
			} catch (AccessDeniedException e) {
				ResponseApdu resp = new ResponseApdu(
						Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this,
						"binary file read access denied", resp);
			}
			
		} else {
			ResponseApdu resp = new ResponseApdu(
					Iso7816.SW_6986_COMMAND_NOT_ALLOWED_NO_EF);
			this.processingData.updateResponseAPDU(this,
					"no elemental file", resp);
		}

		processingData.addUpdatePropagation(this,
				"FileManagement protocol is not supposed to stay on the stack",
				new ProtocolUpdate(true));
	}

}
