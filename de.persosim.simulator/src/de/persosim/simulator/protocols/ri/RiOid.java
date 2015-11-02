package de.persosim.simulator.protocols.ri;

import java.security.GeneralSecurityException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.KeyAgreement;

import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ca.Ca;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.HexString;

/**
 * This class implements functionalities for OIDs used in the {@link RiProtocol}.
 * 
 * @author mboonk
 * 
 */
public class RiOid extends Oid implements Ri, TlvConstants {

	private String idString;

	public RiOid(byte[] byteArrayRepresentation) {
		super(byteArrayRepresentation);

		// check if provided OID is indeed RiOID
		idString = getStringRepresentation(oidByteArray);
		if (idString == null) {
			throw new IllegalArgumentException("RI OID "
					+ HexString.encode(oidByteArray)
					+ " is invalid or unknown (not supported)");
		}
	}

	public String getStringRepresentation(byte[] oidByteArray) {
		if (Arrays.equals(oidByteArray, id_RI_DH))
			return id_RI_DH_STRING;
		if (Arrays.equals(oidByteArray, id_RI_ECDH))
			return id_RI_ECDH_STRING;

		if (Arrays.equals(oidByteArray, id_RI_DH_SHA_1))
			return id_RI_DH_SHA_1_STRING;
		if (Arrays.equals(oidByteArray, id_RI_DH_SHA_224))
			return id_RI_DH_SHA_224_STRING;
		if (Arrays.equals(oidByteArray, id_RI_DH_SHA_256))
			return id_RI_DH_SHA_256_STRING;
		if (Arrays.equals(oidByteArray, id_RI_DH_SHA_384))
			return id_RI_DH_SHA_384_STRING;
		if (Arrays.equals(oidByteArray, id_RI_DH_SHA_512))
			return id_RI_DH_SHA_512_STRING;

		if (Arrays.equals(oidByteArray, id_RI_ECDH_SHA_1))
			return id_RI_ECDH_SHA_1_STRING;
		if (Arrays.equals(oidByteArray, id_RI_ECDH_SHA_224))
			return id_RI_ECDH_SHA_224_STRING;
		if (Arrays.equals(oidByteArray, id_RI_ECDH_SHA_256))
			return id_RI_ECDH_SHA_256_STRING;
		if (Arrays.equals(oidByteArray, id_RI_ECDH_SHA_384))
			return id_RI_ECDH_SHA_384_STRING;
		if (Arrays.equals(oidByteArray, id_RI_ECDH_SHA_512))
			return id_RI_ECDH_SHA_512_STRING;
		return null;
	}

	@Override
	public String getIdString() {
		return idString;
	}

	/**
	 * This method returns the OID's byte indicating the key agreement
	 * 
	 * @return the OID's byte indicating the key agreement
	 */
	public byte getKeyAgreementAsByte() {
		return this.oidByteArray[8];
	}

	public byte getHashAsByte() {
		return this.oidByteArray[9];
	}

	/**
	 * @return the DH or ECDH {@link KeyAgreement} instance according to this
	 *         OID
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public KeyAgreement getKeyAgreement() throws NoSuchAlgorithmException,
			NoSuchProviderException {
		switch (this.getKeyAgreementAsByte()) {
		case Ca.DH:
			return KeyAgreement.getInstance("DH", Crypto.getCryptoProvider());
		case Ca.ECDH:
			return KeyAgreement.getInstance("ECDH", Crypto.getCryptoProvider());
		default:
			throw new InvalidParameterException(
					"no or invalid key agreement selected");
		}
	}

	/**
	 * @return the {@link MessageDigest} that is to be used according to this
	 *         OID
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public MessageDigest getHash() throws NoSuchAlgorithmException,
			NoSuchProviderException {
		switch (getHashAsByte()) {
		case SHA_1:
			return MessageDigest.getInstance("SHA-1",
					Crypto.getCryptoProvider());
		case SHA_224:
			return MessageDigest.getInstance("SHA-224",
					Crypto.getCryptoProvider());
		case SHA_256:
			return MessageDigest.getInstance("SHA-256",
					Crypto.getCryptoProvider());
		case SHA_384:
			return MessageDigest.getInstance("SHA-384",
					Crypto.getCryptoProvider());
		case SHA_512:
			return MessageDigest.getInstance("SHA-512",
					Crypto.getCryptoProvider());
		default:
			throw new InvalidParameterException(
					"no or invalid hash function selected");
		}
	}
	
	/**
	 * This method parses a public key given as TLV-encoded data.
	 * 
	 * @param publicKeyData
	 *            data to be parsed
	 * @return the {@link PublicKey} instance
	 * @throws GeneralSecurityException
	 */
	public PublicKey parsePublicKey(ConstructedTlvDataObject publicKeyData)
			throws GeneralSecurityException {
		if (getIdString().contains("ECDH")) {
			return CryptoUtil.parsePublicKeyEc(publicKeyData, CryptoUtil.parseParameterSpecEc(publicKeyData));
		}
		return null;
	}
}
