package de.persosim.simulator.secstatus;

import java.util.Collection;

/**
 * Describes a SecurityCondition to be checked against given
 * {@link SecMechanism} obtained from {@link SecStatus}.
 * 
 * The check itself is implemented within this interface and may rely on all
 * data provided by required {@link SecMechanism}s.
 * 
 * @author amay
 * 
 */
public interface SecCondition {

	/**
	 * Perform the condition check.
	 * 
	 * As the calling entity requires the result of this check it is safe to
	 * rely on it to provide correct mechanisms as input for the check.
	 * 
	 * @param mechanisms
	 *            SecMechanisms required by this SecCondition as input to the
	 *            verification. This Collection needs to be consistent with the
	 *            template returned by {@link #getNeededMechanisms()}.
	 * 
	 * @return true if the condition is fulfilled by the provided SecMechanisms,
	 *         false otherwise
	 */
	boolean check(Collection<SecMechanism> mechanisms);

	/**
	 * This method provides a List of required {@link SecMechanism}s that the
	 * caller of {@link #check(Collection)} needs to query from the
	 * {@link SecStatus} and provide as parameter.
	 * 
	 * The returned Object might be immutable (e.g. immutable empty Collection)
	 * and thus must not be modified by the caller.
	 * 
	 * @return Collection of required {@link SecMechanism}s
	 */
	Collection<Class<? extends SecMechanism>> getNeededMechanisms();
}
