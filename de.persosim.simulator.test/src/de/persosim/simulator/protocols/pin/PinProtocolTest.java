package de.persosim.simulator.protocols.pin;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.PinObject;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class PinProtocolTest extends PersoSimTestCase implements Tr03110{
	
	@Mocked CardStateAccessor mockedCardStateAccessor;
	@Mocked PasswordAuthObjectWithRetryCounter mokedAuthObject;
	@Mocked PinObject mokedPinObject;
	PinProtocol protocol;
	
	@Before
	public void setUp() throws UnsupportedEncodingException{
		
		protocol = new PinProtocol();
		protocol.setCardStateAccessor(mockedCardStateAccessor);
	}
	
	@Test
	public void testProcessCommandVerifyPin() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class), 
						withInstanceOf(Scope.class));
				result = mokedPinObject; //new PinObject();

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
		assertEquals("Statusword is 63C3", SW_63C3_COUNTER_IS_3, processingData.getResponseApdu().getStatusWord()); 
	}
	
	@Test
	public void testProcessCommandChangePin() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class), 
						withInstanceOf(Scope.class));
				result = mokedAuthObject; //new PasswordAuthObjectWithRetryCounter();
				
				mokedAuthObject.getPasswordName();
				result = "PIN";
				
				mokedAuthObject.getPassword();
				result = HexString.toByteArray("313233343536");
				
				mokedAuthObject.setPassword(
						withInstanceOf(byte[].class));
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
		assertEquals("Statusword is 9000", SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord()); 
	}
	
	@Test
	public void testProcessCommandUnblockPin() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class), 
						withInstanceOf(Scope.class));
				result = mokedPinObject; //new PinObject();
				
				mokedPinObject.getRetryCounterCurrentValue();
				result = 0;
				
				mokedPinObject.resetRetryCounterToDefault();
				
				mokedPinObject.getRetryCounterCurrentValue();
				result = 3;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("032C0303");
		processingData.updateCommandApdu(this, "select file APDU",
				CommandApduFactory.createCommandApdu(apduBytes));
		
		// call mut
		protocol.process(processingData);
		
		// check results
		assertEquals("Statusword is 9000", SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord()); 
	}
	
	@Test
	public void testProcessCommandActivatePin() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class), 
						withInstanceOf(Scope.class));
				result = mokedPinObject; //new PinObject();
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
		assertEquals("Statusword is 9000", SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord()); 
	}
	
	@Test
	public void testProcessCommandDeactivatePin() {
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(AuthObjectIdentifier.class), 
						withInstanceOf(Scope.class));
				result = mokedPinObject; //new PinObject();
				
				mockedCardStateAccessor.getCurrentMechanisms(
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
		assertEquals("Statusword is 9000", SW_9000_NO_ERROR, processingData.getResponseApdu().getStatusWord());
	}
}
