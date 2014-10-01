package de.persosim.simulator.protocols.pace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DomainParameterSetCardObject;
import de.persosim.simulator.cardobjects.DomainParameterSetIdentifier;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.MasterFileIdentifier;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.PinObject;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.ResponseData;
import de.persosim.simulator.secstatus.PaceMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class PinManagementTest extends PersoSimTestCase {
	
	@Mocked
	MasterFile mockedMf;
	@Mocked
	CardStateAccessor mockedCardStateAccessor;
	DefaultPaceProtocol paceProtocol;
	Collection<SecMechanism> csmEmpty, csmWithCan, csmWithPin;
	PasswordAuthObject pwdaoWithCan;
	PasswordAuthObjectWithRetryCounter pwdaoWithPinRc0Activated, pwdaoWithPinRc1Activated, pwdaoWithPinRc2Activated, pwdaoWithPinRc3Activated, pwdaoWithPinRc3Deactivated;
	DomainParameterSet domainParameterSet0;
	Collection<CardObject> domainParameterSet0Collection;
	DomainParameterSetCardObject domainParameters0;
	OidIdentifier oidIdentifier0;
	
	/**
	 * Create the test environment.
	 * @throws ReflectiveOperationException 
	 */
	@Before
	public void setUp() {
		AuthObjectIdentifier aoiCan = new AuthObjectIdentifier(new byte[]{(byte) 0x02});
		AuthObjectIdentifier aoiPin = new AuthObjectIdentifier(new byte[]{(byte) 0x03});
		
		pwdaoWithCan = new PasswordAuthObject(aoiCan, new byte[]{(byte) 0xFF});
		
		pwdaoWithPinRc0Activated = new PinObject(aoiPin, new byte[]{(byte) 0xFF}, 0, 16, 3);
		pwdaoWithPinRc0Activated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		pwdaoWithPinRc0Activated.decrementRetryCounter();
		pwdaoWithPinRc0Activated.decrementRetryCounter();
		pwdaoWithPinRc0Activated.decrementRetryCounter();
		
		pwdaoWithPinRc1Activated = new PinObject(aoiPin, new byte[]{(byte) 0xFF}, 0, 16, 3);
		pwdaoWithPinRc1Activated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		pwdaoWithPinRc1Activated.decrementRetryCounter();
		pwdaoWithPinRc1Activated.decrementRetryCounter();
		
		pwdaoWithPinRc2Activated = new PinObject(aoiPin, new byte[]{(byte) 0xFF}, 0, 16, 3);
		pwdaoWithPinRc2Activated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		pwdaoWithPinRc2Activated.decrementRetryCounter();
		
		pwdaoWithPinRc3Activated = new PinObject(aoiPin, new byte[]{(byte) 0xFF}, 0, 16, 3);
		pwdaoWithPinRc3Activated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		pwdaoWithPinRc3Deactivated = new PinObject(aoiPin, new byte[]{(byte) 0xFF}, 0, 16, 3);
		pwdaoWithPinRc3Deactivated.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED);
		
		PaceMechanism paceMechanismWithCan = new PaceMechanism(pwdaoWithCan, null, null);
		PaceMechanism paceMechanismWithPin = new PaceMechanism(pwdaoWithPinRc3Activated, null, null);
		
		csmWithCan = new HashSet<SecMechanism>();
		csmWithCan.add(paceMechanismWithCan);
		
		csmWithPin = new HashSet<SecMechanism>();
		csmWithPin.add(paceMechanismWithPin);
		
		csmEmpty = new HashSet<SecMechanism>();
		
		// create and init the object under test
		paceProtocol = new DefaultPaceProtocol();
		paceProtocol.setCardStateAccessor(mockedCardStateAccessor);
		paceProtocol.init();
		
		oidIdentifier0 = new OidIdentifier(Pace.OID_id_PACE_DH_GM_AES_CBC_CMAC_256);
		domainParameterSet0 = StandardizedDomainParameters.getDomainParameterSetById(0);
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
	// prepare the mock
	new NonStrictExpectations() {
		{
			mockedCardStateAccessor.getCurrentMechanisms(
					withInstanceOf(SecContext.class),
					null);
			result = csmWithCan;
		}
	};
		assertTrue("PIN temporarily resumed", AbstractPaceProtocol.isPinTemporarilyResumed(mockedCardStateAccessor));
	}
	
	/**
	 * Negative test case: check for preceding CAN, PIN preceding.
	 */
	@Test
	public void testIsPinTemporarilyResumedPrecedingPin() {
	// prepare the mock
	new NonStrictExpectations() {
		{
			mockedCardStateAccessor.getCurrentMechanisms(
					withInstanceOf(SecContext.class),
					null);
			result = csmWithPin;
		}
	};
		assertFalse("PIN temporarily resumed", AbstractPaceProtocol.isPinTemporarilyResumed(mockedCardStateAccessor));
	}
	
	/**
	 * Negative test case: check for preceding CAN, no password preceding.
	 */
	@Test
	public void testIsPinTemporarilyResumedNoPrecedingPwd() {
	// prepare the mock
	new NonStrictExpectations() {
		{
			mockedCardStateAccessor.getCurrentMechanisms(
					withInstanceOf(SecContext.class),
					null);
			result = csmEmpty;
		}
	};
		assertFalse("PIN temporarily resumed", AbstractPaceProtocol.isPinTemporarilyResumed(mockedCardStateAccessor));
	}
	
	//ok
	/**
	 * Positive test case: perform PACE with PIN. PIN retry counter is 3 (default), PIN activated.
	 */
	@Test
	public void testSetAtPinRc3Act_NoPrevPwd(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getCurrentMechanisms(
						withInstanceOf(SecContext.class),
						null);
				
				// previously used password
				result = csmEmpty;
				
				mockedCardStateAccessor.getObject(
						withInstanceOf(MasterFileIdentifier.class),
						withInstanceOf(Scope.class));
				result = mockedMf;

				mockedMf.findChildren(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(OidIdentifier.class));
				result = domainParameters0;

				mockedCardStateAccessor.getObject(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(Scope.class));
				result = domainParameters0;
				
				mockedCardStateAccessor.getObject(withInstanceOf(AuthObjectIdentifier.class),null);
				
				// currently used password
				result = pwdaoWithPinRc3Activated;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 01 04 83 01 03");
		processingData.updateCommandApdu(this, "setAT APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();
		System.out.println("sw is: " + HexString.hexifyShort(sw));
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, sw);
	}
	
	//ok
	/**
	 * Positive test case: perform PACE with PIN. PIN retry counter is 3 (default), PIN deactivated.
	 */
	@Test
	public void testSetAtPinRc3Deact_NoPrevPwd(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getCurrentMechanisms(
						withInstanceOf(SecContext.class),
						null);
				// previously used password
				result = csmEmpty;
				
				mockedCardStateAccessor.getObject(
						withInstanceOf(MasterFileIdentifier.class),
						withInstanceOf(Scope.class));
				result = mockedMf;

				mockedMf.findChildren(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(OidIdentifier.class));
				result = domainParameters0;

				mockedCardStateAccessor.getObject(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(Scope.class));
				result = domainParameters0;

				mockedCardStateAccessor.getObject(withInstanceOf(AuthObjectIdentifier.class),null);
				// currently used password
				result = pwdaoWithPinRc3Deactivated;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 01 04 83 01 03");
		processingData.updateCommandApdu(this, "setAT APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();
		System.out.println("sw is: " + HexString.hexifyShort(sw));
		assertEquals("Statusword is not 6283", Iso7816.SW_6283_SELECTED_FILE_DEACTIVATED, sw);
	}
	
	//ok
	/**
	 * Positive test case: perform PACE with PIN. PIN retry counter is 2, PIN activated.
	 */
	@Test
	public void testSetAtPinRc2Act_NoPrevPwd(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getCurrentMechanisms(
						withInstanceOf(SecContext.class),
						null);
				
				// previously used password
				result = csmEmpty;
				
				mockedCardStateAccessor.getObject(
						withInstanceOf(MasterFileIdentifier.class),
						withInstanceOf(Scope.class));
				result = mockedMf;

				mockedMf.findChildren(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(OidIdentifier.class));
				result = domainParameters0;

				mockedCardStateAccessor.getObject(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(Scope.class));
				result = domainParameters0;
				
				mockedCardStateAccessor.getObject(withInstanceOf(AuthObjectIdentifier.class),null);
				
				// currently used password
				result = pwdaoWithPinRc2Activated;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 01 04 83 01 03");
		processingData.updateCommandApdu(this, "setAT APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();
		System.out.println("sw is: " + HexString.hexifyShort(sw));
		assertEquals("Statusword is not 63C2", (short) 0x63C2, sw);
	}
	
	//ok
	/**
	 * Positive test case: perform PACE with PIN. PIN retry counter is 1, PIN activated.
	 */
	@Test
	public void testSetAtPinRc1Act_NoPrevPwd(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getCurrentMechanisms(
						withInstanceOf(SecContext.class),
						null);
				
				// previously used password
				result = csmWithCan;

				mockedCardStateAccessor.getObject(
						withInstanceOf(MasterFileIdentifier.class),
						withInstanceOf(Scope.class));
				result = mockedMf;

				mockedMf.findChildren(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(OidIdentifier.class));
				result = domainParameters0;

				mockedCardStateAccessor.getObject(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(Scope.class));
				result = domainParameters0;

				mockedCardStateAccessor.getObject(withInstanceOf(AuthObjectIdentifier.class), withInstanceOf(Scope.class));
				
				// currently used password
				result = pwdaoWithPinRc1Activated;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 01 04 83 01 03");
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
	public void testSetAtPinRc1Act_PrevCan(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getCurrentMechanisms(
						withInstanceOf(SecContext.class),
						null);
				
				// previously used password
				result = csmWithCan;
				
				mockedCardStateAccessor.getObject(
						withInstanceOf(MasterFileIdentifier.class),
						withInstanceOf(Scope.class));
				result = mockedMf;

				mockedMf.findChildren(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(OidIdentifier.class));
				result = domainParameters0;

				mockedCardStateAccessor.getObject(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(Scope.class));
				result = domainParameters0;
				
				mockedCardStateAccessor.getObject(withInstanceOf(AuthObjectIdentifier.class),null);
				
				// currently used password
				result = pwdaoWithPinRc1Activated;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 01 04 83 01 03");
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
		
		ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pwdaoWithPinRc3Deactivated, mockedCardStateAccessor);
		
		short expectedSw = Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", expectedSw, receivedSw);
	}
	
	/**
	 * Positive test case: check Mutual Authenticate for correct response in case of PACE with PIN, PIN activated, retry counter is 3.
	 */
	@Test
	public void testGetMutualAuthenticatePinManagementResponsePaceSuccessful_PinActivatedRc3(){
		ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pwdaoWithPinRc3Activated, mockedCardStateAccessor);
		
		short expectedSw = Iso7816.SW_9000_NO_ERROR;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", expectedSw, receivedSw);
	}
	
	/**
	 * Positive test case: check Mutual Authenticate for correct response in case of PACE with PIN, PIN activated, retry counter is 2.
	 */
	@Test
	public void testGetMutualAuthenticatePinManagementResponsePaceSuccessful_PinActivatedRc2(){
		ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pwdaoWithPinRc2Activated, mockedCardStateAccessor);
		
		short expectedSw = Iso7816.SW_9000_NO_ERROR;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", expectedSw, receivedSw);
	}
	
	/**
	 * Positive test case: check Mutual Authenticate for correct response in case of PACE with PIN, PIN activated, retry counter is 1, no previous password.
	 */
	@Test
	public void testGetMutualAuthenticatePinManagementResponsePaceSuccessful_PinActivatedRc1NoPrevPwd(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getCurrentMechanisms(
						withInstanceOf(SecContext.class),
						null);

				// previously used password
				result = csmEmpty;
			}
		};
		
		ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pwdaoWithPinRc1Activated, mockedCardStateAccessor);
		
		short expectedSw = Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword mismatch", expectedSw, receivedSw);
	}
	
	/**
	 * Positive test case: check Mutual Authenticate for correct response in case of PACE with PIN, PIN activated, retry counter is 1, previous password is CAN.
	 */
	@Test
	public void testGetMutualAuthenticatePinManagementResponsePaceSuccessful_PinActivatedRc1PrevCan(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getCurrentMechanisms(
						withInstanceOf(SecContext.class),
						null);

				// previously used password
				result = csmWithCan;
			}
		};
		
		ResponseData responseDataReceived = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pwdaoWithPinRc1Activated, mockedCardStateAccessor);
		
		short expectedSw = Iso7816.SW_9000_NO_ERROR;
		short receivedSw = responseDataReceived.getStatusWord();
		assertEquals("Statusword is not " + HexString.hexifyShort(expectedSw), expectedSw, receivedSw);
	}
	
}
