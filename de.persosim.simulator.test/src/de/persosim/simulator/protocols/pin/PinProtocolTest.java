package de.persosim.simulator.protocols.pin;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
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
	@Mocked CardStateAccessor mockedCardStateAccessor;
	@Mocked TerminalAuthenticationMechanism mockedTaMechanism;
	@Mocked TerminalType mockedTaType;
	@Mocked Collection<SecMechanism> mockedCurrentMechanisms;
	
	//FIXME JGE you should initialize all testdata in @Before or the specific testcases to ensure that it is clean for each and every test.
	PasswordAuthObject authObject = new PasswordAuthObject(new AuthObjectIdentifier(ID_PIN), "111111".getBytes(), "PIN");
	PinObject pinObject = new PinObject(new AuthObjectIdentifier(ID_PIN), "111111".getBytes(), 6, 6, 3);
	PasswordAuthObjectWithRetryCounter authObjectRetry = new PasswordAuthObjectWithRetryCounter (new AuthObjectIdentifier(ID_PIN), "111111".getBytes(), "PIN", 6, 6, 3);
	PinProtocol protocol;
	
	@Before
	public void setUp() {
		protocol = new PinProtocol();
		protocol.setCardStateAccessor(mockedCardStateAccessor);
	}
	
	/** 
	 * Positive test case. Send verify APDU to the simulator 
	 * and receives a 63Cx where x stands for the number of left retries. 
	 */
	@Test
	public void testProcessCommandVerifyPassword() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = pinObject; // new PinObject(); FIXME JGE why commented code here?
				
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
		assertEquals(3, pinObject.getRetryCounterCurrentValue());
	}
	
	/**
	 * Negative test case. Send verify apdu to the simulator but the PinObject
	 * is null
	 */
	@Test
	public void testProcessCommandVerifyPassword_PasswordObjectIsNull() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = null; // new PinObject(); FIXME JGE why commented code here?
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
	
	/**
	 * Positive test case. Send changePin APDU to the simulator and receives a
	 * 9000 if the PIN was successfully changed. 
	 */
	@Test
	public void testProcessCommandChangePassword() {
		authObjectRetry.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED); //FIXME JGE I guess this line will become redundant after fixing issues above ;-)
		
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = authObjectRetry;
				
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("002C020306323232323232");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 9000", SW_9000_NO_ERROR, processingData
				.getResponseApdu().getStatusWord());
		assertArrayEquals("222222".getBytes(), authObjectRetry.getPassword());
	}
	
	/**
	 * Negative test case. Send changePin APDU to the simulator
	 * and receives a 6984 because the object has no retry counter.
	 */
	@Test
	public void testProcessCommandChangePassword_NoRertyCnt() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = authObject; // new PasswordAuthObject(); FIXME JGE why commented code here?
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
		assertArrayEquals("111111".getBytes(), authObject.getPassword());
	}
	
	/** 
	 * Negative test case. Send changePin APDU with no pin
	 *  to the simulator (tlvData is empty) and receives 6A80.
	 */
	@Test
	public void testProcessCommandChangePassword_EmptyPassword() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = authObjectRetry; // new PasswordAuthObjectWithRetryCounter(); FIXME JGE why commented code here?
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
		assertArrayEquals("111111".getBytes(), authObjectRetry.getPassword());
		
	}
	
	/** 
	 * Positive test case. Send apdu to unblock PIN and receives 9000 
	 */
	@Test
	public void testProcessCommandUnblockPassword_PasswordBlocked() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = pinObject; // new PinObject(); FIXME JGE why commented code here?
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
		assertEquals(3, pinObject.getRetryCounterCurrentValue());
	}
	
	/**
	 * Negative test case. Send apdu to unblock PIN 
	 * but the PIN is already unblocked (retry counter is 3). 
	 */
	@Test
	public void testProcessCommandUnblockPassword_PasswordUnblocked() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = pinObject; // new PinObject(); FIXME JGE why commented code here?
				
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
		assertEquals(3, pinObject.getRetryCounterCurrentValue());
	}
	
	/**
	 * Positive test case. Send apdu to activate the PIN and receives
	 * a 9000.
	 */
	//FIXME JGE ensure that the pinObject is in deactivated sate before the command is executed
	@Test
	public void testProcessCommandActivatePassword() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = pinObject; // new PinObject(); FIXME JGE why commented code here?
				
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
		
		assertEquals(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED, pinObject.getLifeCycleState());
	}
	
	/**
	 * Positive test case. Send apdu to deactivate the PIN an receives a 9000.
	 */
	//FIXME JGE ensure that the pinObject is in deactivated sate before the command is executed
	@Test
	public void testProcessCommandDeactivatePassword() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = pinObject; // new PinObject(); FIXME JGE why commented code here?
				
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
		assertEquals(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED, pinObject.getLifeCycleState());
	}
	
	/**
	 * Negative test case. Send apdu to deactivate the Pin but Pin
	 * management rights from TA are required to perform the deactivate.
	 */
	@Test
	public void testProcessCommandDeactivatePassword_SecStatusNotSatisfied() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = pinObject; // new PinObject(); FIXME JGE why commented code here?
				
				mockedCardStateAccessor
						.getCurrentMechanisms(
								withInstanceOf(SecContext.class),
								withInstanceLike(new HashSet<Class<? extends SecMechanism>>()));
				result = mockedCurrentMechanisms; //FIXME JGE isn't it easier to return a full object here instead of mocking all methods?
				
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
	
	//FIXME JGE some assert messages are missleading if the testcase fails
	//FIXME JGE add message to distinguish the two asserts within this method
}
