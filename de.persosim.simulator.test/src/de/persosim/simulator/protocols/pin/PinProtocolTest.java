package de.persosim.simulator.protocols.pin;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.LifeCycleChangeException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.RoleOid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.protocols.pace.Pace;
import de.persosim.simulator.protocols.ta.AuthenticatedAuxiliaryData;
import de.persosim.simulator.protocols.ta.Authorization;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.seccondition.OrSecCondition;
import de.persosim.simulator.seccondition.PaceWithPasswordRunningSecurityCondition;
import de.persosim.simulator.seccondition.PaceWithPasswordSecurityCondition;
import de.persosim.simulator.seccondition.TaSecurityCondition;
import de.persosim.simulator.secstatus.AuthorizationStore;
import de.persosim.simulator.secstatus.EffectiveAuthorizationMechanism;
import de.persosim.simulator.secstatus.PaceMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusMechanismUpdatePropagation;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.HexString;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;

public class PinProtocolTest extends PersoSimTestCase implements Tr03110 {
	@Mocked CardStateAccessor mockedCardStateAccessor;
	MasterFile mf;
	SecStatus secStatus;

	
	PasswordAuthObject authObject;
	PasswordAuthObjectWithRetryCounter pinObject;
	PinProtocol protocol;
	
	TerminalAuthenticationMechanism taMechanismIs;
	TerminalAuthenticationMechanism taMechanismAt;
	EffectiveAuthorizationMechanism authMechanismAtPinMgmt;
	
	@Before
	public void setUp() throws Exception {
		mf = new MasterFile();
		secStatus= new SecStatus();
		protocol = new PinProtocol();
		protocol.setCardStateAccessor(mockedCardStateAccessor);
		
		authObject = new PasswordAuthObject(new AuthObjectIdentifier(ID_PIN), "111111".getBytes(), "PIN");
		
		TaSecurityCondition pinManagementCondition = new TaSecurityCondition(TerminalType.AT,
				new RelativeAuthorization(CertificateRole.TERMINAL, new BitField(38).flipBit(5)));
		
		pinObject = new PasswordAuthObjectWithRetryCounter(new AuthObjectIdentifier(ID_PIN), "111111".getBytes(), "PIN",
				6, 6, 3, pinManagementCondition, new OrSecCondition(new PaceWithPasswordSecurityCondition("PIN"), new PaceWithPasswordSecurityCondition("PUK")),
				new PaceWithPasswordSecurityCondition("PUK"),
				new PaceWithPasswordRunningSecurityCondition("PIN"));
		mf.addChild(pinObject);
		pinObject.setSecStatus(secStatus);
		pinObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		taMechanismIs = new TerminalAuthenticationMechanism(new byte[]{1,2,3}, TerminalType.IS, new ArrayList<AuthenticatedAuxiliaryData>(), new byte[]{1,2,3}, new byte[]{1,2,3}, "test", null);
		taMechanismAt = new TerminalAuthenticationMechanism(new byte[]{1,2,3}, TerminalType.AT, new ArrayList<AuthenticatedAuxiliaryData>(), new byte[]{1,2,3}, new byte[]{1,2,3}, "test", null);
		
		HashMap<Oid, Authorization> authorizations = new HashMap<>();
		authorizations.put(RoleOid.id_AT, new RelativeAuthorization(new BitField(40).flipBit(5)));
		AuthorizationStore authStore = new AuthorizationStore(authorizations);
		authMechanismAtPinMgmt = new EffectiveAuthorizationMechanism(authStore);

	}
	
	/** 
	 * Positive test case. Send verify APDU to the simulator 
	 * and receives a 63Cx where x stands for the number of left retries. 
	 */
	@Test
	public void testProcessCommandVerifyPassword() throws Exception {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getMasterFile();
				result = mf;
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
		mf = new MasterFile(); //use empty MF
		new Expectations() {
			{
				mockedCardStateAccessor.getMasterFile();
				result = mf;
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
	public void testProcessCommandChangePassword() throws Exception {
		// prepare the mock
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, new PaceMechanism(Pace.id_PACE_ECDH_GM_AES_CBC_CMAC_128, pinObject, null, null, null, null)));
		new Expectations() {
			{
				mockedCardStateAccessor.getMasterFile();
				result = mf;
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
		assertArrayEquals("Password", "222222".getBytes(), pinObject.getPassword());
	}
	
	/**
	 * Negative test case. Send changePin APDU to the simulator
	 * and receives a 6984 because the object has no retry counter.
	 */
	@Test
	public void testProcessCommandChangePassword_NoRertyCnt() throws Exception {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getMasterFile();
				result = mf;
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
		assertEquals("Statusword", SW_6982_SECURITY_STATUS_NOT_SATISFIED, processingData
				.getResponseApdu().getStatusWord());
		assertArrayEquals("Password", "111111".getBytes(), authObject.getPassword());
	}
	
	/** 
	 * Negative test case. Send changePin APDU with no pin
	 *  to the simulator (tlvData is empty) and receives 6A80.
	 */
	@Test
	public void testProcessCommandChangePassword_EmptyPassword() throws Exception {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getMasterFile();
				result = mf;
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
		assertArrayEquals("Password", "111111".getBytes(), pinObject.getPassword());
	}
	
	/** 
	 * Positive test case. Send apdu to unblock PIN and receives 9000 
	 */
	@Test
	public void testProcessCommandUnblockPassword_PasswordBlocked() throws Exception {
		// prepare the mock
		Deencapsulation.setField(pinObject, "retryCounterCurrentValue", 0);
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, taMechanismAt));
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, authMechanismAtPinMgmt));
		new Expectations() {
			{
				mockedCardStateAccessor.getMasterFile();
				result = mf;
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
	public void testProcessCommandUnblockPassword_PasswordUnblocked() throws Exception {
		// prepare the mock
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, taMechanismAt));
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, authMechanismAtPinMgmt));
		new Expectations() {
			{
				mockedCardStateAccessor.getMasterFile();
				result = mf;
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
	public void testProcessCommandActivatePassword() throws Exception {
		// prepare the mock
		Deencapsulation.setField(pinObject, "lifeCycleState", Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED);
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, taMechanismAt));
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, authMechanismAtPinMgmt));
		new Expectations() {
			{
				mockedCardStateAccessor.getMasterFile();
				result = mf;
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
	 * @throws AccessDeniedException 
	 */
	@Test
	public void testProcessCommandDeactivatePassword() throws Exception {
		// prepare the mock
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, taMechanismAt));
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, authMechanismAtPinMgmt));
		new Expectations() {
			{
				mockedCardStateAccessor.getMasterFile();
				result = mf;
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
	public void testProcessCommandDeactivatePassword_SecStatusNotSatisfied() throws Exception {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getMasterFile();
				result = mf;
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
