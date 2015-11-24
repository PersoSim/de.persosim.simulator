package de.persosim.simulator.secstatus;

/**
 * This Mechanism contains the Identifier that should be used to store the
 * session context. The ID for the Default Context (ID=0) is set at the end of
 * the PACE protocol. Other IDs are provided during MSE: set AT commands in tag
 * E0. The storing of the default context happens before the CA initializes new
 * Secure Messaging. Storing of other contexts happens before restoring old ones.
 * 
 * @author jkoch
 *
 */
public class SessionContextIdMechanism extends AbstractSecMechanism{
	
	private int sessionContextId;

	public SessionContextIdMechanism(int sessionContextId) {
		this.sessionContextId = sessionContextId;
	}

	public int getSessionContextId() {
		return sessionContextId;
	}
}
