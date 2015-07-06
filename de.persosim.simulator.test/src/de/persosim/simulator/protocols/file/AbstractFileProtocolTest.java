package de.persosim.simulator.protocols.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;

import mockit.Deencapsulation;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.AbstractFile;
import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ObjectStore;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.secstatus.NullSecurityCondition;
import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.InfoSource;

/**
 * Unit tests for the file management protocol.
 * 
 * @author mboonk
 * 
 */
public class AbstractFileProtocolTest extends PersoSimTestCase {

	AbstractFile elementaryFile;
	byte[] elementaryFileContent;
	@Mocked
	CardStateAccessor mockedCardStateAccessor;
	DefaultFileProtocol fileProtocol;

	/**
	 * Create the test environment containing an elementary file and the mocked
	 * object store.
	 * @throws ReflectiveOperationException 
	 * @throws AccessDeniedException 
	 */
	@Before
	public void setUp() throws ReflectiveOperationException, AccessDeniedException {
		elementaryFileContent = new byte[] { 1, 2, 3, 4, 5, 6 };

		// create file to test
		LinkedList<SecCondition> unprotected = new LinkedList<>();
		unprotected.add(new NullSecurityCondition());
		elementaryFile = new ElementaryFile(new FileIdentifier(0x011A), new ShortFileIdentifier(1), elementaryFileContent, unprotected, unprotected, unprotected);
		elementaryFile.setSecStatus(new SecStatus());
		
		//create and init the object under test
		fileProtocol = new DefaultFileProtocol();
		fileProtocol.setCardStateAccessor(mockedCardStateAccessor);
		fileProtocol.init();
	}

