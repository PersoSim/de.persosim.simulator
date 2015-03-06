package de.persosim.simulator.protocols.pin;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.PinObject;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class PinProtocolTest extends PersoSimTestCase implements Tr03110 {
	@Mocked
	CardStateAccessor mockedCardStateAccessor;
	@Mocked
	PasswordAuthObjectWithRetryCounter mokedAuthObject;
	@Mocked
	PasswordAuthObject mokedAuthObjectNoRetry;
	@Mocked
	PinObject mokedPinObject;
	@Mocked
	TerminalAuthenticationMechanism mockedTaMechanism;
	@Mocked
	TerminalType mockedTaType;
	@Mocked
	Collection<SecMechanism> mockedCurrentMechanisms;
	PinProtocol protocol;

	@Before
	public void setUp() throws UnsupportedEncodingException {
		protocol = new PinProtocol();
		protocol.setCardStateAccessor(mockedCardStateAccessor);
	}
	
	@Test
	/*
	 * Positive test case. Send verify APDU to the simulator and receives a 63Cx
	 * where x stands for the number of left retries.
	 */
	public void testProcessCommandVerifyPin() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = mokedPinObject; // new PinObject();
				
				mokedPinObject.getPasswordName();
				result = "PIN";
				
				mokedPinObject.getRetryCounterCurrentValue();
				result = 3;
				
				mokedPinObject.getRetryCounterCurrentValue();
				result = 3;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00200003");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 63C3", SW_63C3_COUNTER_IS_3, processingData
				.getResponseApdu().getStatusWord());
	}

	@Test
	/*
	 * Negative test case. Send verify apdu to the simulator but the PinObject
	 * is null
	 */
	public void testProcessCommandVerifyPin_PinObjectIsNull() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = null; // new PinObject();
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00200003");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 6984", SW_6984_REFERENCE_DATA_NOT_USABLE,
				processingData.getResponseApdu().getStatusWord());
	}

	@Test
	/*
	 * Positive test case. Send changePin APDU to the simulator and receives a
	 * 9000 if the PIN was successfully changed.
	 */
	public void testProcessCommandChangePin() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = mokedAuthObject; // new PasswordAuthObjectWithRetryCounter();
				
				mokedAuthObject.getPasswordName();
				result = "PIN";
				
				mokedAuthObject.getPassword();
				result = HexString.toByteArray("313233343536");
				
				mokedAuthObject.setPassword(withInstanceOf(byte[].class));
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("002C020306313432353336");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 9000", SW_9000_NO_ERROR, processingData
				.getResponseApdu().getStatusWord());
	}
	
	@Test
	public void testProcessCommandChangePin_NoRertyCnt() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = mokedAuthObjectNoRetry; // new PasswordAuthObject();
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("002C020306313432353336");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 6984", SW_6984_REFERENCE_DATA_NOT_USABLE, processingData
				.getResponseApdu().getStatusWord());
	}

	@Test
	/*
	 * Negative test case. Send changePin APDU with no pin to the simulator and
	 * receives 6A80 (wrong data).
	 */
	public void testProcessCommandChangePin_TlvDataIsNull() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = mokedAuthObject; // new
											// PasswordAuthObjectWithRetryCounter();
				mokedAuthObject.getPasswordName();
				result = "PIN";
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("002C0203");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 6A80", SW_6A80_WRONG_DATA, processingData
				.getResponseApdu().getStatusWord());
	}

	@Test
	public void testProcessCommandUnblockPin_PinBlocked() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = mokedPinObject; // new PinObject();
				
				mokedPinObject.getPasswordName();
				result = "PIN";
				
				mokedPinObject.getRetryCounterCurrentValue();
				result = 0;
				
				mokedPinObject.resetRetryCounterToDefault();
				
				mokedPinObject.getRetryCounterCurrentValue();
				result = 3;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("002C0303");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 9000", SW_9000_NO_ERROR, processingData
				.getResponseApdu().getStatusWord());
	}

	@Test
	/* Negative test case. */
	public void testProcessCommandUnblockPin_PinUnblocked() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = mokedPinObject; // new PinObject();
				
				mokedPinObject.getPasswordName();
				result = "PIN";
				
				mokedPinObject.getRetryCounterCurrentValue();
				result = 3;
				
				mokedPinObject.resetRetryCounterToDefault();
				
				mokedPinObject.getRetryCounterCurrentValue();
				result = 3;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("002C0303");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 9000", SW_9000_NO_ERROR, processingData
				.getResponseApdu().getStatusWord());
	}

	@Test
	public void testProcessCommandActivatePin() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = mokedPinObject; // new PinObject();
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00441003");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 9000", SW_9000_NO_ERROR, processingData
				.getResponseApdu().getStatusWord());
	}

	@Test
	public void testProcessCommandDeactivatePin() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = mokedPinObject; // new PinObject();
				
				mockedCardStateAccessor
						.getCurrentMechanisms(
								withInstanceOf(SecContext.class),
								withInstanceLike(new HashSet<Class<? extends SecMechanism>>()));
				result = new HashSet<>();
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00041003");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 9000", SW_9000_NO_ERROR, processingData
				.getResponseApdu().getStatusWord());
	}

	@Test
	public void testProcessCommandDeactivatePin_SecStatusNotSatisfied() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = mokedPinObject; // new PinObject();
				
				mockedCardStateAccessor
						.getCurrentMechanisms(
								withInstanceOf(SecContext.class),
								withInstanceLike(new HashSet<Class<? extends SecMechanism>>()));
				result = mockedCurrentMechanisms;
				
				mockedCurrentMechanisms.size();
				result = 1;
				
				mockedCurrentMechanisms.toArray();
				result = mockedTaMechanism;
				
				mockedTaMechanism.getTerminalType();
				result = mockedTaType;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00041003");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 6982",
				SW_6982_SECURITY_STATUS_NOT_SATISFIED, processingData
						.getResponseApdu().getStatusWord());
	}
}
