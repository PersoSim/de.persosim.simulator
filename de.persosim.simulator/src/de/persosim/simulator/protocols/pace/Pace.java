package de.persosim.simulator.protocols.pace;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.utils.HexString;

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
	
	public static final Oid id_PACE                             = new GenericOid(id_BSI, HexString.toByteArray("020204"));
	public static final String id_PACE_STRING                      = "id-PACE";
	
	/*--------------------------------------------------------------------------------*/
	
	/* Key agreement & mapping */
	public static final byte ECDH_GM                               = (byte) 0x02;
	
	/*--------------------------------------------------------------------------------*/
	
	/* Key agreement & mapping */
	public static final String ECDH_GM_STRING                      = "ECDH-GM";
	
	/*--------------------------------------------------------------------------------*/
		
	
	/* ECDH-GM */
	public static final PaceOid id_PACE_ECDH_GM_AES_CBC_CMAC_128    = new PaceOid(id_PACE, ECDH_GM, AES_CBC_CMAC_128);
	public static final PaceOid id_PACE_ECDH_GM_AES_CBC_CMAC_192    = new PaceOid(id_PACE, ECDH_GM, AES_CBC_CMAC_192);
	public static final PaceOid id_PACE_ECDH_GM_AES_CBC_CMAC_256    = new PaceOid(id_PACE, ECDH_GM, AES_CBC_CMAC_256);
	
	/*--------------------------------------------------------------------------------*/
	
	// {@link OidIdentifier}
	
	/* ECDH-GM */
	public static final OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_GM_AES_CBC_CMAC_128    = new OidIdentifier(id_PACE_ECDH_GM_AES_CBC_CMAC_128);
	public static final OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_GM_AES_CBC_CMAC_192    = new OidIdentifier(id_PACE_ECDH_GM_AES_CBC_CMAC_192);
	public static final OidIdentifier OID_IDENTIFIER_id_PACE_ECDH_GM_AES_CBC_CMAC_256    = new OidIdentifier(id_PACE_ECDH_GM_AES_CBC_CMAC_256);
	
	/*--------------------------------------------------------------------------------*/
	
	/* ECDH-GM */
	public static final String id_PACE_ECDH_GM_AES_CBC_CMAC_128_STRING    = "id-PACE-ECDH-GM-AES-CBC-CMAC-128";
	public static final String id_PACE_ECDH_GM_AES_CBC_CMAC_192_STRING    = "id-PACE-ECDH-GM-AES-CBC-CMAC-192";
	public static final String id_PACE_ECDH_GM_AES_CBC_CMAC_256_STRING    = "id-PACE-ECDH-GM-AES-CBC-CMAC-256";
	
	/*--------------------------------------------------------------------------------*/
	
	/* Password */
	public static final byte PWD_MRZ = (byte) 0x01;
	public static final byte PWD_CAN = (byte) 0x02;
	public static final byte PWD_PIN = (byte) 0x03;
	public static final byte PWD_PUK = (byte) 0x04;
	
	/*--------------------------------------------------------------------------------*/
	
	/* Password */
	public static final String PWD_MRZ_STRING = "MRZ"; //NOSONAR this is an identifier only, no credential leaked
	public static final String PWD_CAN_STRING = "CAN"; //NOSONAR this is an identifier only, no credential leaked
	public static final String PWD_PIN_STRING = "PIN"; //NOSONAR this is an identifier only, no credential leaked
	public static final String PWD_PUK_STRING = "PUK"; //NOSONAR this is an identifier only, no credential leaked
	
}
