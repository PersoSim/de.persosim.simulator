package de.persosim.simulator.protocols.ri;

import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.tlv.TlvTag;

/**
 * This interface provides constants used in the context of the Restricted
 * Identification protocol according to TR-03110.
 * 
 * @author mboonk
 * 
 */
public interface Ri extends Tr03110 {
	public final static Oid id_RI = new GenericOid(id_BSI, new byte[] {0x02, 0x02, 0x05 });

	public final static String id_RI_STRING = "id-RI";

	public final static byte DH = 1;
	public final static byte ECDH = 2;

	public final static Oid id_RI_DH = new GenericOid(id_RI, DH);
	public final static Oid id_RI_ECDH = new GenericOid(id_RI, ECDH);

	public final static String id_RI_DH_STRING = "id-RI-DH";
	public final static String id_RI_ECDH_STRING = "id-RI-ECDH";

	public final static String DH_STRING = "DH";
	public final static String ECDH_STRING = "ECDH";

	public final static byte SHA_1 = 1;
	public final static byte SHA_224 = 2;
	public final static byte SHA_256 = 3;
	public final static byte SHA_384 = 4;
	public final static byte SHA_512 = 5;

	public final static String SHA_1_STRING = "SHA-1";
	public final static String SHA_224_STRING = "SHA-224";
	public final static String SHA_256_STRING = "SHA-256";
	public final static String SHA_384_STRING = "SHA-384";
	public final static String SHA_512_STRING = "SHA-512";

	public final static RiOid id_RI_DH_SHA_1 = new RiOid(id_RI_DH, SHA_1);
	public final static RiOid id_RI_DH_SHA_224 = new RiOid(id_RI_DH, SHA_224);
	public final static RiOid id_RI_DH_SHA_256 = new RiOid(id_RI_DH, SHA_256);
	public final static RiOid id_RI_DH_SHA_384 = new RiOid(id_RI_DH, SHA_384);
	public final static RiOid id_RI_DH_SHA_512 = new RiOid(id_RI_DH, SHA_512);

	public final static RiOid id_RI_ECDH_SHA_1 = new RiOid(id_RI_ECDH, SHA_1);
	public final static RiOid id_RI_ECDH_SHA_224 = new RiOid(id_RI_ECDH, SHA_224);
	public final static RiOid id_RI_ECDH_SHA_256 = new RiOid(id_RI_ECDH, SHA_256);
	public final static RiOid id_RI_ECDH_SHA_384 = new RiOid(id_RI_ECDH, SHA_384);
	public final static RiOid id_RI_ECDH_SHA_512 = new RiOid(id_RI_ECDH, SHA_512);

	public final static String id_RI_DH_SHA_1_STRING = "id-RI-DH-SHA-1";
	public final static String id_RI_DH_SHA_224_STRING = "id-RI-DH-SHA-224";
	public final static String id_RI_DH_SHA_256_STRING = "id-RI-DH-SHA-256";
	public final static String id_RI_DH_SHA_384_STRING = "id-RI-DH-SHA-384";
	public final static String id_RI_DH_SHA_512_STRING = "id-RI-DH-SHA-512";

	public final static String id_RI_ECDH_SHA_1_STRING = "id-RI-ECDH-SHA-1";
	public final static String id_RI_ECDH_SHA_224_STRING = "id-RI-ECDH-SHA-224";
	public final static String id_RI_ECDH_SHA_256_STRING = "id-RI-ECDH-SHA-256";
	public final static String id_RI_ECDH_SHA_384_STRING = "id-RI-ECDH-SHA-384";
	public final static String id_RI_ECDH_SHA_512_STRING = "id-RI-ECDH-SHA-512";

	public final static TlvTag RI_FIRST_SECTOR_KEY_TAG = new TlvTag((byte) 0xA0);
	public final static TlvTag RI_SECOND_SECTOR_KEY_TAG = new TlvTag((byte) 0xA2);

}
