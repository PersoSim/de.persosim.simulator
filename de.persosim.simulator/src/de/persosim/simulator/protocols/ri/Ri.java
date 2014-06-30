package de.persosim.simulator.protocols.ri;

import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.Utils;

/**
 * This interface provides constants used in the context of the Restricted
 * Identification protocol according to TR-03110.
 * 
 * @author mboonk
 * 
 */
public interface Ri {
	public final static byte[] id_BSI = { 0x04, 0x00, 0x7F, 0x00, 0x07 };
	public final static byte[] id_RI = Utils.appendBytes(id_BSI, new byte[] {
			0x02, 0x02, 0x05 });

	public final static String id_RI_STRING = "id-RI";

	public final static byte DH = 1;
	public final static byte ECDH = 2;

	public final static byte[] id_RI_DH = Utils.appendBytes(id_RI, DH);
	public final static byte[] id_RI_ECDH = Utils.appendBytes(id_RI, ECDH);

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

	public final static byte[] id_RI_DH_SHA_1 = Utils.appendBytes(id_RI_DH,
			SHA_1);
	public final static byte[] id_RI_DH_SHA_224 = Utils.appendBytes(id_RI_DH,
			SHA_224);
	public final static byte[] id_RI_DH_SHA_256 = Utils.appendBytes(id_RI_DH,
			SHA_256);
	public final static byte[] id_RI_DH_SHA_384 = Utils.appendBytes(id_RI_DH,
			SHA_384);
	public final static byte[] id_RI_DH_SHA_512 = Utils.appendBytes(id_RI_DH,
			SHA_512);

	public final static byte[] id_RI_ECDH_SHA_1 = Utils.appendBytes(id_RI_ECDH,
			SHA_1);
	public final static byte[] id_RI_ECDH_SHA_224 = Utils.appendBytes(
			id_RI_ECDH, SHA_224);
	public final static byte[] id_RI_ECDH_SHA_256 = Utils.appendBytes(
			id_RI_ECDH, SHA_256);
	public final static byte[] id_RI_ECDH_SHA_384 = Utils.appendBytes(
			id_RI_ECDH, SHA_384);
	public final static byte[] id_RI_ECDH_SHA_512 = Utils.appendBytes(
			id_RI_ECDH, SHA_512);

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
