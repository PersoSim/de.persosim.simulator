package de.persosim.simulator.protocols.ta;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.util.Arrays;

import org.globaltester.cryptoprovider.Crypto;

import de.persosim.simulator.crypto.certificates.CvOid;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class TaOid extends GenericOid implements Tr03110, CvOid {
	public static final Oid id_TA                  = new GenericOid(id_BSI, new byte[]{0x02, 0x02, 0x02});
	public static final String ID_TA_STRING = "id-TA";
	
	public static final TaOid id_TA_RSA              = new TaOid(id_TA,     (byte) 0x01);
	public static final TaOid id_TA_RSA_v1_5_SHA_1   = new TaOid(id_TA_RSA, (byte) 0x01);
	public static final TaOid id_TA_RSA_v1_5_SHA_256 = new TaOid(id_TA_RSA, (byte) 0x02);
	public static final TaOid id_TA_RSA_PSS_SHA_1    = new TaOid(id_TA_RSA, (byte) 0x03);
	public static final TaOid id_TA_RSA_PSS_SHA_256  = new TaOid(id_TA_RSA, (byte) 0x04);
	public static final TaOid id_TA_RSA_v1_5_SHA_512 = new TaOid(id_TA_RSA, (byte) 0x05);
	public static final TaOid id_TA_RSA_PSS_SHA_512  = new TaOid(id_TA_RSA, (byte) 0x06);
	
	public static final String id_TA_RSA_STRING              = "id-TA-RSA";
	public static final String id_TA_RSA_v1_5_SHA_1_STRING   = "id-TA-RSA-v1-5-SHA-1";
	public static final String id_TA_RSA_v1_5_SHA_256_STRING = "id-TA-RSA-v1-5-SHA-256";
	public static final String id_TA_RSA_PSS_SHA_1_STRING    = "id-TA-RSA-PSS-SHA-1";
	public static final String id_TA_RSA_PSS_SHA_256_STRING  = "id-TA-RSA-PSS-SHA-256";
	public static final String id_TA_RSA_v1_5_SHA_512_STRING = "id-TA-RSA-v1-5-SHA-512";
	public static final String id_TA_RSA_PSS_SHA_512_STING   = "id-TA-RSA-PSS-SHA-512";

	public static final TaOid id_TA_ECDSA            = new TaOid(id_TA,       (byte) 0x02);
	public static final TaOid id_TA_ECDSA_SHA_1      = new TaOid(id_TA_ECDSA, (byte) 0x01);
	public static final TaOid id_TA_ECDSA_SHA_224    = new TaOid(id_TA_ECDSA, (byte) 0x02);
	public static final TaOid id_TA_ECDSA_SHA_256    = new TaOid(id_TA_ECDSA, (byte) 0x03);
	public static final TaOid id_TA_ECDSA_SHA_384    = new TaOid(id_TA_ECDSA, (byte) 0x04);
	public static final TaOid id_TA_ECDSA_SHA_512    = new TaOid(id_TA_ECDSA, (byte) 0x05);
	
	public static final String id_TA_ECDSA_STRING            = "id-TA-ECDSA";
	public static final String id_TA_ECDSA_SHA_1_STRING      = "id-TA-ECDSA-SHA-1";
	public static final String id_TA_ECDSA_SHA_224_STRING    = "id-TA-ECDSA-SHA-224";
	public static final String id_TA_ECDSA_SHA_256_STRING    = "id-TA-ECDSA-SHA-256";
	public static final String id_TA_ECDSA_SHA_384_STRING    = "id-TA-ECDSA-SHA-384";
	public static final String id_TA_ECDSA_SHA_512_STRING    = "id-TA-ECDSA-SHA-512";
	
	
	/**
	 * This constructor constructs a {@link TaOid} based on a byte array representation.
	 * @param oidByteArray the byte array representation
	 */
	public TaOid(byte[] oidByteArray) {
		super(oidByteArray);
		
		if(!startsWithPrefix(id_TA)) {
			throw new IllegalArgumentException("TA OID " + HexString.encode(oidByteArray) + " is invalid or unknown (not supported)");
		}
	}
	
	public TaOid(Oid prefix, byte... suffix) {
		this(Utils.appendBytes(prefix.toByteArray(), suffix));
	}
	
	/**
	 * @see Oid#getIdString()
	 * @return common name of OID or null if parameter does not encode a TaOid
	 */
	public static String getStringRepresentation(byte[] oidByteArray) {
		if (Arrays.equals(oidByteArray, id_TA_RSA              .toByteArray())) return id_TA_RSA_STRING             ;
		if (Arrays.equals(oidByteArray, id_TA_RSA_v1_5_SHA_1   .toByteArray())) return id_TA_RSA_v1_5_SHA_1_STRING  ;
		if (Arrays.equals(oidByteArray, id_TA_RSA_v1_5_SHA_256 .toByteArray())) return id_TA_RSA_v1_5_SHA_256_STRING;
		if (Arrays.equals(oidByteArray, id_TA_RSA_PSS_SHA_1    .toByteArray())) return id_TA_RSA_PSS_SHA_1_STRING   ;
		if (Arrays.equals(oidByteArray, id_TA_RSA_PSS_SHA_256  .toByteArray())) return id_TA_RSA_PSS_SHA_256_STRING ;
		if (Arrays.equals(oidByteArray, id_TA_RSA_v1_5_SHA_512 .toByteArray())) return id_TA_RSA_v1_5_SHA_512_STRING;
		if (Arrays.equals(oidByteArray, id_TA_RSA_PSS_SHA_512  .toByteArray())) return id_TA_RSA_PSS_SHA_512_STING  ;

		if (Arrays.equals(oidByteArray, id_TA_ECDSA            .toByteArray())) return id_TA_ECDSA_STRING        ; 
		if (Arrays.equals(oidByteArray, id_TA_ECDSA_SHA_1      .toByteArray())) return id_TA_ECDSA_SHA_1_STRING  ; 
		if (Arrays.equals(oidByteArray, id_TA_ECDSA_SHA_224    .toByteArray())) return id_TA_ECDSA_SHA_224_STRING; 
		if (Arrays.equals(oidByteArray, id_TA_ECDSA_SHA_256    .toByteArray())) return id_TA_ECDSA_SHA_256_STRING; 
		if (Arrays.equals(oidByteArray, id_TA_ECDSA_SHA_384    .toByteArray())) return id_TA_ECDSA_SHA_384_STRING; 
		if (Arrays.equals(oidByteArray, id_TA_ECDSA_SHA_512    .toByteArray())) return id_TA_ECDSA_SHA_512_STRING; 
		
		
		return null;
	}
	
	@Override
	public String getIdString() {
		return getStringRepresentation(oidByteArray);
	}
	
	public String getHashAlgorithmName() {
		if (Arrays.equals(oidByteArray, id_TA_ECDSA_SHA_1.oidByteArray) || Arrays.equals(oidByteArray, id_TA_RSA_PSS_SHA_1.oidByteArray) || Arrays.equals(oidByteArray, id_TA_RSA_v1_5_SHA_1.oidByteArray)){
			return "SHA-1";
		}
		if (Arrays.equals(oidByteArray, id_TA_ECDSA_SHA_224.oidByteArray)){
			return "SHA-224";
		}
		if (Arrays.equals(oidByteArray, id_TA_ECDSA_SHA_256.oidByteArray) || Arrays.equals(oidByteArray, id_TA_RSA_PSS_SHA_256.oidByteArray) || Arrays.equals(oidByteArray, id_TA_RSA_v1_5_SHA_256.oidByteArray)){
			return "SHA-256";
		}
		if (Arrays.equals(oidByteArray, id_TA_ECDSA_SHA_384.oidByteArray)){
			return "SHA-384";
		}
		if (Arrays.equals(oidByteArray, id_TA_ECDSA_SHA_512.oidByteArray) || Arrays.equals(oidByteArray, id_TA_RSA_PSS_SHA_512.oidByteArray) || Arrays.equals(oidByteArray, id_TA_RSA_v1_5_SHA_512.oidByteArray)){
			return "SHA-512";
		}
		throw new IllegalArgumentException("unknown or invalid algorithm");
	}



	/**
	 * This method finds a signature object fitting this Oid as defined in
	 * TR-03110 v2.10.
	 * 
	 * @return an instance of a {@link Signature} object
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */	
	public Signature getSignature() throws NoSuchAlgorithmException {
		String signatureString = getSignatureString();
		if(signatureString != null) {
			return Signature.getInstance(signatureString, Crypto.getCryptoProvider());
		} else{
			return null;
		}
	}
	
	public String getSignatureString() {
		if (equals(TaOid.id_TA_RSA_v1_5_SHA_1)){
			return "SHA1withRSA";
		} else if (equals(TaOid.id_TA_RSA_v1_5_SHA_256)){
			return "SHA256withRSA";
		} else if (equals(TaOid.id_TA_RSA_v1_5_SHA_512)){
			return "SHA512withRSA";
		} else if (equals(TaOid.id_TA_RSA_PSS_SHA_1)){
			return "SHA1withRSA/PSS";
		} else if (equals(TaOid.id_TA_RSA_PSS_SHA_256)){
			return "SHA256withRSA/PSS";
		} else if (equals(TaOid.id_TA_RSA_PSS_SHA_512)){
			return "SHA512withRSA/PSS";
		} else if (equals(TaOid.id_TA_ECDSA_SHA_1)){
			return "SHA1withECDSA";
		} else if (equals(TaOid.id_TA_ECDSA_SHA_224)){
			return "SHA224withECDSA";
		} else if (equals(TaOid.id_TA_ECDSA_SHA_256)){
			return "SHA256withECDSA";
		} else if (equals(TaOid.id_TA_ECDSA_SHA_384)){
			return "SHA384withECDSA";
		} else if (equals(TaOid.id_TA_ECDSA_SHA_512)){
			return "SHA512withECDSA";
		}
		return null;
	}

	@Override
	public String getKeyType() {
		if (equals(TaOid.id_TA_RSA_v1_5_SHA_1) || equals(TaOid.id_TA_RSA_v1_5_SHA_256)
				|| equals(TaOid.id_TA_RSA_v1_5_SHA_512) || equals(TaOid.id_TA_RSA_PSS_SHA_1)
				|| equals(TaOid.id_TA_RSA_PSS_SHA_256) || equals(TaOid.id_TA_RSA_PSS_SHA_512)) {
			return "RSA";
		} else if (equals(TaOid.id_TA_ECDSA_SHA_1) || equals(TaOid.id_TA_ECDSA_SHA_224)
				|| equals(TaOid.id_TA_ECDSA_SHA_256) || equals(TaOid.id_TA_ECDSA_SHA_384)
				|| equals(TaOid.id_TA_ECDSA_SHA_512)) {
			return "EC";
		}
		return null;
	}
}
