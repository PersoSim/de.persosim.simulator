package de.persosim.simulator.protocols.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.ProcessingException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusMechanismUpdatePropagation;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import mockit.Deencapsulation;

/**
 * Unit tests for the file management protocol.
 * 
 * @author mboonk
 * 
 */
public class AbstractFileProtocolTest extends PersoSimTestCase {

	CardStateAccessor cardStateAccessor;
	SecStatus secStatus;
	MasterFile masterFile;
	DedicatedFile dedicatedFile;
	CardFile elementaryFileUnderDF1;
	CardFile elementaryFileUnderDF2;
	ElementaryFile elementaryFileUnderMf;
	byte[] elementaryFileContent;
	DefaultFileProtocol fileProtocol;

	/**
	 * Set up a fresh file tree for each test. 
	 * 
	 * MF ------DF(0110) - EF1(011A,1)
	 *    \      \
	 *     \      ----- EF2(011B,2)
	 *      \
	 *       --- EF3(011C,3)
	 * @throws ReflectiveOperationException 
	 * @throws AccessDeniedException 
	 */
	@Before	public void setUp() throws ReflectiveOperationException, AccessDeniedException {
		secStatus = new SecStatus();

		//define file contents
		byte[] elementaryFile1UnderDFContent = new byte []{1,2,3,4,5,6};
		byte[] elementaryFile2UnderDFContent = new byte []{7,8,9,10,11,12};
		elementaryFileContent = new byte[] { 1, 2, 3, 4, 5, 6 };
		
		// setup fresh object tree
		masterFile = new MasterFile();
		masterFile.setSecStatus(secStatus);
		
		dedicatedFile = new DedicatedFile(new FileIdentifier(0x0110), new DedicatedFileIdentifier(new byte [] {0x0A, 0x00, 0x00, 0x01}));
		masterFile.addChild(dedicatedFile);
		elementaryFileUnderMf = new ElementaryFile(new FileIdentifier(0x011C), new ShortFileIdentifier(3), elementaryFileContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		elementaryFileUnderMf.setSecStatus(new SecStatus());
		masterFile.addChild(elementaryFileUnderMf);
		elementaryFileUnderDF1 = new ElementaryFile(new FileIdentifier(0x011A), new ShortFileIdentifier(1), elementaryFile1UnderDFContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		dedicatedFile.addChild(elementaryFileUnderDF1);
		elementaryFileUnderDF2 = new ElementaryFile(new FileIdentifier(0x011B), new ShortFileIdentifier(2), elementaryFile2UnderDFContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		dedicatedFile.addChild(elementaryFileUnderDF2);
	
		cardStateAccessor = new CardStateAccessor(){
			@Override
			public MasterFile getMasterFile() {
				return masterFile;
			}

			@Override
			public Collection<SecMechanism> getCurrentMechanisms(SecContext context,
					Collection<Class<? extends SecMechanism>> wantedMechanisms) {
				return secStatus.getCurrentMechanisms(context, wantedMechanisms);
			}
		};
		
		

		// create file to test
		
		//create and init the object under test
		fileProtocol = new DefaultFileProtocol();
		fileProtocol.setCardStateAccessor(cardStateAccessor);
		fileProtocol.init();
	}

	/**
	 * Select a file and check for the status word.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectFile() throws FileNotFoundException {
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00A4020C02011C");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertTrue("Statusword is not 9000", processingData.getResponseApdu()
				.getStatusWord() == Iso7816.SW_9000_NO_ERROR);
		secStatus.updateMechanisms(processingData.getUpdatePropagations(SecStatusMechanismUpdatePropagation.class).toArray(new SecStatusMechanismUpdatePropagation[]{}));
		assertEquals("file not correctly selected", elementaryFileUnderMf, CurrentFileHandler.getCurrentFile(cardStateAccessor));
	}

	/**
	 * Select a non existing file and check for the file not found status word.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSelectNonExistingFile() throws FileNotFoundException {
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xA4, 0x02, 0x0C, 0x02,
				0x01, 0x1B };
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertTrue(processingData.getResponseApdu().getStatusWord() == Iso7816.SW_6A82_FILE_NOT_FOUND);
	}

	/**
	 * Selecting MF with special file identifier 3F 00 .
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectMf() throws FileNotFoundException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		//select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 A4 00 0C 02 3F 00");
		processingData.updateCommandApdu(this, "select MF APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertEquals("wrong SW returned", Iso7816Lib.SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord());
		secStatus.updateMechanisms(processingData.getUpdatePropagations(SecStatusMechanismUpdatePropagation.class).toArray(new SecStatusMechanismUpdatePropagation[]{}));
		assertEquals("file not correctly selected", masterFile, CurrentFileHandler.getCurrentFile(cardStateAccessor));
	}

	/**
	 * Select MF through empty data field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectMf_emptyDataField() throws FileNotFoundException {
		//prepare test data
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 A4 00 0C");
		processingData.updateCommandApdu(this, "select MF APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertEquals("wrong SW returned", Iso7816Lib.SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord());
		secStatus.updateMechanisms(processingData.getUpdatePropagations(SecStatusMechanismUpdatePropagation.class).toArray(new SecStatusMechanismUpdatePropagation[]{}));
		assertEquals("file not correctly selected", masterFile, CurrentFileHandler.getCurrentFile(cardStateAccessor));
	}

	/**
	 * Try to read too many bytes of a file and check for the correct status
	 * word.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testReadBinaryTooLong() throws FileNotFoundException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// read binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB0, 0x00, 0x00, 0x09 };
		processingData.updateCommandApdu(this, "read binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertTrue(
				"Incorrect status word",
				processingData.getResponseApdu().getStatusWord() == Iso7816.SW_6282_END_OF_FILE_REACHED_BEFORE_READING_NE_BYTES);
		assertTrue("no file content", processingData.getResponseApdu().getData()
				!= null);
		assertArrayEquals("file content not as expected", elementaryFileContent, processingData
				.getResponseApdu().getData().toByteArray());
	}

	/**
	 * Try to read up to 255 bytes of a file using a LE=0 encoding (NE=255) and
	 * check for the correct status word.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testReadBinaryExpectedLengthEncodingZero()
			throws FileNotFoundException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// read binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB0, 0x00, 0x00, 0x00 };
		processingData.updateCommandApdu(this, "read binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertTrue("Incorrect status word", processingData.getResponseApdu()
				.getStatusWord() == Iso7816.SW_9000_NO_ERROR);
	}

	/**
	 * Try to read 0 bytes of a file and check for the correct status word.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testReadBinaryExpectedLengthZero() throws FileNotFoundException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// read binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB0, 0x00, 0x00 };
		processingData.updateCommandApdu(this, "read binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertTrue("Incorrect status word", processingData.getResponseApdu()
				.getStatusWord() == Iso7816.SW_9000_NO_ERROR);
	}

	/**
	 * Read a part of a file and check the status word and file content.
	 * @throws Exception 
	 */
	@Test
	public void testReadBinary() throws FileNotFoundException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// read binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB0, 0x00, 0x00, 0x04 };
		processingData.updateCommandApdu(this, "read binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertTrue("Statusword is not 9000", processingData.getResponseApdu()
				.getStatusWord() == Iso7816.SW_9000_NO_ERROR);
		assertArrayEquals("file content not as expected", processingData
				.getResponseApdu().getData().toByteArray(),
				Arrays.copyOfRange(elementaryFileContent, 0, 4));
	}

	/**
	 * Read a file using a short file identifier and check the statusword.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testReadBinaryShortFileIdentifier() throws FileNotFoundException {
		// read binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB0, (byte) 0x83, 0x01, 0x04 }; // read a file with SFI = 1 and offset 1
		processingData.updateCommandApdu(this, "read binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertTrue("Statusword is not 9000", processingData.getResponseApdu()
				.getStatusWord() == Iso7816.SW_9000_NO_ERROR);
	}

	/**
	 * Read a part of a file using an odd instruction and check the status word
	 * and response content encoding.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadBinaryOddInstruction() throws FileNotFoundException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// read binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB1, 0x00, 0x00, 0x03,
				0x54, 0x01, 0x00, 0x04 };
		processingData.updateCommandApdu(this, "read binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		TlvValue expected = new TlvDataObjectContainer(
				new PrimitiveTlvDataObject(new TlvTag((byte) 0x53),
						Arrays.copyOfRange(elementaryFileContent, 0, 4)));
		assertTrue("Statusword is not 9000", processingData.getResponseApdu()
				.getStatusWord() == Iso7816.SW_9000_NO_ERROR);
		assertArrayEquals("file content not as expected", processingData
				.getResponseApdu().getData().toByteArray(),
				expected.toByteArray());
	}

	/**
	 * Read a file using an odd instruction and a short file identifier and check the status word
	 * and response content encoding.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadBinaryOddInstructionWithShortFileIdentifier() throws FileNotFoundException {
		// read binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB1, 0x00, 0x03, 0x03,
				0x54, 0x01, 0x00, 0x04 };
		processingData.updateCommandApdu(this, "read binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		TlvValue expected = new TlvDataObjectContainer(
				new PrimitiveTlvDataObject(new TlvTag((byte) 0x53),
						Arrays.copyOfRange(elementaryFileContent, 0, 4)));
		assertTrue("Statusword is not 9000", processingData.getResponseApdu()
				.getStatusWord() == Iso7816.SW_9000_NO_ERROR);
		assertArrayEquals("file content not as expected", processingData
				.getResponseApdu().getData().toByteArray(),
				expected.toByteArray());
	}

	/**
	 * Try to update a binary file and check the status word and new file
	 * contents by reading it directly after writing.
	 * @throws AccessDeniedException 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateBinary() throws FileNotFoundException, AccessDeniedException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// update binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xD6, 0x00, 0x00, 0x06, 0,
				0, 0, 0, 0, 0 };
		processingData.updateCommandApdu(this, "update binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertTrue("Statusword is not 9000", processingData.getResponseApdu()
				.getStatusWord() == Iso7816.SW_9000_NO_ERROR);
		assertArrayEquals("file content not updated correctly", new byte[]{0, 0, 0, 0, 0, 0}, elementaryFileUnderMf.getContent());
	}

	/**
	 * Try to update a binary file using an odd instruction offset and data and
	 * check the status word and new file contents by reading it directly after
	 * writing.
	 * 
	 * @throws AccessDeniedException
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateBinaryOddInstruction() throws FileNotFoundException,
			AccessDeniedException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// update binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xD7, 0x00, 0x00,
				0x07, //length
				0x54, 0x01, 2, //offset data object
				0x53, 0x02, (byte) 0xFF, (byte) 0xFF // DDO containing Data
				};
		processingData.updateCommandApdu(this, "update binary APDU odd ins",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertTrue(processingData.getResponseApdu().getStatusWord() == Iso7816.SW_9000_NO_ERROR);
		assertArrayEquals("file content not updated correctly", new byte[]{1, 2, (byte) 0xFF, (byte) 0xFF, 5, 6}, elementaryFileUnderMf.getContent());
	}

	@Test
	public void testProtocolRemovalAfterSelect() throws FileNotFoundException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		final ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xA4, 0x02, 0x0C, 0x02,
				0x01, 0x1B };
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);
		
		//check that the protocol is removed
		LinkedList<UpdatePropagation> updatePropagations = processingData.getUpdatePropagations(ProtocolUpdate.class);
		assertEquals("No ProtocolUpdate available", 1, updatePropagations.size());
		assertTrue("Protocol shall not be removed", ((ProtocolUpdate) updatePropagations.get(0)).isFinished());
	}

	@Test
	public void testProtocolRemovalAfterReadBinary()
			throws FileNotFoundException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// read binary APDU
		final ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB0, 0x00, 0x00, 0x04 };
		processingData.updateCommandApdu(this, "read binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		//check that the protocol is removed
		LinkedList<UpdatePropagation> updatePropagations = processingData.getUpdatePropagations(ProtocolUpdate.class);
		assertEquals("No ProtocolUpdate available", 1, updatePropagations.size());
		assertTrue("Protocol shall not be removed", ((ProtocolUpdate) updatePropagations.get(0)).isFinished());
	}

	@Test
	public void testProtocolRemovalAfterUpdateBinary()
			throws FileNotFoundException {
		//prepare test data (select elementaryFile)
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// update binary APDU
		final ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xD6, 0x00, 0x00, 0x06, 0,
				0, 0, 0, 0, 0 };
		processingData.updateCommandApdu(this, "update binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		//check that the protocol is removed
		LinkedList<UpdatePropagation> updatePropagations = processingData.getUpdatePropagations(ProtocolUpdate.class);
		assertEquals("No ProtocolUpdate available", 1, updatePropagations.size());
		assertTrue("Protocol shall not be removed", ((ProtocolUpdate) updatePropagations.get(0)).isFinished());
	}
	
	/**
	 * Positive test case: perform getFileContents requesting only a small part of the file.
	 */
	@Test
	public void testGetFileContents() {
		byte[] testDataSource = HexString.toByteArray("00 01 02 03 04 05 06 07 08 09");
		
		byte[] dataReceived = Deencapsulation.invoke(AbstractFileProtocol.class, "getFileContents", 2, 2, testDataSource);
		
		byte[] dataExpected = HexString.toByteArray("02 03");
		
		assertArrayEquals("array not matching", dataExpected, dataReceived);
	}
	//IMPL missing tests getContents, with zero offset, with range larger than file, etc.

	@Test
	public void testEraseFileEvenInstructionNoCommandData() throws Exception{

		// erase binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x0E, 0x00, 0x00 };
		processingData.updateCommandApdu(this, "erase binary APDU", CommandApduFactory.createCommandApdu(apduBytes));
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));
		// call mut
		fileProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", processingData.getResponseApdu().getStatusWord(),
				Iso7816.SW_9000_NO_ERROR);
		assertArrayEquals(new byte [] {0,0,0,0,0,0}, elementaryFileContent);
	}
	
	
	/**
	 * Select a file from MF.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testSelectFileFromMF() throws FileNotFoundException{
		//construct test data
		FileIdentifier id = new FileIdentifier(0x011C);
		
		//run mut
		CardFile result = AbstractFileProtocol.getFileForSelection(masterFile, id);

		
		// evaluate result
		assertEquals("wrong file returned", elementaryFileUnderMf, result);
	}
	
	/**
	 * Select a file from DF.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testSelectFileFromDF() throws FileNotFoundException{
		//construct test data
		FileIdentifier id = new FileIdentifier(0x011A);

		//run mut
		CardFile result = AbstractFileProtocol.getFileForSelection(dedicatedFile, id);

		
		// evaluate result
		assertEquals("wrong file returned", elementaryFileUnderDF1, result);
	}
	
	/**
	 * Search for EF3 using with DF as the last selected file.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testGetObjectFromDF() throws FileNotFoundException{		//construct test data
		//construct test data
		FileIdentifier id = new FileIdentifier(0x011C);
		
		//run mut
		CardFile result = AbstractFileProtocol.getFileForSelection(dedicatedFile, id);

		
		// evaluate result
		assertEquals("wrong file returned", elementaryFileUnderMf, result);
	}
	
	@Test
	public void testEraseFileEvenInstructionValidOffset() throws Exception{

		// erase binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x0E, 0x00, 0x02 };
		processingData.updateCommandApdu(this, "erase binary APDU", CommandApduFactory.createCommandApdu(apduBytes));
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));
		// call mut
		fileProtocol.process(processingData);

		assertEquals("Statusword is not 9000", processingData.getResponseApdu().getStatusWord(),
				Iso7816.SW_9000_NO_ERROR);

		assertArrayEquals(new byte [] {1,2,0,0,0,0}, elementaryFileContent);
	}
	
	@Test
	public void testEraseFileOddInstructionInvalidOffsetDataObject() throws Exception{

		// erase binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x0F, 0x00, 0x00, 0x02, 0x54, 0x00 };
		processingData.updateCommandApdu(this, "erase binary APDU", CommandApduFactory.createCommandApdu(apduBytes));
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));
		
		// call mut
		try {
		fileProtocol.process(processingData);
		} catch (ProcessingException e) {

			assertEquals(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE, e.getStatusWord());
	
			assertArrayEquals(new byte [] {1,2,3,4,5,6}, elementaryFileContent);
			return;
		}
		fail("Expected Exception not thrown");
	}
	
	@Test
	public void testEraseFileOddInstructionValidOffsetDataObject() throws Exception{

		// erase binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x0F, 0x00, 0x00, 0x03, 0x54, 0x01, 0x03 };
		processingData.updateCommandApdu(this, "erase binary APDU", CommandApduFactory.createCommandApdu(apduBytes));
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));
		
		// call mut
		fileProtocol.process(processingData);

		assertEquals("Statusword is not 9000", processingData.getResponseApdu().getStatusWord(),
				Iso7816.SW_9000_NO_ERROR);

		assertArrayEquals(new byte [] {1,2,3,0,0,0}, elementaryFileUnderMf.getContent());
	}
	
	@Test
	public void testEraseFileOddInstructionTwoValidOffsetDataObjects() throws Exception{

		// erase binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x0F, 0x00, 0x00, 0x06, 0x54, 0x01, 0x03, 0x54, 0x01, 0x04 };
		processingData.updateCommandApdu(this, "erase binary APDU", CommandApduFactory.createCommandApdu(apduBytes));
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(elementaryFileUnderMf)));

		// call mut
		fileProtocol.process(processingData);

		assertEquals("Statusword is not 9000", processingData.getResponseApdu().getStatusWord(),
				Iso7816.SW_9000_NO_ERROR);

		assertArrayEquals(new byte [] {1,2,3,0,5,6}, elementaryFileContent);
	}
}
