package de.persosim.simulator.secstatus;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.seccondition.SecCondition;

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
public class SecStatus {

	public enum SecContext {
		GLOBAL, APPLICATION, FILE, COMMAND
	}

	EnumMap<SecContext, HashMap<Class<? extends SecMechanism>, SecMechanism>> contexts = new EnumMap<>(
			SecContext.class);

	public SecStatus() {
		reset();
	}

	public void reset(){
		// initialize the contexts
		for (SecContext curSecContext : SecContext.values()) {
			contexts.put(curSecContext,
					new HashMap<Class<? extends SecMechanism>, SecMechanism>());
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
			if (securityContext.containsKey(clazz)){
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
	public void updateMechanisms(
			SecStatusMechanismUpdatePropagation... updatePropagation) {
		for (SecStatusMechanismUpdatePropagation curUpdate : updatePropagation) {
			SecStatusMechanismUpdatePropagation mechanismPropagation = (SecStatusMechanismUpdatePropagation) curUpdate;
			contexts.get(mechanismPropagation.getContext()).put(
					mechanismPropagation.getMechanism().getClass(),
					mechanismPropagation.getMechanism());

		}
	}

	/**
	 * This method updates internal state of the SecStatus according to the
	 * UpdatePropagation.
	 * 
	 * @param updatePropagation
	 */
	private void updateEvents(SecStatusEventUpdatePropagation... updatePropagation){
		for (SecStatusEventUpdatePropagation curUpdate : updatePropagation){

			SecStatusEventUpdatePropagation eventPropagation = (SecStatusEventUpdatePropagation) curUpdate;
			for (SecContext context : contexts.keySet()) {
				Collection<Class<? extends SecMechanism>> toBeDeleted = new HashSet<Class<? extends SecMechanism>>();
				for (Class<? extends SecMechanism> clazz : contexts
						.get(context).keySet()) {
					if (contexts.get(context).get(clazz)
							.needsDeletionInCaseOf(eventPropagation.getEvent())) {
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
		for (UpdatePropagation update : processingData.getUpdatePropagations(SecStatusEventUpdatePropagation.class)){
			updateEvents((SecStatusEventUpdatePropagation)update);
		}
		for (UpdatePropagation update : processingData.getUpdatePropagations(SecStatusMechanismUpdatePropagation.class)){
			updateMechanisms((SecStatusMechanismUpdatePropagation)update);
		}
	}
	
	/**
	 * This method can be used to check if necessary access conditions are
	 * fulfilled. It uses the application security context.
	 * 
	 * @param state
	 *            the lifecycle state of the {@link CardObject}
	 * @param secCondition
	 *            the {@link SecCondition} to verify
	 * @return true, if at least one security condition is fulfilled or the if
	 *         the {@link Iso7816LifeCycleState} grants access
	 */
	public boolean checkAccessConditions(Iso7816LifeCycleState state, SecCondition secCondition){
		return checkAccessConditions(state, secCondition, SecContext.APPLICATION);
	}

	
	/**
	 * This method can be used to check if necessary access conditions are
	 * fulfilled.
	 * 
	 * @param state
	 *            the lifecycle state of the {@link CardObject}
	 * @param secCondition
	 *            the {@link SecCondition} to verify
	 * @param context
	 *            {@link SecContext} the context to check the conditions for
	 * @return true, if at least one security condition is fulfilled or the if
	 *         the {@link Iso7816LifeCycleState} grants access
	 */
	public boolean checkAccessConditions(Iso7816LifeCycleState state, SecCondition secConditions, SecContext context){
		if (checkAccessConditions(state)){
			return true;
		} else if (secConditions.check(this.getCurrentMechanisms(context, secConditions.getNeededMechanisms()))){
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
}
