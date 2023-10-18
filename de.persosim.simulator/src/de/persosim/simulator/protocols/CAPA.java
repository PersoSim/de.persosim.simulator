package de.persosim.simulator.protocols;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.utils.HexString;

/**
 * This interface provides constants used in the context of the CA+PA protocol.
 */
public interface CAPA extends Tr03110 {

	public static final String PROTOCOL_NAME = "CA+PA";

	public static final Oid id_protocols = new GenericOid(id_BSI, HexString.toByteArray("02"));
	public static final Oid id_smartcard = new GenericOid(id_protocols, HexString.toByteArray("02"));

	public static final Oid id_CAPA = new GenericOid(id_smartcard, HexString.toByteArray("0E"));

	public static final Oid id_CAPA_ECDH = new GenericOid(id_CAPA, HexString.toByteArray("02"));
	public static final Oid id_CAPA_ECDH_AES_CBC_CMAC_128 = new GenericOid(id_CAPA_ECDH, HexString.toByteArray("02"));
	public static final Oid id_CAPA_ECDH_AES_CBC_CMAC_192 = new GenericOid(id_CAPA_ECDH, HexString.toByteArray("03"));
	public static final Oid id_CAPA_ECDH_AES_CBC_CMAC_256 = new GenericOid(id_CAPA_ECDH, HexString.toByteArray("04"));

	public static final OidIdentifier OID_IDENTIFIER_id_CAPA_ECDH = new OidIdentifier(id_CAPA_ECDH);
	public static final OidIdentifier OID_IDENTIFIER_id_CAPA_ECDH_AES_CBC_CMAC_128 = new OidIdentifier(
			id_CAPA_ECDH_AES_CBC_CMAC_128);
	public static final OidIdentifier OID_IDENTIFIER_id_CAPA_ECDH_AES_CBC_CMAC_192 = new OidIdentifier(
			id_CAPA_ECDH_AES_CBC_CMAC_192);
	public static final OidIdentifier OID_IDENTIFIER_id_CAPA_ECDH_AES_CBC_CMAC_256 = new OidIdentifier(
			id_CAPA_ECDH_AES_CBC_CMAC_256);

	public static final int CAPA_INFO_VERSION = 2;

}
