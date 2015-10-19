package de.persosim.simulator.protocols.ta;

import java.util.Arrays;
import java.util.Collection;

import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecurityEvent;

/**
 * This {@link SecMechanism} is used to communicate all useful information
 * created while executing terminal authentication.
 * 
 * @author mboonk
 * 
 */
public class TerminalAuthenticationMechanism implements SecMechanism {

	private Collection<AuthenticatedAuxiliaryData> auxiliaryData;
	private TerminalType terminalType;
	private byte [] compressedTerminalEphemeralPublicKey;
	private byte [] firstSectorPublicKeyHash;
	private byte [] secondSectorPublicKeyHash;
	private String sectorPublicKeyHashAlgorithm;

	public TerminalAuthenticationMechanism(byte [] compressedEphemeralTerminalPublicKey, TerminalType terminalType,
			Collection<AuthenticatedAuxiliaryData> auxiliaryData, byte [] firstSectorPublicKeyHash, byte [] secondSectorPublicKeyHash, String sectorPublicKeyHashAlgorithm) {
		this.auxiliaryData = auxiliaryData;
		this.terminalType = terminalType;
		this.compressedTerminalEphemeralPublicKey = Arrays.copyOf(compressedEphemeralTerminalPublicKey, compressedEphemeralTerminalPublicKey.length);
		if (firstSectorPublicKeyHash != null){
			this.firstSectorPublicKeyHash = Arrays.copyOf(firstSectorPublicKeyHash, firstSectorPublicKeyHash.length);	
		}
		if (secondSectorPublicKeyHash != null){
			this.secondSectorPublicKeyHash = Arrays.copyOf(secondSectorPublicKeyHash, secondSectorPublicKeyHash.length);
		}
		this.sectorPublicKeyHashAlgorithm = sectorPublicKeyHashAlgorithm;
	}

	/**
	 * @return the firstSectorPublicKeyHash
	 */
	public byte[] getFirstSectorPublicKeyHash() {
		return firstSectorPublicKeyHash;
	}

	/**
	 * @return the secondSectorPublicKeyHash
	 */
	public byte[] getSecondSectorPublicKeyHash() {
		return secondSectorPublicKeyHash;
	}

	/**
	 * @return the sectorPublicKeyHashAlgorithm
	 */
	public String getSectorPublicKeyHashAlgorithm() {
		return sectorPublicKeyHashAlgorithm;
	}

	/**
	 * @return the terminalType
	 */
	public TerminalType getTerminalType() {
		return terminalType;
	}

	/**
	 * @return the auxiliaryData
	 */
	public Collection<AuthenticatedAuxiliaryData> getAuxiliaryData() {
		return auxiliaryData;
	}

	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		return true;
	}

	/**
	 * @return the compressedTerminalEphemeralPublicKey
	 */
	public byte[] getCompressedTerminalEphemeralPublicKey() {
		return compressedTerminalEphemeralPublicKey;
	}
}
