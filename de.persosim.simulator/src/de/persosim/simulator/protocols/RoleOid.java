package de.persosim.simulator.protocols;

import java.util.Arrays;

/**
 * The {@link RoleOid} encodes the terminal type used during Terminal
 * Authentication version 2 as defined in TR-03110.
 * 
 * @author mboonk
 *
 */
public class RoleOid extends GenericOid {

	// terminal types
	public static final RoleOid id_Roles = new RoleOid(Tr03110.id_BSI, new byte[] { 0x03, 0x01, 0x02 });

	public static final RoleOid id_IS = new RoleOid(id_Roles, (byte) 0x01);
	public static final RoleOid id_AT = new RoleOid(id_Roles, (byte) 0x02);
	public static final RoleOid id_ST = new RoleOid(id_Roles, (byte) 0x03);
	
	public static final String ID_ROLES_STRING = "id-roles";
	public static final String ID_IS_STRING = "id-IS";
	public static final String ID_AT_STRING = "id-AT";
	public static final String ID_ST_STRING = "id-ST";

	public RoleOid(byte[] byteArrayRepresentation) {
		super(byteArrayRepresentation);
	}

	public RoleOid(Oid prefix, byte... suffix) {
		super(prefix, suffix);
	}

	@Override
	public String getIdString() {
		if (Arrays.equals(oidByteArray, id_Roles.toByteArray())) return ID_ROLES_STRING;
		if (Arrays.equals(oidByteArray, id_IS.toByteArray())) return ID_IS_STRING;
		if (Arrays.equals(oidByteArray, id_AT.toByteArray())) return ID_AT_STRING;
		if (Arrays.equals(oidByteArray, id_ST.toByteArray())) return ID_ST_STRING;
		
		return null;
	}
	
}
