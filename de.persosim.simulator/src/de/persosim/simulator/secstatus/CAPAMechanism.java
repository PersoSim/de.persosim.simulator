package de.persosim.simulator.secstatus;

import de.persosim.simulator.protocols.Oid;

/**
 * This {@link SecMechanism} implements the information store for security state
 * information generated by executions of the CA+PA protocol.
 */
public class CAPAMechanism extends AbstractSecMechanism {

	private Oid terminalTypeOid;
	private boolean mutualAuthSuccessful = false;
	private boolean pinVerifySuccessful = false;

	public CAPAMechanism(boolean mutualAuthSuccessful, Oid terminalTypeOid) {
		this.mutualAuthSuccessful = mutualAuthSuccessful;
		this.setTerminalTypeOid(terminalTypeOid);
	}

	public boolean isMutualAuthSuccessful() {
		return mutualAuthSuccessful;
	}

	public boolean isPinVerifySuccessful() {
		return pinVerifySuccessful;
	}

	public void setPinVerifySuccessful(boolean pinVerifySuccessful) {
		this.pinVerifySuccessful = pinVerifySuccessful;
	}

	public Oid getTerminalTypeOid() {
		return terminalTypeOid;
	}

	public void setTerminalTypeOid(Oid terminalTypeOid) {
		this.terminalTypeOid = terminalTypeOid;
	}
}
