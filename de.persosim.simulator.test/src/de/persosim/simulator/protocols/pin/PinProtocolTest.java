package de.persosim.simulator.protocols.pin;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
import de.persosim.simulator.exception.LifeCycleChangeException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.protocols.ta.AuthenticatedAuxiliaryData;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.AuthorizationMechanism;
import de.persosim.simulator.secstatus.AuthorizationStore;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class PinProtocolTest extends PersoSimTestCase implements Tr03110 {
	@Mocked CardStateAccessor mockedCardStateAccessor;

	
	PasswordAuthObject authObject;
	PinObject pinObject;
	PasswordAuthObjectWithRetryCounter authObjectRetry;
	PinProtocol protocol;
	
	TerminalAuthenticationMechanism taMechanism;
	AuthorizationMechanism authMechanism;
	HashSet<SecMechanism> currentMechanisms;
	
	@Before
	public void setUp() throws LifeCycleChangeException {
		protocol = new PinProtocol();
		protocol.setCardStateAccessor(mockedCardStateAccessor);
		
		authObject = new PasswordAuthObject(new AuthObjectIdentifier(ID_PIN), "111111".getBytes(), "PIN");
		pinObject = new PinObject(new AuthObjectIdentifier(ID_PIN), "111111".getBytes(), 6, 6, 3);
		pinObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		authObjectRetry = new PasswordAuthObjectWithRetryCounter (new AuthObjectIdentifier(ID_PIN), "111111".getBytes(), "PIN", 6, 6, 3);
		authObjectRetry.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		currentMechanisms = new HashSet<>();
		taMechanism = new TerminalAuthenticationMechanism(new byte[]{1,2,3}, TerminalType.IS, new ArrayList<AuthenticatedAuxiliaryData>(), new byte[]{1,2,3}, new byte[]{1,2,3}, "test");
		AuthorizationStore authStore = new AuthorizationStore();
		authStore.updateAuthorization(TaOid.id_AT, new RelativeAuthorization());
		authMechanism = new AuthorizationMechanism(authStore);
		currentMechanisms.add(taMechanism);
		currentMechanisms.add(authMechanism);
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
				result = pinObject;
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
		assertEquals("Statusword", SW_63C3_COUNTER_IS_3, processingData
				.getResponseApdu().getStatusWord());
		assertEquals("RetryCounterValue", 3, pinObject.getRetryCounterCurrentValue());
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
				result = null;
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
		assertEquals("Statusword", SW_6984_REFERENCE_DATA_NOT_USABLE,
				processingData.getResponseApdu().getStatusWord());
	}
	
	/**
	 * Positive test case. Send changePin APDU to the simulator and receives a
	 * 9000 if the PIN was successfully changed. 
	 */
	@Test
	public void testProcessCommandChangePassword() {
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
		assertEquals("Statusword", SW_9000_NO_ERROR, processingData
				.getResponseApdu().getStatusWord());
		assertArrayEquals("Password", "222222".getBytes(), authObjectRetry.getPassword());
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
				result = authObject;
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
		assertEquals("Statusword", SW_6984_REFERENCE_DATA_NOT_USABLE, processingData
				.getResponseApdu().getStatusWord());
		assertArrayEquals("Password", "111111".getBytes(), authObject.getPassword());
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
				result = authObjectRetry;
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
		assertEquals("Statusword", SW_6A80_WRONG_DATA, processingData
				.getResponseApdu().getStatusWord());
		assertArrayEquals("Password", "111111".getBytes(), authObjectRetry.getPassword());
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
				result = pinObject;
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
		assertEquals("RetryCounterValue", 3, pinObject.getRetryCounterCurrentValue());
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
				result = pinObject;
				
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
		assertEquals("Statusword", SW_9000_NO_ERROR, processingData
				.getResponseApdu().getStatusWord());
		assertEquals("RetryCounterValue", 3, pinObject.getRetryCounterCurrentValue());
	}
	
	/**
	 * Positive test case. Send apdu to activate the PIN and receives
	 * a 9000.
	 * @throws LifeCycleChangeException 
	 */
	@Test
	public void testProcessCommandActivatePassword() throws LifeCycleChangeException {
		// prepare the mock
		pinObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED);
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = pinObject;
				
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
		assertEquals("Statusword", SW_9000_NO_ERROR, processingData
				.getResponseApdu().getStatusWord());
		
		assertEquals("Lifecycle", Iso7816LifeCycleState.OPERATIONAL_ACTIVATED, pinObject.getLifeCycleState());
	}
	
	/**
	 * Positive test case. Send apdu to deactivate the PIN an receives a 9000.
	 */
	@Test
	public void testProcessCommandDeactivatePassword() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = pinObject;
				
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
		assertEquals("Statusword", SW_9000_NO_ERROR, processingData
				.getResponseApdu().getStatusWord());
		assertEquals("Lifecycle", Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED, pinObject.getLifeCycleState());
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
				result = pinObject;
				
				mockedCardStateAccessor
						.getCurrentMechanisms(
								withInstanceOf(SecContext.class),
								withInstanceLike(new HashSet<Class<? extends SecMechanism>>()));
				result = currentMechanisms;
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
		assertEquals("Statusword",
				SW_6982_SECURITY_STATUS_NOT_SATISFIED, processingData
						.getResponseApdu().getStatusWord());
	}
}
