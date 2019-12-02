package de.persosim.simulator.protocols.pace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DomainParameterSetCardObject;
import de.persosim.simulator.cardobjects.DomainParameterSetIdentifier;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.exception.LifeCycleChangeException;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.ResponseData;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.seccondition.OrSecCondition;
import de.persosim.simulator.seccondition.PaceWithPasswordRunningSecurityCondition;
import de.persosim.simulator.seccondition.PaceWithPasswordSecurityCondition;
import de.persosim.simulator.seccondition.TaSecurityCondition;
import de.persosim.simulator.secstatus.PaceMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.HexString;

public class PinManagementTest extends PersoSimTestCase {
	
	MasterFile testMf;
	TestCardStateAccessor testCardState;
	
	DefaultPaceProtocol paceProtocol;
	Collection<SecMechanism> csmEmpty, csmWithPin;
	PasswordAuthObject pwdaoWithCan;
	PasswordAuthObjectWithRetryCounter pwdaoWithPinRc0Activated, pwdaoWithPinRc1Activated, pwdaoWithPinRc2Activated, pwdaoWithPinRc3Activated, pwdaoWithPinRc3Deactivated;
	DomainParameterSet domainParameterSet0;
	Collection<CardObject> domainParameterSet0Collection;
	DomainParameterSetCardObject domainParameters0;
	PaceOid oid0;
	OidIdentifier oidIdentifier0;
	PaceMechanism paceMechanismWithCan;
	PaceMechanism paceMechanismWithPin;
	
	/**
	 * Create the test environment.
	 * @throws LifeCycleChangeException 
	 * @throws ReflectiveOperationException 
	 */
	@Before
	public void setUp() throws Exception {
		AuthObjectIdentifier aoiCan = new AuthObjectIdentifier(new byte[]{(byte) 0x02});
		AuthObjectIdentifier aoiPin = new AuthObjectIdentifier(new byte[]{(byte) 0x03});
		
		pwdaoWithCan = new PasswordAuthObject(aoiCan, new byte[]{(byte) 0xFF});
		
		TaSecurityCondition pinManagementCondition = new TaSecurityCondition(TerminalType.AT,
				new RelativeAuthorization(CertificateRole.TERMINAL, new BitField(38).flipBit(5)));
		
		pwdaoWithPinRc0Activated = new PasswordAuthObjectWithRetryCounter(aoiPin, new byte[] { (byte) 0xFF }, "PIN", 0,
				16, 3, pinManagementCondition, new OrSecCondition(new PaceWithPasswordSecurityCondition("PIN"), new PaceWithPasswordSecurityCondition("PUK")),
				new PaceWithPasswordSecurityCondition("PUK"),
				new PaceWithPasswordRunningSecurityCondition("PIN"));
		pwdaoWithPinRc0Activated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		pwdaoWithPinRc0Activated.decrementRetryCounter();
		pwdaoWithPinRc0Activated.decrementRetryCounter();
		pwdaoWithPinRc0Activated.decrementRetryCounter();

		pwdaoWithPinRc1Activated = new PasswordAuthObjectWithRetryCounter(aoiPin, new byte[] { (byte) 0xFF }, "PIN", 0,
				16, 3, pinManagementCondition, new OrSecCondition(new PaceWithPasswordSecurityCondition("PIN"), new PaceWithPasswordSecurityCondition("PUK")),
				new PaceWithPasswordSecurityCondition("PUK"),
				new PaceWithPasswordRunningSecurityCondition("PIN"));
		pwdaoWithPinRc1Activated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		pwdaoWithPinRc1Activated.decrementRetryCounter();
		pwdaoWithPinRc1Activated.decrementRetryCounter();

		pwdaoWithPinRc2Activated = new PasswordAuthObjectWithRetryCounter(aoiPin, new byte[] { (byte) 0xFF }, "PIN", 0,
				16, 3, pinManagementCondition, new OrSecCondition(new PaceWithPasswordSecurityCondition("PIN"), new PaceWithPasswordSecurityCondition("PUK")),
				new PaceWithPasswordSecurityCondition("PUK"),
				new PaceWithPasswordRunningSecurityCondition("PIN"));
		pwdaoWithPinRc2Activated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		pwdaoWithPinRc2Activated.decrementRetryCounter();

		pwdaoWithPinRc3Activated = new PasswordAuthObjectWithRetryCounter(aoiPin, new byte[] { (byte) 0xFF }, "PIN", 0,
				16, 3, pinManagementCondition, new OrSecCondition(new PaceWithPasswordSecurityCondition("PIN"), new PaceWithPasswordSecurityCondition("PUK")),
				new PaceWithPasswordSecurityCondition("PUK"),
				new PaceWithPasswordRunningSecurityCondition("PIN"));
		pwdaoWithPinRc3Activated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);

