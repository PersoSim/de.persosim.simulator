package de.persosim.simulator.protocols.ca;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.utils.Utils;

/**
 * This interface provides constants used in the context of the Chip Authentication protocol according to TR-03110.
 * @author slutters
 *
 */
public interface Ca extends Tr03110 {
	/* CA OIDs according to TR-03110 v2.1 part 3 */
	
	/* CA id */
//	                                                                                                      0x02 bsi-de protocols(2)
//	                                                                                                            0x02 smartcard(2)
//	                                                                                                                  0x03 3 ca protocol(3)
	
	public final static byte[] id_CA                               = Utils.appendBytes(id_BSI, new byte[]{0x02, 0x02, 0x03});
	public final static String id_CA_STRING                        = "id-CA";
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	/* Key agreement & mapping */
	public final static byte DH                                    = (byte) 0x01;
	public final static byte ECDH                                  = (byte) 0x02;
	
	public final static byte[] KEY_AGREEMENT_AND_MAPPING           = new byte[]{DH, ECDH};
	
	/*--------------------------------------------------------------------------------*/
	
	/* Key agreement & mapping */
	public final static String DH_STRING                           = "DH";
	public final static String ECDH_STRING                         = "ECDH";
	
	public final static String[] KEY_AGREEMENT_AND_MAPPING_STRING  = new String[]{DH_STRING, ECDH_STRING};
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	/* DH */
	public final static byte[] id_CA_DH_3DES_CBC_CBC          = Utils.appendBytes(id_CA, DH, DES3_CBC_CBC);
	public final static byte[] id_CA_DH_AES_CBC_CMAC_128      = Utils.appendBytes(id_CA, DH, AES_CBC_CMAC_128);
	public final static byte[] id_CA_DH_AES_CBC_CMAC_192      = Utils.appendBytes(id_CA, DH, AES_CBC_CMAC_192);
	public final static byte[] id_CA_DH_AES_CBC_CMAC_256      = Utils.appendBytes(id_CA, DH, AES_CBC_CMAC_256);
	
	/* ECDH */
	public final static byte[] id_CA_ECDH_3DES_CBC_CBC        = Utils.appendBytes(id_CA, ECDH, DES3_CBC_CBC);
	public final static byte[] id_CA_ECDH_AES_CBC_CMAC_128    = Utils.appendBytes(id_CA, ECDH, AES_CBC_CMAC_128);
	public final static byte[] id_CA_ECDH_AES_CBC_CMAC_192    = Utils.appendBytes(id_CA, ECDH, AES_CBC_CMAC_192);
	public final static byte[] id_CA_ECDH_AES_CBC_CMAC_256    = Utils.appendBytes(id_CA, ECDH, AES_CBC_CMAC_256);
	
	/*--------------------------------------------------------------------------------*/
	
	public final static Oid OID_id_CA                            = new Oid(id_CA);
	
	/* DH */
	public final static CaOid OID_id_CA_DH_3DES_CBC_CBC          = new CaOid(id_CA_DH_3DES_CBC_CBC);
	public final static CaOid OID_id_CA_DH_AES_CBC_CMAC_128      = new CaOid(id_CA_DH_AES_CBC_CMAC_128);
	public final static CaOid OID_id_CA_DH_AES_CBC_CMAC_192      = new CaOid(id_CA_DH_AES_CBC_CMAC_192);
	public final static CaOid OID_id_CA_DH_AES_CBC_CMAC_256      = new CaOid(id_CA_DH_AES_CBC_CMAC_256);
	
	/* ECDH */
	public final static CaOid OID_id_CA_ECDH_3DES_CBC_CBC        = new CaOid(id_CA_ECDH_3DES_CBC_CBC);
	public final static CaOid OID_id_CA_ECDH_AES_CBC_CMAC_128    = new CaOid(id_CA_ECDH_AES_CBC_CMAC_128);
	public final static CaOid OID_id_CA_ECDH_AES_CBC_CMAC_192    = new CaOid(id_CA_ECDH_AES_CBC_CMAC_192);
	public final static CaOid OID_id_CA_ECDH_AES_CBC_CMAC_256    = new CaOid(id_CA_ECDH_AES_CBC_CMAC_256);
	
	/*--------------------------------------------------------------------------------*/
	
	// {@link OidIdentifier}
	
	/* DH */
	public final static OidIdentifier OID_IDENTIFIER_id_CA_DH_3DES_CBC_CBC          = new OidIdentifier(OID_id_CA_DH_3DES_CBC_CBC);
	public final static OidIdentifier OID_IDENTIFIER_id_CA_DH_AES_CBC_CMAC_128      = new OidIdentifier(OID_id_CA_DH_AES_CBC_CMAC_128);
	public final static OidIdentifier OID_IDENTIFIER_id_CA_DH_AES_CBC_CMAC_192      = new OidIdentifier(OID_id_CA_DH_AES_CBC_CMAC_192);
	public final static OidIdentifier OID_IDENTIFIER_id_CA_DH_AES_CBC_CMAC_256      = new OidIdentifier(OID_id_CA_DH_AES_CBC_CMAC_256);
	
	/* ECDH */
	public final static OidIdentifier OID_IDENTIFIER_id_CA_ECDH_3DES_CBC_CBC        = new OidIdentifier(OID_id_CA_ECDH_3DES_CBC_CBC);
	public final static OidIdentifier OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128    = new OidIdentifier(OID_id_CA_ECDH_AES_CBC_CMAC_128);
	public final static OidIdentifier OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_192    = new OidIdentifier(OID_id_CA_ECDH_AES_CBC_CMAC_192);
	public final static OidIdentifier OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_256    = new OidIdentifier(OID_id_CA_ECDH_AES_CBC_CMAC_256);
	
	/*--------------------------------------------------------------------------------*/
	
	/* DH */
	public final static String id_CA_DH_3DES_CBC_CBC_STRING          = "id-CA-DH-3DES-CBC-CBC";
	public final static String id_CA_DH_AES_CBC_CMAC_128_STRING      = "id-CA-DH-AES-CBC-CMAC-128";
	public final static String id_CA_DH_AES_CBC_CMAC_192_STRING      = "id-CA-DH-AES-CBC-CMAC-192";
	public final static String id_CA_DH_AES_CBC_CMAC_256_STRING      = "id-CA-DH-AES-CBC-CMAC-256";
	
	/* ECDH */
	public final static String id_CA_ECDH_3DES_CBC_CBC_STRING        = "id-CA-ECDH-3DES-CBC-CBC";
	public final static String id_CA_ECDH_AES_CBC_CMAC_128_STRING    = "id-CA-ECDH-AES-CBC-CMAC-128";
	public final static String id_CA_ECDH_AES_CBC_CMAC_192_STRING    = "id-CA-ECDH-AES-CBC-CMAC-192";
	public final static String id_CA_ECDH_AES_CBC_CMAC_256_STRING    = "id-CA-ECDH-AES-CBC-CMAC-256";
	
}
