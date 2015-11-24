package de.persosim.simulator.secstatus;

public class SessionContextIdMechanism extends AbstractSecMechanism{
	
	private int sessionContextId;

	public SessionContextIdMechanism(int sessionContextId) {
		this.sessionContextId = sessionContextId;
	}

	public int getSessionContextId() {
		return sessionContextId;
	}
}
