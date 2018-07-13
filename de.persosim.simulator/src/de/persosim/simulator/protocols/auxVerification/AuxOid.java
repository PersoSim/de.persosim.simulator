package de.persosim.simulator.protocols.auxVerification;

import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Tr03110;

/**
 * This contains OIDs used in the context of auxiliary data verification.
 * @author mboonk
 *
 */
public class AuxOid {

	public static final Oid id_AuxiliaryData       = new GenericOid(Tr03110.id_BSI, new byte[]{0x03, 0x01, 0x04});
	
	public static final Oid id_DateOfBirth         = new GenericOid(id_AuxiliaryData, (byte) 0x01);
	public static final Oid id_DateOfExpiry        = new GenericOid(id_AuxiliaryData, (byte) 0x02);
	public static final Oid id_CommunityID         = new GenericOid(id_AuxiliaryData, (byte) 0x03);
		
}