		pwdaoWithPinRc3Deactivated = new PasswordAuthObjectWithRetryCounter(aoiPin, new byte[] { (byte) 0xFF }, "PIN",
				0, 16, 3, pinManagementCondition, new OrSecCondition(new PaceWithPasswordSecurityCondition("PIN"), new PaceWithPasswordSecurityCondition("PUK")),
				new PaceWithPasswordSecurityCondition("PUK"),
				new PaceWithPasswordRunningSecurityCondition("PIN"));
		pwdaoWithPinRc3Deactivated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		pwdaoWithPinRc3Deactivated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED);
		
		oid0 = Pace.id_PACE_ECDH_GM_AES_CBC_CMAC_128;
		oidIdentifier0 = new OidIdentifier(oid0);
		
		paceMechanismWithCan = new PaceMechanism(oid0, pwdaoWithCan, null, null, null, null);
		paceMechanismWithPin = new PaceMechanism(oid0, pwdaoWithPinRc3Activated, null, null, null, null);
		
		
		csmWithPin = new HashSet<SecMechanism>();
		csmWithPin.add(paceMechanismWithPin);
		
		csmEmpty = new HashSet<SecMechanism>();
		
		testMf = new TestMasterFile();
		testCardState = new TestCardStateAccessor(testMf);
		
		// create and init the object under test
		paceProtocol = new DefaultPaceProtocol();
		paceProtocol.setCardStateAccessor(testCardState);
		paceProtocol.init();
		
		domainParameters0 = new DomainParameterSetCardObject(domainParameterSet0, new DomainParameterSetIdentifier(0));
		domainParameters0.addOidIdentifier(oidIdentifier0);
		domainParameterSet0Collection = new ArrayList<CardObject>();
		domainParameterSet0Collection.add(domainParameters0);
	}
	
	/**
	 * Positive test case: check for preceding CAN, CAN preceding.
	 */
	@Test
	public void testIsPinTemporarilyResumedPrecedingCan() {
		// prepare the cardState
		testCardState.putSecMechanism(PaceMechanism.class, paceMechanismWithCan);
	
		assertTrue("PIN temporarily resumed", AbstractPaceProtocol.isPinTemporarilyResumed(testCardState));
	}
	
	/**
	 * Negative test case: check for preceding CAN, PIN preceding.
	 */
	@Test
	public void testIsPinTemporarilyResumedPrecedingPin() {
		// prepare the cardState
		testCardState.putSecMechanism(PaceMechanism.class, paceMechanismWithPin);
		
		assertFalse("PIN temporarily resumed", AbstractPaceProtocol.isPinTemporarilyResumed(testCardState));
	}
	
	/**
	 * Negative test case: check for preceding CAN, no password preceding.
	 */
	@Test
	public void testIsPinTemporarilyResumedNoPrecedingPwd() {
		assertFalse("PIN temporarily resumed", AbstractPaceProtocol.isPinTemporarilyResumed(testCardState));
	}
	
	/**
	 * Positive test case: perform PACE with PIN. PIN retry counter is 3 (default), PIN activated.
	 */
	@Test
	public void testSetAtPinRc3Act_NoPrevPwd() throws Exception {
		
		// prepare the MasterFile
        testMf.addChild(domainParameters0);
        testMf.addChild(pwdaoWithPinRc3Activated);
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 02 02 83 01 03");
		processingData.updateCommandApdu(this, "setAT APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();
		System.out.println("sw is: " + HexString.hexifyShort(sw));
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, sw);
	}
	
	/**
	 * Positive test case: perform PACE with PIN. PIN retry counter is 3 (default), PIN deactivated.
	 */
	@Test
	public void testSetAtPinRc3Deact_NoPrevPwd() throws Exception {
		
		// prepare the MasterFile
        testMf.addChild(domainParameters0);
        testMf.addChild(pwdaoWithPinRc3Deactivated);
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 02 02 83 01 03");
		processingData.updateCommandApdu(this, "setAT APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();
		System.out.println("sw is: " + HexString.hexifyShort(sw));
		assertEquals("Statusword is not 6283", Iso7816.SW_6283_SELECTED_FILE_DEACTIVATED, sw);
	}
	
	/**
	 * Positive test case: perform PACE with PIN. PIN retry counter is 2, PIN activated.
	 */
	@Test
	public void testSetAtPinRc2Act_NoPrevPwd() throws Exception{
		
		// prepare the MasterFile
        testMf.addChild(domainParameters0);
        testMf.addChild(pwdaoWithPinRc2Activated);
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 02 02 83 01 03");
		processingData.updateCommandApdu(this, "setAT APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();
		System.out.println("sw is: " + HexString.hexifyShort(sw));
		assertEquals("Statusword is not 63C2", (short) 0x63C2, sw);
	}
	
	/**
	 * Positive test case: perform PACE with PIN. PIN retry counter is 1, PIN activated.
	 */
	@Test
	public void testSetAtPinRc1Act_NoPrevPwd() throws Exception{
		// prepare the cardState
		testCardState.putSecMechanism(PaceMechanism.class, paceMechanismWithCan);
		
		// prepare the MasterFile
        testMf.addChild(domainParameters0);
        testMf.addChild(pwdaoWithPinRc1Activated);
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 02 02 83 01 03");
		processingData.updateCommandApdu(this, "setAT APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();
		System.out.println("sw is: " + HexString.hexifyShort(sw));
		System.out.println("state is: " + paceProtocol.getInnermostActiveState());
		assertEquals("Statusword is not 63C1", Iso7816.SW_63C1_COUNTER_IS_1, sw);
		assertEquals("State is not " + DefaultPaceProtocol.PACE_SET_AT_PROCESSED, DefaultPaceProtocol.PACE_SET_AT_PROCESSED, paceProtocol.getInnermostActiveState());
	}
	
	/**
	 * Positive test case: perform PACE with PIN. PIN retry counter is 1, PIN activated, previous CAN.
	 */
	@Test
	public void testSetAtPinRc1Act_PrevCan() throws Exception{
		// prepare the cardState
		testCardState.putSecMechanism(PaceMechanism.class, paceMechanismWithCan);
		
		// prepare the MasterFile
        testMf.addChild(domainParameters0);
        testMf.addChild(pwdaoWithPinRc1Activated);
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 02 02 83 01 03");
		processingData.updateCommandApdu(this, "setAT APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();
		System.out.println("sw is: " + HexString.hexifyShort(sw));
		System.out.println("state is: " + paceProtocol.getInnermostActiveState());
		assertEquals("Statusword is not 63C1", Iso7816.SW_63C1_COUNTER_IS_1, sw);
		assertEquals("State is not " + DefaultPaceProtocol.PACE_SET_AT_PROCESSED, DefaultPaceProtocol.PACE_SET_AT_PROCESSED, paceProtocol.getInnermostActiveState());
	}
	
	/**
	 * Positive test case: check Mutual Authenticate for correct response if PACE with PIN failed due to wrong PIN.
	 */
	@Test
	public void testGetMutualAuthenticatePinManagementResponsePaceFailed(){
		short sw = (short) 0x63C0;

		for(int i = 2; i > 0; i--) {
			ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceFailed(pwdaoWithPinRc3Activated);
			
			short expectedSw = (short) (sw | ((short) (i & (short) 0x000F)));
			short receivedSw = responseDataReceived.getStatusWord();
			
			assertEquals("Statusword is not " + HexString.hexifyShort(expectedSw), expectedSw, receivedSw);
		}
	}
	
	/**
	 * Positive test case: check Mutual Authenticate for correct response in case of PACE with PIN, PIN deactivated.
	 */
	@Test
	public void testGetMutualAuthenticatePinManagementResponsePaceSuccessful_PinDeactivated(){
		
		ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pwdaoWithPinRc3Deactivated, testCardState);
		
		short expectedSw = Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", expectedSw, receivedSw);
	}
	
	/**
	 * Positive test case: check for correct response in case of PACE with PIN, PIN active, retry counter is 1
	 */
	@Test
	public void testIsPasswordUsable_PinActivatedRc1(){
		
		ResponseData responseDataReceived = AbstractPaceProtocol.isPasswordUsable(pwdaoWithPinRc1Activated, testCardState);
		
		short expectedSw = Iso7816.SW_63C1_COUNTER_IS_1;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", HexString.hexifyShort(expectedSw), HexString.hexifyShort(receivedSw));
	}
	
	/**
	 * Positive test case: check for correct response in case of PACE with PIN, PIN active, retry counter is 2
	 */
	@Test
	public void testIsPasswordUsable_PinActivatedRc2(){
		
		ResponseData responseDataReceived = AbstractPaceProtocol.isPasswordUsable(pwdaoWithPinRc2Activated, testCardState);
		
		short expectedSw = 0x63C2;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", HexString.hexifyShort(expectedSw), HexString.hexifyShort(receivedSw));
	}
	
	/**
	 * Positive test case: check for correct response in case of PACE with PIN, PIN active, retry counter is 3
	 */
	@Test
	public void testIsPasswordUsable_PinActivatedRc3(){
		ResponseData responseDataReceived = AbstractPaceProtocol.isPasswordUsable(pwdaoWithPinRc3Activated, testCardState);
		
		assertEquals(null, responseDataReceived);
	}
	
	/**
	 * Positive test case: check for correct response in case of PACE with PIN, PIN inactive, retry counter is 3
	 */
	@Test
	public void testIsPasswordUsable_PinDeactivatedRc3(){
		ResponseData responseDataReceived = AbstractPaceProtocol.isPasswordUsable(pwdaoWithPinRc3Deactivated, testCardState);

		
		short expectedSw = SW_6283_SELECTED_FILE_DEACTIVATED;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", HexString.hexifyShort(expectedSw), HexString.hexifyShort(receivedSw));
	}
	
	/**
	 * Positive test case: check Mutual Authenticate for correct response in case of PACE with PIN, PIN activated, retry counter is 3.
	 */
	@Test
	public void testGetMutualAuthenticatePinManagementResponsePaceSuccessful_PinActivatedRc3(){
		ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pwdaoWithPinRc3Activated, testCardState);
		
		short expectedSw = Iso7816.SW_9000_NO_ERROR;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", expectedSw, receivedSw);
	}
	
	/**
	 * Positive test case: check Mutual Authenticate for correct response in case of PACE with PIN, PIN activated, retry counter is 2.
	 */
	@Test
	public void testGetMutualAuthenticatePinManagementResponsePaceSuccessful_PinActivatedRc2(){
		ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pwdaoWithPinRc2Activated, testCardState);
		
		short expectedSw = Iso7816.SW_9000_NO_ERROR;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", expectedSw, receivedSw);
	}
	
	/**
	 * Positive test case: check Mutual Authenticate for correct response in case of PACE with PIN, PIN activated, retry counter is 1, no previous password.
	 */
	@Test
	public void testGetMutualAuthenticatePinManagementResponsePaceSuccessful_PinActivatedRc1NoPrevPwd(){
		ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pwdaoWithPinRc1Activated, testCardState);
		
		short expectedSw = Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", expectedSw, receivedSw);
	}
	
	/**
	 * Positive test case: check Mutual Authenticate for correct response in case of PACE with PIN, PIN activated, retry counter is 1, previous password is CAN.
	 */
	@Test
	public void testGetMutualAuthenticatePinManagementResponsePaceSuccessful_PinActivatedRc1PrevCan(){
		// prepare the CardState
		testCardState.putSecMechanism(PaceMechanism.class, paceMechanismWithCan);
		
		ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pwdaoWithPinRc1Activated, testCardState);
		
		short expectedSw = Iso7816.SW_9000_NO_ERROR;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword is not " + HexString.hexifyShort(expectedSw), expectedSw, receivedSw);
	}
	

	/**
	 * Negative testcase: Perform PACE with Pin. Retry counter is 0, PIN activated
	 */
	@Test
	public void testSetAtPinRc0Act_NoPrevPwd() throws Exception {
		// prepare the MasterFile
        testMf.addChild(domainParameters0);
        testMf.addChild(pwdaoWithPinRc0Activated);
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 02 02 83 01 03");
		processingData.updateCommandApdu(this, "pseudo pace APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();
		assertEquals("Statusword is not correct", HexString.hexifyShort(0x63C0), HexString.hexifyShort(sw));
	}
	
}
