package de.persosim.simulator.protocols.ca;

import java.security.InvalidParameterException;
import java.util.Arrays;

import de.persosim.simulator.crypto.CryptoSupport;
import de.persosim.simulator.crypto.CryptoSupportAes;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.utils.HexString;

public class CaOid extends GenericOid implements Ca {
	
	private String idString;
	
	/**
	 * This constructor constructs a {@link CaOid} based on a byte array representation of a CA OID.
	 * @param oidByteArray the byte array representation of a CA OID
	 */
	public CaOid(byte[] oidByteArray) {
		super(oidByteArray);
		
		//check if provided OID is indeed CaOID
		idString =getStringRepresentation (oidByteArray);
		if(idString == null) {
			throw new IllegalArgumentException("CA OID " + HexString.encode(oidByteArray) + " is invalid or unknown (not supported)");
		}
	}
	
	
	
	/*----------------------------------------------------------------*/
	
	/**
	 * This method returns the OID's byte indicating the key agreement
	 * @return the OID's byte indicating the key agreement
	 */
	public byte getKeyAgreementAsByte() {
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
		switch (this.getKeyAgreementAsByte()) {
		case Ca.DH:
			return "DH";
		case Ca.ECDH:
			return "ECDH";
		default:
			throw new InvalidParameterException("no or invalid key agreement selected");
		}
	}
	
	protected CryptoSupport cryptoSupportCache = null;
	/**
	 * This method returns the {@link CryptoSupport} object matching the crypto systems indicated by the OID.
	 * @return the {@link CryptoSupport} object matching the crypto systems indicated by the OID
	 */
	public CryptoSupport getCryptoSupport() {
		if(cryptoSupportCache == null) {
			String cipherName = getSymmetricCipherAlgorithmNameModePadding();
			String macName = getMacName();
			
			switch (CryptoUtil.getCipherNameAsString(cipherName)) {
			case "AES":
				cryptoSupportCache = new CryptoSupportAes(cipherName, macName);
				break;
			default:
				throw new IllegalArgumentException("algorithm " + cipherName + " is unknown or not supported");
			}
		}
		
		return cryptoSupportCache;
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * This method returns the used cipher algorithm name including name, mode
	 * of operation and used padding.
	 * 
	 * @return the used cipher algorithm name, mode and padding
	 */
	public String getSymmetricCipherAlgorithmNameModePadding() {
		switch (this.getSymmetricCipherAndKeySizeAsByte()) {
		case Ca.AES_CBC_CMAC_128:
			return "AES/CBC/NoPadding";
		case Ca.AES_CBC_CMAC_192:
			return "AES/CBC/NoPadding";
		case Ca.AES_CBC_CMAC_256:
			return "AES/CBC/NoPadding";
		default:
			throw new InvalidParameterException("no or invalid symmetric cipher selected");
		}
	}
	
	/**
	 * This method returns the used cipher algorithm's name, i.e. "AES".
	 * @return the used cipher algorithm's name
	 */
	public String getSymmetricCipherAlgorithmName() {
		return CryptoUtil.getCipherNameAsString(this.getSymmetricCipherAlgorithmNameModePadding());
	}
	
	/**
	 * This method returns the used cipher algorithm's mode of operation, i.e. "CBC".
	 * @return the used cipher algorithm's mode of operation
	 */
	public String getSymmetricCipherAlgorithmMode() {
		return CryptoUtil.getCipherAlgorithmModeAsString(this.getSymmetricCipherAlgorithmNameModePadding());
	}
	
	/**
	 * This method returns the used cipher algorithm's padding, i.e. "NoPadding".
	 * @return the used cipher algorithm's padding
	 */
	public String getSymmetricCipherAlgorithmPadding() {
		return CryptoUtil.getCipherAlgorithmPaddingAsString(this.getSymmetricCipherAlgorithmNameModePadding());
	}
	
	/**
	 * This method returns the key length in bytes to be used with the symmetric cipher implied by the OID.
	 * @return the key length in byte as indicated by the OID
	 */
	public int getSymmetricCipherKeyLengthInBytes() {
		switch (this.getSymmetricCipherAndKeySizeAsByte()) {
		case Ca.AES_CBC_CMAC_128:
			return 16;
		case Ca.AES_CBC_CMAC_192:
			return 24;
		case Ca.AES_CBC_CMAC_256:
			return 32;
		default:
			throw new InvalidParameterException("no or invalid symmetric cipher selected");
		}
	}
	
	/**
	 * This method returns the MAC name indicated by the OID.
	 * @return the MAC name indicated by the OID
	 */
	public String getMacName() {
		switch (this.getSymmetricCipherAlgorithmName()) {
		case "AES":
			return "aescmac";
		default:
			throw new InvalidParameterException("no or invalid mac selected");
		}
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * @see Oid#getIdString()
	 * @return common name of OID or null if parameter does not encode a CaOid
	 */
	public String getStringRepresentation(byte[] oidByteArray) {
		if (Arrays.equals(oidByteArray, id_CA_DH_AES_CBC_CMAC_128)) return id_CA_DH_AES_CBC_CMAC_128_STRING;
		if (Arrays.equals(oidByteArray, id_CA_DH_AES_CBC_CMAC_192)) return id_CA_DH_AES_CBC_CMAC_192_STRING;
		if (Arrays.equals(oidByteArray, id_CA_DH_AES_CBC_CMAC_256)) return id_CA_DH_AES_CBC_CMAC_256_STRING;
		 
		if (Arrays.equals(oidByteArray, id_CA_ECDH_AES_CBC_CMAC_128)) return id_CA_ECDH_AES_CBC_CMAC_128_STRING;
		if (Arrays.equals(oidByteArray, id_CA_ECDH_AES_CBC_CMAC_192)) return id_CA_ECDH_AES_CBC_CMAC_192_STRING;
		if (Arrays.equals(oidByteArray, id_CA_ECDH_AES_CBC_CMAC_256)) return id_CA_ECDH_AES_CBC_CMAC_256_STRING;
		
		return null;
	}
	
	@Override
	public String getIdString() {
		return idString;
	}
	
}
