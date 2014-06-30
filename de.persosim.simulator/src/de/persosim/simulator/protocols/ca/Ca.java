package de.persosim.simulator.protocols.ca;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.utils.Utils;

/**
 * This interface provides constants used in the context of the Chip Authentication protocol according to TR-03110.
 * @author slutters
 *
 */
//XXX SLS move overlap with Pace interface to common parent interface
public interface Ca {
	/* CA OIDs according to TR-03110 v2.1 part 3 */
	
	/* CA id */
	  
//	                                                            0x00 itu-t(0)
//	                                                                  0x04 identified-organization(4)
//	                                                                        0x00 etsi(0)
//	                                                                              0x7F reserved(127)
//	                                                                                    0x00 etsi-identified-organization(0)
//	                                                                                          0x07 7
//	                                                                                                0x02 bsi-de protocols(2)
//	                                                                                                      0x02 smartcard(2)
//	                                                                                                            0x03 3 ca protocol(3)
	
	public final static byte[] id_BSI                              = {0x04, 0x00, 0x7F, 0x00, 0x07};
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
	
	/* Symmetric cipher */
	public final static byte DES3_CBC_CBC                          = (byte) 0x01;
	public final static byte AES_CBC_CMAC_128                      = (byte) 0x02;
	public final static byte AES_CBC_CMAC_192                      = (byte) 0x03;
	public final static byte AES_CBC_CMAC_256                      = (byte) 0x04;
	
	public final static byte[] SYMMETRIC_CIPHER                    = new byte[]{DES3_CBC_CBC, AES_CBC_CMAC_128, AES_CBC_CMAC_192, AES_CBC_CMAC_256};
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	// XXX SLS migrate uses of byte[] to CaOid where possible
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
