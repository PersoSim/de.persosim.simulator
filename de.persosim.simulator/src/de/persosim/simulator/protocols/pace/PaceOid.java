package de.persosim.simulator.protocols.pace;

import java.security.InvalidParameterException;
import java.util.Arrays;

import de.persosim.simulator.crypto.CryptoSupport;
import de.persosim.simulator.crypto.CryptoSupportAes;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class PaceOid extends GenericOid implements Pace {
	public static final int HASHCODEMULTIPLICATOR = 3;
	
	/*----------------------------------------------------------------*/
	
	/**
	 * This constructor constructs a {@link PaceOid} based on a byte array representation of a Pace OID.
	 * @param oidByteArray the byte array representation of a Pace OID
	 */
	public PaceOid(byte[] oidByteArray, Oid prefixOid) {
		super(oidByteArray);
		
		if(!startsWithPrefix(prefixOid)) {
			throw new IllegalArgumentException("OID '" + HexString.encode(oidByteArray) + "' is invalid or unknown (not supported)");
		}
	}
	
	public PaceOid(byte[] oidByteArray) {
		this(oidByteArray, Pace.id_PACE);
	}

	public PaceOid(Oid prefix, byte... suffix) {
		this(Utils.concatByteArrays(prefix.toByteArray(), suffix));
	}
	
	/*----------------------------------------------------------------*/
	

	/**
	 * This method returns the OID's byte indicating the key agreement and mapping
	 * @return the OID's byte indicating the key agreement and mapping
	 */
	public byte getKeyAgreementAndMappingAsByte() {
		return this.oidByteArray[8];
	}
	
	/**
	 * This method returns the OID's byte indicating the symmetric cipher and key size
	 * @return the OID's byte indicating the symmetric cipher and key size
	 */
	public byte getSymmetricCipherAndKeySizeAsByte() {
		return this.oidByteArray[9];
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * This method returns the common name of the used key agreement.
	 * @return the common name of the used key agreement
	 */
	public String getKeyAgreementName() {
		switch (this.getKeyAgreementAndMappingAsByte()) {
		case Pace.ECDH_GM:
			return "ECDH";
		default:
			throw new InvalidParameterException("no or invalid key agreement selected");
		}
	}
	
	/**
	 * This method returns the mapping used with the key agreement.
	 * @return the mapping used with the key agreement
	 */
	public String getMappingName() {
		switch (this.getKeyAgreementAndMappingAsByte()) {
		case Pace.ECDH_GM:
			return "GM";
		default:
			throw new InvalidParameterException("no or invalid mapping selected");
		}
	}
	
	/**
	 * This method returns the mapping function to be used for mapping.
	 * @return the mapping function to be used for mapping
	 */
	public Mapping getMapping() {
		switch (this.getKeyAgreementAndMappingAsByte()) {
		case Pace.ECDH_GM:
			return new GenericMappingEcdh();
		default:
			throw new InvalidParameterException("selected mapping not supported");
		}
	}
	
	protected CryptoSupport cryptoSupportCache = null;
	
	/**
	 * This method returns the {@link CryptoSupport} object that provides support for the selected symmetric cipher.
	 * @return the {@link CryptoSupport} object that provides support for the selected symmetric cipher
	 */
	public CryptoSupport getCryptoSupport() {
		if(cryptoSupportCache == null) {
			cryptoSupportCache = createCryptoSupport();
		}
		
		return cryptoSupportCache;
	}

	protected CryptoSupport createCryptoSupport() {
		String cipherName = getSymmetricCipherAlgorithmNameModePadding();
		String macName = getMacName();
		
		switch (CryptoUtil.getCipherNameAsString(cipherName)) {
		case "AES":
			return new CryptoSupportAes(cipherName, macName);
		default:
			throw new IllegalArgumentException("algorithm " + cipherName + " is unknown or not supported");
		}
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * This method returns the symmetric cipher algorithm's name, mode and padding in the form cipher/mode/padding.
	 * @return the symmetric cipher algorithm's name, mode and padding
	 */
	public String getSymmetricCipherAlgorithmNameModePadding() {
		switch (this.getSymmetricCipherAndKeySizeAsByte()) {
		case Pace.AES_CBC_CMAC_128:
			return "AES/CBC/NoPadding";
		case Pace.AES_CBC_CMAC_192:
			return "AES/CBC/NoPadding";
		case Pace.AES_CBC_CMAC_256:
			return "AES/CBC/NoPadding";
		default:
			throw new InvalidParameterException("no or invalid symmetric cipher selected");
		}
	}
	
	/**
	 * This method returns the symmetric cipher algorithm's name, e.g. "AES".
	 * @return the symmetric cipher algorithm's name
	 */
	public String getSymmetricCipherAlgorithmName() {
		return CryptoUtil.getCipherNameAsString(this.getSymmetricCipherAlgorithmNameModePadding());
	}
	
	/**
	 * This method returns the symmetric cipher algorithm's mode of operation, e.g. "CBC".
	 * @return the symmetric cipher algorithm's mode of operation
	 */
	public String getSymmetricCipherAlgorithmMode() {
		return CryptoUtil.getCipherAlgorithmModeAsString(this.getSymmetricCipherAlgorithmNameModePadding());
	}
	
	/**
	 * This method returns the symmetric cipher algorithm's padding, e.g. "NoPadding".
	 * @return the symmetric cipher algorithm's padding
	 */
	public String getSymmetricCipherAlgorithmPadding() {
		return CryptoUtil.getCipherAlgorithmPaddingAsString(this.getSymmetricCipherAlgorithmNameModePadding());
	}
	
	/**
	 * This method returns the key length in Bytes as indicated by the OID.
	 * @return the key length in Bytes as indicated by the OID
	 */
	public int getSymmetricCipherKeyLengthInBytes() {
		switch (this.getSymmetricCipherAndKeySizeAsByte()) {
		case Pace.AES_CBC_CMAC_128:
			return 16;
		case Pace.AES_CBC_CMAC_192:
			return 24;
		case Pace.AES_CBC_CMAC_256:
			return 32;
		default:
			throw new InvalidParameterException("no or invalid symmetric cipher selected");
		}
	}
	
	/**
	 * This method returns the MAC name as indicated by the OID.
	 * @return the MAC name as indicated by the OID
	 */
	public String getMacName() {
		switch (this.getSymmetricCipherAlgorithmName()) {
		case "AES":
			return "aescmac";
		default:
			throw new InvalidParameterException("no or invalid mac selected");
		}
	}
	
	/**
	 * This method returns the name of the message digest as indicated by the OID.
	 * @return the name of the message digest as indicated by the OID
	 */
	public String getMessageDigestName() {
		int keyLengthInBytes;
		
		keyLengthInBytes = this.getSymmetricCipherKeyLengthInBytes();
		
		if(keyLengthInBytes <= 20) {
			return "SHA-1";
		}
		
		if(keyLengthInBytes <= 32) {
			return "SHA-256";
		}
		
		throw new NullPointerException("no message digest specified");
	}
	
	/*----------------------------------------------------------------*/
	
	public String getStringRepresentation(byte[] oidByteArray) {		
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_GM_AES_CBC_CMAC_128.toByteArray())) return id_PACE_ECDH_GM_AES_CBC_CMAC_128_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_GM_AES_CBC_CMAC_192.toByteArray())) return id_PACE_ECDH_GM_AES_CBC_CMAC_192_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_GM_AES_CBC_CMAC_256.toByteArray())) return id_PACE_ECDH_GM_AES_CBC_CMAC_256_STRING;
		
		return null;
	}

	@Override
	public String getIdString() {
		return getStringRepresentation(oidByteArray);
	}
}
