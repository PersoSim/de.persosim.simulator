package de.persosim.simulator.protocols.pace;

import static org.junit.Assert.*;

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
import de.persosim.simulator.secstatus.PaceMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class PinManagementPaceBypassTest extends PersoSimTestCase {
	
	@Mocked
	MasterFile mockedMf;
	@Mocked
	CardStateAccessor mockedCardStateAccessor;
	PaceBypassProtocol paceProtocol;
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
		paceProtocol = new PaceBypassProtocol();
		paceProtocol.setCardStateAccessor(mockedCardStateAccessor);
		
		oidIdentifier0 = new OidIdentifier(Pace.OID_id_PACE_DH_GM_AES_CBC_CMAC_256);
		domainParameterSet0 = StandardizedDomainParameters.getDomainParameterSetById(0);
		domainParameters0 = new DomainParameterSetCardObject(domainParameterSet0, new DomainParameterSetIdentifier(0));
		domainParameters0.addOidIdentifier(oidIdentifier0);
		domainParameterSet0Collection = new ArrayList<CardObject>();
		domainParameterSet0Collection.add(domainParameters0);
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
		byte[] apduBytes = HexString.toByteArray("FF 86 00 00 06 83 01 03 92 01 FF");
		processingData.updateCommandApdu(this, "pseudo pace APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();
		assertEquals("Statusword is not correct", HexString.hexifyShort(Iso7816.SW_9000_NO_ERROR), HexString.hexifyShort(sw));
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
		byte[] apduBytes = HexString.toByteArray("FF 86 00 00 06 83 01 03 92 01 FF");
		processingData.updateCommandApdu(this, "pseudo pace APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();

		TlvValue data = processingData.getResponseApdu().getData();
		
		assertTrue("Encoding is valid", data.isValidBerEncoding());

		TlvDataObjectContainer container = new TlvDataObjectContainer(data);
		
		assertEquals("setAt status word is not correct", HexString.hexifyShort(0x6283), HexString.hexifyShort(Utils.getShortFromUnsignedByteArray(container.getTlvDataObject(TlvConstants.TAG_80).getValueField())));

		assertEquals("Statusword is not correct", HexString.hexifyShort(0x6984), HexString.hexifyShort(sw));
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
		byte[] apduBytes = HexString.toByteArray("FF 86 00 00 06 83 01 03 92 01 FF");
		processingData.updateCommandApdu(this, "pseudo pace APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();

		TlvValue data = processingData.getResponseApdu().getData();
		
		assertTrue("Encoding is valid", data.isValidBerEncoding());

		TlvDataObjectContainer container = new TlvDataObjectContainer(data);
		
		assertEquals("setAt status word is not correct", HexString.hexifyShort(0x63C2), HexString.hexifyShort(Utils.getShortFromUnsignedByteArray(container.getTlvDataObject(TlvConstants.TAG_80).getValueField())));
		
		assertEquals("Statusword is not correct", HexString.hexifyShort(0x9000), HexString.hexifyShort(sw));
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
		byte[] apduBytes = HexString.toByteArray("FF 86 00 00 06 83 01 03 92 01 FF");
		processingData.updateCommandApdu(this, "pseudo pace APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();

		TlvValue data = processingData.getResponseApdu().getData();
		
		assertTrue("Encoding is valid", data.isValidBerEncoding());

		TlvDataObjectContainer container = new TlvDataObjectContainer(data);
		
		assertEquals("setAt status word is not correct", HexString.hexifyShort(0x63C1), HexString.hexifyShort(Utils.getShortFromUnsignedByteArray(container.getTlvDataObject(TlvConstants.TAG_80).getValueField())));
		
		assertEquals("Statusword is not correct", HexString.hexifyShort(0x9000), HexString.hexifyShort(sw));
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
		byte[] apduBytes = HexString.toByteArray("FF 86 00 00 06 83 01 03 92 01 FF");
		processingData.updateCommandApdu(this, "pseudo pace APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();

		TlvValue data = processingData.getResponseApdu().getData();
		
		assertTrue("Encoding is valid", data.isValidBerEncoding());

		TlvDataObjectContainer container = new TlvDataObjectContainer(data);
		
		assertEquals("setAt status word is not correct", HexString.hexifyShort(0x63C1), HexString.hexifyShort(Utils.getShortFromUnsignedByteArray(container.getTlvDataObject(TlvConstants.TAG_80).getValueField())));
		
		assertEquals("Statusword is not correct", HexString.hexifyShort(0x9000), HexString.hexifyShort(sw));
	}
	
	/**
	 * Negative testcase: Perform PACE with Pin. Retry counter is 0, PIN activated
	 */
	@Test
	public void testSetAtPinRc0Act_NoPrevPwd(){
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
				result = pwdaoWithPinRc0Activated;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("FF 86 00 00 06 83 01 03 92 01 FF");
		processingData.updateCommandApdu(this, "pseudo pace APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		short sw = processingData.getResponseApdu().getStatusWord();

		TlvValue data = processingData.getResponseApdu().getData();
		
		assertTrue("Encoding is valid", data.isValidBerEncoding());

		TlvDataObjectContainer container = new TlvDataObjectContainer(data);
		
		assertEquals("setAt status word is not correct", HexString.hexifyShort(0x63C0), HexString.hexifyShort(Utils.getShortFromUnsignedByteArray(container.getTlvDataObject(TlvConstants.TAG_80).getValueField())));
		
		assertEquals("Statusword is not correct", HexString.hexifyShort(0x6983), HexString.hexifyShort(sw));
	}
	
}
