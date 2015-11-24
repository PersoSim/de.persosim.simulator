package de.persosim.simulator.secstatus;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.securemessaging.SmDataProviderGenerator;
import de.persosim.simulator.utils.InfoSource;

/**
 * Representation of the current security status of the card.
 * 
 * The SecStatus is owned and managed by the {@link CommandProcessor}. The
 * active protocols can query the SecStatus through a facade provided during
 * protocol initialization and modify by adding UpdatePropagations to the
 * ProcessingData.
 * 
 * @author amay
 * 
 */
public class SecStatus implements InfoSource{
	
	public enum SecContext {
		GLOBAL, APPLICATION, FILE, COMMAND, PERSISTANT
	}

	EnumMap<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>> contexts = new EnumMap<>(
			SecContext.class);

	HashMap<Integer, EnumMap<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>>> storedSecStatusContents = new HashMap<>();

	public SecStatus() {
		reset();
	}

	/**
	 * Resets the complete contents of this security status.
	 */
	public void reset() {
		reset(true);
	}

	/**
	 * Resets the security status. The parameter decides if a complete reset of
	 * all inner state is performed. In case of a partial reset the
	 * {@link SecContext#PERSISTANT} is not cleared.
	 * 
	 * @param completeReset
	 */
	private void reset(boolean completeReset) {
		// initialize the contexts
		for (SecContext curSecContext : SecContext.values()) {
			if (!completeReset && curSecContext == SecContext.PERSISTANT && contexts.containsKey(SecContext.PERSISTANT)){
				continue;
			}
			contexts.put(curSecContext, new HashMap<Class<? extends SecMechanism>, SecMechanism>());
		}
	}

	/**
	 * This method finds all currently active mechanisms (instances) as defined
	 * by wantedMechanisms.
	 * 
	 * @param context
	 *            to be searched for mechanisms
	 * @param wantedMechanisms
	 *            as classes to be matched on
	 * @return all wanted SecMechanism instances in the given context
	 */
	public Collection<SecMechanism> getCurrentMechanisms(SecContext context,
			Collection<Class<? extends SecMechanism>> wantedMechanisms) {
		HashSet<SecMechanism> result = new HashSet<>();
		for (Class<? extends SecMechanism> clazz : wantedMechanisms) {
			HashMap<Class<? extends SecMechanism>, SecMechanism> securityContext = contexts.get(context);
			if (securityContext.containsKey(clazz)) {
				result.add(contexts.get(context).get(clazz));
			}
		}
		return result;
	}

	/**
	 * This method updates internal state of the SecStatus according to the
	 * UpdatePropagation.
	 * 
	 * @param updatePropagation
	 */
	public void updateMechanisms(SecStatusMechanismUpdatePropagation... updatePropagation) {
		for (SecStatusMechanismUpdatePropagation curUpdate : updatePropagation) {
			SecStatusMechanismUpdatePropagation mechanismPropagation = (SecStatusMechanismUpdatePropagation) curUpdate;
			updateContext(mechanismPropagation.getContext(), mechanismPropagation.getMechanism());
		}
	}

	private void updateContext(SecContext context, SecMechanism mechanism) {
		contexts.get(context).put(mechanism.getKey(), mechanism);
	}

	/**
	 * This method updates internal state of the SecStatus according to the
	 * UpdatePropagation.
	 * 
	 * @param updatePropagation
	 */
	private void updateEvents(SecStatusEventUpdatePropagation... updatePropagation) {
		for (SecStatusEventUpdatePropagation curUpdate : updatePropagation) {

			SecStatusEventUpdatePropagation eventPropagation = (SecStatusEventUpdatePropagation) curUpdate;
			for (SecContext context : contexts.keySet()) {
				Collection<Class<? extends SecMechanism>> toBeDeleted = new HashSet<Class<? extends SecMechanism>>();
				for (Class<? extends SecMechanism> clazz : contexts.get(context).keySet()) {
					if (contexts.get(context).get(clazz).needsDeletionInCaseOf(eventPropagation.getEvent())) {
						toBeDeleted.add(clazz);
					}
				}

				for (Class<? extends SecMechanism> clazz : toBeDeleted) {
					contexts.get(context).remove(clazz);
				}
			}
		}

	}

	/**
	 * This method updates internal state of the SecStatus according to the
	 * UpdatePropagations contained in the processing data.
	 * 
	 * Called by the {@link CommandProcessor} during processing of each APDU.
	 * 
	 * @param processingData
	 */
	public void updateSecStatus(ProcessingData processingData) {	
		for (UpdatePropagation update : processingData.getUpdatePropagations(SecStatusStoreUpdatePropagation.class)) {
			storeRestoreSession(processingData,(SecStatusStoreUpdatePropagation) update);
		}
		for (UpdatePropagation update : processingData.getUpdatePropagations(SecStatusEventUpdatePropagation.class)) {
			updateEvents((SecStatusEventUpdatePropagation) update);
		}
		for (UpdatePropagation update : processingData
				.getUpdatePropagations(SecStatusMechanismUpdatePropagation.class)) {
			updateMechanisms((SecStatusMechanismUpdatePropagation) update);
		}
	}

	/**
	 * This stores all {@link SecMechanism}s currently existing in the
	 * {@link SecStatus}. Already existing stored contents are replaced when the
	 * same id is reused.
	 * 
	 * @param id
	 *            the integer id to reference the stored contents
	 */
	public void storeSecStatus(int id) {
		storedSecStatusContents.put(id, createCopyForStoring(contexts));
	}

	/**
	 * This calls {@link #storeSecStatus(int)} using a unused id and returns it
	 * for further use.
	 * 
	 * @see #storeSecStatus(int)
	 * @return the id used for storing the {@link SecStatus} contents
	 */
	public int storeSecStatus() {
		int freeId = 0;
		while (storedSecStatusContents.containsKey(freeId)) {
			freeId++;
		}
		storeSecStatus(freeId);
		return freeId;
	}