	/**
	 * Select a file and check for the status word.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectFile() throws FileNotFoundException {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.selectFile(
						withInstanceOf(FileIdentifier.class),
						withInstanceOf(Scope.class));
				result = elementaryFile;
			}
		};

		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00A4020C02011A");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertTrue("Statusword is not 9000", processingData.getResponseApdu()
				.getStatusWord() == Iso7816.SW_9000_NO_ERROR);
	}

	/**
	 * Select a non existing file and check for the file not found status word.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSelectNonExistingFile() throws FileNotFoundException {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.selectFile(
						withInstanceOf(FileIdentifier.class),
						withInstanceOf(Scope.class));
				result = new FileNotFoundException();
			}
		};

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
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.selectMasterFile();
				result = new MasterFile();
			}
		};

		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 A4 00 0C 02 3F 00");
		processingData.updateCommandApdu(this, "select MF APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertEquals("wrong SW returned", Iso7816Lib.SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord());
	}

	/**
	 * Select MF through empty data field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectMf_emptyDataField() throws FileNotFoundException {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.selectMasterFile();
				result = new MasterFile();
			}
		};

		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 A4 00 0C");
		processingData.updateCommandApdu(this, "select MF APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		fileProtocol.process(processingData);

		// check results
		assertEquals("wrong SW returned", Iso7816Lib.SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord());
	}

	/**
	 * Try to read too many bytes of a file and check for the correct status
	 * word.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testReadBinaryTooLong() throws FileNotFoundException {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor
						.getCurrentFile();
				result = new Delegate<ObjectStore>() {
					@SuppressWarnings("unused") // JMockit
					public CardFile getCurrentFile() {
						return elementaryFile;
					}
				};
				mockedCardStateAccessor.selectFile();
			}
		};
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
		assertArrayEquals("file content not as expected", processingData
				.getResponseApdu().getData().toByteArray(),
				elementaryFileContent);
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
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor
						.getCurrentFile();
				result = new Delegate<ObjectStore>() {
					@SuppressWarnings("unused") // JMockit
					public CardFile getCurrentFile() {
						return elementaryFile;
					}
				};
				mockedCardStateAccessor.selectFile();
			}
		};

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
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor
						.getCurrentFile();
				result = elementaryFile;
				mockedCardStateAccessor.selectFile();
			}
		};

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
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor
						.getCurrentFile();
				result = elementaryFile;
				mockedCardStateAccessor.selectFile();
			}
		};

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
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor
						.getObject(withInstanceOf(CardObjectIdentifier.class), withInstanceOf(Scope.class));
				result = elementaryFile;
				mockedCardStateAccessor.selectFile();
			}
		};

		// read binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB0, (byte) 0x81, 0x01, 0x04 }; // read a file with SFI = 1 and offset 1
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
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor
						.getCurrentFile();
				result = elementaryFile;
				mockedCardStateAccessor.selectFile();
			}
		};

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
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(CardObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = elementaryFile;
				mockedCardStateAccessor.selectFile();
			}
		};

		// read binary APDU
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB1, 0x00, 0x01, 0x03,
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
		// prepare the mock

		new Expectations(elementaryFile) {
			{
				((ElementaryFile) elementaryFile)
						.update(anyInt, withInstanceLike(new byte[] {}));
			}
		};
		
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getCurrentFile();
				result = elementaryFile;
				mockedCardStateAccessor.selectFile();
			}
		};

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
		// prepare the mock

		new Expectations(elementaryFile) {
			{
				((ElementaryFile) elementaryFile).update(
						anyInt,
						withEqual(new byte [] {(byte) 0xFF, (byte) 0xFF}));
			}
		};

		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getCurrentFile();
				result = elementaryFile;
				mockedCardStateAccessor.selectFile();
			}
		};

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
		assertTrue("Statusword is not 9000", processingData.getResponseApdu()
				.getStatusWord() == Iso7816.SW_9000_NO_ERROR);
	}

	@Test
	public void testProtocolRemovalAfterSelect() throws FileNotFoundException {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor
						.getCurrentFile();
				result = elementaryFile;
				mockedCardStateAccessor.selectFile(
						withInstanceOf(CardObjectIdentifier.class),
						withInstanceOf(Scope.class));
			}
		};

		final ProcessingData mockedProcessingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xA4, 0x02, 0x0C, 0x02,
				0x01, 0x1B };
		mockedProcessingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		new Expectations(mockedProcessingData) {
			{
				mockedProcessingData.addUpdatePropagation(
						withInstanceOf(InfoSource.class),
						withInstanceOf(String.class),
						withInstanceOf(UpdatePropagation.class));
			}
		};

		// call mut
		fileProtocol.process(mockedProcessingData);
	}

	@Test
	public void testProtocolRemovalAfterReadBinary()
			throws FileNotFoundException {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor
						.getCurrentFile();
				result = elementaryFile;
				mockedCardStateAccessor.selectFile(
						withInstanceOf(CardObjectIdentifier.class),
						withInstanceOf(Scope.class));
			}
		};

		// read binary APDU
		final ProcessingData mockedProcessingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xB0, 0x00, 0x00, 0x04 };
		mockedProcessingData.updateCommandApdu(this, "read binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		new Expectations(mockedProcessingData) {
			{
				mockedProcessingData.addUpdatePropagation(
						withInstanceOf(InfoSource.class),
						withInstanceOf(String.class),
						withInstanceOf(UpdatePropagation.class));
			}
		};

		// call mut
		fileProtocol.process(mockedProcessingData);
	}

	@Test
	public void testProtocolRemovalAfterUpdateBinary()
			throws FileNotFoundException {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor
						.getCurrentFile();
				result = elementaryFile;
				mockedCardStateAccessor.selectFile(
						withInstanceOf(CardObjectIdentifier.class),
						withInstanceOf(Scope.class));
			}
		};

		// update binary APDU
		final ProcessingData mockedProcessingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0xD6, 0x00, 0x00, 0x06, 0,
				0, 0, 0, 0, 0 };
		mockedProcessingData.updateCommandApdu(this, "update binary APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		new Expectations(mockedProcessingData) {
			{
				mockedProcessingData.addUpdatePropagation(
						withInstanceOf(InfoSource.class),
						withInstanceOf(String.class),
						withInstanceOf(UpdatePropagation.class));
			}
		};

		// call mut
		fileProtocol.process(mockedProcessingData);
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
	//TODO missing tests getContents, with zero offset, with range larger than file, etc.
	
}
