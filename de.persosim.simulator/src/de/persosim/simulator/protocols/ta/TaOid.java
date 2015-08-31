package de.persosim.simulator.protocols.ta;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

//XXX MBK complete this class and extract according methods from TR03110
public class TaOid extends Oid implements Tr03110 {
	public final static TaOid id_TA                  = new TaOid(Utils.appendBytes(id_BSI, new byte[]{0x02, 0x02, 0x02}), "id-TA");
	
	public static final TaOid id_TA_RSA              = new TaOid(Utils.appendBytes(id_TA.oidByteArray,     (byte) 0x01),  "id-TA-RSA");
	public static final TaOid id_TA_RSA_v1_5_SHA_1   = new TaOid(Utils.appendBytes(id_TA_RSA.oidByteArray, (byte) 0x01),  "id-TA-RSA-v1-5-SHA-1");
	public static final TaOid id_TA_RSA_v1_5_SHA_256 = new TaOid(Utils.appendBytes(id_TA_RSA.oidByteArray, (byte) 0x02),  "id-TA-RSA-v1-5-SHA-256");
	public static final TaOid id_TA_RSA_PSS_SHA_1    = new TaOid(Utils.appendBytes(id_TA_RSA.oidByteArray, (byte) 0x03),  "id-TA-RSA-PSS-SHA-1");
	public static final TaOid id_TA_RSA_PSS_SHA_256  = new TaOid(Utils.appendBytes(id_TA_RSA.oidByteArray, (byte) 0x04),  "id-TA-RSA-PSS-SHA-256");
	public static final TaOid id_TA_RSA_v1_5_SHA_512 = new TaOid(Utils.appendBytes(id_TA_RSA.oidByteArray, (byte) 0x05),  "id-TA-RSA-v1-5-SHA-512");
	public static final TaOid id_TA_RSA_PSS_SHA_512  = new TaOid(Utils.appendBytes(id_TA_RSA.oidByteArray, (byte) 0x06),  "id-TA-RSA-PSS-SHA-512");

	public static final TaOid id_TA_ECDSA            = new TaOid(Utils.appendBytes(id_TA.oidByteArray,       (byte) 0x02), "id-TA-ECDSA");
	public static final TaOid id_TA_ECDSA_SHA_1      = new TaOid(Utils.appendBytes(id_TA_ECDSA.oidByteArray, (byte) 0x01), "id-TA-ECDSA-SHA-1");
	public static final TaOid id_TA_ECDSA_SHA_224    = new TaOid(Utils.appendBytes(id_TA_ECDSA.oidByteArray, (byte) 0x02), "id-TA-ECDSA-SHA-224");
	public static final TaOid id_TA_ECDSA_SHA_256    = new TaOid(Utils.appendBytes(id_TA_ECDSA.oidByteArray, (byte) 0x03), "id-TA-ECDSA-SHA-256");
	public static final TaOid id_TA_ECDSA_SHA_384    = new TaOid(Utils.appendBytes(id_TA_ECDSA.oidByteArray, (byte) 0x04), "id-TA-ECDSA-SHA-384");
	public static final TaOid id_TA_ECDSA_SHA_512    = new TaOid(Utils.appendBytes(id_TA_ECDSA.oidByteArray, (byte) 0x05), "id-TA-ECDSA-SHA-512");

	// Auxiliary data verification
	public final static TaOid id_AuxiliaryData       = new TaOid(Utils.appendBytes(id_BSI, new byte[]{0x03, 0x01, 0x04}), "id-AuxiliaryData");
	
	public static final TaOid id_PSM_MESSAGE         = new TaOid(Utils.appendBytes(id_AuxiliaryData.oidByteArray, (byte) 0x04), "id-PSM-Message");
	
	public static final TaOid id_DateOfBirth         = new TaOid(Utils.appendBytes(id_AuxiliaryData.oidByteArray, (byte) 0x01), "id-DateOfBirth");
	public static final TaOid id_DateOfExpiry        = new TaOid(Utils.appendBytes(id_AuxiliaryData.oidByteArray, (byte) 0x02), "id-DateOfExpiry");
	public static final TaOid id_CommunityID         = new TaOid(Utils.appendBytes(id_AuxiliaryData.oidByteArray, (byte) 0x03), "id-CommunityID");
	
	// terminal types
	public final static TaOid id_Roles               = new TaOid(Utils.appendBytes(id_BSI, new byte[]{0x03, 0x01, 0x02}), "id-roles");
	
