package de.persosim.simulator.crypto;

import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class SignatureOids {
	public final static Oid id_Pkcs1 = new GenericOid(HexString.toByteArray("2A864886F70D0101"));

	public final static Oid id_sha1withrsaencryption = new GenericOid(Utils.appendBytes(id_Pkcs1.toByteArray(), new byte[] { 0x05 }));
	public final static Oid id_sha256withrsaencryption = new GenericOid(Utils.appendBytes(id_Pkcs1.toByteArray(), new byte[] { 0x0b }));
	public final static Oid id_rsassapss = new GenericOid(Utils.appendBytes(id_Pkcs1.toByteArray(), new byte[] { 0x0a }));
	public final static Oid id_ecdsawithSHA256 = new GenericOid(HexString.toByteArray("2A8648CE3D040302"));

	
	
}
