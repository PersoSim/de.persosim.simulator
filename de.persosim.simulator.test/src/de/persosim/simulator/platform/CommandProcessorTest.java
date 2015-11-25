package de.persosim.simulator.platform;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.AbstractProtocolStateMachine;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.TlvValuePlain;
import de.persosim.simulator.utils.InfoSource;
import mockit.Delegate;
import mockit.Invocation;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import mockit.VerificationsInOrder;

public class CommandProcessorTest extends PersoSimTestCase {

	private static final int LAYER_ID = 0;
	
	@Mocked Protocol mockedProtocol;

	CommandProcessor commandProcessor;

	InfoSource source = new InfoSource() {
		
		@Override
		public String getIDString() {
			return "TestSource";
		}
	};

	/**
	 * Instantiate and initialize the object under test
	 * {@link #commandProcessor} with the mocked personalization.
	 * @throws AccessDeniedException 
	 * 
	 */
	@Before
	public void setUp() throws AccessDeniedException {
		MasterFile mf = new MasterFile();
	    List<Protocol> protocols = new ArrayList<>();
	    protocols.add(mockedProtocol);
	    
		commandProcessor = new CommandProcessor(LAYER_ID, protocols, mf);
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
		new Verifications() {
			{
				mockedProtocol.reset();
			}
		};
	}

	/**
	 * It is expected that an active Protocol (e.g. a protocol on the stack,
	 * that means a Protocol that already processed an APDU but did not
	 * announce that it is finished) is not reset before the following APDU is
	 * transmitted for processing.
	 */
	@Test
	public void testProcessProcessingData_NoResetForActiveProtocol() {
		// prepare the mocked protocol
		new NonStrictExpectations() {{
				mockedProtocol.process(withInstanceOf(ProcessingData.class));
				result = new Delegate<AbstractProtocolStateMachine>() {

					@SuppressWarnings("unused") // JMockit
					public void process(Invocation inv,	ProcessingData processingData) {
						TlvValuePlain responseData = new TlvValuePlain(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 });
						ResponseApdu resp = new ResponseApdu(responseData, Iso7816.SW_9000_NO_ERROR);
						processingData.updateResponseAPDU(source, "GetNonce processed successfully", resp);
					}
				};
				
				mockedProtocol.getProtocolName();
				result = "MockedProtocol";
		}};
		
		
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
		
		// make sure reset has been called only once (implicitly known to be before the first APDU)
		new Verifications() {{
				mockedProtocol.reset(); times = 1;
				mockedProtocol.process(pData1);
				mockedProtocol.process(pData2);
		}};
	}

	/**
	 * It is expected that a formerly active Protocol (e.g. a protocol that already processed an APDU and announced that it is finished)
	 *  is not reset before the following APDU is transmitted for processing.
	 */
	@Test
	public void testProcessProcessingData_ResetFormerlyActiveProtocol() {
		// prepare the mocked protocol
		new NonStrictExpectations() {{
				mockedProtocol.process(withInstanceOf(ProcessingData.class));
				result = new Delegate<AbstractProtocolStateMachine>() {

					@SuppressWarnings("unused") // JMockit
					public void process(Invocation inv,	ProcessingData processingData) {
						TlvValuePlain responseData = new TlvValuePlain(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 });
						ResponseApdu resp = new ResponseApdu(responseData, Iso7816.SW_9000_NO_ERROR);
						processingData.updateResponseAPDU(source, "GetNonce processed successfully", resp);

						processingData.addUpdatePropagation(source, "mocked protocol completed", new ProtocolUpdate(true));
					}
				};
				
				mockedProtocol.getProtocolName();
				result = "MockedProtocol";
		}};
		
		
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
		
		// make sure reset has been called twice (once before every APDU)
		new VerificationsInOrder() {{
				mockedProtocol.reset();
				mockedProtocol.process(pData1);
				mockedProtocol.reset();
				mockedProtocol.process(pData2);
		}};
	}

	/**
	 * It is expected that all active Protocols (e.g. a protocol on the stack,
	 * that means Protocols that already processed an APDU but did not announce
	 * that it is finished) is removed from the protocol stack during powerOn
	 * and thus reset before the following APDU is transmitted for processing.
	 */
	@Test
	public void testPowerOn_ResetForActiveProtocols() {
		// prepare the mocked protocol
		new NonStrictExpectations() {{
				mockedProtocol.process(withInstanceOf(ProcessingData.class));
				result = new Delegate<AbstractProtocolStateMachine>() {

					@SuppressWarnings("unused") // JMockit
					public void process(Invocation inv,	ProcessingData processingData) {
						TlvValuePlain responseData = new TlvValuePlain(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 });
						ResponseApdu resp = new ResponseApdu(responseData, Iso7816.SW_9000_NO_ERROR);
						processingData.updateResponseAPDU(source, "GetNonce processed successfully", resp);
					}
				};
				
				mockedProtocol.getProtocolName();
				result = "MockedProtocol";
		}};
		
		
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
		
		// make sure reset has been called once (implicitly known to be before the first APDU)
		new VerificationsInOrder() {{
				mockedProtocol.reset();
				mockedProtocol.process(pData1);
				mockedProtocol.reset();
				mockedProtocol.process(pData2);
		}};
	}

}
