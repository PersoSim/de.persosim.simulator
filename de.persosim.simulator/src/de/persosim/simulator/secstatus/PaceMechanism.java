package de.persosim.simulator.secstatus;

import java.util.Arrays;

import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.pace.PaceOid;
import de.persosim.simulator.protocols.ta.TaOid;

/**
 * This {@link SecMechanism} implements the information store for security state
 * information generated by executions of the PACE protocol.
 * 
 * @author mboonk
 * 
 */
public class PaceMechanism extends AbstractSecMechanism {
	
	private PaceOid paceOid;
	private PasswordAuthObject usedPassword;
	private byte [] compressedEphemeralPublicKey;
	private Oid oidForTa;

	public PaceMechanism(PaceOid paceOid, PasswordAuthObject usedPassword, byte[] compressedPublicKey, Oid terminalTypeOid){
		this.paceOid = paceOid;
		this.usedPassword = usedPassword;
		this.compressedEphemeralPublicKey = compressedPublicKey;
		this.oidForTa = terminalTypeOid;
	}
	
	/**
	 * @return the OID, that was used to execute PACE
	 */
	public PaceOid getPaceOid() {
		return paceOid;
	}
	
	/**
	 * @return the password, that was used to execute PACE
	 */
	public PasswordAuthObject getUsedPassword() {
		return usedPassword;
	}

	/**
	 * @return the ephemeralPublicKey of the PICC generated during PACE
	 */
	public byte [] getCompressedEphemeralPublicKey() {
		return Arrays.copyOf(compressedEphemeralPublicKey, compressedEphemeralPublicKey.length);
	}
	
	/**
	 * @return the {@link TaOid} identifying the terminal type
	 */
	public Oid getOidForTa() {
		return oidForTa;
	}

}
