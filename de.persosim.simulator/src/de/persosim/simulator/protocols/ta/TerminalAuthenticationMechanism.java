package de.persosim.simulator.protocols.ta;

import java.util.Arrays;
import java.util.List;

import de.persosim.simulator.crypto.certificates.CertificateExtension;
import de.persosim.simulator.secstatus.AbstractSecMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecurityEvent;

/**
 * This {@link SecMechanism} is used to communicate all useful information
 * created while executing terminal authentication.
 * 
 * @author mboonk
 * 
 */
public class TerminalAuthenticationMechanism extends AbstractSecMechanism {

	private List<AuthenticatedAuxiliaryData> auxiliaryData;
	private TerminalType terminalType;
	private byte [] compressedTerminalEphemeralPublicKey;
	private byte [] firstSectorPublicKeyHash;
	private byte [] secondSectorPublicKeyHash;
	private String sectorPublicKeyHashAlgorithm;
	private List<CertificateExtension> certificateExtensions;

	public TerminalAuthenticationMechanism(byte [] compressedEphemeralTerminalPublicKey, TerminalType terminalType,
			List<AuthenticatedAuxiliaryData> auxiliaryData, byte [] firstSectorPublicKeyHash, byte [] secondSectorPublicKeyHash, String sectorPublicKeyHashAlgorithm, List<CertificateExtension> certificateExtensions) {
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
		this.certificateExtensions = certificateExtensions;
	}

	/**
	 * @return the firstSectorPublicKeyHash or null if none set
	 */
	public byte[] getFirstSectorPublicKeyHash() {
		if (firstSectorPublicKeyHash != null){
			return Arrays.copyOf(firstSectorPublicKeyHash, firstSectorPublicKeyHash.length);	
		}
		return null;
	}

	/**
	 * @return the secondSectorPublicKeyHash or null if none set
	 */
	public byte[] getSecondSectorPublicKeyHash() {
		if (secondSectorPublicKeyHash != null){
			return Arrays.copyOf(secondSectorPublicKeyHash, secondSectorPublicKeyHash.length);	
		}
		return null;
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
	public List<AuthenticatedAuxiliaryData> getAuxiliaryData() {
		return auxiliaryData;
	}

	@Override
	public boolean needsDeletionInCaseOf(SecurityEvent event) {
		return true;
	}

	/**
	 * @return the compressedTerminalEphemeralPublicKey or null if none set
	 */
	public byte[] getCompressedTerminalEphemeralPublicKey() {
		if (compressedTerminalEphemeralPublicKey != null){
			return Arrays.copyOf(compressedTerminalEphemeralPublicKey, compressedTerminalEphemeralPublicKey.length);	
		}
		return null;
	}

	public List<CertificateExtension> getCertificateExtensions() {
		return certificateExtensions;
	}
	
}
