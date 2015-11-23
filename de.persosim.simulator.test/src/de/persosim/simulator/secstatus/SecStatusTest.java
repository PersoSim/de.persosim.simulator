package de.persosim.simulator.secstatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.ProtocolMechanism;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.protocols.file.FileProtocol;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.securemessaging.SmDataProvider;
import de.persosim.simulator.securemessaging.SmDataProviderGenerator;
import de.persosim.simulator.test.PersoSimTestCase;
import mockit.Mocked;

public class SecStatusTest extends PersoSimTestCase{
	
	SecStatus securityStatus;
	@Mocked SecMechanism mechanism;
	
	@Before
	public void setUp(){
		securityStatus = new SecStatus();
		
	}
	
	//TODO define tests for SecStatus
	
	/**
	 * Positive test case: This test checks that a session is stored correctly
	 */
	@Test
	public void testSecStatus_StoreSession()
	{
		// store SecMechanism in SecStatus
		ProtocolMechanism protocolMechanism = new ProtocolMechanism(FileProtocol.class);
		SecStatusMechanismUpdatePropagation myMechanism = new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, protocolMechanism);
		securityStatus.updateMechanisms(myMechanism);
		
		// create processing data
		ProcessingData processingData = new ProcessingData();
		processingData.updateResponseAPDU(this, "Session context successful stored", new ResponseApdu(SW_9000_NO_ERROR));
		
		// send update propagation to store the security status
		processingData.addUpdatePropagation(this, "Inform the SecStatus to store the security status",
				new SecStatusStoreUpdatePropagation(SecurityEvent.STORE_SESSION_CONTEXT, 1));
		
		// update the security status and check that it has been stored
		securityStatus.updateSecStatus(processingData);

		// check that a session context has been stored
		assertEquals(securityStatus.storedSecStatusContents.size(), 1);
		
		// get the current stored mechanism from the stored security status
		EnumMap<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>> contexts = securityStatus.storedSecStatusContents.values().iterator().next();
		Set<Entry<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>>> contextSet = contexts.entrySet();
		
		SecMechanism storedMechanism = null;
		for( Entry<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>> context : contextSet){
			Set<Entry<Class<? extends SecMechanism>, SecMechanism>> test = context.getValue().entrySet();
			if (!test.isEmpty()){
				storedMechanism = test.iterator().next().getValue();
			}
		}
		
		// checks that the stored security mechanism is equal to the one created
		assertEquals(protocolMechanism, storedMechanism);
		
		//ensure that the response has not been modified
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, processingData.getResponseApdu()
				.getStatusWord());
	}
	
	/**
	 * Negative test case: This test tries to restore a session with an not existing session identifier
	 */
	@Test
	public void testSecStatus_RestoreNotExistingSession()
	{
		ProcessingData processingData = new ProcessingData();
		processingData.updateResponseAPDU(this, "Session context successful stored", new ResponseApdu(SW_9000_NO_ERROR));
		
		processingData.addUpdatePropagation(this, "Inform the SecStatus to restore the security status",
				new SecStatusStoreUpdatePropagation(SecurityEvent.RESTORE_SESSION_CONTEXT, 1));
		securityStatus.updateSecStatus(processingData);
		
		assertEquals(securityStatus.storedSecStatusContents.size(), 0);
		assertEquals("Statusword is not 6A88", Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, processingData.getResponseApdu()
				.getStatusWord());
	}
	
	/**
	 * Positive test case: This test stores a session and restores a session
	 */
	@Test
	public void testSecStatus_StoreRestoreSession()
	{
		// test cases set up
		final SmDataProvider smDataProvider = new SmDataProvider(){

			@Override
			public Class<? extends UpdatePropagation> getKey() {
				return SmDataProvider.class;
			}

			@Override
			public void init(SmDataProvider prev) {}

			@Override
			public void nextIncoming() {}

			@Override
			public void nextOutgoing() {}

			@Override
			public Cipher getCipher() {return null;}

			@Override
			public IvParameterSpec getCipherIv() {return null;}

			@Override
			public SecretKey getKeyEnc() {return null;}

			@Override
			public Mac getMac() {return null;}

			@Override
			public byte[] getMacAuxiliaryData() {return null;}

			@Override
			public SecretKey getKeyMac() {return null;}
			@Override
			public Integer getMacLength() {return null;}
			
			@Override
			public SmDataProviderGenerator getSmDataProviderGenerator() {return null;}
		};
			
		SmDataProviderGenerator dataProviderGenerator = new SmDataProviderGenerator(){

			@Override
			public boolean needsDeletionInCaseOf(SecurityEvent event) {
				return false;
			}

			@Override
			public Class<? extends SecMechanism> getKey() {
				return SmDataProviderGenerator.class;
			}

			@Override
			public SmDataProvider generateSmDataProvider() {
				return smDataProvider;
			}
			
		};
		
		
		
		SecStatusMechanismUpdatePropagation mySecMechanism = new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, dataProviderGenerator);
		securityStatus.updateMechanisms(mySecMechanism);
		
		// create processing data
		ProcessingData processingData = new ProcessingData();
		processingData.updateResponseAPDU(this, "Session context successful stored", new ResponseApdu(SW_9000_NO_ERROR));
		
		// call store security status update propagation 
		processingData.addUpdatePropagation(this, "Inform the SecStatus to store the security status", 
				new SecStatusStoreUpdatePropagation(SecurityEvent.STORE_SESSION_CONTEXT, 1));
		
		// update the security status
		securityStatus.updateSecStatus(processingData);
		
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, processingData.getResponseApdu()
				.getStatusWord());
				
		// get the stored security mechanism 	