	/**
	 * Restores the previously stored contents to be the current content of the
	 * {@link SecStatus}. This implicitly removes all {@link SecMechanism}s that
	 * were added or differ from the ones in the restored state.
	 * 
	 * @param id
	 *            the id of the stored contents to be restored
	 * @throws IllegalArgumentException
	 *             if the no contents were stored using the given id
	 */
	public void restoreSecStatus(int id) {
		EnumMap<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>> toRestore = storedSecStatusContents
				.get(id);

		if (toRestore == null) {
			throw new IllegalArgumentException("The given id does not exist in the stored contents.");
		}

		reset(false);
		for (SecContext context : toRestore.keySet()) {
			for (SecMechanism mechanism : toRestore.get(context).values()) {
				updateContext(context, mechanism);
			}
		}
	}

	// TODO move this suppression into the cast as soon as compliance level 1.8
	// is used for this code
	/**
	 * Creates a copy of the data structure storing the {@link SecMechanism}s.
	 * The returned object is a duplicate using the same references to
	 * {@link SecMechanism} objects. This relies on the immutability of the
	 * {@link SecMechanism}s.
	 * 
	 * @param source
	 *            the object to copy
	 * @return the copied object
	 */
	@SuppressWarnings("unchecked") // This suppression is needed because
									// Object.clone() does not support generics
	private EnumMap<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>> createCopyForStoring(
			EnumMap<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>> source) {

		EnumMap<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>> copy = new EnumMap<>(
				SecContext.class);

		for (SecContext context : source.keySet()) {
			if (context == SecContext.PERSISTANT){
				continue;
			}
			copy.put(context, (HashMap<Class<? extends SecMechanism>, SecMechanism>) source.get(context).clone());
		}
		return copy;
	}
	
	/**
	 * Processes all restore session context event update propagations. Calls for every event the appropriate function either
	 * storeSecStatus or restoreSecStatus. If the SecStatus has to be restored this function also set the {@link SmDataProviderGenerator}
	 * to restore all needed keys for the securemessaging.
	 * 
	 * @param processingData the processind data
	 * @param update the SecStatusStoreUpdatePropagation 
	 */
	private void storeRestoreSession(ProcessingData processingData, SecStatusStoreUpdatePropagation ... update) {
		try {
			for (SecStatusStoreUpdatePropagation curUpdate : update) {
				SecStatusStoreUpdatePropagation eventPropagation = (SecStatusStoreUpdatePropagation) curUpdate;
				if (eventPropagation.getEvent().equals(SecurityEvent.RESTORE_SESSION_CONTEXT)){
					restoreSecStatus(eventPropagation.getSessionContextIdentifier());
					HashSet<Class<? extends SecMechanism>> set = new HashSet<>();
					set.add(SmDataProviderGenerator.class);
					Collection<SecMechanism> generators = getCurrentMechanisms(SecContext.APPLICATION, set);
					if (generators.size() > 1){
						processingData.updateResponseAPDU(this, "More than one secure messaging context found", new ResponseApdu(Iso7816.SW_6400_EXECUTION_ERROR));
					}
					if (generators.size() == 1){
						processingData.addUpdatePropagation(this, "restore Secure Messaging", ((SmDataProviderGenerator)generators.iterator().next()).generateSmDataProvider());
					}
				}
				
				if (eventPropagation.getEvent().equals(SecurityEvent.STORE_SESSION_CONTEXT)){
						storeSecStatus(eventPropagation.getSessionContextIdentifier());
				}
			}
		} catch(IllegalArgumentException e) {
			processingData.updateResponseAPDU(this, e.getMessage(), new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND));
		}
	}
	
	/**
	 * This method can be used to check whether necessary access conditions are
	 * fulfilled. It uses the application security context.
	 * 
	 * @param state
	 *            the lifecycle state of the {@link CardObject}
	 * @param secCondition
	 *            the {@link SecCondition} to verify
	 * @return true, if at least one security condition is fulfilled or the
	 *         {@link Iso7816LifeCycleState} grants access
	 */
	public boolean checkAccessConditions(Iso7816LifeCycleState state, SecCondition secCondition){
		return checkAccessConditions(state, secCondition, SecContext.APPLICATION);
	}

	
	/**
	 * This method can be used to check whether necessary access conditions are
	 * fulfilled.
	 * 
	 * @param state
	 *            the lifecycle state of the {@link CardObject}
	 * @param secCondition
	 *            the {@link SecCondition} to verify. Must not be null.
	 * @param context
	 *            {@link SecContext} the context to check the conditions for
	 * @return true, if at least one security condition is fulfilled or the
	 *         {@link Iso7816LifeCycleState} grants access
	 */
	public boolean checkAccessConditions(Iso7816LifeCycleState state, SecCondition secCondition, SecContext context){
		if (checkAccessConditions(state)){
			return true;
		} else if (secCondition.check(this.getCurrentMechanisms(context, secCondition.getNeededMechanisms()))){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This only checks the lifecycle state for necessary access conditions.
	 * 
	 * @param state
	 *            the lifecycle state of the {@link CardObject}
	 * @return true, if the {@link Iso7816LifeCycleState} grants access
	 */
	public static boolean checkAccessConditions(Iso7816LifeCycleState state){
		if (state.equals(Iso7816LifeCycleState.CREATION)){
			return true;
		}
		return false;
		// IMPL the implementation of checks regarding the initialization state
		// is missing, as it is not yet needed since personalization happens before starting the simulator
	}

	@Override
	public String getIDString() {
		return "SecStatus";
	}
	
}
