package de.persosim.simulator.protocols;

import de.persosim.simulator.utils.Utils;

/**
 * The {@link RoleOid} encodes the terminal type used during Terminal
 * Authentication version 2 as defined in TR-03110.
 * 
 * @author mboonk
 *
 */
public class RoleOid extends GenericOid {

	// terminal types
	public final static RoleOid id_Roles = new RoleOid(
			Utils.appendBytes(Tr03110.id_BSI, new byte[] { 0x03, 0x01, 0x02 }), "id-roles");

	public static final RoleOid id_IS = new RoleOid(Utils.appendBytes(id_Roles.oidByteArray, (byte) 0x01), "id-IS");
	public static final RoleOid id_AT = new RoleOid(Utils.appendBytes(id_Roles.oidByteArray, (byte) 0x02), "id-AT");
	public static final RoleOid id_ST = new RoleOid(Utils.appendBytes(id_Roles.oidByteArray, (byte) 0x03), "id-ST");

	private String idString;
	
	public RoleOid(byte[] byteArrayRepresentation, String id) {
		super(byteArrayRepresentation);
		this.idString = id;
	}

	@Override
	public String getIdString() {
		return idString;
	}
	
}
