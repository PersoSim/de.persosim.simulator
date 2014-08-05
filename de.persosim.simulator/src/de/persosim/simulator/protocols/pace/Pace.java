package de.persosim.simulator.protocols.pace;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.utils.Utils;

/**
 * This interface provides constants used in the context of the PACE protocol according to TR-03110.
 * @author slutters
 *
 */
public interface Pace extends Tr03110 {
	
	/* PACE OIDs according to TR-03110 v2.1 part 3 */
	
	/* PACE id */
//	                                                                                                      0x02 bsi-de protocols(2)
//	                                                                                                            0x02 smartcard(2)
//	                                                                                                                  0x04 4 pace protocol(4)
	
	public final static byte[] id_PACE                             = Utils.appendBytes(id_BSI, new byte[]{0x02, 0x02, 0x04});
	public final static String id_PACE_STRING                      = "id-PACE";
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	/* Key agreement & mapping */
	public final static byte DH_GM                                 = (byte) 0x01;
	public final static byte ECDH_GM                               = (byte) 0x02;
	public final static byte DH_IM                                 = (byte) 0x03;
	public final static byte ECDH_IM                               = (byte) 0x04;
	
	public final static byte[] KEY_AGREEMENT_AND_MAPPING           = new byte[]{DH_GM, ECDH_GM, DH_IM, ECDH_IM};
	
	/*--------------------------------------------------------------------------------*/
	
	/* Key agreement & mapping */
	public final static String DH_GM_STRING                        = "DH-GM";
	public final static String ECDH_GM_STRING                      = "ECDH-GM";
	public final static String DH_IM_STRING                        = "DH-IM";
	public final static String ECDH_IM_STRING                      = "ECDH-IM";
	
	public final static String[] KEY_AGREEMENT_AND_MAPPING_STRING  = new String[]{DH_GM_STRING, ECDH_GM_STRING, DH_IM_STRING, ECDH_IM_STRING};
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	/* DH-GM */
	public final static byte[] id_PACE_DH_GM_3DES_CBC_CBC          = Utils.appendBytes(id_PACE, DH_GM, DES3_CBC_CBC);
	public final static byte[] id_PACE_DH_GM_AES_CBC_CMAC_128      = Utils.appendBytes(id_PACE, DH_GM, AES_CBC_CMAC_128);
	public final static byte[] id_PACE_DH_GM_AES_CBC_CMAC_192      = Utils.appendBytes(id_PACE, DH_GM, AES_CBC_CMAC_192);
	public final static byte[] id_PACE_DH_GM_AES_CBC_CMAC_256      = Utils.appendBytes(id_PACE, DH_GM, AES_CBC_CMAC_256);
	
	/* ECDH-GM */
	public final static byte[] id_PACE_ECDH_GM_3DES_CBC_CBC        = Utils.appendBytes(id_PACE, ECDH_GM, DES3_CBC_CBC);
	public final static byte[] id_PACE_ECDH_GM_AES_CBC_CMAC_128    = Utils.appendBytes(id_PACE, ECDH_GM, AES_CBC_CMAC_128);
	public final static byte[] id_PACE_ECDH_GM_AES_CBC_CMAC_192    = Utils.appendBytes(id_PACE, ECDH_GM, AES_CBC_CMAC_192);
	public final static byte[] id_PACE_ECDH_GM_AES_CBC_CMAC_256    = Utils.appendBytes(id_PACE, ECDH_GM, AES_CBC_CMAC_256);
	
	/* DH-IM */
	public final static byte[] id_PACE_DH_IM_3DES_CBC_CBC          = Utils.appendBytes(id_PACE, DH_IM, DES3_CBC_CBC);
	public final static byte[] id_PACE_DH_IM_AES_CBC_CMAC_128      = Utils.appendBytes(id_PACE, DH_IM, AES_CBC_CMAC_128);
	public final static byte[] id_PACE_DH_IM_AES_CBC_CMAC_192      = Utils.appendBytes(id_PACE, DH_IM, AES_CBC_CMAC_192); 
	public final static byte[] id_PACE_DH_IM_AES_CBC_CMAC_256      = Utils.appendBytes(id_PACE, DH_IM, AES_CBC_CMAC_256); 
	
	/* ECDH-IM */
	public final static byte[] id_PACE_ECDH_IM_3DES_CBC_CBC        = Utils.appendBytes(id_PACE, ECDH_IM, DES3_CBC_CBC);
	public final static byte[] id_PACE_ECDH_IM_AES_CBC_CMAC_128    = Utils.appendBytes(id_PACE, ECDH_IM, AES_CBC_CMAC_128);
	public final static byte[] id_PACE_ECDH_IM_AES_CBC_CMAC_192    = Utils.appendBytes(id_PACE, ECDH_IM, AES_CBC_CMAC_192);
	public final static byte[] id_PACE_ECDH_IM_AES_CBC_CMAC_256    = Utils.appendBytes(id_PACE, ECDH_IM, AES_CBC_CMAC_256);
	
