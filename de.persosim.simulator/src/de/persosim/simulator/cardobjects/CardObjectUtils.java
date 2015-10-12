package de.persosim.simulator.cardobjects;

import java.util.Collection;

import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;

/**
 * This class contains utility methods for handling {@link CardObject}s.
 * @author mboonk
 *
 */
public class CardObjectUtils {
	
	/**
	 * This method can be used to check if necessary access conditions are
	 * fulfilled. It uses the application security context.
	 * 
	 * @param state
	 *            the lifecycle state of the {@link CardObject}
	 * @param securityStatus
	 *            the {@link SecStatus} to use
	 * @param secConditions
	 *            the {@link SecCondition}s to verify
	 * @return true, if at least one security condition is fulfilled or the if
	 *         the {@link Iso7816LifeCycleState} grants access
	 */
	public static boolean checkAccessConditions(Iso7816LifeCycleState state, SecStatus securityStatus, Collection<SecCondition> secConditions){
		return checkAccessConditions(state, securityStatus, secConditions, SecContext.APPLICATION);
	}

	
	/**
	 * This method can be used to check if necessary access conditions are
	 * fulfilled.
	 * 
	 * @param state
	 *            the lifecycle state of the {@link CardObject}
	 * @param securityStatus
	 *            the {@link SecStatus} to use
	 * @param secConditions
	 *            the {@link SecCondition}s to verify
	 * @param context
	 *            {@link SecContext} the context to check the conditions for
	 * @return true, if at least one security condition is fulfilled or the if
	 *         the {@link Iso7816LifeCycleState} grants access
	 */
	public static boolean checkAccessConditions(Iso7816LifeCycleState state, SecStatus securityStatus, Collection<SecCondition> secConditions, SecContext context){
		if (checkAccessConditions(state)){
			return true;
		}
				
		for (SecCondition condition : secConditions){
			if (condition.check(securityStatus.getCurrentMechanisms(context, condition.getNeededMechanisms()))){
				return true;
			}
		}
		return false;
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
