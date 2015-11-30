package de.persosim.simulator.crypto.certificates;

import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.RoleOid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.utils.Utils;

/**
 * This OID contains the {@link Oid} instances used for certificate extensions.
 * @author mboonk
 *
 */
public class ExtensionOid extends GenericOid {
	public ExtensionOid(byte[] byteArrayRepresentation) {
		super(byteArrayRepresentation);
	}

	public final static Oid id_Extensions          = new GenericOid(Utils.appendBytes(Tr03110.id_BSI, new byte[]{0x03, 0x01, 0x03}));
	
	public static final Oid id_Description         = new GenericOid(Utils.appendBytes(id_Extensions.toByteArray(), (byte) 0x01));
	public static final Oid id_Sector              = new GenericOid(Utils.appendBytes(id_Extensions.toByteArray(), (byte) 0x02));

	public static final Oid id_eIDAccess           = new GenericOid(Utils.appendBytes(RoleOid.id_AT.toByteArray(), (byte) 0x01));
	public static final Oid id_specialFunctions    = new GenericOid(Utils.appendBytes(RoleOid.id_AT.toByteArray(), (byte) 0x02));
	
	public static final Oid id_Ps_Sector           = new GenericOid(Utils.appendBytes(id_Extensions.toByteArray(), (byte) 0x03));
	
}
