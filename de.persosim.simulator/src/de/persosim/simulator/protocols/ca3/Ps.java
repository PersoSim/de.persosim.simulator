package de.persosim.simulator.protocols.ca3;

import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;

/**
 * This interface provides constants used in the context of the Pseudonymous Signatures according to TR-03110.
 * 
 * @author slutters
 *
 */
public interface Ps extends de.persosim.simulator.protocols.Tr03110 {
	
	public static final Oid id_PS 									= new GenericOid(id_BSI, new byte[] {0x02, 0x02, 0x0B });
	public static final Oid id_PS_PK								= new GenericOid(id_BSI, new byte[] {0x02, 0x02, 0x01, 0x03 });
	public static final Oid id_PS_PK_ECDH_ECSchnorr					= new GenericOid(id_PS_PK, new byte[] {0x02});
	public static final Oid id_EC_PSPUBLIC_KEY						= new GenericOid(id_BSI, new byte[] {0x01, 0x01, 0x02, 0x03 });
	
	public static final byte ECDH									= (byte) 0x02;
	public static final byte SHA_256								= (byte) 0x03;
	
	public static final String id_PS_STRING							= "id-PS";
	public static final String ECDH_STRING							= "ECDH";
	
}