	/*--------------------------------------------------------------------------------*/
	
	public final static Oid OID_id_PACE          = new Oid(id_PACE);
	
	/* DH-GM */
	public final static PaceOid OID_id_PACE_DH_GM_3DES_CBC_CBC          = new PaceOid(id_PACE_DH_GM_3DES_CBC_CBC);
	public final static PaceOid OID_id_PACE_DH_GM_AES_CBC_CMAC_128      = new PaceOid(id_PACE_DH_GM_AES_CBC_CMAC_128);
	public final static PaceOid OID_id_PACE_DH_GM_AES_CBC_CMAC_192      = new PaceOid(id_PACE_DH_GM_AES_CBC_CMAC_192);
	public final static PaceOid OID_id_PACE_DH_GM_AES_CBC_CMAC_256      = new PaceOid(id_PACE_DH_GM_AES_CBC_CMAC_256);
	
	/* ECDH-GM */
	public final static PaceOid OID_id_PACE_ECDH_GM_3DES_CBC_CBC        = new PaceOid(id_PACE_ECDH_GM_3DES_CBC_CBC);
	public final static PaceOid OID_id_PACE_ECDH_GM_AES_CBC_CMAC_128    = new PaceOid(id_PACE_ECDH_GM_AES_CBC_CMAC_128);
	public final static PaceOid OID_id_PACE_ECDH_GM_AES_CBC_CMAC_192    = new PaceOid(id_PACE_ECDH_GM_AES_CBC_CMAC_192);
	public final static PaceOid OID_id_PACE_ECDH_GM_AES_CBC_CMAC_256    = new PaceOid(id_PACE_ECDH_GM_AES_CBC_CMAC_256);
	
	/* DH-IM */
	public final static PaceOid OID_id_PACE_DH_IM_3DES_CBC_CBC          = new PaceOid(id_PACE_DH_IM_3DES_CBC_CBC);
	public final static PaceOid OID_id_PACE_DH_IM_AES_CBC_CMAC_128      = new PaceOid(id_PACE_DH_IM_AES_CBC_CMAC_128);
	public final static PaceOid OID_id_PACE_DH_IM_AES_CBC_CMAC_192      = new PaceOid(id_PACE_DH_IM_AES_CBC_CMAC_192); 
	public final static PaceOid OID_id_PACE_DH_IM_AES_CBC_CMAC_256      = new PaceOid(id_PACE_DH_IM_AES_CBC_CMAC_256); 
	
	/* ECDH-IM */
	public final static PaceOid OID_id_PACE_ECDH_IM_3DES_CBC_CBC        = new PaceOid(id_PACE_ECDH_IM_3DES_CBC_CBC);
	public final static PaceOid OID_id_PACE_ECDH_IM_AES_CBC_CMAC_128    = new PaceOid(id_PACE_ECDH_IM_AES_CBC_CMAC_128);
	public final static PaceOid OID_id_PACE_ECDH_IM_AES_CBC_CMAC_192    = new PaceOid(id_PACE_ECDH_IM_AES_CBC_CMAC_192);
	public final static PaceOid OID_id_PACE_ECDH_IM_AES_CBC_CMAC_256    = new PaceOid(id_PACE_ECDH_IM_AES_CBC_CMAC_256);
	
	/*--------------------------------------------------------------------------------*/
	
	// {@link OidIdentifier}
	
	/* DH-GM */
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_DH_GM_3DES_CBC_CBC          = new OidIdentifier(OID_id_PACE_DH_GM_3DES_CBC_CBC);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_DH_GM_AES_CBC_CMAC_128      = new OidIdentifier(OID_id_PACE_DH_GM_AES_CBC_CMAC_128);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_DH_GM_AES_CBC_CMAC_192      = new OidIdentifier(OID_id_PACE_DH_GM_AES_CBC_CMAC_192);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_DH_GM_AES_CBC_CMAC_256      = new OidIdentifier(OID_id_PACE_DH_GM_AES_CBC_CMAC_256);
	
	/* ECDH-GM */
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_GM_3DES_CBC_CBC        = new OidIdentifier(OID_id_PACE_ECDH_GM_3DES_CBC_CBC);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_GM_AES_CBC_CMAC_128    = new OidIdentifier(OID_id_PACE_ECDH_GM_AES_CBC_CMAC_128);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_GM_AES_CBC_CMAC_192    = new OidIdentifier(OID_id_PACE_ECDH_GM_AES_CBC_CMAC_192);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_GM_AES_CBC_CMAC_256    = new OidIdentifier(OID_id_PACE_ECDH_GM_AES_CBC_CMAC_256);
	
