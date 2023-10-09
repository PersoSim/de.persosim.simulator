package de.persosim.simulator.secstatus;

import java.util.Arrays;

import de.persosim.simulator.protocols.Oid;

/**
 * This {@link SecMechanism} implements the information store for security state
 * information generated by executions of the CA+PA protocol.
 */
public class CAPAMechanism extends AbstractSecMechanism {

	private boolean mutualAuthSuccessful = false;
	private boolean pinVerifySuccessful = false;
	private byte[] compressedEphemeralPublicKeyChip;
	private Oid terminalTypeOid;

	public CAPAMechanism(boolean mutualAuthSuccessful, byte[] compressedEphemeralPublicKeyChip, Oid terminalTypeOid) {
		this.mutualAuthSuccessful = mutualAuthSuccessful;
		this.compressedEphemeralPublicKeyChip = compressedEphemeralPublicKeyChip;
		this.terminalTypeOid = terminalTypeOid;
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

	public byte[] getCompressedEphemeralPublicKeyChip() {
		if (compressedEphemeralPublicKeyChip == null)
			return null;
		return Arrays.copyOf(compressedEphemeralPublicKeyChip, compressedEphemeralPublicKeyChip.length);
	}

	public Oid getTerminalTypeOid() {
		return terminalTypeOid;
	}

}
