package de.persosim.simulator.crypto.certificates;

import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.RoleOid;
import de.persosim.simulator.protocols.Tr03110;

/**
 * This OID contains the {@link Oid} instances used for certificate extensions.
 * @author mboonk
 *
 */
public class ExtensionOid {
	public static final Oid id_Extensions          = new GenericOid(Tr03110.id_BSI, new byte[]{0x03, 0x01, 0x03});
	
	public static final Oid id_Description         = new GenericOid(id_Extensions, (byte) 0x01);
	public static final Oid id_Sector              = new GenericOid(id_Extensions, (byte) 0x02);

	public static final Oid id_eIDAccess           = new GenericOid(RoleOid.id_AT, (byte) 0x01);
	public static final Oid id_specialFunctions    = new GenericOid(RoleOid.id_AT, (byte) 0x02);
	
	public static final Oid id_Ps_Sector           = new GenericOid(id_Extensions, (byte) 0x03);
	
}
