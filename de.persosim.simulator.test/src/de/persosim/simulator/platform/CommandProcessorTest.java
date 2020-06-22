package de.persosim.simulator.platform;

import static de.persosim.simulator.platform.TestProtocol.methodsWhereCalledInSequence;
import static de.persosim.simulator.platform.TestProtocol.wasMethodCalled;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.test.PersoSimTestCase;

public class CommandProcessorTest extends PersoSimTestCase {

	TestProtocol testProtocol;

	CommandProcessor commandProcessor;


	/**
	 * Instantiate and initialize the object under test
	 * {@link #commandProcessor} with the mocked personalization.
	 * @throws AccessDeniedException 
	 * 
	 */
	@Before
	public void setUp() throws AccessDeniedException {
		testProtocol = new TestProtocol();
		MasterFile mf = new MasterFile();
	    List<Protocol> protocols = new ArrayList<>();
	    protocols.add(testProtocol);
	    
		commandProcessor = new CommandProcessor(protocols, mf);
		commandProcessor.init();
	}
	
	/**
	 * Expected behavior for the {@link CommandProcessor} is that a
	 * {@link Protocol} that has not been active yet is reset before every APDU
	 * that it is requested to process.
	 */
	@Test
	public void testProcessProcessingData_ResetProtocol() {
		// provide simple APDU
		ProcessingData pData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x84, 0x00, 0x00, 0x08 };
		pData.updateCommandApdu(this, "test command APDU", CommandApduFactory.createCommandApdu(
				apduBytes));

		// call mut
		commandProcessor.processAscending(pData);

		// make sure reset has been called
		assertThat(testProtocol, wasMethodCalled("reset"));
	}

	/**
	 * It is expected that an active Protocol (e.g. a protocol on the stack,
	 * that means a Protocol that already processed an APDU but did not
	 * announce that it is finished) is not reset before the following APDU is
	 * transmitted for processing.
	 */
	@Test
	public void testProcessProcessingData_NoResetForActiveProtocol() {
		// transmit first APDU
		final ProcessingData pData1 = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x84, 0x00, 0x00, 0x08 };
		pData1.updateCommandApdu(this, "1st test APDU", CommandApduFactory.createCommandApdu(
				apduBytes));
		commandProcessor.processAscending(pData1);

		// transmit second APDU
		final ProcessingData pData2 = new ProcessingData();
		pData2.updateCommandApdu(this, "2nd test APDU", CommandApduFactory.createCommandApdu(
				apduBytes));
		commandProcessor.processAscending(pData2);
		
		// make sure needed methods have been called
		assertThat(testProtocol, methodsWhereCalledInSequence(true, "reset", "process", "process"));

	}

	/**
	 * It is expected that a formerly active Protocol (e.g. a protocol that already processed an APDU and announced that it is finished)
	 *  is not reset before the following APDU is transmitted for processing.
	 */
	@Test
	public void testProcessProcessingData_ResetFormerlyActiveProtocol() {
		// prepare TestProtocol
		testProtocol.setFinishProtocol(true);
		
		
		// transmit first APDU
		final ProcessingData pData1 = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x84, 0x00, 0x00, 0x08 };
		pData1.updateCommandApdu(this, "1st test APDU", CommandApduFactory.createCommandApdu(
				apduBytes));
		commandProcessor.processAscending(pData1);

		// transmit second APDU
		final ProcessingData pData2 = new ProcessingData();
		pData2.updateCommandApdu(this, "2nd test APDU", CommandApduFactory.createCommandApdu(
				apduBytes));
		commandProcessor.processAscending(pData2);
		
		// make sure needed methods have been called
		assertThat(testProtocol, methodsWhereCalledInSequence(true, "reset", "process", "reset", "process"));
	}

	/**
	 * It is expected that all active Protocols (e.g. a protocol on the stack,
	 * that means Protocols that already processed an APDU but did not announce
	 * that it is finished) is removed from the protocol stack during powerOn
	 * and thus reset before the following APDU is transmitted for processing.
	 */
	@Test
	public void testPowerOn_ResetForActiveProtocols() {
		
		// transmit first APDU
		final ProcessingData pData1 = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x84, 0x00, 0x00, 0x08 };
		pData1.updateCommandApdu(this, "1st test APDU", CommandApduFactory.createCommandApdu(
				apduBytes));
		commandProcessor.processAscending(pData1);
		
		//mut, repower the card
		commandProcessor.powerOn();

		// transmit second APDU
		final ProcessingData pData2 = new ProcessingData();
		pData2.updateCommandApdu(this, "2nd test APDU", CommandApduFactory.createCommandApdu(
				apduBytes));
		commandProcessor.processAscending(pData2);
		
		// make sure needed methods have been called
		assertThat(testProtocol, methodsWhereCalledInSequence(true, "reset", "process", "reset", "process"));
	}

}