//		EnumMap<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>> contexts = securityStatus.storedSecStatusContents.values().iterator().next();
//		Set<Entry<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>>> contextSet = contexts.entrySet();
//		
//		SecMechanism storedSecMechanism = null;
//		for( Entry<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>> context : contextSet){
//			
//			Set<Entry<Class<? extends SecMechanism>, SecMechanism>> secMechanisms = context.getValue().entrySet();
//			
//			if (!secMechanisms.isEmpty()){
//				Object object = secMechanisms.iterator().next().getValue();
//				if (object instanceof SmDataProviderGenerator) {
//					storedSecMechanism =  (SecMechanism) object;
//				}
//				
//			}
//		}
//		
//		// compare that the stored security status and the one created before are equal
//		assertEquals(dataProviderGenerator, storedSecMechanism);
//		assertEquals(securityStatus.storedSecStatusContents.size(), 1);
		
		// insert a new protocol mechanism
		ProtocolMechanism protocolMechanism = new ProtocolMechanism(FileProtocol.class);
		SecStatusMechanismUpdatePropagation myMechanism = new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, protocolMechanism);
		securityStatus.updateMechanisms(myMechanism);
		
		
		//test the restore
		processingData = new ProcessingData();
		processingData.updateResponseAPDU(this, "Session context successful restored", new ResponseApdu(SW_9000_NO_ERROR));
		processingData.addUpdatePropagation(this, "Inform the SecStatus to restore the security status",
				new SecStatusStoreUpdatePropagation(SecurityEvent.RESTORE_SESSION_CONTEXT, 1));
		
		securityStatus.updateSecStatus(processingData);
		
		// check if restore security status propagation has been processed
		LinkedList<UpdatePropagation> secStoreUpdatePropagations = processingData.getUpdatePropagations(SecStatusStoreUpdatePropagation.class);
		assertEquals(1, secStoreUpdatePropagations.size());
		
		// ensure that the SmDataProviderGenerator mechanism is correctly restored
		HashSet<Class<? extends SecMechanism>>  smDataMechanisms = new HashSet<>();
		smDataMechanisms.add(SmDataProviderGenerator.class);
		Collection<SecMechanism> storedSmDataMechanism = securityStatus.getCurrentMechanisms(SecContext.APPLICATION, smDataMechanisms);
		assertEquals(1, storedSmDataMechanism.size());
		
		// ensure that the mechanism inserted before the restore got removed
		HashSet<Class<? extends SecMechanism>> protocolMechanisms = new HashSet<>();
		protocolMechanisms.add(ProtocolMechanism.class);
		Collection<SecMechanism> storedProtocolMechanism = securityStatus.getCurrentMechanisms(SecContext.APPLICATION, protocolMechanisms);
		assertEquals(0, storedProtocolMechanism.size());
		
		// ensure that the correct SmDataUpgradePropagation is fired
		LinkedList<UpdatePropagation> smDataProviders = processingData.getUpdatePropagations(SmDataProvider.class);
		assertEquals(1, smDataProviders.size());
		assertEquals(smDataProvider, smDataProviders.getFirst());
		
		// ensure that the status word is not modified by the process
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, processingData.getResponseApdu()
				.getStatusWord());
		
	}
	
	/**
	 * Positive test case: check the updateSecStatus method in the SecStatus class.
	 */
	@Test
	public void testUpdateSecStatus_Input_Is_ProcessingData_Object()
	{
		SecStatus test = new SecStatus();
		ProcessingData lol = new ProcessingData();
		test.updateSecStatus(lol);		
	}
	
	/**
	 * Positive test case: check that the getCurrentMechanisms method correctly finds mechanisms
	 */
	@Test
	public void testGetCurrentMechanisms()
	{
		SecMechanism mechanismToFind = new AbstractSecMechanism() {
			@Override
			public boolean needsDeletionInCaseOf(SecurityEvent event) {
				return false;
			}
		};
		
		populateSecStatus(SecContext.APPLICATION, mechanismToFind);
		
		Collection<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(mechanismToFind.getClass());
		
		Collection<SecMechanism> foundMechanisms = securityStatus.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		assertEquals(1, foundMechanisms.size());
		assertSame(mechanismToFind, foundMechanisms.iterator().next());
		
	}
	
	/**
	 * Adds a mechanism to the {@link SecStatus} by wrapping it in a {@link SecStatusMechanismUpdatePropagation}
	 * @param context
	 * @param mechanisms
	 */
	private void populateSecStatus(SecContext context, SecMechanism ...mechanisms){
		for (SecMechanism mechanism : mechanisms){
			SecStatusMechanismUpdatePropagation updatePropagation = new SecStatusMechanismUpdatePropagation(context, mechanism);
			securityStatus.updateMechanisms(updatePropagation);	
		}
	}
	
	/**
	 * Check that restoring using an incorrect ID causes the correct exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRestoreNotExisting(){
		securityStatus.restoreSecStatus(10);
	}
	
	@Test
	public void testStoreAndRestoreStatus(){
		SecMechanism beforeStoring = new AbstractSecMechanism() {
			@Override
			public boolean needsDeletionInCaseOf(SecurityEvent event) {
				return false;
			}
		};
		SecMechanism afterStoring = new AbstractSecMechanism() {
			
			@Override
			public boolean needsDeletionInCaseOf(SecurityEvent event) {
				return true;
			}
		};

		securityStatus.storeSecStatus(0);
		
		populateSecStatus(SecStatus.SecContext.GLOBAL, beforeStoring);
		
		Collection<Class<? extends SecMechanism>> previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(beforeStoring.getClass());
		
		Collection<SecMechanism> mechanismsBeforeStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(1, mechanismsBeforeStore.size());
		assertSame(beforeStoring, mechanismsBeforeStore.iterator().next());
		
		securityStatus.storeSecStatus(1);
		
		populateSecStatus(SecStatus.SecContext.GLOBAL, afterStoring);
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(beforeStoring.getClass());
		Collection<SecMechanism> mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(1, mechanismsAfterStore.size());
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(afterStoring.getClass());
		mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(1, mechanismsAfterStore.size());
		
		securityStatus.restoreSecStatus(1);
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(beforeStoring.getClass());
		mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(1, mechanismsAfterStore.size());
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(afterStoring.getClass());
		mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(0, mechanismsAfterStore.size());
		
		securityStatus.restoreSecStatus(0);
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(beforeStoring.getClass());
		mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(0, mechanismsAfterStore.size());
		
		previousMechanisms = new HashSet<Class<? extends SecMechanism>>();
		previousMechanisms.add(afterStoring.getClass());
		mechanismsAfterStore = securityStatus.getCurrentMechanisms(SecContext.GLOBAL, previousMechanisms);
		assertEquals(0, mechanismsAfterStore.size());
	}
	
	/**
	 * Positive test case checking the life cycle allowing access even if the
	 * security conditions do not match.
	 */
	@Test
	public void testCheckAccessConditionsLifecycleAllows() {
		Iso7816LifeCycleState state = Iso7816LifeCycleState.CREATION;
		SecStatus securityStatus = new SecStatus();
		SecCondition secCondition = new SecCondition() {
			
			@Override
			public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
				return Collections.emptySet();
			}
			
			@Override
			public boolean check(Collection<SecMechanism> mechanisms) {
				return false;
			}
		};
		assertTrue(securityStatus.checkAccessConditions(state, secCondition, SecContext.APPLICATION));
	}
	
	/**
	 * Positive test case checking whether the security conditions prohibit access.
	 */
	@Test
	public void testCheckAccessConditionsSecurityConditionsProhibit(){

		Iso7816LifeCycleState state = Iso7816LifeCycleState.OPERATIONAL_ACTIVATED;
		SecStatus securityStatus = new SecStatus();
		SecCondition secCondition = new SecCondition() {
			
			@Override
			public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
				return Collections.emptySet();
			}
			
			@Override
			public boolean check(Collection<SecMechanism> mechanisms) {
				return false;
			}
		};
		assertFalse(securityStatus.checkAccessConditions(state, secCondition, SecContext.APPLICATION));
	}
	
	/**
	 * Positive test case checking whether the security conditions can allow access.
	 */
	@Test
	public void testCheckAccessConditionsSecurityConditionsAllow(){

		Iso7816LifeCycleState state = Iso7816LifeCycleState.OPERATIONAL_ACTIVATED;
		SecStatus securityStatus = new SecStatus();
		SecCondition secCondition = new SecCondition() {
			
			@Override
			public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
				return Collections.emptySet();
			}
			
			@Override
			public boolean check(Collection<SecMechanism> mechanisms) {
				return true;
			}
		};
		assertTrue(securityStatus.checkAccessConditions(state, secCondition, SecContext.APPLICATION));
	}
	
}
