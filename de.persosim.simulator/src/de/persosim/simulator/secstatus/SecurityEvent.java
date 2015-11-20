package de.persosim.simulator.secstatus;

/**
 * This enum lists all possible events, that can be used by the
 * {@link SecStatus} to determine via
 * {@link SecMechanism#needsDeletionInCaseOf(SecurityEvent)} if this particular
 * algorithm must be removed.
 * 
 * @author mboonk
 * 
 */
public enum SecurityEvent {
	SECURE_MESSAGING_SESSION_ENDED,
	STORE_SESSION_CONTEXT,
	RESTORE_SESSION_CONTEXT
}