	public static final TaOid id_IS                  = new TaOid(Utils.appendBytes(id_Roles.oidByteArray, (byte) 0x01), "id-IS");
	public static final TaOid id_AT                  = new TaOid(Utils.appendBytes(id_Roles.oidByteArray, (byte) 0x02), "id-AT");
	public static final TaOid id_ST                  = new TaOid(Utils.appendBytes(id_Roles.oidByteArray, (byte) 0x03), "id-ST"); 
	
	// certificate extensions

	public final static TaOid id_Extensions          = new TaOid(Utils.appendBytes(id_BSI, new byte[]{0x03, 0x01, 0x03}), "id-extensions");
	
	public static final TaOid id_Description         = new TaOid(Utils.appendBytes(id_Extensions.oidByteArray, (byte) 0x01), "id-description");
	public static final TaOid id_Sector              = new TaOid(Utils.appendBytes(id_Extensions.oidByteArray, (byte) 0x02), "id-sector");
	
	private static Set<TaOid> allKnownTaOids= new HashSet<>();
	static {
		//add all static fields with name id_* to allKnownTaOids
		Field[] fields = TaOid.class.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);

			if ((f.getName().startsWith("id_"))
					&& Modifier.isStatic(f.getModifiers())
					&& Modifier.isFinal(f.getModifiers())
					&& f.getType().equals(TaOid.class)) {
				try {
					allKnownTaOids.add((TaOid) f.get(null));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// ignore the current field
				}
			}
		}

	}
	
	private String idString;

	public TaOid() {
	}
	
	private TaOid(byte[] oidByteArray, String idString) {
		super(oidByteArray);
		this.idString = idString;
	}

	/**
	 * This constructor constructs a {@link TaOid} based on a byte array representation.
	 * @param oidByteArray the byte array representation
	 */
	public TaOid(byte[] oidByteArray) {
		super(oidByteArray);
		
		idString = getStringRepresentation (oidByteArray);
		if(idString == null) {
			throw new IllegalArgumentException("TA OID " + HexString.encode(oidByteArray) + " is invalid or unknown (not supported)");
		}
	}
	
	/**
	 * @see Oid#getIdString()
	 * @return common name of OID or null if parameter does not encode a TaOid
	 */
	public static String getStringRepresentation(byte[] oidByteArray) {
		for (TaOid curTaOid : allKnownTaOids) {
			if (Arrays.equals(curTaOid.oidByteArray, oidByteArray)) {
				return curTaOid.idString;
			}
		}
		return null;
	}
	
	@Override
	public String getIdString() {
		return idString;
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
	public Signature getSignature() throws NoSuchAlgorithmException, NoSuchProviderException {
		if (equals(TaOid.id_TA_RSA_v1_5_SHA_1)){
			return Signature.getInstance("SHA1withRSA", Crypto.getCryptoProvider());
		} else if (equals(TaOid.id_TA_RSA_v1_5_SHA_256)){
			return Signature.getInstance("SHA256withRSA", Crypto.getCryptoProvider());
		} else if (equals(TaOid.id_TA_RSA_v1_5_SHA_512)){
			return Signature.getInstance("SHA512withRSA", Crypto.getCryptoProvider());
		} else if (equals(TaOid.id_TA_RSA_PSS_SHA_1)){
			return Signature.getInstance("SHA1withRSA/PSS", Crypto.getCryptoProvider());
		} else if (equals(TaOid.id_TA_RSA_PSS_SHA_256)){
			return Signature.getInstance("SHA256withRSA/PSS", Crypto.getCryptoProvider());
		} else if (equals(TaOid.id_TA_RSA_PSS_SHA_512)){
			return Signature.getInstance("SHA512withRSA/PSS", Crypto.getCryptoProvider());
		} else if (equals(TaOid.id_TA_ECDSA_SHA_1)){
			return Signature.getInstance("SHA1withECDSA", Crypto.getCryptoProvider());
		} else if (equals(TaOid.id_TA_ECDSA_SHA_224)){
			return Signature.getInstance("SHA224withECDSA", Crypto.getCryptoProvider());
		} else if (equals(TaOid.id_TA_ECDSA_SHA_256)){
			return Signature.getInstance("SHA256withECDSA", Crypto.getCryptoProvider());
		} else if (equals(TaOid.id_TA_ECDSA_SHA_384)){
			return Signature.getInstance("SHA384withECDSA", Crypto.getCryptoProvider());
		} else if (equals(TaOid.id_TA_ECDSA_SHA_512)){
			return Signature.getInstance("SHA512withECDSA", Crypto.getCryptoProvider());
		}
		return null;
	}
	
}
