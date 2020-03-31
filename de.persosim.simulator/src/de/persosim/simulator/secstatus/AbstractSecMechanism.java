package de.persosim.simulator.secstatus;

/**
 * This is the parent class for all {@link SecMechanism} implementations and
 * provides the default behavior for deletion in case of any event.
 * 
 * @author mboonk
 * 
 */
public class AbstractSecMechanism implements SecMechanism {

	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		return !SecurityEvent.EXTENDED_ACCESS_SESSION_ENDED.equals(event);
	}

	@Override
	public Class<? extends SecMechanism> getKey() {
		return this.getClass();
	}

}
