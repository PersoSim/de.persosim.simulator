package de.persosim.simulator.protocols.auxVerification;

import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.utils.Utils;

/**
 * This contains OIDs used in the context of auxiliary data verification.
 * @author mboonk
 *
 */
public class AuxOid {

	public final static Oid id_AuxiliaryData       = new GenericOid(Utils.appendBytes(Tr03110.id_BSI, new byte[]{0x03, 0x01, 0x04}));
	
	public static final Oid id_DateOfBirth         = new GenericOid(Utils.appendBytes(id_AuxiliaryData.toByteArray(), (byte) 0x01));
	public static final Oid id_DateOfExpiry        = new GenericOid(Utils.appendBytes(id_AuxiliaryData.toByteArray(), (byte) 0x02));
	public static final Oid id_CommunityID         = new GenericOid(Utils.appendBytes(id_AuxiliaryData.toByteArray(), (byte) 0x03));
		
}
