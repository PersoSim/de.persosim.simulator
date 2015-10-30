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
import de.persosim.simulator.crypto.certificates.CvOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

//XXX MBK complete this class and extract according methods from TR03110
public class TaOid extends Oid implements Tr03110, CvOid {
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
	public final static TaOid id_AuxiliaryData       = new TaOid(Utils.appendBytes(id_BSI, new byte[]{0x03, 0x01, 0x04}), "id-AuxiliaryData");      // TODO no native TA OID, move
	
	public static final TaOid id_PSM_MESSAGE         = new TaOid(Utils.appendBytes(id_AuxiliaryData.oidByteArray, (byte) 0x04), "id-PSM-Message");  // TODO no native TA OID, move
	
	public static final TaOid id_DateOfBirth         = new TaOid(Utils.appendBytes(id_AuxiliaryData.oidByteArray, (byte) 0x01), "id-DateOfBirth");  // TODO no native TA OID, move
	public static final TaOid id_DateOfExpiry        = new TaOid(Utils.appendBytes(id_AuxiliaryData.oidByteArray, (byte) 0x02), "id-DateOfExpiry"); // TODO no native TA OID, move
	public static final TaOid id_CommunityID         = new TaOid(Utils.appendBytes(id_AuxiliaryData.oidByteArray, (byte) 0x03), "id-CommunityID");  // TODO no native TA OID, move
	
	// terminal types
	public final static TaOid id_Roles               = new TaOid(Utils.appendBytes(id_BSI, new byte[]{0x03, 0x01, 0x02}), "id-roles");              // TODO no native TA OID, move
	
	public static final TaOid id_IS                  = new TaOid(Utils.appendBytes(id_Roles.oidByteArray, (byte) 0x01), "id-IS");                   // TODO no native TA OID, move
	public static final TaOid id_AT                  = new TaOid(Utils.appendBytes(id_Roles.oidByteArray, (byte) 0x02), "id-AT");                   // TODO no native TA OID, move
	public static final TaOid id_ST                  = new TaOid(Utils.appendBytes(id_Roles.oidByteArray, (byte) 0x03), "id-ST");                   // TODO no native TA OID, move
	
	public static final TaOid id_eIDAccess           = new TaOid(Utils.appendBytes(id_AT.oidByteArray, (byte) 0x01), "id-eIDAccess");               // TODO no native TA OID, move
	public static final TaOid id_specialFunctions    = new TaOid(Utils.appendBytes(id_AT.oidByteArray, (byte) 0x02), "id-specialFunctions");        // TODO no native TA OID, move
	
	// certificate extensions

	public final static TaOid id_Extensions          = new TaOid(Utils.appendBytes(id_BSI, new byte[]{0x03, 0x01, 0x03}), "id-extensions");         // TODO no native TA OID, move
	
	public static final TaOid id_Description         = new TaOid(Utils.appendBytes(id_Extensions.oidByteArray, (byte) 0x01), "id-description");     // TODO no native TA OID, move
	public static final TaOid id_Sector              = new TaOid(Utils.appendBytes(id_Extensions.oidByteArray, (byte) 0x02), "id-sector");          // TODO no native TA OID, move
	
//	id-CVCExtension OBJECT IDENTIFIER ::= {
//			iso(1) member-body(2) f(250) type-org(1) anssi(223) eIDAStoken(1001) 1
//	}
	public final static TaOid id_CVCExtension       = new TaOid(HexString.toByteArray("01 02 FA 01 DF 03E9 01"), "id-CVCExtension");                // TODO no native TA OID, move
	public final static TaOid id_ERAspecific        = new TaOid(Utils.appendBytes(id_CVCExtension.oidByteArray, (byte) 0x03), "id-ERAspecific");    // TODO no native TA OID, move
	
	
	
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
	
}
