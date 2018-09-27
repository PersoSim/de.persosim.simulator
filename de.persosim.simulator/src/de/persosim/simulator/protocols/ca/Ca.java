package de.persosim.simulator.protocols.ca;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.utils.HexString;

/**
 * This interface provides constants used in the context of the Chip Authentication protocol according to TR-03110.
 * @author slutters
 *
 */
public interface Ca extends Tr03110 {
	/* CA OIDs according to TR-03110 v2.1 part 3 */
	

//	                                                                                                   0x02 bsi-de protocols(2)
//	                                                                                                         0x02 smartcard(2)
//	                                                                                                               0x03 ca protocol(3)
	public static final Oid id_CA                                  = new GenericOid(id_BSI, HexString.toByteArray("020203"));
	public static final String id_CA_STRING                        = "id-CA";
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	/* Key agreement & mapping */
	public static final byte DH                                    = (byte) 0x01;
	public static final byte ECDH                                  = (byte) 0x02;
	
	public static final CaOid id_CA_DH                             = new CaOid(id_CA, DH);
	public static final CaOid id_CA_ECDH                           = new CaOid(id_CA, ECDH);
	
	/*--------------------------------------------------------------------------------*/
	
	/* Key agreement & mapping */
	public static final String DH_STRING                           = "DH";
	public static final String ECDH_STRING                         = "ECDH";
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	/*--------------------------------------------------------------------------------*/
	
	
	
	/* DH */
	public static final CaOid id_CA_DH_AES_CBC_CMAC_128      = new CaOid(id_CA_DH, AES_CBC_CMAC_128);
	public static final CaOid id_CA_DH_AES_CBC_CMAC_192      = new CaOid(id_CA_DH, AES_CBC_CMAC_192);
	public static final CaOid id_CA_DH_AES_CBC_CMAC_256      = new CaOid(id_CA_DH, AES_CBC_CMAC_256);
	
	/* ECDH */
	public static final CaOid id_CA_ECDH_AES_CBC_CMAC_128    = new CaOid(id_CA_ECDH, AES_CBC_CMAC_128);
	public static final CaOid id_CA_ECDH_AES_CBC_CMAC_192    = new CaOid(id_CA_ECDH, AES_CBC_CMAC_192);
	public static final CaOid id_CA_ECDH_AES_CBC_CMAC_256    = new CaOid(id_CA_ECDH, AES_CBC_CMAC_256);
	
	/*--------------------------------------------------------------------------------*/
	
	// {@link OidIdentifier}
	
	/* DH */
	public static final OidIdentifier OID_IDENTIFIER_id_CA_DH_AES_CBC_CMAC_128      = new OidIdentifier(id_CA_DH_AES_CBC_CMAC_128);
	public static final OidIdentifier OID_IDENTIFIER_id_CA_DH_AES_CBC_CMAC_192      = new OidIdentifier(id_CA_DH_AES_CBC_CMAC_192);
	public static final OidIdentifier OID_IDENTIFIER_id_CA_DH_AES_CBC_CMAC_256      = new OidIdentifier(id_CA_DH_AES_CBC_CMAC_256);
	
	/* ECDH */
	public static final OidIdentifier OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128    = new OidIdentifier(id_CA_ECDH_AES_CBC_CMAC_128);
	public static final OidIdentifier OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_192    = new OidIdentifier(id_CA_ECDH_AES_CBC_CMAC_192);
	public static final OidIdentifier OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_256    = new OidIdentifier(id_CA_ECDH_AES_CBC_CMAC_256);
	
	/*--------------------------------------------------------------------------------*/
	
	/* DH */
	public static final String id_CA_DH_AES_CBC_CMAC_128_STRING      = "id-CA-DH-AES-CBC-CMAC-128";
	public static final String id_CA_DH_AES_CBC_CMAC_192_STRING      = "id-CA-DH-AES-CBC-CMAC-192";
	public static final String id_CA_DH_AES_CBC_CMAC_256_STRING      = "id-CA-DH-AES-CBC-CMAC-256";
	
	/* ECDH */
	public static final String id_CA_ECDH_AES_CBC_CMAC_128_STRING    = "id-CA-ECDH-AES-CBC-CMAC-128";
	public static final String id_CA_ECDH_AES_CBC_CMAC_192_STRING    = "id-CA-ECDH-AES-CBC-CMAC-192";
	public static final String id_CA_ECDH_AES_CBC_CMAC_256_STRING    = "id-CA-ECDH-AES-CBC-CMAC-256";
	
}
