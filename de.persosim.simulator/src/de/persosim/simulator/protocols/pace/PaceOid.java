package de.persosim.simulator.protocols.pace;

import java.security.InvalidParameterException;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.crypto.CryptoSupport;
import de.persosim.simulator.crypto.CryptoSupportAes;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.utils.HexString;

@XmlRootElement
public class PaceOid extends Oid implements Pace {
	public static final int HASHCODEMULTIPLICATOR = 3;
	
	protected String idString;
	
	/*----------------------------------------------------------------*/
	
	public PaceOid() {
	}
	
	/**
	 * This constructor constructs a {@link PaceOid} based on a byte array representation of a Pace OID.
	 * @param oidByteArray the byte array representation of a Pace OID
	 */
	public PaceOid(byte[] oidByteArray) {
		super(oidByteArray);
		
		//check if provided OID is indeed PaceOid
		idString = getStringRepresentation(oidByteArray);
		if(idString == null) {
			throw new IllegalArgumentException("PACE OID " + HexString.encode(oidByteArray) + " is invalid or unknown (not supported)");
		}
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
		case Pace.DH_GM:
			return "DH";
		case Pace.ECDH_GM:
			return "ECDH";
		case Pace.DH_IM:
			return "DH";
		case Pace.ECDH_IM:
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
		case Pace.DH_GM:
			return "GM";
		case Pace.ECDH_GM:
			return "GM";
		case Pace.DH_IM:
			return "IM";
		case Pace.ECDH_IM:
			return "IM";
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
	
	/**
	 * This method returns the {@link CryptoSupport} object that provides support for the selected symmetric cipher.
	 * @return the {@link CryptoSupport} object that provides support for the selected symmetric cipher
	 */
	protected CryptoSupport cryptoSupportCache = null;
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
	 * This method returns the symmetric cipher algorithm's name, mode and padding in the form cipher/mode/padding.
	 * @return the symmetric cipher algorithm's name, mode and padding
	 */
	public String getSymmetricCipherAlgorithmNameModePadding() {
		switch (this.getSymmetricCipherAndKeySizeAsByte()) {
		case Pace.DES3_CBC_CBC:
			return "DESede/CBC/NoPadding";
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
		case Pace.DES3_CBC_CBC:
			/* 
			 * this is the actual key length,
			 * effective key length is only 14 bytes as 1 bit per byte is parity information
			 */
			return 16;
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
		case "DESede":
			return "ISO9797ALG3WITHISO7816-4PADDING";
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
	
	public static String getStringRepresentation(byte[] oidByteArray) {
		if (Arrays.equals(oidByteArray, id_PACE_DH_GM_3DES_CBC_CBC))     return id_PACE_DH_GM_3DES_CBC_CBC_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_DH_GM_AES_CBC_CMAC_128)) return id_PACE_DH_GM_AES_CBC_CMAC_128_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_DH_GM_AES_CBC_CMAC_192)) return id_PACE_DH_GM_AES_CBC_CMAC_192_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_DH_GM_AES_CBC_CMAC_256)) return id_PACE_DH_GM_AES_CBC_CMAC_256_STRING;
		
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_GM_3DES_CBC_CBC))     return id_PACE_ECDH_GM_3DES_CBC_CBC_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_GM_AES_CBC_CMAC_128)) return id_PACE_ECDH_GM_AES_CBC_CMAC_128_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_GM_AES_CBC_CMAC_192)) return id_PACE_ECDH_GM_AES_CBC_CMAC_192_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_GM_AES_CBC_CMAC_256)) return id_PACE_ECDH_GM_AES_CBC_CMAC_256_STRING;
		 
		if (Arrays.equals(oidByteArray, id_PACE_DH_IM_3DES_CBC_CBC))     return id_PACE_DH_IM_3DES_CBC_CBC_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_DH_IM_AES_CBC_CMAC_128)) return id_PACE_DH_IM_AES_CBC_CMAC_128_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_DH_IM_AES_CBC_CMAC_192)) return id_PACE_DH_IM_AES_CBC_CMAC_192_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_DH_IM_AES_CBC_CMAC_256)) return id_PACE_DH_IM_AES_CBC_CMAC_256_STRING;
		 
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_IM_3DES_CBC_CBC))     return id_PACE_ECDH_IM_3DES_CBC_CBC_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_IM_AES_CBC_CMAC_128)) return id_PACE_ECDH_IM_AES_CBC_CMAC_128_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_IM_AES_CBC_CMAC_192)) return id_PACE_ECDH_IM_AES_CBC_CMAC_192_STRING;
		if (Arrays.equals(oidByteArray, id_PACE_ECDH_IM_AES_CBC_CMAC_256)) return id_PACE_ECDH_IM_AES_CBC_CMAC_256_STRING;
		
		return null;
	}

	@Override
	public String getIdString() {
		return idString;
	}
}