	/* DH-IM */
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_DH_IM_3DES_CBC_CBC          = new OidIdentifier(OID_id_PACE_DH_IM_3DES_CBC_CBC);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_DH_IM_AES_CBC_CMAC_128      = new OidIdentifier(OID_id_PACE_DH_IM_AES_CBC_CMAC_128);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_DH_IM_AES_CBC_CMAC_192      = new OidIdentifier(OID_id_PACE_DH_IM_AES_CBC_CMAC_192); 
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_DH_IM_AES_CBC_CMAC_256      = new OidIdentifier(OID_id_PACE_DH_IM_AES_CBC_CMAC_256); 
	
	/* ECDH-IM */
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_IM_3DES_CBC_CBC        = new OidIdentifier(OID_id_PACE_ECDH_IM_3DES_CBC_CBC);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_IM_AES_CBC_CMAC_128    = new OidIdentifier(OID_id_PACE_ECDH_IM_AES_CBC_CMAC_128);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_IM_AES_CBC_CMAC_192    = new OidIdentifier(OID_id_PACE_ECDH_IM_AES_CBC_CMAC_192);
	public final static OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_IM_AES_CBC_CMAC_256    = new OidIdentifier(OID_id_PACE_ECDH_IM_AES_CBC_CMAC_256);
	
	/*--------------------------------------------------------------------------------*/
	
	/* DH-GM */
	public final static String id_PACE_DH_GM_3DES_CBC_CBC_STRING          = "id-PACE-DH-GM-3DES-CBC-CBC";
//	public final static String id_PACE_DH_GM_3DES_CBC_CBC_STRING          = id_PACE_STRING + "-" + DH_GM_STRING + "-" + DES3_CBC_CBC_STRING;
	public final static String id_PACE_DH_GM_AES_CBC_CMAC_128_STRING      = "id-PACE-DH-GM-AES-CBC-CMAC-128";
	public final static String id_PACE_DH_GM_AES_CBC_CMAC_192_STRING      = "id-PACE-DH-GM-AES-CBC-CMAC-192";
	public final static String id_PACE_DH_GM_AES_CBC_CMAC_256_STRING      = "id-PACE-DH-GM-AES-CBC-CMAC-256";
	
	/* ECDH-GM */
	public final static String id_PACE_ECDH_GM_3DES_CBC_CBC_STRING        = "id-PACE-ECDH-GM-3DES-CBC-CBC";
	public final static String id_PACE_ECDH_GM_AES_CBC_CMAC_128_STRING    = "id-PACE-ECDH-GM-AES-CBC-CMAC-128";
	public final static String id_PACE_ECDH_GM_AES_CBC_CMAC_192_STRING    = "id-PACE-ECDH-GM-AES-CBC-CMAC-192";
	public final static String id_PACE_ECDH_GM_AES_CBC_CMAC_256_STRING    = "id-PACE-ECDH-GM-AES-CBC-CMAC-256";
	
	/* DH-IM */
	public final static String id_PACE_DH_IM_3DES_CBC_CBC_STRING          = "id-PACE-DH-IM-3DES-CBC-CBC";
	public final static String id_PACE_DH_IM_AES_CBC_CMAC_128_STRING      = "id-PACE-DH-IM-AES-CBC-CMAC-128";
	public final static String id_PACE_DH_IM_AES_CBC_CMAC_192_STRING      = "id-PACE-DH-IM-AES-CBC-CMAC-192"; 
	public final static String id_PACE_DH_IM_AES_CBC_CMAC_256_STRING      = "id-PACE-DH-IM-AES-CBC-CMAC-256"; 
	
	/* ECDH-IM */
	public final static String id_PACE_ECDH_IM_3DES_CBC_CBC_STRING        = "id-PACE-ECDH-IM-3DES-CBC-CBC";
	public final static String id_PACE_ECDH_IM_AES_CBC_CMAC_128_STRING    = "id-PACE-ECDH-IM-AES-CBC-CMAC-128";
	public final static String id_PACE_ECDH_IM_AES_CBC_CMAC_192_STRING    = "id-PACE-ECDH-IM-AES-CBC-CMAC-192";
	public final static String id_PACE_ECDH_IM_AES_CBC_CMAC_256_STRING    = "id-PACE-ECDH-IM-AES-CBC-CMAC-256";
	
	/*--------------------------------------------------------------------------------*/
	
	/* Password */
	public static final byte PWD_MRZ = (byte) 0x01;
	public static final byte PWD_CAN = (byte) 0x02;
	public static final byte PWD_PIN = (byte) 0x03;
	public static final byte PWD_PUK = (byte) 0x04;
	
	/*--------------------------------------------------------------------------------*/
	
	/* Password */
	public static final String PWD_MRZ_STRING = "MRZ";
	public static final String PWD_CAN_STRING = "CAN";
	public static final String PWD_PIN_STRING = "PIN";
	public static final String PWD_PUK_STRING = "PUK";
	
	/*--------------------------------------------------------------------------------*/
	
	/* Password */
	public final static String[] PASSWORD                                 = new String[]{PWD_MRZ_STRING, PWD_CAN_STRING, PWD_PIN_STRING, PWD_PUK_STRING};
	
}
