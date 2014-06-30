package de.persosim.simulator.secstatus;

/**
 * Classes that implement this interface are used to store relevant data for the
 * given SecurityMechanism within the SecStatus.
 * 
 * The state of SecMechanisms can be queried from the SecStatus, which returns
 * the genuine SecMechanism as is. SecMechanisms are expected to be immutable to
 * ensure the consistency of the SecStatus.
 * 
 * SecConditions can refer to SecMechanisms and rely on the provided data to
 * perform the condition checking.
 * 
 * This is only a flagging interface and does not include any own functionality
 * in terms of methods. But the contract provided by this JavaDoc needs to be
 * adhered to by all classes implementing this interface.
 * 
 * @author amay
 * 
 */
public interface SecMechanism {
	/**
	 * Decide if this {@link SecMechanism} needs to be deleted if the
	 * {@link SecStatus} receives the given event.
	 * 
	 * @param event
	 * @return true, if deletion is necessary
	 */
	boolean needsDeletionInCaseOf(SecurityEvent event);
}
